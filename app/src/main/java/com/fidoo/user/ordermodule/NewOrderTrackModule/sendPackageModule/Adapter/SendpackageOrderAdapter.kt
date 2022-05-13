package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.Message
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.sendPackageModel
import kotlinx.android.synthetic.main.orders_item_list.view.*
import kotlinx.android.synthetic.main.sendpackageitem.view.*

class sendpackageOrderAdapter (
    var con: Context,var arraylist: List<Message>) : RecyclerView.Adapter<sendpackageOrderAdapter.UserViewHolder>() {

        class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sendpackageitem, parent, false)
        )

        override fun getItemCount()  = arraylist.size

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val index = arraylist[position]

            try {
                holder.itemView.textView7.text=index.message
                holder.itemView.textView8.text=index.desc

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }