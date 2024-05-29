package com.codebusters.idealizeprojectdraft.fragments

import CategoryAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.RequestActivity
import com.codebusters.idealizeprojectdraft.util.CustomProgressDialog
import com.codebusters.idealizeprojectdraft.util.ModelBuilder
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.recycle_view_adapter.RecyclerViewAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment(idealizeUser: IdealizeUser) : Fragment() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var requestsButton : ImageView
    private lateinit var refreshButton : SwipeRefreshLayout
    private lateinit var refreshButton2 : SwipeRefreshLayout
    private lateinit var newNotificationTag : ImageView
    private lateinit var dataList : ArrayList<ItemModel>
    private lateinit var firestore: FirebaseFirestore
    private val user = idealizeUser
    private var myTags = MyTags()
    private lateinit var searchEditText: TextInputEditText
    private lateinit var welcomeTextView: TextView
    private lateinit var cities: List<*>
    private val progressDialog by lazy { context?.let { CustomProgressDialog(it) } }
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var toggleButton: Button
    private var isGreen = true

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        progressDialog?.start("Loading...")
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.Home_Recycler_view)
        categoryRecyclerView = view.findViewById(R.id.Category_Recycler_view)
        requestsButton = view.findViewById(R.id.Home_request_action_btn)
        refreshButton = view.findViewById(R.id.Home_swiper_button)
        refreshButton2 = view.findViewById(R.id.Category_swiper_button)
        newNotificationTag = view.findViewById(R.id.new_tag_home_screen)
        refreshButton2.isEnabled = false
        searchEditText = view.findViewById(R.id.searchEditText)
        toggleButton = view.findViewById(R.id.toggleButton)
        val autoCompleteTextView: AutoCompleteTextView = view.findViewById(R.id.autoCompleteTextView)
        autoCompleteTextView.threshold = 0
        var selectedCity = ""
        var filterdBy = ""
        var itemcategory = ""
        var mycity = ""

        newNotificationTag.visibility = View.GONE

        firestore= FirebaseFirestore.getInstance()

        val type : Int = if(user.uid == ""){//Guest Mode
            myTags.guestMode
        }else{//User Mode
            myTags.userViewMode
        }

        // Set welcome message
        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        val welcomeMessage = if (type == myTags.guestMode) {
            "Welcome Guest User"
        } else {
            "Welcome ${user.name}"
        }
        welcomeTextView.text = welcomeMessage

        if(type==myTags.userViewMode){
            newRequestsChecking()
        }else{
            newNotificationTag.visibility = View.GONE
        }

        toggleButton.setOnClickListener {
            if (type == myTags.guestMode) {
                Toast.makeText(context, "You need to sign in to your account to use this feature", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isGreen = !isGreen  // Toggle the variable
            if (isGreen) {
                toggleButton.setBackgroundColor(Color.parseColor("#00DE04")) // Green
                mycity = ""
            } else {
                toggleButton.setBackgroundColor(Color.parseColor("#808080")) // Grey
                mycity = user.location
            }

            initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
        }

        categoryList = arrayListOf(
            Category("Fruits", R.drawable.cat1),
            Category("Vegetables", R.drawable.cat2),
            Category("Flowers", R.drawable.c3),
            Category("Herbals", R.drawable.c4),
            Category("Spices", R.drawable.cat5),
            Category("Plants", R.drawable.c6),
            Category("Diary", R.drawable.cat7),
            Category("Hand Crafts", R.drawable.cat8),
            Category("Other", R.drawable.cat9)
        )

        categoryAdapter = CategoryAdapter(categoryList) { category ->
            if(itemcategory == category.name){
                categoryAdapter.selectedPosition = RecyclerView.NO_POSITION
                categoryAdapter.notifyDataSetChanged()
                itemcategory = ""
            }else{
                itemcategory = category.name
            }
            initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
        }

        categoryRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.adapter = categoryAdapter


        // Fetch cities from Firebase

        firestore.collection(myTags.appData).document(myTags.tags).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firebaseArray = document.get(myTags.cities) as? List<*>
                    if (firebaseArray != null) {
                        cities = firebaseArray
                    }
                    autoCompleteTextView.setOnClickListener {
                        autoCompleteTextView.showDropDown()
                    }

                    // Set up ArrayAdapter with the cities list
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
                    autoCompleteTextView.setAdapter(adapter)


                    // Optional: Set a listener for when a city is selected
                    autoCompleteTextView.setOnItemClickListener { parent, view, position, _ ->
                        selectedCity = parent.getItemAtPosition(position) as String
                        initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
                    }
                } else {
                    Toast.makeText(requireContext(), "No cities found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load cities: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Initialize data with an empty search query
        initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)

        refreshButton.setOnRefreshListener {
            dataList.clear() // Clear previous data
            val temp = autoCompleteTextView.text.toString()
            if(temp == ""){
                selectedCity = ""
            }
            itemcategory = ""
            isGreen = true
            toggleButton.setBackgroundColor(Color.parseColor("#00DE04")) // Green
            mycity = ""
            categoryAdapter.selectedPosition = RecyclerView.NO_POSITION
            categoryAdapter.notifyDataSetChanged()

            initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity) // Use search query when refreshing
            refreshButton.isRefreshing = false
        }

        if(user.uid == ""){
            requestsButton.setOnClickListener{
                Toast.makeText(context,"You need to sign in to your account before see your bookings",Toast.LENGTH_SHORT).show()
            }
        }else{
            requestsButton.setOnClickListener{
                if(newNotificationTag.visibility==View.VISIBLE){
                    val i = Intent(context,RequestActivity::class.java)
                    i.putExtra(myTags.intentUID,user.uid)
                    i.putExtra(myTags.intentFragmentRequest,"1")
                    startActivity(i)
                }else{
                    val i = Intent(context,RequestActivity::class.java)
                    i.putExtra(myTags.intentUID,user.uid)
                    startActivity(i)
                }

            }
        }

        // Add TextWatcher to searchEditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Update the data based on the search query
                dataList.clear() // Clear previous data
                initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val filterButton: MaterialButton = view.findViewById(R.id.settings_filter_button)
        filterButton.setOnClickListener { view ->

            val popupMenu = PopupMenu(requireActivity(), view)
            popupMenu.inflate(R.menu.filter_menu) // Create a filter_menu.xml

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.filter_date -> {
                        filterdBy = "date"
                        initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
                        true
                    }
                    R.id.filter_price -> {
                        filterdBy = "price"
                        initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
                        true
                    }
                    R.id.filter_rating -> {
                        filterdBy = "rating"
                        initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
                        true
                    }
                    else -> {
                        initData(type, view, searchEditText.text.toString(),selectedCity,filterdBy,itemcategory,mycity)
                        true
                    }
                }
            }
            popupMenu.show()
        }

        return view
    }


    data class Category(val name: String, val imageResId: Int)


    private fun newRequestsChecking(){

            FirebaseFirestore.getInstance().collection(myTags.users).document(user.uid)
                .collection(myTags.userRequests)
                .addSnapshotListener { _, _ ->
                            FirebaseFirestore.getInstance().collection(myTags.users).document(user.uid)
                                .get().addOnSuccessListener { document ->
                                    if (document.get(myTags.areNewRequests) != null) {
                                        if (document.get(myTags.areNewRequests).toString().trim().toInt()==1) {
                                            newNotificationTag.visibility = View.VISIBLE
                                        }else{
                                            newNotificationTag.visibility = View.GONE
                                        }
                                    }else{
                                        newNotificationTag.visibility = View.GONE
                                    }
                                }.addOnFailureListener{
                                    newNotificationTag.visibility = View.GONE
                                }
                }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData(type: Int, view: View, searchQuery: String = "",location: String = "",filtering: String = "",itemcategory: String = "",mycity: String = "") {
        dataList = ArrayList()
        val adapter = RecyclerViewAdapter(dataList, type, view.context, user.uid)
        recyclerView.adapter = adapter
        val gl= GridLayoutManager(view.context,2)
        recyclerView.layoutManager = gl


        // Create Firestore query with or without search query
        var query: Query = firestore.collection(myTags.ads)

        if(itemcategory.isNotEmpty()){
            query = query.whereEqualTo(myTags.adCategory, itemcategory)
        }

        if (mycity.isNotEmpty()) {
            val index = cities.indexOf(mycity)
            val nearbyCities = mutableListOf<String>()
            nearbyCities.add(cities[index].toString())
            for (i in 1..5) {
                if((index - i >= 0) && (index + i < cities.size)){
                    nearbyCities.add(cities[index - i].toString())
                    nearbyCities.add(cities[index + i].toString())
                }
            }
            query = query.whereIn(myTags.adLocation, nearbyCities)
        }

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

                        // Sort by date and time
                        dataList.sortBy { it.time }
                        dataList.sortBy { it.date }

                        if(filtering == "price") {
                            dataList.reverse()
                        }

                        // Filter by date, rate or price
                        when (filtering) {
                            "date" -> dataList.sortBy { it.date }
                            "price" -> dataList.sortBy { it.price.toLong() }
                            "rating" -> dataList.sortBy { it.rating }
                        }

                        if(filtering != "price") {
                            dataList.reverse()
                        }

                        if(location.isNotEmpty()){
                            var j = 0
                            for(i in 0 until dataList.size){
                                if(dataList[i].location == location){
                                    dataList.add(j,dataList[i])
                                    dataList.removeAt(i+1)
                                    j++
                                }
                            }
                        }

                        adapter.notifyDataSetChanged()
                    }
            }
            progressDialog?.stop()
        }
    }

}