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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

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
        initData(type, view, "")

        refreshButton.setOnRefreshListener {
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
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        // Create Firestore query with or without search query
        var query: Query = firestore.collection(myTags.ads)
        if (searchQuery.isNotEmpty()) {
            query = query.whereArrayContains(myTags.keywords,searchQuery.lowercase(Locale.getDefault())
            )
        }

        query.get().addOnSuccessListener { result ->
            dataList.clear() // Clear previous data
            for (document in result) {
                firestore.collection(myTags.users)
                    .document(document.get(myTags.adUser).toString())
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        dataList.add(ModelBuilder().getAdItem(document, documentSnapshot))
                        adapter.notifyDataSetChanged()
                    }
            }
        }
    }
}