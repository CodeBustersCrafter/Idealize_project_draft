package com.codebusters.idealizeprojectdraft

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    private lateinit var recycler_view : RecyclerView
    private lateinit var dataList : ArrayList<Item>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recycler_view = view.findViewById(R.id.Home_Recycler_view)

        dataList = ArrayList<Item>()
        dataList.add(Item("Sample1","1000","Gampaha","Sahan","4.5","765820661",
            "2024-05-10","20:45","ABCD","5", BitmapFactory.decodeResource(resources,R.drawable.bat)))
        dataList.add(Item("Sample2","1200","Gaha","Shan","5","765861",
            "2024-05-10","20:45","ABCD","5", BitmapFactory.decodeResource(resources,R.drawable.ben)))

        val adapter = RecyclerViewAdapter(dataList)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(view.context)

        return view
    }
}