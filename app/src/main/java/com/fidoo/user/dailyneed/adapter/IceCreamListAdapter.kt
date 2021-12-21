package com.fidoo.user.dailyneed.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dailyneed.model.Subcategory
import kotlinx.android.synthetic.main.item_ice_cream_ui.view.*


class IceCreamListAdapter(
    val con: Context, val
    subcategory: MutableList<Subcategory>,
    var itemClick: ItemClickShop,
    var width: Int
) :
    RecyclerView.Adapter<IceCreamListAdapter.UserViewHolder>() {
    var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_ice_cream_ui, parent, false)
    )

    override fun getItemCount() = subcategory.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


        holder.itemView.text_iceCream.text=subcategory[position].name

        Glide.with(con)
            .load(subcategory[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.ice_itemImg)

        holder.itemView.iceItemClickll.setOnClickListener {
            itemClick.onItemClick(position, subcategory[position])
        }

        if (subcategory.size - 1 == position) {
            holder.itemView.itemIceSpace_ll.visibility = View.VISIBLE
        } else {
            holder.itemView.itemIceSpace_ll.visibility = View.GONE
        }

    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: Subcategory)
    }
}