package com.codebusters.idealizeprojectdraft.recycle_view_adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.ItemRequestModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class RecyclerViewRequestAdapter(private val itemList: ArrayList<ItemRequestModel>, private val type:Int, con : Context) : RecyclerView.Adapter<RecycleViewItemViewHolder>() {

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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "ResourceAsColor", "CutPasteId")
    override fun onBindViewHolder(holder: RecycleViewItemViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.itemName.text = currentItem.name
        holder.itemRating.text = currentItem.rating
        holder.itemPrice.text = currentItem.price
        Picasso.get().load(currentItem.photo).into(holder.itemImage)

        holder.itemDelete.visibility = View.GONE
        holder.itemVisible.visibility = View.GONE
        holder.itemBooking.visibility = View.GONE
        holder.itemRequestRate.visibility = View.GONE
        holder.itemReteReview.visibility = View.GONE


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

        if (type == myTags.requestMode) {

            if(currentItem.isReviewDone == "1"){
                holder.itemLl.setBackgroundResource(R.color.colorSecondary)
                holder.itemDelete.setOnClickListener {
                    deleteRequestFromHistory(currentItem, context)
                }
                holder.itemDelete.visibility = View.VISIBLE
                holder.itemRating.text = holder.itemRating.text.toString()+" + "+currentItem.reviewRate
            }else if(currentItem.requestReview == "1"){
                holder.itemDelete.visibility = View.GONE
                holder.itemRequestRate.visibility = View.GONE
                holder.itemLl.setBackgroundResource(R.color.teal_200)
            }else{
                holder.itemDelete.visibility = View.VISIBLE
                holder.itemRequestRate.visibility = View.VISIBLE

                holder.itemDelete.setOnClickListener {
                    deleteRequest(currentItem, context)
                }

                holder.itemRequestRate.setOnClickListener{
                    sendRequestToReview(currentItem,holder)
                }
            }

        }else{
            if(currentItem.isReviewDone=="1"){
                holder.itemReteReview.visibility = View.GONE
                holder.itemDelete.visibility =View.VISIBLE
                holder.itemDelete.setOnClickListener {
                    deleteRequestFromHistory(currentItem, context)
                }
                holder.itemRating.text = holder.itemRating.text.toString()+" + "+currentItem.reviewRate
                holder.itemLl.setBackgroundResource(R.color.colorSecondary)
            }else
            if(currentItem.requestReview == "1"){
                holder.itemReteReview.visibility = View.VISIBLE
                holder.itemDelete.visibility =View.GONE
                holder.itemReteReview.setOnClickListener{
                    val dialog = Dialog(context)
                    dialog.setContentView(R.layout.rating_view)
                    dialog.setCancelable(false)

                    val btnSubmit = dialog.findViewById<Button>(R.id.btn_rate_submit)
                    dialog.findViewById<RadioButton>(R.id.radio_5).isChecked = true

                    btnSubmit.setOnClickListener{
                        val rate: String = if((dialog.findViewById<RadioButton>(R.id.radio_1)).isChecked){
                            "1.0"
                        }else if(dialog.findViewById<RadioButton>(R.id.radio_2).isChecked){
                            "2.0"
                        }else if(dialog.findViewById<RadioButton>(R.id.radio_3).isChecked){
                            "3.0"
                        }else if(dialog.findViewById<RadioButton>(R.id.radio_4).isChecked){
                            "4.0"
                        }else {
                            "5.0"
                        }
                        currentItem.reviewRate =rate
                        submitRate(rate, currentItem,holder)
                        dialog.cancel()
                    }
                    dialog.create()
                    dialog.show()
                }
            }else
            if(currentItem.requestIsCancelled == "1"){
                holder.itemLl.setBackgroundResource(R.color.yellow)
                holder.itemDelete.visibility = View.VISIBLE

                holder.itemDelete.setOnClickListener {
                    deleteRequestOnlyFromMySide(currentItem, context)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun submitRate(rate : String, request: ItemRequestModel,holder : RecycleViewItemViewHolder){
        firestore = FirebaseFirestore.getInstance()

        val map=HashMap<String,Any>()
        map[myTags.requestReviewSubmit] = "1"
        map[myTags.requestReviewRate] = rate
        firestore.collection(myTags.users).document(request.requestBuyerID).collection(myTags.userMyRequests).document(request.requestID).update(map).addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Send Review from my side", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Sent! from My Side. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        firestore.collection(myTags.users).document(request.idealizeUserID).collection(myTags.userRequests).document(request.requestID).update(map).addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Updated! from other side", Toast.LENGTH_SHORT).show()
                request.isReviewDone = "1"
                request.reviewRate=rate
                holder.itemLl.setBackgroundResource(R.color.colorSecondary)
                holder.itemRating.text = holder.itemRating.text.toString()+" + "+rate
                holder.itemReteReview.visibility =View.GONE
                holder.itemDelete.visibility = View.VISIBLE
                this.notifyDataSetChanged()
            }else{
                Toast.makeText(context,"Not Updated! from Other Side. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun sendRequestToReview(request: ItemRequestModel, holder : RecycleViewItemViewHolder){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.users).document(request.idealizeUserID).collection(myTags.userRequests).document(request.requestID).update(myTags.requestReview,"1").addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Send request to review from my side", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Sent! from My Side. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        firestore.collection(myTags.users).document(request.requestBuyerID).collection(myTags.userMyRequests).document(request.requestID).update(myTags.requestReview,"1").addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Updated! from other side", Toast.LENGTH_SHORT).show()
                request.requestReview = "1"
                holder.itemLl.setBackgroundResource(R.color.teal_200)
                holder.itemRequestRate.visibility = View.GONE
                holder.itemDelete.visibility = View.GONE
                this.notifyDataSetChanged()
            }else{
                Toast.makeText(context,"Not Updated! from Other Side. Try Again", Toast.LENGTH_SHORT).show()
            }
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteRequestOnlyFromMySide(request : ItemRequestModel, context : Context){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.users).document(request.requestBuyerID).collection(myTags.userMyRequests).document(request.requestID).delete().addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Deleted! from My Side", Toast.LENGTH_SHORT).show()
                itemList.remove(request)
                this.notifyDataSetChanged()
            }else{
                Toast.makeText(context,"Not Deleted! from My Side. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteRequest(request : ItemRequestModel, context : Context){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.users).document(request.idealizeUserID).collection(myTags.userRequests).document(request.requestID).delete().addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Deleted! from user", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Not Deleted! from user. Try Again", Toast.LENGTH_SHORT).show()
            }
        }

        val map = HashMap<String,Any>()
        map[myTags.requestIsCancelled] = "1"
        firestore.collection(myTags.users).document(request.requestBuyerID).collection(myTags.userMyRequests).document(request.requestID).update(map).addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Updated! from buyer", Toast.LENGTH_SHORT).show()
                itemList.remove(request)
                this.notifyDataSetChanged()
            }else{
                Toast.makeText(context,"Not Updated! from buyer. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun deleteRequestFromHistory(request: ItemRequestModel, context: Context){
        firestore = FirebaseFirestore.getInstance()

        firestore.collection(myTags.users).document(request.idealizeUserID).collection(myTags.userRequests).document(request.requestID).delete().addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Deleted! from user", Toast.LENGTH_SHORT).show()
                itemList.remove(request)
                this.notifyDataSetChanged()
            }else{
                Toast.makeText(context,"Not Deleted! from user. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }
}