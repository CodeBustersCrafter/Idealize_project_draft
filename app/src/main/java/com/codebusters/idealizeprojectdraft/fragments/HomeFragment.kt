package com.codebusters.idealizeprojectdraft.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebusters.idealizeprojectdraft.ModelBuilder
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.recycle_view_adapter.RecyclerViewAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class HomeFragment : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var dataList : ArrayList<ItemModel>
    private lateinit var firestore: FirebaseFirestore
    private var myTags = MyTags()
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.Home_Recycler_view)

        firestore= FirebaseFirestore.getInstance()

        dataList = ArrayList()
        val adapter = RecyclerViewAdapter(dataList, 0,view.context)

        firestore.addSnapshotsInSyncListener{

        }

        /*firestore.collection(myTags.ads).get().addOnSuccessListener{
            result ->
            for(document in result){
                firestore.collection(myTags.users).document(document.get(myTags.adUser).toString()).get().addOnSuccessListener{
                    documentSnapshot->
                    dataList.add(ModelBuilder().getAdItem(document,documentSnapshot))
                    adapter.notifyDataSetChanged()
                }

            }
        }*/

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        listenForUpdates()
        listenForDeletions()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
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
    }
}

