package com.fidoo.user.newsearch.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.newsearch.model.Product
import com.fidoo.user.newsearch.model.Store
import kotlinx.android.synthetic.main.item_search_parent.view.*

class SearchCategoryStoreAdapter(
    var context: Context,
    var list: ArrayList<Store>,
    var type: String,
    var categoryItemClick: CategoryItemClick
) : RecyclerView.Adapter<SearchCategoryStoreAdapter.ViewHolder>() {
    var index: Int? = -1
    var isMore = false
    var searchCategoryStoreChildAdapter: SearchCategoryStoreChildAdapter? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_search_parent, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.itemView.categoryTypeTxt.text = list.get(position)?.store_name
        holder.itemView.distanceTxt.text =
            list.get(position)?.locality + " | " + list.get(position)?.distance + "Km"

        if (!list[position].cuisines.isNullOrEmpty()) {
            holder.itemView.cuisine_typesTxt.text = list[position].cuisines.joinToString(separator = ", ")
        } else {
            holder.itemView.cuisine_typesTxt.text = ""
        }

        var coupanStr: String? = ""

        try {
            if (list[position].couponsAvailable.isNotEmpty()) {
                for (i in list[position].couponsAvailable.indices) {
                    holder.itemView.offerTxt.text =
                        list[position].couponsAvailable[i].discount + "% OFF"
                    holder.itemView.offerTxt.visibility = View.VISIBLE
                    holder.itemView.offerLL.visibility = View.VISIBLE
                }
            } else {
                holder.itemView.offerLL.visibility = View.GONE
                holder.itemView.offerTxt.visibility = View.GONE
            }

        } catch (e: Exception) { e.printStackTrace() }


        Glide.with(context)
            .load(list.get(position)?.store_image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.IconImg)

        holder.itemView.categoryTypeConsLl.setOnClickListener {
            categoryItemClick.onItemClick(position, list.get(position))
            index = position
        }

        try {
            searchCategoryStoreChildAdapter =
                SearchCategoryStoreChildAdapter(context, list[position].products as ArrayList, object:SearchCategoryStoreChildAdapter.CategoryProductItemClick{
                    override fun onProductItemClick
                                (pos: Int,
                                model: Product,
                                type: String,
                                count:Int
                    ) {
                        categoryItemClick.onProductItemClick(position, list[position],pos,model,type,count)
                    }

                })
            holder.itemView.searchStoreChildRv.adapter = searchCategoryStoreChildAdapter

        } catch (e: Exception) {
        }

        holder.itemView.timeLableTxt.text = list[position].delivery_time.toString() + " MIN"
        holder.itemView.TypeTxt.text = type
        if (position == 0) {
            holder.itemView.TypeLL.visibility = View.VISIBLE
        } else {
            holder.itemView.TypeLL.visibility = View.GONE
        }

//        if (list.size - 1 == position) {
//            holder.itemView.progressRl.visibility = View.VISIBLE
//            if (isMore) {
//                holder.itemView.progressRl.visibility = View.VISIBLE
//            } else {
//                holder.itemView.progressRl.visibility = View.GONE
//            }
//        } else {
//            holder.itemView.progressRl.visibility = View.GONE
//        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface CategoryItemClick {
        fun onItemClick(pos: Int, model: Store)
        fun onProductItemClick(
            mainPos: Int,
            modelStore: Store,
            pos: Int,
            model: Product,
            type: String,
            count:Int
        )
    }

    fun updateData(listData_: ArrayList<Store>, isMore1: Boolean) {
        list = java.util.ArrayList<Store>()
        list.addAll(listData_)
        isMore = isMore1
        notifyDataSetChanged()
    }
}