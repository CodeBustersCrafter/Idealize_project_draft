package com.codebusters.idealizeprojectdraft.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment(idealizeUser: IdealizeUser) : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var requestsButton : FloatingActionButton
    private lateinit var refreshButton : SwipeRefreshLayout
    private lateinit var dataList : ArrayList<ItemModel>
    private lateinit var firestore: FirebaseFirestore
    private val user = idealizeUser
    private var myTags = MyTags()
    private lateinit var searchEditText: TextInputEditText

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
        searchEditText = view.findViewById(R.id.searchEditText)

        firestore= FirebaseFirestore.getInstance()

        val type : Int = if(user.uid == ""){//Guest Mode
            myTags.guestMode
        }else{//User Mode
            myTags.userViewMode
        }


        // Initialize data with an empty search query
        initData(type, view, searchEditText.text.toString())

        refreshButton.setOnRefreshListener {
            dataList.clear() // Clear previous data
            initData(type, view, searchEditText.text.toString()) // Use search query when refreshing
            refreshButton.isRefreshing = false
        }

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

        // Add TextWatcher to searchEditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Update the data based on the search query
                dataList.clear() // Clear previous data
                initData(type, view, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    @SuppressLint("NotifyDataSetChanged")

    private fun initData(type: Int, view: View, searchQuery: String = "") {
            dataList = ArrayList()
            val adapter = RecyclerViewAdapter(dataList, type, view.context, user.uid)


            // Create Firestore query with or without search query
            var query: Query = firestore.collection(myTags.ads)
            if (searchQuery.isNotEmpty()) {
                query = query.whereArrayContains(myTags.keywords,searchQuery.lowercase())
            }

            query.get().addOnSuccessListener { result ->
                dataList.clear() // Clear previous data
                for (document in result.documents) {
                    firestore.collection(myTags.users)
                        .document(document.get(myTags.adUser).toString())
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val item = ModelBuilder().getAdItem(document, documentSnapshot)
                            if (!dataList.contains(item)) {

                                    dataList.add(item)
                                adapter.notifyDataSetChanged()

                            }
                        }
                }
                recyclerView.adapter = adapter
                val gl= GridLayoutManager(view.context,2)
                recyclerView.layoutManager = gl

            }
    }
   /* @SuppressLint("NotifyDataSetChanged")

    private fun initData(type: Int, view: View, searchQuery: String = "") {
            dataList = ArrayList()
            val adapter = RecyclerViewAdapter(dataList, type, view.context, user.uid)


            // Create Firestore query with or without search query
            var query: Query = firestore.collection(myTags.ads)
            if (searchQuery.isNotEmpty()) {
                query = query.whereArrayContains(myTags.keywords,searchQuery.lowercase())
            }

            query.get().addOnSuccessListener { result ->
                dataList.clear() // Clear previous data
                for (document in result.documents) {
                    if(end<result.size()){
                        if(start<end){
                            firestore.collection(myTags.users)
                                .document(document.get(myTags.adUser).toString())
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val item = ModelBuilder().getAdItem(document, documentSnapshot)
                                    if (!dataList.contains(item)) {
                                        dataList.add(item)
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            start++
                        }else{
                            end+=5
                            break
                        }
                    }else if(end>result.size() && start==0){
                        firestore.collection(myTags.users)
                            .document(document.get(myTags.adUser).toString())
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                val item = ModelBuilder().getAdItem(document, documentSnapshot)
                                if (!dataList.contains(item)) {
                                    dataList.add(item)
                                    adapter.notifyDataSetChanged()
                                }
                            }
                    }else{
                        if(start<result.size()){
                            firestore.collection(myTags.users)
                                .document(document.get(myTags.adUser).toString())
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val item = ModelBuilder().getAdItem(document, documentSnapshot)
                                    if (!dataList.contains(item)) {
                                        dataList.add(item)
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            start++
                        }else{
                            end=5
                            start=0
                            break
                        }
                    }

                }
                recyclerView.adapter = adapter
                val gl= GridLayoutManager(view.context,2)
                recyclerView.layoutManager = gl

            }
    }*/


    /*
    @SuppressLint("NotifyDataSetChanged")

    private fun initData(type: Int, view: View, searchQuery: String = "",isContinue : Boolean) {
        if(!isContinue){
            dataList = ArrayList()
            dataShifter = DataShifter(dataList,5)
            var finalDataList = ArrayList<ItemModel>()
            val adapter = RecyclerViewAdapter(finalDataList, type, view.context, user.uid)


            // Create Firestore query with or without search query
            var query: Query = firestore.collection(myTags.ads)
            if (searchQuery.isNotEmpty()) {
                query = query.whereArrayContains(myTags.keywords,searchQuery.lowercase())
            }

            query.get().addOnSuccessListener { result ->
                dataList.clear() // Clear previous data
                finalDataList.clear()
                var count = 0
                for (document in result.documents) {
                    firestore.collection(myTags.users)
                        .document(document.get(myTags.adUser).toString())
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val item = ModelBuilder().getAdItem(document, documentSnapshot)
                            if (!dataList.contains(item)) {
                                if(count<5){
                                    dataList.add(item)
                                    finalDataList.add(item)
                                    adapter.notifyDataSetChanged()
                                }else{
                                    dataList.add(item)
                                }
                                count++
                            }
                        }
                }
                recyclerView.adapter = adapter
                val gl= GridLayoutManager(view.context,2)
                recyclerView.layoutManager = gl

            }
        }else{
            dataShifter.setData(dataList)
            val finalDataList : ArrayList<ItemModel> = dataShifter.getNextDataSet()
            val adapter = RecyclerViewAdapter(finalDataList, type, view.context, user.uid)
            recyclerView.adapter = adapter
            val gl= GridLayoutManager(view.context,2)
            recyclerView.layoutManager = gl
            adapter.notifyDataSetChanged()
            }
        }
*/
    }