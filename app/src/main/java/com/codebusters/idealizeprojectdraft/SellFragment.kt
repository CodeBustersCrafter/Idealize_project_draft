package com.codebusters.idealizeprojectdraft

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class SellFragment(userID : String,userMail : String,Type : Int) : Fragment() {
    private var uid=userID
    private var mail=userMail
    private var type=Type
    private lateinit var recyclerView : RecyclerView
    private lateinit var dataList : ArrayList<Item>
    private lateinit var btnAddAdd : FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sell, container, false)
        recyclerView = view.findViewById(R.id.Sell_Recycler_view)
        btnAddAdd = view.findViewById(R.id.btn_add_ad_sell_screen)

        firestore= FirebaseFirestore.getInstance()

        dataList = ArrayList()

        firestore.collection("Users").document(uid).collection("Ads").get().addOnSuccessListener{
                result ->
            for(document in result){
                dataList.add(Item(document.get("Name").toString(),
                    document.get("Price").toString(),
                    document.get("Location").toString(),
                    document.get("User").toString(),
                    document.get("Rating").toString(),
                    document.get("Phone").toString(),
                    document.get("Date").toString(),
                    document.get("Time").toString(),
                    document.get("Description").toString(),
                    document.get("Quantity").toString(),
                    Uri.parse(document.get("Photo").toString()),
                    document.get("Category").toString()
                ))
            }
        }

        val adapter = RecyclerViewAdapter(dataList)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        btnAddAdd.setOnClickListener {
            val intent = Intent(context,AddingAdsActivity::class.java)
            intent.putExtra("Type",type)
            intent.putExtra("Email", mail)
            intent.putExtra("ID",uid)
            startActivity(intent)
        }

        return view
    }
}