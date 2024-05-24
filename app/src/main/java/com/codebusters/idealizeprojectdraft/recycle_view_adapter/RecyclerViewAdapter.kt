package com.codebusters.idealizeprojectdraft.recycle_view_adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.codebusters.idealizeprojectdraft.ModelBuilder
import com.codebusters.idealizeprojectdraft.NormalCalls
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.models.RequestModel
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


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

        holder.itemName.text = currentItem.name
        holder.itemRating.text = currentItem.rating
        holder.itemPrice.text = currentItem.price
        Picasso.get().load(currentItem.photo).into(holder.itemImage)

        holder.itemDelete.visibility = View.GONE
        holder.itemVisible.visibility = View.GONE
        holder.itemBooking.visibility = View.VISIBLE
        holder.itemReteReview.visibility = View.GONE
        holder.itemRequestRate.visibility = View.GONE


        holder.itemLl.setOnClickListener {
            val d = Dialog(context)
            d.setContentView(R.layout.item_dialogue)
            d.setCancelable(false)

            val name = d.findViewById<TextView>(R.id.dialogue_name)
            val price = d.findViewById<TextView>(R.id.dialogue_price)
            val quantity = d.findViewById<TextView>(R.id.dialogue_quantity)
            val location = d.findViewById<TextView>(R.id.dialogue_location)
            val phone = d.findViewById<TextView>(R.id.dialogue_phone)
            val seller = d.findViewById<TextView>(R.id.dialogue_seller)
            val dateTime = d.findViewById<TextView>(R.id.dialogue_date_time)
            val description = d.findViewById<TextView>(R.id.dialogue_description)
            val image = d.findViewById<ImageView>(R.id.dialogue_image)
            val close = d.findViewById<ImageView>(R.id.dialogue_btn_close)
            val category = d.findViewById<TextView>(R.id.dialog_category)
            val call = d.findViewById<ImageView>(R.id.call_dialogue)

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
            price.text = "Price : LKR . " + currentItem.price
            quantity.text = "Quantity : " + currentItem.quantity
            location.text = currentItem.location
            phone.text = currentItem.phone
            seller.text = currentItem.username
            dateTime.text = currentItem.date + "__" + currentItem.time
            description.text = currentItem.description
            category.text = currentItem.category
            Picasso.get().load(currentItem.photo).into(image)
            close.setOnClickListener {
                d.cancel()
            }
            d.create()
            d.show()

        }

        when (type) {
            myTags.userMode -> {

                holder.itemDelete.visibility = View.VISIBLE
                holder.itemVisible.visibility = View.VISIBLE
                holder.itemBooking.visibility = View.GONE

                if(currentItem.visibility==myTags.adVisible){
                    Picasso.get().load(R.drawable.baseline_visibility_24).into(holder.itemVisible)
                }else{
                    Picasso.get().load(R.drawable.baseline_visibility_off_24).into(holder.itemVisible)
                }

                holder.itemDelete.setOnClickListener {
                    //deleteItem()
                    deleteAd(currentItem.idealizeUserID, currentItem.adId, context)
                    itemList.remove(currentItem)
                    this.notifyDataSetChanged()
                }

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
            }
            myTags.userViewMode -> {//
                holder.itemBooking.setOnClickListener{
                    val request = RequestModel(currentItem.adId,uid,currentItem.idealizeUserID,"0","0","0","0",uid+"_"+currentItem.idealizeUserID+"_"+currentItem.adId)
                    sendRequests(request)
                }
            }
            else -> {
                holder.itemBooking.setOnClickListener{
                    Toast.makeText(context,"You need to sign in to your account before requesting",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun sendRequests(requestModel: RequestModel){
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
        firestore.collection(myTags.users).document(requestModel.buyerId).collection(myTags.userMyRequests).document(requestModel.requestID).set(ModelBuilder().getRequestAsMap(requestModel)).addOnCompleteListener{
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Request is shown to you", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Showed! Try Again", Toast.LENGTH_SHORT).show()
            }
        }
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
        val map=ModelBuilder().getItemAsMap(item)

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
    }
}