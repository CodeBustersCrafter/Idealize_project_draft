package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    public lateinit var binding : ActivitySplashScreenBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContent { 
            MainScreen()
        }*/
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val path = "android.resource://"+packageName+"/"+R.raw.updatedlogo
        binding.videoViewSplash.setVideoURI(Uri.parse(path))
        binding.videoViewSplash.start()

        binding.videoViewSplash.setOnCompletionListener {
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }
}