package com.fidoo.user.dailyneed.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dailyneed.model.Category
import kotlinx.android.synthetic.main.item_dailyneed_cat.view.*


class BrowseDailyEssentialAdapter(
    val con: Context, val
    categoryList: MutableList<Category>,
    var itemClick: ItemClickShop,
    var width: Int
) :
    RecyclerView.Adapter<BrowseDailyEssentialAdapter.UserViewHolder>() {
    var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_dailyneed_cat, parent, false)
    )

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(0, 10, 10, 10)
        holder.itemView.itemLayout_width.layoutParams = params
        holder.itemView.dailyNeedItem_Txt.text = categoryList[position].category_name

        Glide.with(con)
            .load(categoryList[position].background_image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.bg_img)

        Glide.with(con)
            .load(categoryList[position].category_image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.dailyNeedItem_img)

        holder.itemView.itemLayout_width.setOnClickListener {
            itemClick.onItemClick(position, categoryList[position])
        }

    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: Category)
    }
}