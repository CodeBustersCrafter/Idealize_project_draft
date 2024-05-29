package com.codebusters.idealizeprojectdraft

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.codebusters.idealizeprojectdraft.util.NotificationService
import com.codebusters.idealizeprojectdraft.databinding.ActivityRequestBinding
import com.codebusters.idealizeprojectdraft.fragments.FragmentRequestPageAdapter
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.network_services.NetworkChangeListener

class RequestActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRequestBinding
    private var myTags = MyTags()
    private var uid = ""
    private val networkChangeListener: NetworkChangeListener = NetworkChangeListener()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        uid=intent.getStringExtra(myTags.intentUID).toString()

        val fragmentAdapter = FragmentRequestPageAdapter(supportFragmentManager,lifecycle,uid)
        binding.requestsViewPager.adapter = fragmentAdapter
        binding.requestsViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{
                        binding.bottomAppBarRequestScreen.selectedItemId = R.id.menu_my_requests
                    }
                    else->{
                        binding.bottomAppBarRequestScreen.selectedItemId = R.id.menu_requests
                    }
                }
            }

        })

        if(intent.getStringExtra(myTags.intentFragmentRequest).toString()=="1"){
            binding.bottomAppBarRequestScreen.selectedItemId = R.id.menu_requests
        }else{
            binding.bottomAppBarRequestScreen.selectedItemId = R.id.menu_my_requests
        }

        binding.bottomAppBarRequestScreen.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_my_requests->binding.requestsViewPager.currentItem = 0
                else->binding.requestsViewPager.currentItem = 1
            }
            true
        }

    }

    @Suppress("DEPRECATION")
    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,MainActivity::class.java)
        i.putExtra(myTags.intentUID,uid)
        i.putExtra(myTags.intentType,myTags.userMode)
        startActivity(i)
    }

    @Suppress("DEPRECATION")
    override fun onStart() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeListener, intentFilter)
        uid=intent.getStringExtra(myTags.intentUID).toString()

        if(intent.getStringExtra(myTags.intentFragmentRequest).toString()=="1"){
            binding.requestsViewPager.currentItem = 1
            binding.bottomAppBarRequestScreen.selectedItemId = R.id.menu_requests
            stopService(Intent(this, NotificationService::class.java))
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
            val intent = Intent(this, NotificationService::class.java)
            startService(intent)
        }else{
            binding.requestsViewPager.currentItem = 0
        }
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChangeListener)
        super.onStop()
    }
}