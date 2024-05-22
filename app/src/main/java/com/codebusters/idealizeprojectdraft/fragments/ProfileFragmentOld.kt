package com.codebusters.idealizeprojectdraft.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.squareup.picasso.Picasso

class ProfileFragmentOld(idealizeUser: IdealizeUser) : Fragment() {
    private val user = idealizeUser

    private lateinit var view : View
    private lateinit var textName : TextView
    private lateinit var textPhone : TextView
    private lateinit var textLocation : TextView
    private lateinit var textRating : TextView
    private lateinit var textEmail : TextView
    private lateinit var imageView : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false)
        textName = view.findViewById(R.id.text_name_profile_screen)
        textPhone = view.findViewById(R.id.text_phone_profile_screen)
        textLocation = view.findViewById(R.id.text_location_profile_screen)
        textRating = view.findViewById(R.id.text_rating_profile_screen)
        textEmail = view.findViewById(R.id.text_email_profile_screen)
        imageView = view.findViewById(R.id.image_view_profile)

        init()

        return view
    }

    private fun init(){
        textEmail.text = user.email
        textRating.text = user.rating
        textName.text = user.name
        textPhone.text = user.phone
        textLocation.text = user.location
        Picasso.get().load(user.profile).into(imageView)
    }
}