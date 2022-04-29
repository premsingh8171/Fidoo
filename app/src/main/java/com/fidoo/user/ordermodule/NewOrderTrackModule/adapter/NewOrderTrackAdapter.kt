package com.fidoo.user.ordermodule.ui.NewOrderTrackModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.ordermodule.NewOrderTrackModule.adapter.NewOrderTrackViewHolder
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.Message


class NewOrderTrackAdapter(val context: Context, var msgdatalist:List<Message>) : RecyclerView.Adapter<NewOrderTrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrderTrackViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.newordertrackitem,parent,false)
        return NewOrderTrackViewHolder(view , context)
    }

    override fun onBindViewHolder(holder: NewOrderTrackViewHolder, position: Int) {
        val model=msgdatalist[position]
        holder.setdata(model)
    }

    override fun getItemCount(): Int {
        return  msgdatalist.size
    }

    fun updateData(updateList: Any) {
        msgdatalist = java.util.ArrayList<Message>()

        notifyDataSetChanged()
    }

}