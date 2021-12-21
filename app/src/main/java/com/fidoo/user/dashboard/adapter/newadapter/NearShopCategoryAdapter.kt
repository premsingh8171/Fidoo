package com.fidoo.user.dashboard.adapter.newadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dashboard.model.newmodel.ShopCategory
import kotlinx.android.synthetic.main.item_near_shop_cat_ui.view.*


class NearShopCategoryAdapter(
    val con: Context, val
    serviceList: MutableList<ShopCategory>,
    var itemClick: ItemClickShop,
    var width: Int
) :
    RecyclerView.Adapter<NearShopCategoryAdapter.UserViewHolder>() {
     var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_near_shop_cat_ui, parent, false)
    )

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


        Glide.with(con)
            .load(serviceList[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.nearShop_cat_itemImg)

        holder.itemView.shopItemClickll.setOnClickListener {
            itemClick.onItemClick(position,serviceList[position])
        }

        if (serviceList.size-1==position){
            holder.itemView.itemSpace_ll.visibility=View.VISIBLE
        }else{
            holder.itemView.itemSpace_ll.visibility=View.GONE
        }

    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: ShopCategory)
    }
}