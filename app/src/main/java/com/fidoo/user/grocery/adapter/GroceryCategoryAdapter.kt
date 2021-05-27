package com.fidoo.user.grocery.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.grocery.model.getGroceryProducts.Category
import kotlinx.android.synthetic.main.grocery_cat_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.grocery_sub_tv

class GroceryCategoryAdapter(
    var context: Context,
    var index:Int=-1,
    var list:ArrayList<Category>,
    var categoryItemClick:CategoryItemClick): RecyclerView.Adapter<GroceryCategoryAdapter.ViewHolder>() {
   // var index:Int=-1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_cat_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.cat_tv.text = list.get(position).cat_name
        holder.itemView.category_constL.setOnClickListener {
         //  index=position
            categoryItemClick.onItemClick(position,list.get(position))
            notifyDataSetChanged()
        }
        if (index==position){
            holder.itemView.cat_tv.setTextColor(Color.parseColor("#339347"))
        }else{
            holder.itemView.cat_tv.setTextColor(Color.parseColor("#000000"))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface CategoryItemClick {
        fun onItemClick(pos: Int,grocery:Category)
    }

}