package com.fidoo.user.dailyneed.adapter.prdadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dailyneed.model.prdmodel.Data
import kotlinx.android.synthetic.main.item_circular_tab.view.*


class CircularTabAdapter(
    var con: Context,
    var dataList: ArrayList<Data>,
    var itemClick: ItemClickShop,
    var width: Int,
    var selectedTab: Int
) :
    RecyclerView.Adapter<CircularTabAdapter.UserViewHolder>() {
    var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_circular_tab, parent, false)
    )

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


        holder.itemView.cat_itemTxt_.text=dataList[position].subcategory_name

            Glide.with(con)
                .load(dataList[position].image)
                .fitCenter()
                .placeholder(R.drawable.default_item)
                .error(R.drawable.default_item)
                .into(holder.itemView.tab_itemImg)

        holder.itemView.cons_ll_tab.setOnClickListener {
            index = position
            itemClick.onItemClick(position, dataList[position])
            notifyDataSetChanged()
        }

        if (selectedTab == position) {
            holder.itemView.linear_subCat_tab.setBackgroundResource(R.drawable.circular_tab_selection)
        } else {
            holder.itemView.linear_subCat_tab.setBackgroundResource(R.drawable.circular_border)

        }

//        if (subcategory.size - 1 == position) {
//            holder.itemView.itemHotspotSpace_ll.visibility = View.VISIBLE
//        } else {
//            holder.itemView.itemHotspotSpace_ll.visibility = View.GONE
//        }

    }

    fun customNotifyDataChanged(listData_: ArrayList<Data>,selected_Tab: Int) {
        selectedTab=selected_Tab
        dataList = ArrayList<Data>()
        dataList.addAll(listData_)
        notifyDataSetChanged()
    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: Data)
    }

}