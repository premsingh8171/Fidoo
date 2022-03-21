package com.fidoo.user.chatbot.adapter.cancelOrderWithoutRefundAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R


class CancelWithoutRefundAdapter(val context: Context, var datalist:List<String>) : RecyclerView.Adapter<CancelWithoutRefundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelWithoutRefundViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.botitemlayout,parent,false)
        return CancelWithoutRefundViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: CancelWithoutRefundViewHolder, position: Int) {

        //for animation
        val model = datalist[position]
        holder.setdata(model)

    }


    override fun getItemCount(): Int {
        return  datalist.size
    }

}