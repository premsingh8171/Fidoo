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
import kotlinx.android.synthetic.main.item_ready_to_cook.view.*


class ReadyToCook2Adapter(
    val con: Context, val
    subcategoryList: MutableList<Subcategory>,
    var itemClick: ItemClickShop,
    var width: Int
) :
    RecyclerView.Adapter<ReadyToCook2Adapter.UserViewHolder>() {
     var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_ready_to_cook, parent, false)
    )

    override fun getItemCount() = subcategoryList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            width,
            width
        )
        if (position==0) {
            params.setMargins(15, 15, 20, 30)
        }else{
            params.setMargins(0, 15, 20, 30)
        }
        holder.itemView.readyToCookCardL.layoutParams = params

        Glide.with(con)
            .load(subcategoryList[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.readyToCook_Img)

        holder.itemView.readyToCookCardL.setOnClickListener {
            itemClick.onItemClick(position,subcategoryList[position])
        }

//        if (subcategoryList.size-1==position){
//            holder.itemView.upcoming_service_Space_ll.visibility=View.VISIBLE
//        }else{
//            holder.itemView.upcoming_service_Space_ll.visibility=View.GONE
//        }

    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: Subcategory)
    }
}