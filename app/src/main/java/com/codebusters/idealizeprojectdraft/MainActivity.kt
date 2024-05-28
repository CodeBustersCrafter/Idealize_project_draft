package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

    private var nonChangingFragment : Boolean = false

    private val networkChangeListener: NetworkChangeListener = NetworkChangeListener()

    @SuppressLint("UseCompatLoadingForDrawables", "MissingInflatedId", "UseSupportActionBar",
        "ResourceType", "SourceLockedOrientationActivity"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()

        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(
                android.Manifest.permission.CALL_PHONE
            ), PackageManager.PERMISSION_GRANTED
        )

        bottomNavigationBar = findViewById(R.id.bottom_app_bar)
        frameLayout = findViewById(R.id.main_frame_layout)

        firestore = FirebaseFirestore.getInstance()

        auth = Firebase.auth

        type = intent.getIntExtra(myTags.intentType, 0)
        uid = intent.getStringExtra(myTags.intentUID).toString()

        if(type==myTags.userMode){
            startStopService()
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
                R.id.menu_explore -> {replaceFragment(HomeFragment(idealizeUser))
                nonChangingFragment =false}
                R.id.menu_my_ads -> {replaceFragment(SellFragment(idealizeUser))
                    nonChangingFragment =false}
                R.id.menu_ai_help -> {replaceFragment(GeminiFragment(idealizeUser.name))
                                        nonChangingFragment = true
                                        }
                else ->{ replaceFragment(ProfileFragment())
                    nonChangingFragment =false}
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
        if(nonChangingFragment){
            replaceFragment(GeminiFragment(idealizeUser.name))
            nonChangingFragment = false
        }else{
            auth.signOut()
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun startStopService(){
        if(!isMyServiceRunning(NotificationService::class.java)){
            Toast.makeText(this,"Started",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,NotificationService::class.java)
            startService(intent)
        }else{
            Toast.makeText(this,"Running...",Toast.LENGTH_SHORT).show()
        }
    }
    private fun isMyServiceRunning(mClass : Class<NotificationService>):Boolean{
        val manager : ActivityManager = getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager

        for(services : ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)){
            if(mClass.name.equals(services.service.className)){
                return true
            }
        }
        return false
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
