package com.fidoo.user.sendpackages.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.sendpackages.model.Categories
import kotlinx.android.synthetic.main.cat_sendpckages_item_lay.view.*

class SendPackagesCategoryAdapter(
    var context: Context,
    var list:ArrayList<Categories>,
    var categoryItemClick:CategoryItemClick): RecyclerView.Adapter<SendPackagesCategoryAdapter.ViewHolder>() {
    var index:Int=-1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.cat_sendpckages_item_lay, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.cat_sendPackagetv.text = list.get(position).cat_name
        holder.itemView.category_sendPackageConstL.setOnClickListener {
            index=position
            categoryItemClick.onItemClick(position,list.get(position))
            notifyDataSetChanged()
        }
        if (index==position){
            holder.itemView.cat_sendPackagetv.setTextColor(Color.parseColor("#a9a9a9"))
        }else{
            holder.itemView.cat_sendPackagetv.setTextColor(Color.parseColor("#000000"))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface CategoryItemClick {
        fun onItemClick(pos: Int,sendPackage:Categories)
    }

}