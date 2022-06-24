package com.fidoo.user.newsearch.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.newsearch.model.Product
import com.fidoo.user.newsearch.model.Store
import kotlinx.android.synthetic.main.item_search_child.view.*

class SearchCategoryStoreChildAdapter(
    var context: Context,
    var list: ArrayList<Product>,
    var click: CategoryProductItemClick
) : RecyclerView.Adapter<SearchCategoryStoreChildAdapter.ViewHolder>() {
    var index: Int? = -1
    var isMore = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_search_child, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.categoryTypeTxtChild.text = list.get(position).product_name
        holder.itemView.productPriceTxt.text = "₹ " + list.get(position).offer_price
        holder.itemView.add_item_lay_child.setOnClickListener {
            click.onProductItemClick(position, list[position], "add", 1)
        }

        if (list[position].cart_quantity != "") {
            holder.itemView.add_item_lay_child.visibility = View.GONE
            Log.d("test_quantity", "${list[position].cart_quantity}, ${list[position].product_id}")
            holder.itemView.tv_count.text = list[position].cart_quantity
            holder.itemView.add_remove_lay.visibility = View.VISIBLE
        }



        Glide.with(context)
            .load(list.get(position).product_image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.categoryImgTxtChild)


        try {
            if (list.get(position).is_nonveg.equals("0")) {
                holder.itemView.vegOrNonVeg.setImageResource(R.drawable.veg)
                holder.itemView.vegOrNonVeg.visibility = View.VISIBLE
            } else if (list.get(position).is_nonveg.equals("1")) {
                if (list[position].contains_egg.equals("1")) {
                    holder.itemView.vegOrNonVeg.setImageResource(R.drawable.egg_icon)
                    holder.itemView.vegOrNonVeg.visibility = View.VISIBLE
                } else {
                    holder.itemView.vegOrNonVeg.setImageResource(R.drawable.non_veg)
                    holder.itemView.vegOrNonVeg.visibility = View.VISIBLE
                }
            } else {
                holder.itemView.vegOrNonVeg.visibility = View.INVISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


//        holder.itemView.categoryTypeConsLl.setOnClickListener {
//            categoryItemClick.onItemClick(position, list.get(position))
//            index = position
//        }

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

        holder.itemView.btn_plus.setOnClickListener {
            click.onAddProductItemClick(position, list[position], "add", list[position].cart_quantity.toInt())
        }

        holder.itemView.btn_minus.setOnClickListener {
            click.onRemoveProductItemClick(position, list[position], "remove", list[position].cart_quantity.toInt())
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface CategoryProductItemClick {
        fun onProductItemClick(
            pos: Int,
           model: Product,
           type: String,
           count:Int
        )

        fun onAddProductItemClick(
            pos: Int,
            model: Product,
            type: String,
            count: Int
        )

        fun onRemoveProductItemClick(
            pos: Int,
            model: Product,
            type: String,
            count: Int
        )
    }

//    fun updateData(listData_: ArrayList<Store>, isMore1: Boolean) {
//        list = java.util.ArrayList<Store>()
//        list.addAll(listData_)
//        isMore = isMore1
//        notifyDataSetChanged()
//    }

}