package com.codebusters.idealizeprojectdraft.recycle_view_adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebusters.idealizeprojectdraft.CustomMaskTransformation
import com.codebusters.idealizeprojectdraft.CustomProgressDialog
import com.codebusters.idealizeprojectdraft.ModelBuilder
import com.codebusters.idealizeprojectdraft.NormalCalls
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.ItemRequestModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.math.RoundingMode


class RecyclerViewRequestAdapter(private val itemList: ArrayList<ItemRequestModel>, private val type:Int, con : Context) : RecyclerView.Adapter<RecycleViewItemViewHolder>() {

    private var myTags = MyTags()
    private val context = con
    private lateinit var firestore : FirebaseFirestore
    private val progressDialog by lazy { CustomProgressDialog(context) }

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

        createUI(holder,currentItem)
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun createUI(holder: RecycleViewItemViewHolder, currentItem : ItemRequestModel){
        holder.itemName.text = currentItem.name
        holder.itemPrice.text = currentItem.price
        Picasso.get().load(currentItem.photo).into(holder.itemImage)

        holder.itemDelete.visibility = View.GONE
        holder.itemVisible.visibility = View.GONE
        holder.itemReteReview.visibility = View.GONE
        holder.itemRequestRate.visibility = View.GONE
        holder.itemRating.visibility = View.VISIBLE
        holder.itemNew.visibility = View.GONE

        holder.itemRating.text = (currentItem.rate.toFloat()*5).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat().toString()

        if (type == myTags.requestMode) {

            if(currentItem.isReviewDone == "1"){
                holder.itemLl.setBackgroundResource(R.color.colorSecondary)
                holder.itemDelete.setOnClickListener {
                    deleteRequestFromHistory(currentItem, context)
                }
                holder.itemDelete.visibility = View.VISIBLE
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
                            val rate: Float = if((dialog.findViewById<RadioButton>(R.id.radio_1)).isChecked){
                                1f
                            }else if(dialog.findViewById<RadioButton>(R.id.radio_2).isChecked){
                                2f
                            }else if(dialog.findViewById<RadioButton>(R.id.radio_3).isChecked){
                                3f
                            }else if(dialog.findViewById<RadioButton>(R.id.radio_4).isChecked){
                                4f
                            }else {
                                5f
                            }
                            currentItem.reviewRate =rate.toString()

                            //Calculate new rate
                            currentItem.rateCount++
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


        holder.itemLl.setOnClickListener{
            dialogueCreate(currentItem)

        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun dialogueCreate(currentItem: ItemRequestModel){
        val d = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        d.setContentView(R.layout.item_view_dialogue_bishma)
        d.setCancelable(false)
        d.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        val name = d.findViewById<TextView>(R.id.dialogue_name)
        val price = d.findViewById<TextView>(R.id.dialogue_price)
        val quantity = d.findViewById<TextView>(R.id.dialogue_quantity)
        val location = d.findViewById<TextView>(R.id.dialogue_location)
        val seller = d.findViewById<TextView>(R.id.dialogue_seller)
        val dateTime = d.findViewById<TextView>(R.id.dialogue_date_time)
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

        val dialogRecyclerView = d.findViewById<RecyclerView>(R.id.dialogue_recycler_view)

        itemBooking.visibility = View.GONE

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
        if(type==myTags.requestMode){
            seller.text = "Buyer Details : \n" + currentItem.username
            call.text = "Call Buyer"
        }else{
            seller.text = "Seller Details : \n" + currentItem.username
            call.text = "Call Seller"
        }
        dateTime.text = "Date : " + currentItem.date + " | Time : " + currentItem.time
        description.text = currentItem.description
        category.text = currentItem.category

        Picasso.get().load(currentItem.photo).transform(CustomMaskTransformation(context, R.drawable.grey_background)).into(image)

        adRate.text = "(" + (currentItem.rate.toFloat()*5).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat() + ")"
        adRateBar.rating = (currentItem.rate.toFloat()*5).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()
        userRateBar.rating = currentItem.rating.toFloat().toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()

        if(currentItem.isReviewDone=="1"){
            userRate.text = "(" + currentItem.rating.toFloat().toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat() + ")" + "\nReview Rate : " + currentItem.reviewRate
        }else{
            userRate.text = "(" + currentItem.rating.toFloat().toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat() + ")"
        }


        val dataList = ArrayList<ItemModel>()
        val adapter = RecyclerViewAdapter(dataList, myTags.userViewMode, context, FirebaseAuth.getInstance().currentUser?.uid.toString())
        dialogRecyclerView.adapter = adapter
        val ll= LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
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

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun submitRate(rate : Float, request: ItemRequestModel,holder : RecycleViewItemViewHolder){
        progressDialog.start("Loading...")
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
            }else{
                Toast.makeText(context,"Not Updated! from Other Side. Try Again", Toast.LENGTH_SHORT).show()
            }
        }
        val newRate = ((rate+(request.rating.toFloat()*(request.rateCount-1)))/(request.rateCount)).toString()
        val map2=HashMap<String,Any>()
        map2[myTags.userRating] = newRate
        map2[myTags.userRateCount] = request.rateCount
        firestore.collection(myTags.users).document(request.idealizeUserID).update(map2).addOnCompleteListener {
                result ->
            if(result.isSuccessful){
                Toast.makeText(context,"Updated! from Seller side", Toast.LENGTH_SHORT).show()
                request.isReviewDone = "1"
                request.reviewRate=rate.toString()
                holder.itemLl.setBackgroundResource(R.color.colorSecondary)
                holder.itemReteReview.visibility =View.GONE
                holder.itemDelete.visibility = View.VISIBLE
                this.notifyDataSetChanged()
            }else{
                Toast.makeText(context,"Not Updated! from Seller Side. Try Again", Toast.LENGTH_SHORT).show()
            }
            progressDialog.stop()
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun sendRequestToReview(request: ItemRequestModel, holder : RecycleViewItemViewHolder){
        progressDialog.start("Loading...")
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
            progressDialog.stop()
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteRequestOnlyFromMySide(request : ItemRequestModel, context : Context){
        progressDialog.start("Loading...")
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
            progressDialog.stop()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteRequest(request : ItemRequestModel, context : Context){
        progressDialog.start("Loading...")
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
            progressDialog.stop()
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun deleteRequestFromHistory(request: ItemRequestModel, context: Context){
        progressDialog.start("Loading...")
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
            progressDialog.stop()
        }
    }
}