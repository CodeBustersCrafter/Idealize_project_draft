@file:Suppress("DEPRECATION")

package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityHomeBinding
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.network_services.NetworkChangeListener
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
    private val networkChangeListener: NetworkChangeListener = NetworkChangeListener()

    private val progressDialog by lazy { CustomProgressDialog(this) }

    @SuppressLint("SourceLockedOrientationActivity", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
            progressDialog.start("Loading...")

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
                    progressDialog.stop()
                }
            }
            if (!isFound){
                val d = Dialog(this)
                d.setContentView(R.layout.sign_in_form)
                d.setCancelable(false)
                val location = d.findViewById<AutoCompleteTextView>(R.id.editText_edit_location)
                val phone = d.findViewById<EditText>(R.id.editText_edit_mobile)
                val button = d.findViewById<Button>(R.id.button_save)
                button.setOnClickListener{
                    val phoneNumber = phone.text.toString().trim()
                    val city = location.text.toString().trim()
                    fetchCities { firebaseArray ->
                        if (validatePhoneNumber(phoneNumber) && validateCity(city, firebaseArray)) {
                            val idealizeUser = IdealizeUser(
                                account.email.toString(),
                                auth.uid.toString(),
                                "0",
                                Uri.parse(account.photoUrl.toString()),
                                city,
                                phoneNumber,
                                "0.0",
                                account.displayName.toString(),
                                0
                            )
                            saveUser(idealizeUser)
                        }
                    }
                }
                fetchCities { firebaseArray ->
                    val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, firebaseArray)
                    location.setAdapter(adapter)
                    location.threshold = 0
                    location.setOnClickListener {
                        location.showDropDown()
                    }
                }
                d.create()
                d.show()
            }
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        val isValid = phoneNumber.length == 10 && phoneNumber.startsWith("0")
        if (!isValid) {
            Toast.makeText(this, "Invalid phone number. It should be 10 digits and start with 0.", Toast.LENGTH_SHORT).show()
        }
        return isValid
    }

    private fun validateCity(city: String, firebaseArray: List<String>): Boolean {
        val isValid = firebaseArray.contains(city)
        if (!isValid) {
            Toast.makeText(this, "Invalid city. Please select a city from the list.", Toast.LENGTH_SHORT).show()
        }
        return isValid
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
        map[myTags.userRateCount] = idealizeUser.rateCount

        firestore.collection(myTags.users).document(auth.uid.toString()).set(map).addOnCompleteListener{
                result->
            if(result.isSuccessful){
                Toast.makeText(this, "Account is successfully created!", Toast.LENGTH_SHORT).show()
                progressDialog.stop()
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra(myTags.intentType,myTags.userMode)
                intent.putExtra(myTags.intentUID,auth.currentUser?.uid)
                startActivity(intent)

            }
        }

    }


    private fun fetchCities(callback: (List<String>) -> Unit) {
        firestore.collection(myTags.appData).document(myTags.tags).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firebaseArray = document.get(myTags.cities) as? List<String> ?: emptyList()
                    callback(firebaseArray)
                } else {
                    callback(emptyList())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load cities: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(emptyList())
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