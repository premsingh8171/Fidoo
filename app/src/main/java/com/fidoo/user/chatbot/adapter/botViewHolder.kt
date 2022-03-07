package com.fidoo.user.chatbot.adapter

import android.content.Context
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
    var count = 1
     fun setdata(orderStatusResponse: String) {
        view.apply {
            CoroutineScope(Dispatchers.Main).launch {
                if (botlogocount.botcount >= 1) {
                    cardbotlayout.visibility = View.INVISIBLE
                    botlogolayout.visibility = View.INVISIBLE
                    Glide.with(context).asGif().load(R.drawable.typing).into(imgv1)
                    delay(count * 1000L)
                    cardbotlayout.visibility = View.VISIBLE
                    imgv1.visibility = View.INVISIBLE
                    tvTitle.text = orderStatusResponse
                    botlogo.visibility = View.INVISIBLE
                    botlogolayout.visibility = View.INVISIBLE


                } else {
                    tvTitle.text = orderStatusResponse
                }
                botlogocount.botcount++
               count++
            }



        }

    }
}
