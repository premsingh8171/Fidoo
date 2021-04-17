package com.fidoo.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import kotlinx.android.synthetic.main.order_items_adapter.view.*


class ItemsAdapter(val con: Context, val items: MutableList<com.fidoo.user.data.model.OrderDetailsModel.Item>) : RecyclerView.Adapter<ItemsAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.order_items_adapter, parent, false)
    )

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        var itemAmount: Double = items[position].price_with_customization.toDouble() * items.get(position).quantity.toString().toInt()
        holder.grandPrice.text = con.resources.getString(R.string.ruppee) + itemAmount.toString()
        holder.itemName.text =
            items.get(position).productName + " (" + items.get(position).quantity.toString() + " * " + con.resources.getString(
                R.string.ruppee
            ) + items.get(position).offerPrice.toString() + ")"
        var itemss: String? = ""
        if (items.get(position).customizeItem.size != 0) {
            for (i in 0..items.get(position).customizeItem.size - 1) {
                itemss = items.get(position).customizeItem.get(i).subCatName + ", " + itemss

            }

            Log.e("itemss", itemss.toString())
            itemss = itemss!!.substring(0, itemss.length - 2)
            holder.customitemName.text = itemss
            holder.customitemName.visibility=View.VISIBLE
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var grandPrice = view.grandPrice
        var itemName = view.itemName
        var customitemName = view.customitemName
    }
}