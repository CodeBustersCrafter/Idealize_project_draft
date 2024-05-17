package com.codebusters.idealizeprojectdraft

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.codebusters.idealizeprojectdraft.fragment_adapters.RequestFragmentPageAdapter
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.android.material.tabs.TabLayout

class RequestActivity : AppCompatActivity() {
    private lateinit var tabLayout : TabLayout
    private lateinit var viewpager : ViewPager2
    private var myTags = MyTags()
    private var uid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_request)

        uid=intent.getStringExtra(myTags.intentUID).toString()

        tabLayout = findViewById(R.id.Tab_layout_request_Screen)
        viewpager = findViewById(R.id.view_pager_request_screen)

        tabLayout.addTab(tabLayout.newTab().setText("My Requests"))
        tabLayout.addTab(tabLayout.newTab().setText("Requests"))

        viewpager.adapter=RequestFragmentPageAdapter(uid,supportFragmentManager,lifecycle)

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

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,MainActivity::class.java)
        i.putExtra(myTags.intentUID,uid)
        i.putExtra(myTags.intentType,myTags.userMode)
        startActivity(i)
    }
}