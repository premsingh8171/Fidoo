package com.fidoo.user.dashboard.adapter.newadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dashboard.model.newmodel.Offer
import kotlinx.android.synthetic.main.item_top_offer_cat.view.*


class TopOfferAdapter(
    val con: Context, val
    serviceList: MutableList<Offer>,
    var itemClick: ItemClickShop,
    var width: Int
) :
    RecyclerView.Adapter<TopOfferAdapter.UserViewHolder>() {
     var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_top_offer_cat, parent, false)
    )

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var retStr = serviceList[position].store_name.toLowerCase().substring(0, 1)
            .toUpperCase() + serviceList[position].store_name.toLowerCase().substring(1)
        holder.itemView.itemNameTopOfferTxt.text=retStr
        holder.itemView.topOfferTxtTime.text=serviceList[position].delivery_time+" mins"
        holder.itemView.offer_discount_txt.text=serviceList[position].coupon_desc

        Glide.with(con)
            .load(serviceList[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.offer_catImg)

        holder.itemView.cardtopOfferView.setOnClickListener {
            itemClick.onItemClick(position,serviceList[position])
        }

    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: Offer)
    }
}