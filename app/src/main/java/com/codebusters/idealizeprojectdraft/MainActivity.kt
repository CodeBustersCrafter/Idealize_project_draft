package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codebusters.idealizeprojectdraft.fragments.HomeFragment
import com.codebusters.idealizeprojectdraft.fragments.ProfileFragment
import com.codebusters.idealizeprojectdraft.fragments.SellFragment
import com.codebusters.idealizeprojectdraft.gemini_support.GeminiFragment
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.network_services.NetworkChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private var myTags = MyTags()
    private var type = 0
    private var uid = "0"

    private lateinit var idealizeUser : IdealizeUser

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore

    private lateinit var bottomNavigationBar : BottomNavigationView
    private lateinit var frameLayout : FrameLayout

    private val networkChangeListener: NetworkChangeListener = NetworkChangeListener()

    @SuppressLint("UseCompatLoadingForDrawables", "MissingInflatedId", "UseSupportActionBar",
        "ResourceType", "SourceLockedOrientationActivity"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()

        bottomNavigationBar = findViewById(R.id.bottom_app_bar)
        frameLayout = findViewById(R.id.main_frame_layout)

        firestore = FirebaseFirestore.getInstance()

        auth = Firebase.auth

        type = intent.getIntExtra(myTags.intentType, 0)
        uid = intent.getStringExtra(myTags.intentUID).toString()

        if(type==myTags.userMode){
            firestore =FirebaseFirestore.getInstance()
            firestore.collection(myTags.users).document(uid).get().addOnSuccessListener {
                    documentSnapshot ->
                if(documentSnapshot.exists()){
                    idealizeUser = ModelBuilder().getUser(documentSnapshot)
                    bottomNavigationBar.visibility= View.VISIBLE
                    replaceFragment(HomeFragment(idealizeUser))
                }
            }
        }else{
            bottomNavigationBar.visibility= View.GONE
            replaceFragment(HomeFragment(IdealizeUser()))
        }

        bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_explore -> replaceFragment(HomeFragment(idealizeUser))
                R.id.menu_my_ads -> replaceFragment(SellFragment(idealizeUser))
                R.id.menu_ai_help -> replaceFragment(GeminiFragment())
                else -> replaceFragment(ProfileFragment())
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame_layout,fragment)
        fragmentTransaction.commit()
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        auth.signOut()
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
        finish()
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
