package com.codebusters.idealizeprojectdraft.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codebusters.idealizeprojectdraft.gemini_support.GeminiFragment
import com.codebusters.idealizeprojectdraft.models.IdealizeUser

class FragmentPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val user: IdealizeUser
    ) :
        FragmentStateAdapter(fragmentManager,lifecycle){
    override fun getItemCount(): Int {
        if(user.uid == ""){
            return 1
        }
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
                0 -> {
                    HomeFragment(user)
                }
                1 -> {
                    SellFragment(user)
                }
                2 -> {
                    GeminiFragment(user.name)
                }
                else -> {
                    ProfileFragment()
                }
        }
    }

}