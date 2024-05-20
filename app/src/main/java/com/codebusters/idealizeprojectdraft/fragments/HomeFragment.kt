package com.codebusters.idealizeprojectdraft.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
        val autoCompleteTextView: AutoCompleteTextView = view.findViewById(R.id.autoCompleteTextView)
        var selectedCity = ""

        firestore= FirebaseFirestore.getInstance()

        val type : Int = if(user.uid == ""){//Guest Mode
            myTags.guestMode
        }else{//User Mode
            myTags.userViewMode
        }

        // Fetch cities from Firebase
        firestore.collection(myTags.appData).document(myTags.tags).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firebaseArray = document.get(myTags.cities) as? List<*>
                    val cities = listOf("All") + (firebaseArray ?: listOf())

                    // Set up ArrayAdapter with the cities list
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
                    autoCompleteTextView.setAdapter(adapter)

                    // Optional: Set a listener for when a city is selected
                    autoCompleteTextView.setOnItemClickListener { parent, view, position, _ ->
                        selectedCity = parent.getItemAtPosition(position) as String
                        if(parent.getItemAtPosition(position) as String == "All"){
                            selectedCity = ""
                        }
                        initData(type, view, searchEditText.text.toString(),selectedCity)
                    }
                } else {
                    Toast.makeText(requireContext(), "No cities found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load cities: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Initialize data with an empty search query
        initData(type, view, searchEditText.text.toString(),selectedCity)

        refreshButton.setOnRefreshListener {
            dataList.clear() // Clear previous data
            initData(type, view, searchEditText.text.toString(),selectedCity) // Use search query when refreshing
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
                initData(type, view, s.toString(),selectedCity)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData(type: Int, view: View, searchQuery: String = "",location: String = "") {
        dataList = ArrayList()
        val adapter = RecyclerViewAdapter(dataList, type, view.context, user.uid)
        recyclerView.adapter = adapter
        val gl= GridLayoutManager(view.context,2)
        recyclerView.layoutManager = gl

        // Create Firestore query with or without search query
        var query: Query = firestore.collection(myTags.ads)
        if (location.isNotEmpty()) {
            query = query.whereEqualTo(myTags.adLocation, location)
        }
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
                        }
                        adapter.notifyDataSetChanged()
                    }
            }
        }
    }
}