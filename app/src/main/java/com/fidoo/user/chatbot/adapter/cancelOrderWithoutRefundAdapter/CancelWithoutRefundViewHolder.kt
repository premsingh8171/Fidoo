package com.fidoo.user.chatbot.adapter.cancelOrderWithoutRefundAdapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.botlogocount
import kotlinx.android.synthetic.main.botitemlayout.view.*


class CancelWithoutRefundViewHolder (var context: Context, val view : View) : RecyclerView.ViewHolder(view) {
    fun setdata(cancelWithoutRefundEmpty: String) {
        view.apply {
            if (botlogocount.botrefundCount >= 1) {
                tvTitle.text = cancelWithoutRefundEmpty
                botlogo.visibility = View.GONE
                botlogolayout.visibility = View.INVISIBLE

            } else {
                tvTitle.text = cancelWithoutRefundEmpty
            }
            botlogocount.botrefundCount++
            // count++
        }



    }

}
