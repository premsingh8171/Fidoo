package com.fidoo.user.chatbot.adapter.FeedbackAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.chatbot.adapter.cancelOrderWithoutRefundAdapter.CancelWithoutRefundViewHolder


class BotFeedbackAdapter(val context: Context, var datalist:List<String>) : RecyclerView.Adapter<BotFeedbackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BotFeedbackViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.botitemlayout,parent,false)
        return BotFeedbackViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: BotFeedbackViewHolder, position: Int) {

        //for animation
        val model = datalist[position]
        holder.setdata(model)

    }


    override fun getItemCount(): Int {
        return  datalist.size
    }

}