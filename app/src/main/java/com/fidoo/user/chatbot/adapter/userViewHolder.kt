package com.example.myapplication.adapter

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.useritemlayout.view.*

class userViewHolder (val view : View) : RecyclerView.ViewHolder(view) {

    fun setData(intent : Intent){

        view.userResponse.text = intent.getStringExtra("EXTRA_SESSION_ID")

    }
}