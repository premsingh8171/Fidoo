package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.fidoo.user.R
import kotlinx.android.synthetic.main.botitemlayout.view.*


class MainAdapter(val context: Context, var datalist:List<String>) : RecyclerView.Adapter<botViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): botViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.botitemlayout,parent,false)
        return botViewHolder(view)
    }

    override fun onBindViewHolder(holder: botViewHolder, position: Int) {

        //for animation
        if(datalist== null){
            Glide.with(context).asGif().load(R.drawable.typing).into(holder.itemView.imgv1)
        }
        val model=datalist[position]
        holder.setdata(model)

    }

    override fun getItemCount(): Int {
        return  datalist.size
    }

}