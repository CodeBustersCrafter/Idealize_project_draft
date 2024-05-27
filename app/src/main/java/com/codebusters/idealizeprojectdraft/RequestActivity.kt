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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codebusters.idealizeprojectdraft.databinding.ActivityRequestBinding
import com.codebusters.idealizeprojectdraft.fragments.MyRequestsFragment
import com.codebusters.idealizeprojectdraft.fragments.RequestsFragment
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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        if(intent.getStringExtra(myTags.intentFragmentRequest).toString()=="1"){
            replaceFragment(RequestsFragment(uid))
        }else{
            replaceFragment(MyRequestsFragment(uid))
        }




        binding.bottomAppBarRequestScreen.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_my_requests->replaceFragment(MyRequestsFragment(uid))
                else->replaceFragment(RequestsFragment(uid))
            }
            true
        }

    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.request_frame_layout,fragment)
        fragmentTransaction.commit()
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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        if(intent.getStringExtra(myTags.intentFragmentRequest).toString()=="1"){
            replaceFragment(RequestsFragment(uid))
            binding.bottomAppBarRequestScreen.selectedItemId = R.id.menu_requests
        }else{
            replaceFragment(MyRequestsFragment(uid))
        }
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChangeListener)
        super.onStop()
    }
}