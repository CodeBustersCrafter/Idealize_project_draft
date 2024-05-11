package com.codebusters.idealizeprojectdraft

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecycleViewItemViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
    val item_image = itemview.findViewById<ImageView>(R.id.image_view_item_view)
    val item_name = itemview.findViewById<TextView>(R.id.text_view_item_name)
    val item_price = itemview.findViewById<TextView>(R.id.text_view_item_price)
    val item_phone = itemview.findViewById<TextView>(R.id.text_view_item_phone)


}