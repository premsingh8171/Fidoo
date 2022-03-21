package com.fidoo.user.chatbot.adapter.orderStatusMsgAdapter

import android.os.Build
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.orderStatusMsgAdapter.botlogoCountforreply
import kotlinx.android.synthetic.main.botitemlayout.view.*


class botmsgViewHolder (val view : View) : RecyclerView.ViewHolder(view) {

    fun setdata(OrderStatusMsg : String) {
        view.apply {
            if (botlogoCountforreply.botcount1 >= 1) {
                tvTitle.text = OrderStatusMsg
                //botlogo.visibility = View.INVISIBLE
                //botlogolayout.visibility = View.INVISIBLE
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(OrderStatusMsg, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(OrderStatusMsg)
                }
            } else {
                tvTitle.text = OrderStatusMsg
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(OrderStatusMsg, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(OrderStatusMsg)
                }

            }
        }
        botlogoCountforreply.botcount1++
    }
}
