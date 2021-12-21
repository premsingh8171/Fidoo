package com.fidoo.user.addressmodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import kotlinx.android.synthetic.main.address_search_item.view.*

class AddressSearchAdapter(var context: Context,
                           var list: ArrayList<String>,
                           var addSearchClick:AddSearchClick
                          ): RecyclerView.Adapter<AddressSearchAdapter.ViewHolder>() {

   // var index:Int?=-1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.address_search_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.add_search_txt.text = list.get(position)?.toString()

        holder.itemView.select_add_ll.setOnClickListener {
            addSearchClick.onSelectAdd(position,list.get(position))
          //  index=position
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface AddSearchClick {
        fun onSelectAdd(pos: Int,value:String)
    }


}