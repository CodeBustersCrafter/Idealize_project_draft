package com.codebusters.idealizeprojectdraft.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebusters.idealizeprojectdraft.ModelBuilder
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.RequestActivity
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.recycle_view_adapter.RecyclerViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment(idealizeUser: IdealizeUser) : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var requestsButton : FloatingActionButton
    private lateinit var refreshButton : SwipeRefreshLayout
    private lateinit var dataList : ArrayList<ItemModel>
    private lateinit var firestore: FirebaseFirestore
    private val user = idealizeUser
    private var myTags = MyTags()
    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.Home_Recycler_view)
        requestsButton = view.findViewById(R.id.Home_request_action_btn)
        refreshButton = view.findViewById(R.id.Home_swiper_button)

        firestore= FirebaseFirestore.getInstance()

        val type : Int = if(user.uid == ""){//Guest Mode
            myTags.guestMode
        }else{//User Mode
            myTags.userViewMode
        }

        initData(type,view)

        refreshButton.setOnRefreshListener {
            initData(type,view)
            refreshButton.isRefreshing = false
        }

        //listenForUpdates()
        //listenForDeletions()

        if(user.uid == ""){
            requestsButton.setOnClickListener{
                Toast.makeText(context,"You need to sign in to your account before see your bookings",Toast.LENGTH_SHORT).show()
            }
        }else{
            requestsButton.setOnClickListener{
                val i = Intent(context,RequestActivity::class.java)
                i.putExtra(myTags.intentUID,user.uid)
                startActivity(i)
            }
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData(type: Int, view : View){

        dataList = ArrayList()
        val adapter = RecyclerViewAdapter(dataList, type,view.context, user.uid)

        firestore.collection(myTags.ads).get().addOnSuccessListener{
                result ->
            for(document in result){
                firestore.collection(myTags.users).document(document.get(myTags.adUser).toString()).get().addOnSuccessListener{
                        documentSnapshot->
                    dataList.add(ModelBuilder().getAdItem(document,documentSnapshot))
                    adapter.notifyDataSetChanged()
                }

            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
    }
    /*@SuppressLint("NotifyDataSetChanged")
    fun listenForUpdates(){
        firestore.collection(myTags.ads).addSnapshotListener { querySnapshot, _ ->
            if (querySnapshot != null) {
                // Process the updated data (e.g., convert to a list)
                for(document in querySnapshot){
                    firestore.collection(myTags.users).document(document.get(myTags.adUser).toString()).get().addOnSuccessListener{
                            documentSnapshot->

                            dataList.add(ModelBuilder().getAdItem(document,documentSnapshot))
                        recyclerView.adapter?.notifyDataSetChanged()

                    }
                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun listenForDeletions() {
        firestore.collection(myTags.ads).addSnapshotListener { querySnapshot: QuerySnapshot?, _ ->
            querySnapshot?.documentChanges?.forEach { change ->
                if (change.type == DocumentChange.Type.REMOVED) {
                    // Handle the deleted document
                    val deletedDocumentId = change.document.id
                    for (itemModel in dataList) {
                        if(itemModel.adId==deletedDocumentId) {
                            dataList.remove(itemModel)
                            break
                        }
                    }
                    recyclerView.adapter?.notifyDataSetChanged()

                }
            }
        }
    }*/
}

