package com.fidoo.user.chatbot.adapter.BtnYesNoAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.chatbot.adapter.cancelOrderWithoutRefundAdapter.CancelWithoutRefundViewHolder

class BtnYesNoAdapter(val context: Context, var datalist:List<String>) : RecyclerView.Adapter<BtnYesNoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtnYesNoViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.botitemlayout,parent,false)
        return BtnYesNoViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: BtnYesNoViewHolder, position: Int) {

        //for animation
        val model = datalist[position]
        holder.setdata(model)

    }


    override fun getItemCount(): Int {
        return  datalist.size
    }

}