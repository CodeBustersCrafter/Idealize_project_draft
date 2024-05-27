package com.codebusters.idealizeprojectdraft.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebusters.idealizeprojectdraft.ModelBuilder
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.ItemRequestModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.recycle_view_adapter.RecyclerViewRequestAdapter
import com.google.firebase.firestore.FirebaseFirestore

class RequestsFragment(uid : String) : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var dataList : ArrayList<ItemRequestModel>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var refreshButton : SwipeRefreshLayout
    private val userID = uid
    private var myTags = MyTags()
    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_requsts, container, false)
        recyclerView = view.findViewById(R.id.Requests_Recycler_view)
        refreshButton = view.findViewById(R.id.Requests_swiper_button)

        firestore= FirebaseFirestore.getInstance()


        initData(view)

        refreshButton.setOnRefreshListener {
            initData(view)
            refreshButton.isRefreshing = false
        }
        resetNewRequests()

        //listenForUpdates()
        //listenForDeletions()

        return view
    }
    private fun resetNewRequests(){

        val map2 = HashMap<String,Any>()
        map2[myTags.areNewRequests] = 0
        firestore.collection(myTags.users).document(userID).update(map2)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun initData(view : View){
        dataList = ArrayList()
        val adapter = RecyclerViewRequestAdapter(dataList, myTags.requestMode,view.context)

        firestore.collection(myTags.users).document(userID).collection(myTags.userRequests).get().addOnSuccessListener { querySnapshot->
            // Process the updated data (e.g., convert to a list)
            for(document in querySnapshot){
                firestore.collection(myTags.ads).document(document.get(myTags.requestAdID).toString()).get().addOnSuccessListener{
                        documentSnapshot->
                    firestore.collection(myTags.users).document(document.get(myTags.requestBuyerID).toString()).get().addOnSuccessListener{
                            documentSnapshotUser->
                        dataList.add(ModelBuilder().getRequestItem(document,documentSnapshot,documentSnapshotUser))
                        recyclerView.adapter?.notifyDataSetChanged()
                    }

                }
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(view.context,2)
    }

}

