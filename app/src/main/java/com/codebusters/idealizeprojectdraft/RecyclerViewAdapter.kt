package com.codebusters.idealizeprojectdraft

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(private val itemList: ArrayList<Item>) :
    RecyclerView.Adapter<RecycleViewItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.itemview,parent,false)
        return RecycleViewItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecycleViewItemViewHolder, position: Int) {
        val current_item = itemList.get(position)
        holder.item_name.setText(current_item.name)
        holder.item_phone.setText(current_item.phone)
        holder.item_price.setText(current_item.price)
        Picasso.get().load(current_item.photo).into(holder.item_image)
    }

}