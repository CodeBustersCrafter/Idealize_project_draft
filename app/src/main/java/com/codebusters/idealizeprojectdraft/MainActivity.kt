@file:Suppress("DEPRECATION")

package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.codebusters.idealizeprojectdraft.databinding.MainActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var binding : MainActivityBinding
    private var tYPE = 0
    private var id = "0"
    private var email = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var googleCredential : GoogleSignInClient
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()


        if(intent.hasExtra("Type")){
            tYPE = intent.getIntExtra("Type",0)
            id = intent.getStringExtra("ID").toString()
            email = intent.getStringExtra("Email").toString()
        }

        if(tYPE==0){
            //Normal
            //only login
            binding.imageViewUserLogin.setImageDrawable(getDrawable(R.drawable.login))
            Toast.makeText(this, id,Toast.LENGTH_SHORT).show()
        }else{
            //Profile
            //only logout
            binding.imageViewUserLogin.setImageDrawable(getDrawable(R.drawable.logout))
            Toast.makeText(this, id,Toast.LENGTH_SHORT).show()

        }

        binding.imageViewUserLogin.setOnClickListener {
            if (id != "0") {
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("Type", 0)
                intent.putExtra("Email", "")
                intent.putExtra("ID", "0")
                startActivity(intent)
            } else {
                signIn()
            }


            //Code for adding city tags
            /*val db = Firebase.firestore
            val input_stream = baseContext.resources.openRawResource(R.raw.city_names_sorted)
            val buffer = input_stream.bufferedReader()
            var line = buffer.readLine()
            val arr : ArrayList<String> = ArrayList<String>()
            while (line!=null){
                arr.add(line)
                line = buffer.readLine()
            }

            val map : HashMap<String, Any> = HashMap<String, Any>()
            map.put("Cities",arr)
            db.collection("App Data").document("Tags").update(map)
            */

        }
    }

    private fun signIn(){
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail().build()

        googleCredential = GoogleSignIn.getClient(this,gso)

        val signInIntent = googleCredential.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode== RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task : Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if(account!=null){
                //Success
                val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this,MainActivity::class.java)
                        intent.putExtra("Type",1)
                        intent.putExtra("Email", auth.currentUser?.email)
                        intent.putExtra("ID",auth.currentUser?.uid)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()

        }
    }
}
