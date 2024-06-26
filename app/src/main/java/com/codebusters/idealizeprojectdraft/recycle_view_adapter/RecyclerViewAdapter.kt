package com.codebusters.idealizeprojectdraft.recycle_view_adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebusters.idealizeprojectdraft.AddingAdsActivity
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.models.RequestModel
import com.codebusters.idealizeprojectdraft.util.CustomMaskTransformation
import com.codebusters.idealizeprojectdraft.util.ModelBuilder
import com.codebusters.idealizeprojectdraft.util.NormalCalls
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.squareup.picasso.Picasso
import java.math.RoundingMode


class RecyclerViewAdapter(private val itemList: ArrayList<ItemModel>, private val type:Int, con : Context,private val uid : String) : RecyclerView.Adapter<RecycleViewItemViewHolder>() {

    private var myTags = MyTags()
    private val context = con
    private lateinit var firestore : FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ad_item_view,parent,false)
        firestore = FirebaseFirestore.getInstance()
        return RecycleViewItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecycleViewItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        createUI(holder,currentItem)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun dialogueCreate(currentItem: ItemModel){
        val d = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        d.setContentView(R.layout.item_view_dialogue_bishma)
        d.setCancelable(false)
        d.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val name = d.findViewById<TextView>(R.id.dialogue_name)
        val price = d.findViewById<TextView>(R.id.dialogue_price)
        val quantity = d.findViewById<TextView>(R.id.dialogue_quantity)
        val location = d.findViewById<TextView>(R.id.dialogue_location)
        val seller = d.findViewById<TextView>(R.id.dialogue_seller)
        val date = d.findViewById<TextView>(R.id.dialogue_date)
        val time = d.findViewById<TextView>(R.id.dialogue_time)
        val description = d.findViewById<TextView>(R.id.dialogue_description)
        val userRate = d.findViewById<TextView>(R.id.dialogue_user_rate)
        val adRate = d.findViewById<TextView>(R.id.dialogue_ad_rate)
        val userRateBar = d.findViewById<RatingBar>(R.id.dialogue_user_rate_bar)
        val adRateBar = d.findViewById<RatingBar>(R.id.dialogue_ad_rate_bar)
        val image = d.findViewById<ImageView>(R.id.dialogue_image)
        val close = d.findViewById<ImageView>(R.id.dialogue_btn_close)
        val category = d.findViewById<TextView>(R.id.dialog_category)
        val call = d.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.call_dialogue)
        val itemBooking = d.findViewById<ImageView>(R.id.btn_booking_item_view)
        val itemUpdate = d.findViewById<Button>(R.id.btn_update_item_view)
        val itemLL = d.findViewById<LinearLayout>(R.id.dialogue_linear_layout)

        val dialogRecyclerView = d.findViewById<RecyclerView>(R.id.dialogue_recycler_view)

        when (type){
            myTags.userMode->{
                itemLL.visibility = View.GONE
                itemBooking.visibility = View.GONE
                itemUpdate.visibility = View.VISIBLE

                itemUpdate.setOnClickListener{
                    val intent = Intent(context, AddingAdsActivity::class.java)
                    intent.putExtra(myTags.intentUID,uid)
                    intent.putExtra(myTags.intentAddingAdsMode,myTags.updateMode)
                    intent.putExtra(myTags.intentUpdatingAdID,currentItem.adId)
                    context.startActivity(intent)
                    d.cancel()
                }
            }
            myTags.userViewMode -> {//
                itemLL.visibility = View.VISIBLE
                itemUpdate.visibility = View.GONE
                itemBooking.visibility = View.VISIBLE
                itemBooking.setOnClickListener{
                    val request = RequestModel(currentItem.adId,uid,currentItem.idealizeUserID,"0","0","0","0",uid+"_"+currentItem.idealizeUserID+"_"+currentItem.adId)
                    if(sendRequests(request)){
                        //increment the request count
                        currentItem.requestCount++
                        currentItem.rateCount++
                        incrementRequests(currentItem)
                    }
                }
            }
            else -> {
                itemLL.visibility = View.VISIBLE
                itemBooking.visibility = View.VISIBLE
                itemUpdate.visibility = View.GONE
                itemBooking.setOnClickListener{
                    Toast.makeText(context,"You need to sign in to your account before requesting",Toast.LENGTH_SHORT).show()
                }
            }

        }

        call.setOnClickListener{
            try {
                val normalCalls = NormalCalls(currentItem.phone)
                context.startActivity(normalCalls.call())
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Number is not assigned yet to profile"+e.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        name.text = currentItem.name
        price.text = "LKR . " + currentItem.price
        quantity.text = "Quantity : " + currentItem.quantity
        location.text = currentItem.location
        seller.text = currentItem.username
        date.text = currentItem.date
        time.text = currentItem.time
        description.text = currentItem.description
        category.text = currentItem.category

        Picasso.get().load(currentItem.photo).transform(CustomMaskTransformation(context, R.drawable.grey_background)).into(image)

        adRate.text = "(" + (currentItem.rate.toFloat()*5).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat() + ")"
        userRate.text = "(" + currentItem.rating.toFloat().toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat() + ")"
        adRateBar.rating = (currentItem.rate.toFloat()*5).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()
        userRateBar.rating = currentItem.rating.toFloat().toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()

        val dataList = ArrayList<ItemModel>()
        val adapter = RecyclerViewAdapter(dataList, myTags.userViewMode, context, uid)
        dialogRecyclerView.adapter = adapter
        val ll= LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        dialogRecyclerView.layoutManager = ll

        firestore.collection(myTags.users).document(currentItem.idealizeUserID).get().addOnSuccessListener {
            documentSnapshot->
            firestore.collection(myTags.users).document(currentItem.idealizeUserID).collection(myTags.ads).get().addOnSuccessListener {
                for(document in it){
                    if(document.id!=currentItem.adId){
                        val item = ModelBuilder().getAdItem(document,documentSnapshot)
                        dataList.add(item)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        close.setOnClickListener {
            d.cancel()
        }

        d.create()
        d.show()
    }

    private fun addNewTag(holder: RecycleViewItemViewHolder,currentItem : ItemModel){
        if(currentItem.viewCount==0){
            holder.itemRating.visibility = View.GONE
            holder.itemNew.visibility = View.VISIBLE
        }else{
            holder.itemRating.visibility = View.VISIBLE
            holder.itemNew.visibility = View.GONE
            holder.itemRating.text = (currentItem.rate.toFloat()*5).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat().toString()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun createUI(holder: RecycleViewItemViewHolder, currentItem : ItemModel){
        holder.itemName.text = currentItem.name
        holder.itemPrice.text = currentItem.price
        Picasso.get().load(currentItem.photo).into(holder.itemImage)

        holder.itemDelete.visibility = View.GONE
        holder.itemVisible.visibility = View.GONE
        holder.itemReteReview.visibility = View.GONE
        holder.itemRequestRate.visibility = View.GONE
        holder.itemRating.visibility = View.GONE
        holder.itemNew.visibility = View.GONE

        addNewTag(holder,currentItem)

        when(type){
            myTags.userMode->{
                holder.itemDelete.visibility = View.VISIBLE
                holder.itemVisible.visibility = View.VISIBLE

                //initialize visibility
                if(currentItem.visibility==myTags.adVisible){
                    Picasso.get().load(R.drawable.baseline_visibility_24).into(holder.itemVisible)
                }else{
                    Picasso.get().load(R.drawable.baseline_visibility_off_24).into(holder.itemVisible)
                }

                //delete
                holder.itemDelete.setOnClickListener {
                    //deleteItem()
                    deleteAd(currentItem.idealizeUserID, currentItem.adId, context)
                    itemList.remove(currentItem)
                    this.notifyDataSetChanged()
                }

                //visibility
                holder.itemVisible.setOnClickListener {
                    //set visibility
                    //flipVisibility()
                    if (currentItem.visibility == myTags.adNotVisible) {//make visible
                        holder.itemVisible.setBackgroundResource(R.drawable.baseline_visibility_24)
                        currentItem.visibility = myTags.adVisible
                        makeAdVisible(
                            currentItem,
                            context
                        )
                        this.notifyDataSetChanged()

                    } else {
                        holder.itemVisible.setBackgroundResource(R.drawable.baseline_visibility_off_24)
                        currentItem.visibility = myTags.adNotVisible
                        makeAdInvisible(
                            currentItem.idealizeUserID,
                            currentItem.adId,
                            context
                        )
                        this.notifyDataSetChanged()
                    }
                }
                holder.itemLl.setOnClickListener {
                    dialogueCreate(currentItem)
                }

            }
            myTags.userViewMode->{
                holder.itemLl.setOnClickListener {

                    //increment the view count
                    currentItem.viewCount++
                    incrementViews(currentItem)

                    dialogueCreate(currentItem)

                }
            }else->{
            holder.itemLl.setOnClickListener {

                //increment the view count
                currentItem.viewCount++
                incrementViews(currentItem)

                dialogueCreate(currentItem)

            }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun incrementViews(item : ItemModel){
        item.rate=(item.requestCount.toFloat()/(item.viewCount.toFloat())).toString()
        val map = HashMap<String,Any>()
        map[myTags.adViewCount] = item.viewCount
        map[myTags.adRate] = item.rate
        firestore.collection(myTags.ads).document(item.adId).update(map).addOnSuccessListener {
            firestore.collection(myTags.users).document(item.uid).collection(myTags.ads).document(item.adId).update(map).addOnSuccessListener {
                //View Count updated
                this.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun incrementRequests(item : ItemModel){
        item.rate=(item.requestCount.toFloat()/item.viewCount.toFloat()).toString()
        val map = HashMap<String,Any>()
        map[myTags.adRequestCount] = item.requestCount
        map[myTags.adRate] = item.rate
        firestore.collection(myTags.ads).document(item.adId).update(map).addOnSuccessListener {
            firestore.collection(myTags.users).document(item.uid).collection(myTags.ads).document(item.adId).update(map).addOnSuccessListener {
                //request Count updated
            }
        }
    }
    private fun sendRequests(requestModel: RequestModel) : Boolean{
        /*if(requestModel.sellerId==uid){
            Toast.makeText(context,"You can't request for your goods.",Toast.LENGTH_SHORT).show()
            return false
        }*/
        firestore = FirebaseFirestore.getInstance()
        firestore.collection(myTags.users)
            .document(requestModel.sellerId)
            .collection(myTags.userRequests)
            .document(requestModel.requestID)
            .set(ModelBuilder().getRequestAsMap(requestModel)).addOnCompleteListener{
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Request is sent to user", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Sent! Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        firestore.collection(myTags.users).document(requestModel.buyerId).collection(myTags.userMyRequests).document(requestModel.requestID).set(
            ModelBuilder().getRequestAsMap(requestModel)).addOnCompleteListener{
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Request is shown to you", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Showed! Try Again", Toast.LENGTH_SHORT).show()
            }
        }

        val map = HashMap<String,Any>()
        map[requestModel.requestID] = requestModel.requestID
        firestore.collection(myTags.adRequest).document(requestModel.adId).set(map, SetOptions.merge()).addOnSuccessListener {
            //request is added to the queue
        }

        val map2 = HashMap<String,Any>()
        map2[myTags.areNewRequests] = 1
        firestore.collection(myTags.users).document(requestModel.sellerId).set(map2, SetOptions.merge())
        return true
    }
    private fun makeAdVisible(item: ItemModel, context : Context){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.users).document(item.uid).collection(myTags.ads).document(item.adId).update(myTags.adVisibility,myTags.adVisible).addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Updated! from user", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Updated! from user. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        putAd(item,context)
    }

    private fun makeAdInvisible(uid : String, adId : String, context : Context){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.users).document(uid).collection(myTags.ads).document(adId).update(myTags.adVisibility,myTags.adNotVisible).addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Updated! from user", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Updated! from user. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        removeAd(adId,context)
    }

    private fun putAd(item: ItemModel, context : Context){
        val map= ModelBuilder().getItemAsMap(item)

        firestore.collection(myTags.ads).document(item.adId)
            .set(map).addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    Toast.makeText(context,"Updated! from advertisements", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Not Updated! from advertisements. Try Again", Toast.LENGTH_SHORT).show()
                }

            }
    }
    private fun removeAd(adId : String, context : Context){
        firestore.collection(myTags.ads).document(adId)
            .delete().addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    Toast.makeText(context,"Removed! from advertisements", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Not Removed! from advertisements. Try Again", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun deleteAd(uid : String,adId : String ,context : Context){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.users).document(uid).collection(myTags.ads).document(adId).delete().addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Deleted! from user", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Deleted! from user. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        firestore.collection(myTags.ads).document(adId).delete().addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Deleted! from advertisements", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Deleted! from advertisements. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        deleteAllRequests(adId,context)
    }
    private fun deleteAllRequests(adId : String ,context : Context){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.adRequest).document(adId).get().addOnSuccessListener {
            val map = it.data
            if(map!=null){
                for(key in map.keys){
                    val keyList = decodeKey(key.toString())
                    firestore.collection(myTags.users).document(keyList[1]).collection(myTags.userRequests).document(key).delete().addOnCompleteListener {
                    }
                    firestore.collection(myTags.users).document(keyList[0]).collection(myTags.userMyRequests).document(key).delete().addOnCompleteListener {
                        if(map.keys.last()==key){
                            firestore.collection(myTags.adRequest).document(adId).delete().addOnCompleteListener {
                                    result ->
                                if(result.isSuccessful){
                                    Toast.makeText(context,"Deleted! from Requests", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context,"Not Deleted! from Requests. Try Again", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    private fun decodeKey(key : String):List<String>{
        return key.split("_")
    }
}