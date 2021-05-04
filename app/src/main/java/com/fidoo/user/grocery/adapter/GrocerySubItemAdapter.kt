package com.fidoo.user.grocery.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.grocery.model.getGroceryProducts.Subcategory
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*

class GrocerySubItemAdapter(var context: Context,
                            var list:ArrayList<Subcategory>,
                            var subcategoryItemClick:SubcategoryItemClick): RecyclerView.Adapter<GrocerySubItemAdapter.ViewHolder>() {

    var index:Int?=0
    var valueselected = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_sub_cat_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.grocery_sub_tv.text = list.get(position)?.subcategory_name.toString()

       // index=valueselected
        holder.itemView.grocery_sub_cons.setOnClickListener {
            index=position
            subcategoryItemClick.onItemClick(position,list.get(position))
            notifyDataSetChanged()
        }
        if(index==position){
            holder.itemView.active_dotLL.visibility=View.VISIBLE
        }else{
            holder.itemView.active_dotLL.visibility=View.GONE

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface SubcategoryItemClick {
        fun onItemClick(pos: Int,grocery:Subcategory)
    }

    fun updateReceiptsList(value: Int) {
        valueselected = value
        Log.d("valueselected____", java.lang.String.valueOf(valueselected))
        notifyDataSetChanged()
    }
}