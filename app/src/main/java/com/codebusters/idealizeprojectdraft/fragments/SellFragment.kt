package com.codebusters.idealizeprojectdraft.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.recycle_view_adapter.RecyclerViewAdapter
import com.codebusters.idealizeprojectdraft.util.ModelBuilder
import com.google.firebase.firestore.FirebaseFirestore

class SellFragment(u : IdealizeUser) : Fragment() {
    private val user=u
    private lateinit var recyclerView : RecyclerView
    private lateinit var dataList : ArrayList<ItemModel>
    private lateinit var firestore: FirebaseFirestore
    private var myTags = MyTags()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sell, container, false)
        recyclerView = view.findViewById(R.id.Sell_Recycler_view)

        firestore= FirebaseFirestore.getInstance()

        dataList = ArrayList()

        firestore.collection(myTags.users).document(user.uid).collection(myTags.ads).get().addOnSuccessListener{
                result ->
            for(document in result){
                dataList.add(ModelBuilder().getAdItem(document,user))
            }
            val adapter = RecyclerViewAdapter(dataList,1,view.context, user.uid)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            recyclerView.layoutManager = GridLayoutManager(view.context,2)
        }

        return view
    }
}