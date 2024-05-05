package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.codebusters.idealizeprojectdraft.databinding.MainActivityBinding

class MainActivity : ComponentActivity() {
    private lateinit var binding : MainActivityBinding
    private var TYPE = 0
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("Type")){
            TYPE = intent.getIntExtra("Type",0)
        }

        if(TYPE==0){
            //Normal
            //only login
            binding.imageViewUserLogin.setImageDrawable(getDrawable(R.drawable.login))
            Toast.makeText(this,"Normal",Toast.LENGTH_SHORT).show()
        }else{
            //Profile
            //only logout
            binding.imageViewUserLogin.setImageDrawable(getDrawable(R.drawable.logout))
            Toast.makeText(this,"Extra tabs",Toast.LENGTH_SHORT).show()
        }

        binding.imageViewUserLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LoginAtivity::class.java)
            startActivity(intent)
        })
    }
}