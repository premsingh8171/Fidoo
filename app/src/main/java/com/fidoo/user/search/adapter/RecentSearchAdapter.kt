package com.fidoo.user.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import kotlinx.android.synthetic.main.recent_search_item.view.*

class RecentSearchAdapter(var context: Context,
                          var list: ArrayList<String>,
                          var recentSearchItemClick:RecentSearchItemClick
                          ): RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    var index:Int?=-1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_search_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.recent_search_txt.text = list.get(position)?.toString()

        holder.itemView.recentSearch_mainLL.setOnClickListener {
            recentSearchItemClick.onItemClick(position,list.get(position))
            index=position
           // notifyDataSetChanged()
        }

//        if(index==position){
//            holder.itemView.recentSearch_ll.setBackgroundResource(R.drawable.rectangle_shap)
//            holder.itemView.recent_search_txt.setTextColor(Color.parseColor("#ffffff"))
//        }
//        else{
//            holder.itemView.recentSearch_ll.setBackgroundResource(R.drawable.black_rounded_empty)
//            holder.itemView.recent_search_txt.setTextColor(Color.parseColor("#818181"))
//
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface RecentSearchItemClick {

        fun onItemClick(pos: Int,value:String)
    }


}