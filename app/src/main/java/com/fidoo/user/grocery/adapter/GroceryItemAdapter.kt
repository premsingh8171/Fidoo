package com.fidoo.user.grocery.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import kotlinx.android.synthetic.main.grocery_item_layout.view.*

class GroceryItemAdapter(var context: Context,
                         var list: ArrayList<Product>,
                         var groceryItemClick:GroceryItemClick): RecyclerView.Adapter<GroceryItemAdapter.ViewHolder>() {

    var count:Int =0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("ffsfsffs",list.get(position)?.product_name)
        holder.itemView.grocery_item_tv.text = list.get(position)?.product_name
        holder.itemView.qua_txt.text = list.get(position)?.cart_quantity.toString()

        Glide.with(context)
                .load(list.get(position)?.image)
                .fitCenter()
                .into(holder.itemView.grocery_item_img)

        holder.itemView.add_itemll.setOnClickListener {
            groceryItemClick.onItemClick(position,list.get(position))
            holder.itemView.add_itemll.visibility=View.GONE
            holder.itemView.minusplus_ll.visibility=View.VISIBLE
            count=1
            notifyItemRemoved(position)
        }

        holder.itemView.subt_img.setOnClickListener {
                count--
            if (count>0) {
                holder.itemView.minusplus_ll.visibility=View.VISIBLE
            }else{
                holder.itemView.minusplus_ll.visibility=View.GONE
                holder.itemView.add_itemll.visibility=View.VISIBLE

            }

            holder.itemView.qua_txt.text = count.toString()
            groceryItemClick.onItemSub(position,count,list.get(position))
        }

        holder.itemView.add_img.setOnClickListener {
            count++

            holder.itemView.qua_txt.text = count.toString()
            groceryItemClick.onItemAdd(position,count,list.get(position))

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface GroceryItemClick {
        fun onItemClick(pos: Int,grocery:Product)

        fun onItemSub(pos: Int,itemcount:Int,grocery:Product)

        fun onItemAdd(pos: Int,itemcount:Int,grocery:Product)
    }

}