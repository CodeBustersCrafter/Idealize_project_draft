package com.codebusters.idealizeprojectdraft

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class UserProfileActivity : AppCompatActivity() {
// Select Location from DropDown
    // Change profile picture inside Dialog box
    //Validate phone
    private lateinit var profileImageView: ImageView
    private lateinit var progressBarProfilePic: ProgressBar
    private lateinit var textViewShowFullName: TextView
    private lateinit var textViewShowEmail: TextView
    private lateinit var textViewShowMobile: TextView
    private lateinit var textViewShowLocation: TextView
    private lateinit var buttonEditDetails: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("profile_images/${auth.currentUser!!.uid}")

        profileImageView = findViewById(R.id.imageView_profile_dp)
        progressBarProfilePic = findViewById(R.id.progress_bar_profile_pic)
        textViewShowFullName = findViewById(R.id.textView_show_full_name)
        textViewShowEmail = findViewById(R.id.textView_show_email)
        textViewShowMobile = findViewById(R.id.textView_show_mobile)
        textViewShowLocation = findViewById(R.id.textView_show_location)
        buttonEditDetails = findViewById(R.id.button_edit_details)

        loadUserData()

        buttonEditDetails.setOnClickListener {
            showEditDetailsDialog()
        }
    }

    private fun loadUserData() {
        FirebaseFirestore.getInstance().collection(MyTags().users).document(auth.currentUser!!.uid).get().addOnSuccessListener { documentSnapshot ->
            textViewShowFullName.text = documentSnapshot.get(MyTags().userName).toString()
            textViewShowMobile.text = documentSnapshot.get(MyTags().userPhone).toString()
            textViewShowLocation.text = documentSnapshot.get(MyTags().userLocation).toString()
            Picasso.get().load(Uri.parse(documentSnapshot.get(MyTags().userPhoto).toString())).into(profileImageView)
        }
    }

    private fun showEditDetailsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_details, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editText_edit_full_name)
        val editTextMobile = dialogView.findViewById<EditText>(R.id.editText_edit_mobile)
        val editTextLocation = dialogView.findViewById<EditText>(R.id.editText_edit_location)
        val imageViewProfilePic = dialogView.findViewById<ImageView>(R.id.imageView_edit_profile_dp)
        val saveButton = dialogView.findViewById<Button>(R.id.button_save)

        editTextName.setText(textViewShowFullName.text)
        editTextMobile.setText(textViewShowMobile.text)
        editTextLocation.setText(textViewShowLocation.text)
        imageViewProfilePic.setImageURI(imageUri)
        saveButton.setOnClickListener{
            val map = HashMap<String,Any>()
            map[MyTags().userName]=editTextName.text.toString()
            map[MyTags().userPhone]=editTextName.text.toString()
            map[MyTags().userLocation]=editTextName.text.toString()
            //FirebaseFirestore.getInstance().collection(MyTags().users).document(auth.currentUser!!.uid).update()
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Details")
        builder.setView(dialogView)
        builder.setPositiveButton("Save") { _, _ ->
            val newName = editTextName.text.toString().trim()
            val newMobile = editTextMobile.text.toString().trim()
            val newLocation = editTextLocation.text.toString().trim()

            if (newName.isNotEmpty()) {
                databaseReference.collection(MyTags().users).document(auth.currentUser!!.uid).update(MyTags().userName, newName)
            }
            if (newMobile.isNotEmpty()) {
                databaseReference.collection(MyTags().users).document(auth.currentUser!!.uid).update(MyTags().userPhone, newMobile)
            }
            if (newLocation.isNotEmpty()) {
                databaseReference.collection(MyTags().users).document(auth.currentUser!!.uid).update(MyTags().userLocation, newLocation)
            }
            if (imageUri != null) {
                uploadProfilePicture(imageUri!!)
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.setNeutralButton("Change Photo") { _, _ ->
            chooseImageSource()
        }
        builder.create().show()
    }

    private fun chooseImageSource() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Profile Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
                1 -> {
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageUri = getImageUriFromBitmap(imageBitmap)
                    profileImageView.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                    profileImageView.setImageURI(imageUri)
                }
            }
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun uploadProfilePicture(uri: Uri) {
        progressBarProfilePic.visibility = ProgressBar.VISIBLE
        val uploadTask = storageReference.putFile(uri)
        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { downloadUrl ->
                databaseReference.collection(MyTags().users).document(auth.currentUser!!.uid).update(MyTags().userPhoto, downloadUrl.toString())
                progressBarProfilePic.visibility = ProgressBar.GONE
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            progressBarProfilePic.visibility = ProgressBar.GONE
            Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
        }
    }
}
