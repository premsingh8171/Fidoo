package com.fidoo.user.chatbot.adapter.cancelOrderWithoutRefundAdapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.botlogocount
import kotlinx.android.synthetic.main.botitemlayout.view.*


class CancelWithoutRefundViewHolder (var context: Context, val view : View) : RecyclerView.ViewHolder(view) {
    fun setdata(cancelWithoutRefundEmpty: String) {
        view.apply {
            if (botlogocount.botrefundCount >= 1) {
                tvTitle.text = cancelWithoutRefundEmpty
                //botlogo.visibility = View.GONE
                //botlogolayout.visibility = View.INVISIBLE
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(cancelWithoutRefundEmpty, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(cancelWithoutRefundEmpty)
                }

            } else {
                tvTitle.text = cancelWithoutRefundEmpty
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(cancelWithoutRefundEmpty, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(cancelWithoutRefundEmpty)
                }

            }
            botlogocount.botrefundCount++
            // count++
        }



    }

}
