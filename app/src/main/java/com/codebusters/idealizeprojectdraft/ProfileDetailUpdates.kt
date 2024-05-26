package com.codebusters.idealizeprojectdraft

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityProfileDetailUpdatesBinding
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileDetailUpdates : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailUpdatesBinding
    private val myTags = MyTags()
    private var dialogImageUri: Uri? = null
    private var isImageUpdated: Boolean = false

    private lateinit var imageUri: Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickPhotoLauncher: ActivityResultLauncher<Intent>
    private val progressDialog by lazy { CustomProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.editTextEditFullName.setText(intent.getStringExtra(myTags.userName))
        binding.editTextEditMobile.setText(intent.getStringExtra(myTags.userPhone))
        binding.editTextEditLocation.setText(intent.getStringExtra(myTags.userLocation))
        imageUri = Uri.parse(intent.getStringExtra(myTags.userPhoto))
        Picasso.get().load(imageUri).into(binding.imageViewEditProfileDp)
        val autoCompleteTextView: AutoCompleteTextView = binding.editTextEditLocation
        autoCompleteTextView.threshold = 0

        binding.imageViewEditDpIcon.setOnClickListener {
            chooseImageSource()
        }

        binding.buttonSave.setOnClickListener {
            saveProfile()
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                dialogImageUri = getImageUriFromBitmap(imageBitmap)
                Picasso.get().load(dialogImageUri).into(binding.imageViewEditProfileDp)
                isImageUpdated = true
            }
        }

        pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                dialogImageUri = result.data?.data
                Picasso.get().load(dialogImageUri).into(binding.imageViewEditProfileDp)
                isImageUpdated = true
            }
        }

        fetchCities { firebaseArray ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, firebaseArray)
            autoCompleteTextView.setAdapter(adapter)
            autoCompleteTextView.setOnClickListener {
                autoCompleteTextView.showDropDown()
            }
        }
    }

    private fun fetchCities(callback: (List<String>) -> Unit) {
        firestore.collection(myTags.appData).document(myTags.tags).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firebaseArray = document.get(myTags.cities) as? List<String> ?: emptyList()
                    callback(firebaseArray)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load cities: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfile() {
        progressDialog.start("Updating Profile...")
        val map = HashMap<String, Any>()
        val name = binding.editTextEditFullName.text.toString()
        val phone = binding.editTextEditMobile.text.toString()
        val location = binding.editTextEditLocation.text.toString()

        if (name.isNotEmpty() && phone.isNotEmpty() && location.isNotEmpty()) {
            if (isPhoneNumberValid(phone)) {
                fetchCities { firebaseArray ->
                    if (validateCity(location, firebaseArray)) {
                        if (isImageUpdated && dialogImageUri != null) {
                            val imgRef = FirebaseStorage.getInstance().getReference(myTags.users).child(auth.currentUser!!.uid).child("img")
                            val uploadTask = imgRef.putFile(dialogImageUri!!)
                            uploadTask.addOnSuccessListener {
                                imgRef.downloadUrl.addOnSuccessListener { uri ->
                                    dialogImageUri = uri
                                    map[myTags.userName] = name
                                    map[myTags.userPhone] = phone
                                    map[myTags.userLocation] = location
                                    map[myTags.userPhoto] = dialogImageUri.toString()
                                    updateAdsLocation(location) { success ->
                                        if (success) {
                                            updateFirestore(map)
                                        } else {
                                            Toast.makeText(this, "Failed to update ads location", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            map[myTags.userName] = name
                            map[myTags.userPhone] = phone
                            map[myTags.userLocation] = location
                            map[myTags.userPhoto] = imageUri.toString()
                            updateAdsLocation(location) { success ->
                                if (success) {
                                    updateFirestore(map)
                                } else {
                                    Toast.makeText(this, "Failed to update ads location", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Invalid city. Please select a city from the list.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Add valid information", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAdsLocation(location: String, callback: (Boolean) -> Unit) {
        val query: Query = firestore.collection(myTags.ads).whereEqualTo(myTags.adUser, auth.currentUser!!.uid)
        query.get().addOnSuccessListener { result ->
            for (document in result.documents) {
                firestore.collection(myTags.ads).document(document.id).update(myTags.adLocation, location)
                    .addOnFailureListener {
                        callback(false)
                        return@addOnFailureListener
                    }
            }
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    private fun updateFirestore(map: HashMap<String, Any>) {
        firestore.collection(myTags.users).document(auth.currentUser!!.uid).update(map).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
                progressDialog.stop()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(myTags.intentType, myTags.userMode)
                intent.putExtra(myTags.intentUID, auth.currentUser?.uid)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Not Updated! Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chooseImageSource() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Profile Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePictureLauncher.launch(takePictureIntent)
                }
                1 -> {
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    pickPhotoLauncher.launch(pickPhotoIntent)
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        val regex = "^0[0-9]{9}$"
        return phone.matches(regex.toRegex())
    }

    private fun validateCity(city: String, firebaseArray: List<String>): Boolean {
        val isValid = firebaseArray.contains(city)
        if (!isValid) {
            Toast.makeText(this, "Invalid city. Please select a city from the list.", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(myTags.intentType, myTags.userMode)
        intent.putExtra(myTags.intentUID, auth.currentUser?.uid)
        startActivity(intent)
        finish()
    }
}
