package com.codebusters.idealizeprojectdraft.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentRequestPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val user: String
    ) :
        FragmentStateAdapter(fragmentManager,lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
                0 -> {
                    MyRequestsFragment(user)
                }
                else -> {
                    RequestsFragment(user)
                }
        }
    }

}