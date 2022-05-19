package com.fidoo.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.ordermodule.model.OrderDetailsModel
import kotlinx.android.synthetic.main.order_items_adapter.view.*

class ItemsAdapterTrackScreen (val con: Context, val items: MutableList<OrderDetailsModel.Item>) : RecyclerView.Adapter<ItemsAdapterTrackScreen.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.order_items_adaptertrackscreen, parent, false)
    )

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val itemAmount: Double = items[position].price_with_customization.toDouble() * items.get(position).quantity.toString().toInt()
        holder.itemName.text =
            items.get(position).productName + " * " + items.get(position).quantity.toString()
        var itemss: String? = ""
        if (items.get(position).customizeItem.size != 0) {
            for (i in 0..items.get(position).customizeItem.size - 1) {
                itemss = items.get(position).customizeItem.get(i).subCatName + ", " + itemss

            }

            Log.e("itemss", itemss.toString())
            itemss = itemss!!.substring(0, itemss.length - 2)
            holder.customitemName.text = itemss
            holder.customitemName.visibility= View.VISIBLE
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        var grandPrice = view.grandPrice
        var itemName = view.itemName
        var customitemName = view.customitemName
    }
}