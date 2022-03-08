package com.fidoo.user.chatbot.adapter.BtnYesNoAdapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.botlogocount
import kotlinx.android.synthetic.main.botitemlayout.view.*

class BtnYesNoViewHolder (var context: Context, val view : View) : RecyclerView.ViewHolder(view) {
    fun setdata(cancelWithoutRefundWithSendkey: String) {
        view.apply {
            if (botlogocount.botYesNoCount >= 1) {
                tvTitle.text = cancelWithoutRefundWithSendkey
                botlogo.visibility = View.GONE
                botlogolayout.visibility = View.INVISIBLE

            } else {
                tvTitle.text = cancelWithoutRefundWithSendkey
            }
            botlogocount.botYesNoCount++
            // count++
        }



    }

}
