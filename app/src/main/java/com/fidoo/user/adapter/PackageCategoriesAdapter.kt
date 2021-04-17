package com.fidoo.user.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterClick
import kotlinx.android.synthetic.main.category_adapter.view.*

class PackageCategoriesAdapter(
    val con: Context, val
    categoriesList: MutableList<com.fidoo.user.data.model.PackageCatResponseModel.CategoriesList>,
    val adapterClick: AdapterClick
) :
    RecyclerView.Adapter<PackageCategoriesAdapter.UserViewHolder>() {
    var tempPos: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.package_category_item_adapter, parent, false)
    )

    override fun getItemCount() = categoriesList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        holder.catName.text = categoriesList.get(position).catName


        holder.catName.setOnClickListener {
            tempPos = position.toString()
            adapterClick.onItemClick(categoriesList[position].id, "", "", "", 0, "", "")
            notifyDataSetChanged()
        }

        if (position.toString() == tempPos) {
            holder.catName.setTextColor(Color.WHITE)
            holder.catName.setBackgroundColor(Color.BLACK)

        } else {
            holder.catName.setBackgroundColor(Color.WHITE)
            holder.catName.setTextColor(Color.BLACK)
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var catName = view.catName
    }
}