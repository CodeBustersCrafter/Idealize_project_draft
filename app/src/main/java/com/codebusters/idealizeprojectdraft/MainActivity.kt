package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.codebusters.idealizeprojectdraft.fragments.FragmentPageAdapter
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.network_services.NetworkChangeListener
import com.codebusters.idealizeprojectdraft.util.ModelBuilder
import com.codebusters.idealizeprojectdraft.util.NotificationService
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

    private lateinit var viewPager : ViewPager2

    private val networkChangeListener: NetworkChangeListener = NetworkChangeListener()

    private var currentPosition : String = "ground"

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
        viewPager = findViewById(R.id.main_view_pager)

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
                            val fragmentAdapter = FragmentPageAdapter(supportFragmentManager,lifecycle,idealizeUser)
                            viewPager.adapter = fragmentAdapter
                            viewPager.registerOnPageChangeCallback(object :
                                ViewPager2.OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    super.onPageSelected(position)
                                    if (position == 0) {// Disable swiping
                                        viewPager.isUserInputEnabled = false
                                    } else {
                                        // Enable swiping
                                        viewPager.isUserInputEnabled = true
                                    }
                                    when(position){
                                        0->{
                                            bottomNavigationBar.selectedItemId = R.id.menu_explore
                                        }
                                        1->{
                                            bottomNavigationBar.selectedItemId = R.id.menu_my_ads
                                        }
                                        2->{
                                            bottomNavigationBar.selectedItemId = R.id.menu_ai_help
                                        }
                                        else->{
                                            bottomNavigationBar.selectedItemId = R.id.menu_profile
                                        }
                                    }
                                }
                                }
                            )
                    viewPager.currentItem = 0

                }
            }
        }else{
            val fragmentAdapter = FragmentPageAdapter(supportFragmentManager,lifecycle,IdealizeUser())
            viewPager.adapter = fragmentAdapter
            viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when(position){
                        0->{
                            bottomNavigationBar.selectedItemId = R.id.menu_explore
                        }
                        1->{
                            bottomNavigationBar.selectedItemId = R.id.menu_my_ads
                        }
                        2->{
                            bottomNavigationBar.selectedItemId = R.id.menu_ai_help
                        }
                        else->{
                            bottomNavigationBar.selectedItemId = R.id.menu_profile
                        }
                    }
                }
            })
            bottomNavigationBar.visibility= View.GONE
            viewPager.currentItem = 0
        }

        bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_explore -> {
                    viewPager.currentItem = 0}
                R.id.menu_my_ads -> {
                    viewPager.currentItem = 1}
                R.id.menu_ai_help -> {
                    viewPager.currentItem = 2}
                else ->{
                    viewPager.currentItem = 3}
            }
            true
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if(type==myTags.userMode){
            if(bottomNavigationBar.selectedItemId == R.id.menu_ai_help && currentPosition!="ground"){

            }else{
                //Add a alert dialogue here
                val builder = AlertDialog.Builder(this)

                // Set the alert dialog title and message
                builder.setTitle("Sign Out")
                builder.setMessage("Are you sure you want to sign out?")

                // Add a positive button to the alert dialog
                builder.setPositiveButton("Yes") { _, _ ->
                    // Perform sign out and navigate to the HomeActivity
                    auth.signOut()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                // Add a negative button to the alert dialog
                builder.setNegativeButton("No") { dialog, _ ->
                    // Dismiss the alert dialog
                    dialog.dismiss()
                }

                // Create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }else{
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startStopService(){
        if(!isMyServiceRunning(NotificationService::class.java)){
            Toast.makeText(this,"Started",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NotificationService::class.java)
            startService(intent)
        }else{
            Toast.makeText(this,"Running...",Toast.LENGTH_SHORT).show()
        }
    }
    @Suppress("DEPRECATION")
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
    public fun changeCurrentPosition(cmd:Int = 1){
        if(cmd==1){
            currentPosition="up"
        }else{
            currentPosition="ground"
        }
    }
}
