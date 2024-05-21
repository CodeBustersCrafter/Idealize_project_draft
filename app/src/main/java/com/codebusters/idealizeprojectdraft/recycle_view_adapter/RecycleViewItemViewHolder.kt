package com.codebusters.idealizeprojectdraft.recycle_view_adapter

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codebusters.idealizeprojectdraft.R

class RecycleViewItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val itemImage: ImageView = itemView.findViewById(R.id.image_view_item_view)
    val itemName: TextView = itemView.findViewById(R.id.text_view_item_name)
    val itemPrice: TextView = itemView.findViewById(R.id.text_view_item_price)
    val itemRating: TextView = itemView.findViewById(R.id.text_view_item_rating)
    val itemLl: LinearLayout = itemView.findViewById(R.id.ll_item_view)

    val itemDelete: ImageButton = itemView.findViewById(R.id.btn_delete_item_view)
    val itemVisible: ImageButton = itemView.findViewById(R.id.btn_avilable_item_view)
    val itemBooking: ImageButton = itemView.findViewById(R.id.btn_booking_item_view)
    val itemReteReview: ImageButton = itemView.findViewById(R.id.btn_rate_accept_item_view)
    val itemRequestRate: ImageButton = itemView.findViewById(R.id.btn_rate_request_item_view)


}