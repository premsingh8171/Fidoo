package com.fidoo.user.grocery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.grocery.model.getGroceryProducts.Category
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*

class GroceryCategoryAdapter(var context: Context,
                             var list:ArrayList<Category>,
                             var categoryItemClick:CategoryItemClick): RecyclerView.Adapter<GroceryCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_cat_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.grocery_sub_tv.text = list.get(position)?.cat_name

//        holder.itemView.grocery_sub_cons.setOnClickListener {
//            CategoryItemClick.onItemClick(position,list.get(position))
//          //  notifyItemRemoved(position)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface CategoryItemClick {
        fun onItemClick(pos: Int,grocery:Category)
    }

}