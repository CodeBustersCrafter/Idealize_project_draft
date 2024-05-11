package com.codebusters.idealizeprojectdraft

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    ) :
        FragmentStateAdapter(fragmentManager,lifecycle){
            private var userID = ""
    private var userMail = ""
    private var Type = 0
    override fun getItemCount(): Int {
        return 3
    }

    public fun init(userID : String,userMail : String,Type : Int){
        this.userID=userID
        this.userMail=userMail
        this.Type=Type
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
                0 -> {
                    HomeFragment()
                }
                1 -> {
                    SellFragment(userID,userMail,Type)
                }
                else -> {
                    ProfileFragment()
                }
        }
    }

}