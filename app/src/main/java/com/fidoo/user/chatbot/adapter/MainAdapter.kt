package com.fidoo.user.chatbot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.fidoo.user.R


class MainAdapter(val context: Context, var datalist:List<String>) : RecyclerView.Adapter<botViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): botViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.botitemlayout,parent,false)
        return botViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: botViewHolder, position: Int) {

        //for animation
        val model = datalist[position]
            holder.setdata(model)



    }


    override fun getItemCount(): Int {
        return  datalist.size
    }

}
// val model = datalist[position]
//        var count = datalist.size
//        holder.itemLayoutBinding.apply {
//            cardbotlayout.visibility = View.GONE
//            CoroutineScope(Dispatchers.Main).launch {
//                delay(position * 1000L)
//                Glide.with(context).asGif().load(R.drawable.typing).into(loadingGifChat)
//                delay(position*600L)
//                tvTitle.text = model
//                loadingGifChat.visibility = View.GONE
//                cardbotlayout.visibility = View.VISIBLE
//            }
//        }
//    }