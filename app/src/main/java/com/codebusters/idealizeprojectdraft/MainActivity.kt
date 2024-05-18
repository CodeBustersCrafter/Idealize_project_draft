@file:Suppress("DEPRECATION")

package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.codebusters.idealizeprojectdraft.fragment_adapters.FragmentPageAdapter
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize


class MainActivity : AppCompatActivity() {

    private var myTags = MyTags()
    private var type = 0
    private var uid = "0"

    private lateinit var idealizeUser : IdealizeUser

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var googleCredential : GoogleSignInClient

    private lateinit var tabLayout : TabLayout
    private lateinit var viewpager : ViewPager2

    private lateinit var toolbar : Toolbar

    @SuppressLint("UseCompatLoadingForDrawables", "MissingInflatedId", "UseSupportActionBar",
        "ResourceType"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_IdealizeProjectDraft)
        setContentView(R.layout.main_activity)

        toolbar = findViewById(R.id.App_Bar_Main)
        supportActionBar?.hide()
        toolbar.inflateMenu(R.menu.main_tool_bar_menues)
        setActionBar(toolbar)

        tabLayout = findViewById(R.id.Tab_layout_home_Screen)
        viewpager = findViewById(R.id.view_pager_home_screen)

        Firebase.initialize(this)
        firestore =FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()


        if(intent.hasExtra(myTags.intentType)){
            type = intent.getIntExtra(myTags.intentType,0)
            uid = intent.getStringExtra(myTags.intentUID).toString()
        }else {
            uid = FirebaseAuth.getInstance().currentUser?.uid ?: "0"
            type = if(uid == "0"){
                myTags.guestMode
            }else{
                myTags.userMode
            }
        }

        tabLayout.addTab(tabLayout.newTab().setText("Home"))
        if(type==myTags.userMode){
            firestore =FirebaseFirestore.getInstance()
            firestore.collection(myTags.users).document(uid).get().addOnSuccessListener {
                    documentSnapshot ->
                if(documentSnapshot.exists()){
                    idealizeUser = ModelBuilder().getUser(documentSnapshot)
                    tabLayout.addTab(tabLayout.newTab().setText("Sell"))
                    tabLayout.addTab(tabLayout.newTab().setText("Profile"))
                    tabLayout.addTab(tabLayout.newTab().setText("Help"))
                    viewpager.adapter = FragmentPageAdapter(idealizeUser,type,supportFragmentManager,lifecycle)
                }
            }
        }else{
            viewpager.adapter = FragmentPageAdapter(IdealizeUser(),type,supportFragmentManager,lifecycle)
        }


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewpager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        viewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }

        })
    }

    private fun signIn(){
        auth = FirebaseAuth.getInstance()

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
            Toast.makeText(this,"Cancelled Phase 01",Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Cancelled Phase 02",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()

        }
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_tool_bar_menues,menu)
        if(type==myTags.guestMode){
            //Normal
            //only login
            menu?.getItem(0)?.setIcon(getDrawable(R.drawable.login))
            Toast.makeText(this, uid,Toast.LENGTH_SHORT).show()
        }else{
            //Profile
            //only logout
            menu?.getItem(0)?.setIcon(getDrawable(R.drawable.logout))
            Toast.makeText(this, uid,Toast.LENGTH_SHORT).show()


        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.login_menu){
            if (type == myTags.userMode) {
                Toast.makeText(this,"Cancelled Phase 03",Toast.LENGTH_SHORT).show()
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(myTags.intentType, myTags.guestMode)
                intent.putExtra(myTags.intentUID, "0")
                startActivity(intent)
            } else {
                signIn()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveUserInFireStore(account : GoogleSignInAccount){
        val docRef = firestore.collection(myTags.users).document(auth.uid.toString())
        docRef.get().addOnSuccessListener {
            documentSnapshot ->

                if(!documentSnapshot.exists()){
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
                }else{
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("Type",myTags.userMode)
                    intent.putExtra("ID",auth.currentUser?.uid)
                    startActivity(intent)
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
                Toast.makeText(this, "Account is successfully created!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("Type",myTags.userMode)
                intent.putExtra("ID",auth.currentUser?.uid)
                startActivity(intent)

            }
        }

    }




}
