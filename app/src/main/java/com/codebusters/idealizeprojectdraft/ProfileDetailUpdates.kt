package com.codebusters.idealizeprojectdraft

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityProfileDetailUpdatesBinding
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileDetailUpdates : AppCompatActivity() {
    private lateinit var binding : ActivityProfileDetailUpdatesBinding
    private val myTags = MyTags()
    private lateinit var dialogImageUri : Uri
    private var isImageUpdated : Boolean = false

    private lateinit var imageUri : Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val progressDialog by lazy { CustomProgressDialog(this) }

    private val requestImageCapture = 1
    private val requestImagePick = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.editTextEditFullName.setText(intent.getStringExtra(myTags.userName).toString())
        binding.editTextEditMobile.setText(intent.getStringExtra(myTags.userPhone).toString())
        binding.editTextEditLocation.setText(intent.getStringExtra(myTags.userLocation).toString())
        imageUri = Uri.parse(intent.getStringExtra(myTags.userPhoto).toString())
        Picasso.get().load(imageUri).into(binding.imageViewEditProfileDp)

        binding.imageViewEditDpIcon.setOnClickListener{
            chooseImageSource()
        }

        binding.buttonSave.setOnClickListener{
            val map = HashMap<String,Any>()
            val name = binding.editTextEditFullName.text.toString()
            val phone = binding.editTextEditMobile.text.toString()
            val location = binding.editTextEditLocation.text.toString()

            if(name.isNotEmpty() and phone.isNotEmpty() and location.isNotEmpty()){
                progressDialog.start("updating the profile...")
                if(isImageUpdated){
                    val imgRef = FirebaseStorage.getInstance().getReference(myTags.users).child(auth.currentUser!!.uid).child("img")
                    val uploadTask=imgRef.putFile(dialogImageUri)
                    uploadTask.addOnSuccessListener  {
                        imgRef.downloadUrl.addOnSuccessListener{
                                uri ->
                            dialogImageUri = uri
                            map[myTags.userName]= name
                            map[myTags.userPhone]= phone
                            map[myTags.userLocation]=  location
                            map[myTags.userPhoto] = dialogImageUri

                            firestore.collection(myTags.users).document(auth.currentUser!!.uid).update(map).addOnCompleteListener{
                                    task ->
                                progressDialog.stop()
                                if(task.isSuccessful){
                                    Toast.makeText(this,"Updated!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this,MainActivity::class.java)
                                    intent.putExtra(myTags.intentType,myTags.userMode)
                                    intent.putExtra(myTags.intentUID,auth.currentUser?.uid)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this,"Not Updated! Try Again", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }else{
                    map[myTags.userName]= name
                    map[myTags.userPhone]= phone
                    map[myTags.userLocation]=  location
                    map[myTags.userPhoto] = imageUri

                    firestore.collection(myTags.users).document(auth.currentUser!!.uid).update(map).addOnCompleteListener{
                            task ->
                        progressDialog.stop()
                        if(task.isSuccessful){
                            Toast.makeText(this,"Updated!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,MainActivity::class.java)
                            intent.putExtra(myTags.intentType,myTags.userMode)
                            intent.putExtra(myTags.intentUID,auth.currentUser?.uid)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,"Not Updated! Try Again", Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            }else {
                Toast.makeText(this,"Add valid information", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @Suppress("DEPRECATION")
    private fun chooseImageSource() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Profile Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        startActivityForResult(takePictureIntent, requestImageCapture)
                    }
                }
                1 -> {
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhotoIntent, requestImagePick)
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestImageCapture -> {
                    @Suppress("DEPRECATION") val imageBitmap = data?.extras?.get("data") as Bitmap
                    dialogImageUri = getImageUriFromBitmap(imageBitmap)
                    Picasso.get().load(dialogImageUri).into(binding.imageViewEditProfileDp)
                    isImageUpdated = true
                }
                requestImagePick -> {
                    dialogImageUri = data?.data!!
                    Picasso.get().load(dialogImageUri).into(binding.imageViewEditProfileDp)
                    isImageUpdated = true
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if(true){
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra(myTags.intentType,myTags.userMode)
            intent.putExtra(myTags.intentUID,auth.currentUser?.uid)
            startActivity(intent)
            finish()
        }else{
            super.onBackPressed()
        }
    }
}