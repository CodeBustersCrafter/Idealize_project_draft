package com.codebusters.idealizeprojectdraft.fragment_adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.fragments.HomeFragment
import com.codebusters.idealizeprojectdraft.fragments.ProfileFragment
import com.codebusters.idealizeprojectdraft.fragments.SellFragment
import com.codebusters.idealizeprojectdraft.models.IdealizeUser

class FragmentPageAdapter(  user : IdealizeUser, t : Int,  fragmentManager: FragmentManager,    lifecycle: Lifecycle    ) :  FragmentStateAdapter(fragmentManager,lifecycle){

    private var idealizeUser = user
    private var type = t
    private var myTags = MyTags()

    override fun getItemCount(): Int {
        if(type== myTags.guestMode){
            return 1
        }
        return 3
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
                0 -> {
                    HomeFragment(idealizeUser)
                }
                1 -> {
                    SellFragment(idealizeUser)
                }
                else -> {
                    ProfileFragment(idealizeUser)
                }
        }
    }

}