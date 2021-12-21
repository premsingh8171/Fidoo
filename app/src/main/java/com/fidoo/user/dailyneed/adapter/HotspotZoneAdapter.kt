package com.fidoo.user.dailyneed.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dailyneed.model.Subcategory
import kotlinx.android.synthetic.main.item_dailyneed_cat.view.*
import kotlinx.android.synthetic.main.item_hotspot_zone_ui.view.*


class HotspotZoneAdapter(
    val con: Context, val
    subcategory: MutableList<Subcategory>,
    var itemClick: ItemClickShop,
    var width: Int
) :
    RecyclerView.Adapter<HotspotZoneAdapter.UserViewHolder>() {
    var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_hotspot_zone_ui, parent, false)
    )

    override fun getItemCount() = subcategory.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            width,
            width
        )
        params.setMargins(0, 15, 0, 25)
        holder.itemView.hotspotItemClickll.layoutParams = params


        Glide.with(con)
            .load(subcategory[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.hotspot_itemImg)

        holder.itemView.hotspotItemClickll.setOnClickListener {
            itemClick.onItemClick(position, subcategory[position])
        }

//        if (subcategory.size - 1 == position) {
//            holder.itemView.itemHotspotSpace_ll.visibility = View.VISIBLE
//        } else {
//            holder.itemView.itemHotspotSpace_ll.visibility = View.GONE
//        }

    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: Subcategory)
    }
}