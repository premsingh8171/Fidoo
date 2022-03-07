package com.fidoo.user.chatbot.adapter.orderStatusMsgAdapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.orderStatusMsgAdapter.botlogoCountforreply
import kotlinx.android.synthetic.main.botitemlayout.view.*


class botmsgViewHolder (val view : View) : RecyclerView.ViewHolder(view) {

    fun setdata(OrderStatusMsg : String) {
        view.apply {
            if (botlogoCountforreply.botcount1 >= 1) {
                tvTitle.text = OrderStatusMsg
                botlogo.visibility = View.INVISIBLE
                botlogolayout.visibility = View.INVISIBLE
            } else {
                tvTitle.text = OrderStatusMsg
            }
        }
        botlogoCountforreply.botcount1++
    }
}
