@file:Suppress("DEPRECATION")

package com.codebusters.idealizeprojectdraft


import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityAddingAdsBinding
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.Item
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.network_services.NetworkChangeListener
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class AddingAdsActivity : AppCompatActivity() {
    private var myTags = MyTags()
    private lateinit var binding: ActivityAddingAdsBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage : FirebaseStorage

//    private lateinit var progressDialog: ProgressDialog
    private val progressDialog by lazy { CustomProgressDialog(this) }

    private var uid = ""
    private lateinit var item : Item
    private var uri: Uri? = null
    private lateinit var idealizeUser : IdealizeUser

    private val networkChangeListener: NetworkChangeListener = NetworkChangeListener()

    @SuppressLint("SuspiciousIndentation", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddingAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                val catagories = resources.getStringArray(R.array.categories)
                val arrayAdapter = ArrayAdapter(this,R.layout.drop_down_menu,catagories)
                binding.autoCompleteTextViewSellScreen.setAdapter(arrayAdapter)

                binding.btnOpenCameraSellScreen.setOnClickListener {
                    if(binding.autoCompleteTextViewSellScreen.text.toString().trim()!=resources.getString(R.string.select_one)){
                        //open the camera
                        imageChooser()
                    }else{
                        Toast.makeText(this,"Please insert a category", Toast.LENGTH_SHORT).show()
                    }
                }


                binding.btnSaveSellScreen.setOnClickListener {
                    if(uri==null || binding.ediTextNameSellScreen.text.toString().trim()=="" || binding.ediTextPriceSellScreen.text.toString().trim()==""){
                        Toast.makeText(this,"Please fill the required fields", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if(binding.ediTextPriceSellScreen.text.toString()[0] == '-' || binding.ediTextQuantitySellScreen.text.toString()[0] == '-'){
                        Toast.makeText(this,"Please enter a valid price and quantity", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
//                    show progress dialog
//                    progressDialog = ProgressDialog(this)
//                    progressDialog.setCancelable(false)
//                    progressDialog.setTitle("Please wait...")
//                    progressDialog.setMessage("uploading the ad...")
//                    progressDialog.create()
//                    progressDialog.show()

                    progressDialog.start("uploading the ad...")


                    item = init()
                    //validate inputs
                    //upload
                    val imgRef =storage.getReference(myTags.ads).child(item.adId).child("img")
                    val uploadTask=imgRef.putFile(item.photo)
                    uploadTask.addOnSuccessListener  {
                        imgRef.downloadUrl.addOnSuccessListener{
                            uri ->
                            item.photo = uri
                            val count = idealizeUser.adCount.toInt()
                            idealizeUser.adCount = (count+1).toString()

                            val map = ModelBuilder().getItemAsMap(item)

                            // Add keywords to map
                            map[myTags.keywords] = generateKeywords(item.name)
                            map[myTags.adLocation] = idealizeUser.location

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
//                                        progressDialog.cancel()
                                        progressDialog.stop()


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
//                progressDialog = ProgressDialog(this)
//                progressDialog.setCancelable(false)
//                progressDialog.setTitle("Please wait...")
//                progressDialog.setMessage("Verifying the image...")
//                progressDialog.create()
//                progressDialog.show()
                progressDialog.start("verifying the image")


                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.0-pro-vision-latest",
                    apiKey = BuildConfig.apikey,
                )

                var temp = binding.autoCompleteTextViewSellScreen.text.toString()
                if(temp=="select one"){
                    temp = "legal products in market"
                }
                val prompt = "Recognize this image. If it is strictly related to $temp category ,a clear image and the most important fact is, the image should not contain any human bodies or animal bodies, say \"YES\" otherwise \"NO\". Don't give me descriptions."
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
//                    progressDialog.cancel()
                    progressDialog.stop()




                }
            }
        }
    }

    private fun init(): Item {

        item =Item(
            binding.ediTextNameSellScreen.text.toString(),
            binding.ediTextPriceSellScreen.text.toString(),
            "",
            "",
            binding.ediTextDescriptionSellScreen.text.toString(),
            binding.ediTextQuantitySellScreen.text.toString(),
            uri!!,
            binding.autoCompleteTextViewSellScreen.text.toString(),
            myTags.adVisible,
            uid+"_"+(idealizeUser.adCount.toInt()+1),
            uid,
            0,
            0,
            "0.0"
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
        finish()

    }

    @SuppressLint("SimpleDateFormat")
    fun getDateTime(item : Item) : Item {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = Date()
        val currentFormatted = formatter.format(currentDate)

        val formatter2 = SimpleDateFormat("HH:mm")
        val currentTime = Date()
        val currentFormatted2 = formatter2.format(currentTime)

        item.date=currentFormatted
        item.time=currentFormatted2
        return item
    }

    // Function to generate keywords from ad name
    private fun generateKeywords(input: String): List<String> {
        val keywords = mutableListOf<String>()
        val words = input.toLowerCase(Locale.ROOT).split(" ")

        // Add prefixes for each word
        for (word in words) {
            var prefix = ""
            for (char in word) {
                prefix += char
                keywords.add(prefix)
            }
            // Add the full word as a keyword
            keywords.add(word)
        }

        // Add prefixes for the entire phrase
        var phrasePrefix = ""
        for (char in input.toLowerCase(Locale.ROOT)) {
            if (char != ' ') { // Skip spaces in the phrase prefix
                phrasePrefix += char
                keywords.add(phrasePrefix)
            }
        }

        // Add the full phrase as a keyword
        keywords.add(input.toLowerCase(Locale.ROOT))

        return keywords
    }

    @Suppress("DEPRECATION")
    override fun onStart() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeListener, intentFilter)
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChangeListener)
        super.onStop()
    }
}