package com.fidoo.user.grocery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.grocery.model.GroceryItemModel
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*

class GrocerySubItemAdapter(var context: Context,
                            var list:List<GroceryItemModel>,
                            var groceryItemClick:GroceryItemClick): RecyclerView.Adapter<GrocerySubItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_sub_cat_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     //  holder.itemView.grocery_sub_cons.text = list.get(position)?.bookingDetail?.id.toString()
//
//        holder.grocery_sub_cons.add_itemll.setOnClickListener {
//            // myItemClickListener.deleteItemOnClick(position)
//            notifyItemRemoved(position)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface GroceryItemClick {
        fun onItemClick(pos: Int,grocery:GroceryItemModel)
    }

}