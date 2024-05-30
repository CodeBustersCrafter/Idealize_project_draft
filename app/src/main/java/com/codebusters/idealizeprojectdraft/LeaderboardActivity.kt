package com.codebusters.idealizeprojectdraft

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codebusters.idealizeprojectdraft.databinding.ActivityLeaderboardBinding
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLeaderboardBinding
    private val myTags = MyTags()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner = binding.spinnerRowCount
        val rowCounts = arrayOf("Select Row Count","0-5", "0-10", "0-20", "0-50", "0-100", "0-500", "0-1000")
        val adapter = ArrayAdapter(this, R.layout.drop_down_menu, rowCounts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Get the selected row count
                if(position!=0){
                     val selectedRowCount = rowCounts[position].substring(2).toInt()

                // Update the table layout to display the selected number of rows
                // (You need to implement the logic to update the table layout based on the selected row count)
                binding.tableLayoutLeaderboard.removeViews(0, binding.tableLayoutLeaderboard.childCount)
                val d = listOf("Rank","Name","Rate")
                addToTable(d,true)
                gatherData(selectedRowCount)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no item is selected
            }
        }

        binding.tableLayoutLeaderboard.removeViews(0, binding.tableLayoutLeaderboard.childCount)
        val d = listOf("Rank","Name","Rate")
        addToTable(d,true)

        gatherData()
    }

    private fun gatherData(selectedRowCount : Int = 10){
        val firestore = FirebaseFirestore.getInstance()

        //Add top data
        val map = HashMap<Float, MutableList<String>>()
        firestore.collection(myTags.users).get().addOnSuccessListener {
            for (document in it) {
                val rating = document.get(myTags.userRating).toString().toFloat()
                val name = document.get(myTags.userName).toString()

                // Add the user to the list for their corresponding rating
                if (!map.containsKey(rating)) {
                    map[rating] = mutableListOf()
                }
                map[rating]?.add(name)
            }

            // Sort the map by keys (ratings) in descending order
            val sortedMap = map.toSortedMap(reverseOrder())

            // Extract user names and ratings from the sorted map
            var i = 1
            var isLimitExceeded = false
            val limit = selectedRowCount
            for ((rating, names) in sortedMap) {
                for (name in names) {
                    if(limit<i){
                        isLimitExceeded = true
                        break
                    }
                    @Suppress("KotlinConstantConditions")
                    if(!isLimitExceeded){
                        val data = listOf(i, name, rating.toString())
                        addToTable(data)
                        i+=1
                    }
                }
            }

        }


    }


    @Suppress("DEPRECATION")
    private fun addToTable(data : List<*>, isTopic : Boolean = false){
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        tableRow.setPadding(10,10,10,0)

        if(data[0].toString() == "1"){
            tableRow.setBackgroundColor(resources.getColor(R.color.gold))
        }else
        if(data[0].toString() == "2"){
            tableRow.setBackgroundColor(resources.getColor(R.color.silver))
        }else
        if(data[0].toString() == "3"){
            tableRow.setBackgroundColor(resources.getColor(R.color.bronze))
        }else{
            tableRow.setBackgroundColor(resources.getColor(R.color.white))
        }
        tableRow.gravity = android.view.Gravity.CENTER

        var cellNo = 0
        for (cell in data) {
            val textView = TextView(this)
            textView.apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                ).apply {
                    if(cellNo==0 || cellNo==2){
                        weight = .2f
                    }else{
                        weight = 1f
                    }
                }
                text = cell.toString()
                if(cellNo==0 || cellNo==2 || isTopic){
                    gravity = android.view.Gravity.CENTER
                }else{
                    gravity = android.view.Gravity.START
                }
                setTextColor(resources.getColor(R.color.black))
                textSize = 20f

            }

            tableRow.addView(textView)
            cellNo+=1
        }
        binding.tableLayoutLeaderboard.addView(tableRow)
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra(myTags.intentType,myTags.userMode)
        intent.putExtra(myTags.intentUID,FirebaseAuth.getInstance().currentUser?.uid)
        startActivity(intent)
        finish()

    }
}