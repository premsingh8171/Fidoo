package com.fidoo.user.dailyneed.adapter.prdadapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.dailyneed.model.prdmodel.Data
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import kotlinx.android.synthetic.main.item_hotspot_zone_ui.view.*
import kotlinx.android.synthetic.main.item_tab_layout_for_cat.view.*


class TabAdapter(
    val con: Context, var
    dataList: ArrayList<Data>,
    var itemClick: ItemClickShop,
    var width: Int,
    var selectedTab: Int

) :
    RecyclerView.Adapter<TabAdapter.UserViewHolder>() {
    var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_tab_layout_for_cat, parent, false)
    )

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.itemView.subCat_name_.text = dataList[position].subcategory_name

        holder.itemView.subCat_name_.setOnClickListener {
           // index = position
            itemClick.onItemClick(position, dataList[position])
            notifyDataSetChanged()
        }

        if (selectedTab == position) {
            holder.itemView.selectedTabll.setBackgroundResource(R.drawable.top_corner_radius)
            holder.itemView.selectedTabll.background.setTint(Color.parseColor("#33E4BE"))
        } else {
            holder.itemView.selectedTabll.setBackgroundResource(R.color.white)
        }

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