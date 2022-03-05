package com.example.myapplication.adapter.orderStatusMsgAdapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.botlogocount
import kotlinx.android.synthetic.main.botitemlayout.view.*


class botmsgViewHolder (val view : View) : RecyclerView.ViewHolder(view) {

    fun setdata(OrderStatusMsg : String) {
        view.apply {
            if (botlogoCountforreply.botcount1 >= 1) {
                tvTitle.text = OrderStatusMsg
                botlogo.visibility = View.INVISIBLE
            } else {
                tvTitle.text = OrderStatusMsg
            }
        }
        botlogoCountforreply.botcount1++
    }
}
