@file:Suppress("DEPRECATION")

package com.codebusters.idealizeprojectdraft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityHomeBinding
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize

class HomeActivity : AppCompatActivity() {
    private var myTags = MyTags()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var googleCredential : GoogleSignInClient
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.initialize(this)
        firestore = FirebaseFirestore.getInstance()

        auth = Firebase.auth

        binding.btnUser.setOnClickListener{
            signIn()
        }
        binding.btnGuest.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra(myTags.intentType,myTags.guestMode)
            intent.putExtra(myTags.intentUID,"0")
            startActivity(intent)
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
        }else{
            Toast.makeText(this,"Cancelled Phase 01", Toast.LENGTH_SHORT).show()
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
                        saveUserInFireStore(account)
                    }else{
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Cancelled Phase 02", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveUserInFireStore(account : GoogleSignInAccount){
        firestore.collection(myTags.users).get().addOnSuccessListener {
                documentSnapshot ->
            var isFound = false
            for(document in documentSnapshot){
                if(document.id==auth.uid.toString()){
                    isFound = true
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra(myTags.intentType,myTags.userMode)
                    intent.putExtra(myTags.intentUID,auth.currentUser?.uid)
                    startActivity(intent)
                }
            }
            if (!isFound){
                val idealizeUser = IdealizeUser(account.email.toString(),
                    auth.uid.toString(),
                    "0",
                    Uri.parse(account.photoUrl.toString()),
                    "",
                    "",
                    "0.0",
                    account.displayName.toString()
                )
                saveUser(idealizeUser)
            }
        }
    }

    private fun saveUser(idealizeUser : IdealizeUser){
        val map = HashMap<String,Any>()
        map[myTags.userName]=idealizeUser.name
        map[myTags.userEmail]=idealizeUser.email
        map[myTags.userAdCount] = idealizeUser.adCount
        map[myTags.userLocation] = idealizeUser.location
        map[myTags.userRating]=idealizeUser.rating
        map[myTags.userPhone] = idealizeUser.phone
        map[myTags.userPhoto] = idealizeUser.profile
        map[myTags.userUID] = idealizeUser.uid

        firestore.collection(myTags.users).document(auth.uid.toString()).set(map).addOnCompleteListener{
                result->
            if(result.isSuccessful){
                Toast.makeText(this, "Account is successfully created!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra(myTags.intentType,myTags.userMode)
                intent.putExtra(myTags.intentUID,auth.currentUser?.uid)
                startActivity(intent)

            }
        }

    }
}