package com.codebusters.idealizeprojectdraft.fragment_adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codebusters.idealizeprojectdraft.fragments.MyRequestsFragment
import com.codebusters.idealizeprojectdraft.fragments.RequestsFragment

class RequestFragmentPageAdapter(uid : String, fragmentManager: FragmentManager, lifecycle: Lifecycle    ) :  FragmentStateAdapter(fragmentManager,lifecycle){
    private val userID = uid
    override fun getItemCount(): Int {
        return 2
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
                0 -> {
                    MyRequestsFragment(userID)
                }
                else -> {
                    RequestsFragment(userID)
                }
        }
    }

}