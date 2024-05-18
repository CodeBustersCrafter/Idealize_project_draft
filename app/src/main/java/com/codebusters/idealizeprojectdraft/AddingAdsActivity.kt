@file:Suppress("DEPRECATION")

package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityAddingAdsBinding
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.Item
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
class AddingAdsActivity : AppCompatActivity() {
    private var myTags = MyTags()
    private lateinit var binding: ActivityAddingAdsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var progressDialog: ProgressDialog

    private var uid = ""
    private lateinit var item : Item
    private var uri: Uri? = null
    private lateinit var idealizeUser : IdealizeUser

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddingAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageViewSellScreen.visibility = View.GONE

        auth= FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        storage=FirebaseStorage.getInstance()
        uid = intent.getStringExtra(myTags.intentUID).toString()


        firestore =FirebaseFirestore.getInstance()
        firestore.collection(myTags.users).document(uid).get().addOnSuccessListener {
                documentSnapshot ->
            if(documentSnapshot.exists()){
                idealizeUser = ModelBuilder().getUser(documentSnapshot)

                binding.btnOpenCameraSellScreen.setOnClickListener {
                    if(binding.ediTextCategorySellScreen.text.toString().trim()!=""){
                        //open the camera
                        imageChooser()
                    }else{
                        Toast.makeText(this,"Please insert a category", Toast.LENGTH_SHORT).show()
                    }
                }


                binding.btnSaveSellScreen.setOnClickListener {
                    progressDialog = ProgressDialog(this)
                    progressDialog.setCancelable(false)
                    progressDialog.setTitle("Saving...")
                    progressDialog.create()
                    progressDialog.show()
                    item = init()
                    //validate inputs
                    //upload
                    val imgRef =storage.getReference(myTags.ads).child(item.adId).child("img")
                    val uploadTask=imgRef.putFile(item.photo)
                    uploadTask.addOnSuccessListener  {
                        imgRef.downloadUrl.addOnSuccessListener{
                            uri ->
                            item.photo = uri
                            idealizeUser.adCount += 1

                            val map = ModelBuilder().getItemAsMap(item)

                            firestore.collection(myTags.users).document(idealizeUser.uid).collection(myTags.ads).document(item.adId)
                                .set(map).addOnCompleteListener {
                                        task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(this,"Saved! from User", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(this,"Not Saved! from User. Try Again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            firestore.collection(myTags.ads).document(item.adId)
                                .set(map).addOnCompleteListener {
                                        task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(this,"Updated! from advertisements", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(this,"Not Updated! from advertisements. Try Again",
                                            Toast.LENGTH_SHORT).show()
                                    }

                                }
                            firestore.collection(myTags.users).document(idealizeUser.uid).update(myTags.userAdCount,idealizeUser.adCount)
                                .addOnCompleteListener {
                                        task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(this,"Updated! from user", Toast.LENGTH_SHORT).show()
                                        progressDialog.cancel()
                                        val intent = Intent(baseContext,MainActivity::class.java)
                                        intent.putExtra(myTags.intentType,myTags.userMode)
                                        intent.putExtra(myTags.intentUID,uid)
                                        startActivity(intent)
                                    }else{
                                        Toast.makeText(this,"Not Updated! from user. Try Again", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    private fun imageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 200)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                // Display the selected image in the ImageView
                progressDialog = ProgressDialog(this)
                progressDialog.setCancelable(false)
                progressDialog.setTitle("Verifying the image...")
                progressDialog.create()
                progressDialog.show()

                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.0-pro-vision-latest",
                    apiKey = BuildConfig.apikey,
                )

                val prompt = "Recognize this image. If it is strictly related to "+binding.ediTextCategorySellScreen.text.toString()+" category and a clear image, say \"YES\" otherwise \"NO\". Don't give me descriptions."
                val bitmap = Converter().getBitmap(it,this)

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                MainScope().launch {
                    val response = generativeModel.generateContent(inputContent)
                    if (response.text.toString().trim()=="YES"){
                        binding.imageViewSellScreen.setImageURI(it)
                        binding.imageViewSellScreen.visibility = View.VISIBLE
                        uri=it
                    }else{
                        Toast.makeText(this@AddingAdsActivity,"Select a valid and clear image",Toast.LENGTH_SHORT).show()
                        uri = null
                    }
                    progressDialog.cancel()
                }
            }
        }
    }

    private fun init(): Item {
        item =Item(binding.ediTextNameSellScreen.text.toString(),
            binding.ediTextPriceSellScreen.text.toString(),
            "",
            "",
            binding.ediTextDescriptionSellScreen.text.toString(),
            binding.ediTextQuantitySellScreen.text.toString(),
            uri!!,
            binding.ediTextCategorySellScreen.text.toString(),
            myTags.adVisible,
            uid+"_"+(idealizeUser.adCount+1),
            uid
            )
        return getDateTime(item)
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        val intent = Intent(baseContext,MainActivity::class.java)
        intent.putExtra(myTags.intentType,myTags.userMode)
        intent.putExtra(myTags.intentUID,uid)
        startActivity(intent)

    }

    @SuppressLint("SimpleDateFormat")
    fun getDateTime(item : Item) : Item {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        val currentFormatted = formatter.format(currentDate)

        val formatter2 = SimpleDateFormat("hh:mm")
        val currentTime = Date()
        val currentFormatted2 = formatter2.format(currentTime)

        item.date=currentFormatted
        item.time=currentFormatted2
        return item
    }

}