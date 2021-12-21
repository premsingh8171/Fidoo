package com.fidoo.user.newsearch.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.newsearch.model.SuggestionX
import kotlinx.android.synthetic.main.new_search_item_layout.view.*

class SearchCategoryAdapter(
    var context: Context,
    var list: ArrayList<SuggestionX>,
    var categoryItemClick: CategoryItemClick
) : RecyclerView.Adapter<SearchCategoryAdapter.ViewHolder>() {
    var index: Int? = -1
    var isMore = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.new_search_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.categoryTypeTxt.text = list.get(position)?.name
        holder.itemView.productPriceTxt.text = list.get(position)?.type
        holder.itemView.opening_timeTxt.text = list.get(position)?.opening_time

        Glide.with(context)
            .load(list.get(position)?.image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.categoryImgTxt)

        holder.itemView.categoryTypeConsLl.setOnClickListener {
            if (list.get(position).available.equals("1")) {
                index = position
                categoryItemClick.onItemClick(position, list.get(position))
            }
        }

        if (list.get(position).available.equals("1")) {
            holder.itemView.categoryImgTxt.alpha = 1f
            holder.itemView?.categoryTypeTxt.setTextColor(Color.parseColor("#000000"))
            holder.itemView.categoryTypeTxt.alpha = 1f
            holder.itemView?.productPriceTxt.setTextColor(Color.parseColor("#359c47"))
            holder.itemView.productPriceTxt.alpha = 1f
        } else {
            holder.itemView.categoryImgTxt.alpha = 0.2f
            holder.itemView?.categoryTypeTxt.setTextColor(Color.parseColor("#818181"))
            holder.itemView.categoryTypeTxt.alpha = 0.3f
            holder.itemView?.productPriceTxt.setTextColor(Color.parseColor("#359c47"))
            holder.itemView.productPriceTxt.alpha = 0.3f
        }

        if (list.size - 1 == position) {
            holder.itemView.progressRl.visibility = View.VISIBLE

            if (isMore) {
                holder.itemView.progressRl.visibility = View.VISIBLE
            } else {
                holder.itemView.progressRl.visibility = View.GONE
            }

        } else {
            holder.itemView.progressRl.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface CategoryItemClick {
        fun onItemClick(pos: Int, model: SuggestionX)
    }

    fun updateData(listData_: ArrayList<SuggestionX>, isMore1: Boolean) {
        list = java.util.ArrayList<SuggestionX>()
        list.addAll(listData_)
        isMore = isMore1
        notifyDataSetChanged()
    }
}