package com.codebusters.idealizeprojectdraft

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codebusters.idealizeprojectdraft.databinding.ActivityRequestBinding
import com.codebusters.idealizeprojectdraft.fragments.MyRequestsFragment
import com.codebusters.idealizeprojectdraft.fragments.RequestsFragment
import com.codebusters.idealizeprojectdraft.models.MyTags

class RequestActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRequestBinding
    private var myTags = MyTags()
    private var uid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid=intent.getStringExtra(myTags.intentUID).toString()

        replaceFragment(MyRequestsFragment(uid))

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

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,MainActivity::class.java)
        i.putExtra(myTags.intentUID,uid)
        i.putExtra(myTags.intentType,myTags.userMode)
        startActivity(i)
    }
}