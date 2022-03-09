package com.fidoo.user.chatbot.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.adapter.botlogocount
import com.fidoo.user.R

import kotlinx.android.synthetic.main.botitemlayout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class botViewHolder (var context:Context ,val view : View) : RecyclerView.ViewHolder(view) {
    //var count = 1
     fun setdata(orderStatusResponse: String) {
        view.apply {
            if (botlogocount.botcount >= 1) {
                    tvTitle.text = orderStatusResponse
                    botlogo.visibility = View.GONE
                    botlogolayout.visibility = View.INVISIBLE

                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(orderStatusResponse, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(orderStatusResponse)
                }

                } else {
                    tvTitle.text = orderStatusResponse
                tvTitle.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(orderStatusResponse, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(orderStatusResponse)
                }
                }
                botlogocount.botcount++
              // count++
            }



        }

    }
