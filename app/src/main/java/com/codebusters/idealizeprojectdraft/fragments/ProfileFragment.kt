package com.codebusters.idealizeprojectdraft.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codebusters.idealizeprojectdraft.ProfileDetailUpdates
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private lateinit var profileImageView: ImageView
    private lateinit var progressBarProfilePic: ProgressBar
    private lateinit var textViewShowFullName: TextView
    private lateinit var textViewShowEmail: TextView
    private lateinit var textViewShowMobile: TextView
    private lateinit var textViewShowLocation: TextView
    private lateinit var buttonEditDetails: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    private lateinit var view : View
    private lateinit var imageUri: Uri

    private val myTags = MyTags()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_user_profile, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        profileImageView = view.findViewById(R.id.imageView_profile_dp)
        progressBarProfilePic = view.findViewById(R.id.progress_bar_profile_pic)
        textViewShowFullName = view.findViewById(R.id.textView_show_full_name)
        textViewShowEmail = view.findViewById(R.id.textView_show_email)
        textViewShowMobile = view.findViewById(R.id.textView_show_mobile)
        textViewShowLocation = view.findViewById(R.id.textView_show_location)
        buttonEditDetails = view.findViewById(R.id.button_edit_details)

        loadUserData()

        buttonEditDetails.setOnClickListener {
            showEditDetailsDialog()
        }

        return view
    }

    private fun loadUserData() {
        firestore.collection(myTags.users).document(auth.currentUser!!.uid).get().addOnSuccessListener { documentSnapshot ->
            textViewShowFullName.text = documentSnapshot.get(myTags.userName).toString()
            textViewShowMobile.text = documentSnapshot.get(myTags.userPhone).toString()
            textViewShowLocation.text = documentSnapshot.get(myTags.userLocation).toString()
            textViewShowEmail.text = documentSnapshot.get(myTags.userEmail).toString()
            imageUri = Uri.parse(documentSnapshot.get(myTags.userPhoto).toString())
            Picasso.get().load(Uri.parse(documentSnapshot.get(myTags.userPhoto).toString())).into(profileImageView)
        }
    }

    private fun showEditDetailsDialog() {
        val i = Intent(context,ProfileDetailUpdates::class.java)
        i.putExtra(myTags.userName,textViewShowFullName.text.toString())
        i.putExtra(myTags.userPhone,textViewShowMobile.text.toString())
        i.putExtra(myTags.userLocation,textViewShowLocation.text.toString())
        i.putExtra(myTags.userPhoto,imageUri.toString())
        startActivity(i)
    }

}
