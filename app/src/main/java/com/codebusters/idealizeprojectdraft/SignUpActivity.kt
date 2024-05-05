package com.codebusters.idealizeprojectdraft

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivitySignUpBinding

class SignUpActivity : ComponentActivity() {
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("Type", 1)
            startActivity(intent)
            finish()
        })

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,LoginAtivity::class.java)
        startActivity(intent)
        finish()
    }
}
