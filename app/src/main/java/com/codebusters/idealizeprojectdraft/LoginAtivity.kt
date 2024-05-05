package com.codebusters.idealizeprojectdraft

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityLoginAtivityBinding

class LoginAtivity : ComponentActivity() {
    private lateinit var binding : ActivityLoginAtivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("Type",1)
            startActivity(intent)
            finish()
        })
        binding.buttonSignUp.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("Type",0)
        startActivity(intent)
        finish()
    }
    //Sample update v2
}