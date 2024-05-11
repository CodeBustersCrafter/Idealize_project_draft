package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityAddingAdsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddingAdsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddingAdsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private var uid = ""
    private var mail = ""
    private var type = 0
    private lateinit var item : Item
    private var uri: Uri? = null
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddingAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth= FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        storage=FirebaseStorage.getInstance()
        uid = intent.getStringExtra("ID").toString()
        mail = intent.getStringExtra("Mail").toString()
        type = intent.getIntExtra("Type",0)


        var currentAddCount = 0
        firestore.collection("Users").document(uid).get().addOnSuccessListener {
                documentSnapshot  ->
                if(documentSnapshot .exists()){
                    currentAddCount = (documentSnapshot.get("Ad_count").toString()).toInt()
                }
        }
        Toast.makeText(baseContext,(currentAddCount+500).toString(),Toast.LENGTH_SHORT).show()




        binding.btnOpenCameraSellScreen.setOnClickListener {
            //open the camera
            imageChooser()

        }
        binding.btnSaveSellScreen.setOnClickListener {
            item = init()
            //validate inputs
            //upload
            Toast.makeText(baseContext, (item.rating),Toast.LENGTH_SHORT).show()
            val imgRef =storage.getReference("Ads").child(uid+"_"+(currentAddCount+1)).child("img")
            val uploadTask=imgRef.putFile(item.photo)
                uploadTask.addOnSuccessListener  {
                    imgRef.downloadUrl.addOnSuccessListener{
                        uri ->
                        val imageUrl = uri.toString()
                        val map=HashMap<String,Any>()
                        map["Name"] = item.name
                        map["Price"] = item.price
                        map["Location"] = item.location
                        map["Date"] = item.date
                        map["Time"] = item.time
                        map["Photo"] = imageUrl
                        map["Phone"] = item.phone
                        map["Description"] = item.description
                        map["User"] = uid
                        map["Ad_ID"] = uid+"_"+(currentAddCount+1)
                        map["Category"] = item.category
                        map["Rating"] = item.rating

                        // Use imageUrl as needed (e.g., save to database)
                        // Note: This URL can be used to display the image
                        firestore.collection("Users").document(uid).collection("Ads").document(uid+"_"+(currentAddCount+1))
                            .set(map).addOnCompleteListener {
                                    task ->
                                if(task.isSuccessful){
                                    Toast.makeText(baseContext,"OK2",Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(baseContext,"NO2",Toast.LENGTH_SHORT).show()
                                }
                            }
                        firestore.collection("Ads").document(uid+"_"+(currentAddCount+1))
                            .set(map).addOnCompleteListener {
                                    task ->
                                if(task.isSuccessful){
                                    Toast.makeText(baseContext,"OK3",Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(baseContext,"NO3",Toast.LENGTH_SHORT).show()
                                }

                            }

                        firestore.collection("Users").document(uid).update("Ad_count",currentAddCount+1)
                            .addOnCompleteListener {
                                    task ->
                                if(task.isSuccessful){
                                    Toast.makeText(baseContext,"OK3",Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(baseContext,"NO3",Toast.LENGTH_SHORT).show()
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
                uri=it
            }
        }
    }

    private fun init(): Item {
        return Item(binding.ediTextNameSellScreen.text.toString(),
            binding.ediTextPriceSellScreen.text.toString(),
            binding.ediTextLocationSellScreen.text.toString(),
            //seller,
            "Sahan",
            //rating,
            "4.5",
            //phone,
            "0765820661",
            //date,
            "2024-05-06",
            //time,
                    "20:45",
            binding.ediTextDescriptionSellScreen.text.toString(),
            binding.ediTextQuantitySellScreen.text.toString(),
            //photo
            uri!!,
            binding.ediTextCategorySellScreen.text.toString()
            )

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(baseContext,MainActivity::class.java)
        intent.putExtra("Type",type)
        intent.putExtra("Email", mail)
        intent.putExtra("ID",uid)
        startActivity(intent)
    }


}