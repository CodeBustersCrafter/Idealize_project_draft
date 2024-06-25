package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivitySplashScreenBinding
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.network_services.NetworkChangeListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.initialize

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private val myTags = MyTags()
    private val networkChangeListener: NetworkChangeListener = NetworkChangeListener()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val path = "android.resource://"+packageName+"/"+R.raw.final_logo2
        binding.videoViewSplash.setVideoURI(Uri.parse(path))
        binding.videoViewSplash.start()


        binding.videoViewSplash.setOnCompletionListener {
            Firebase.initialize(this)
            if(FirebaseAuth.getInstance().currentUser!=null){
                val i = Intent(this,MainActivity::class.java)
                i.putExtra(myTags.intentUID,FirebaseAuth.getInstance().currentUser?.uid.toString())
                i.putExtra(myTags.intentType,myTags.userMode)
                startActivity(i)
                finish()
            }   else{
                val i = Intent(this,HomeActivity::class.java)
                startActivity(i)
                finish()
            }
        }

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