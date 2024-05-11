package com.codebusters.idealizeprojectdraft

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize

class HomeFragment : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var dataList : ArrayList<Item>
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.Home_Recycler_view)

        firestore= FirebaseFirestore.getInstance()

        dataList = ArrayList()
        val adapter = RecyclerViewAdapter(dataList)

        firestore.collection("Ads").get().addOnSuccessListener{
            result ->
            for(document in result){
                val adData = document.data
                dataList.add(Item(adData["Name"].toString(),
                    adData["Price"].toString(),
                    adData["Location"].toString(),
                    adData["User"].toString(),
                    adData["Rating"].toString(),
                    adData["Phone"].toString(),
                    adData["Date"].toString(),
                    adData["Time"].toString(),
                    adData["Description"].toString(),
                    adData["Quantity"].toString(),
                    Uri.parse(adData["Photo"].toString()),
                    adData["Category"].toString()
                    ))

                adapter.notifyDataSetChanged()
            }
        }
        /*dataList.add(Item("Sample1","1000","Gampaha","Sahan","4.5","765820661",
            "2024-05-10","20:45","ABCD","5", BitmapFactory.decodeResource(resources,R.drawable.bat)))
        dataList.add(Item("Sample2","1200","Gaha","Shan","5","765861",
            "2024-05-10","20:45","ABCD","5", BitmapFactory.decodeResource(resources,R.drawable.ben)))
*/

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        return view
    }
}