package com.fidoo.user.ordermodule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.restaurants.listener.AdapterCartAddRemoveClick
import com.fidoo.user.restaurants.model.StoreDetailsModel
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity
import com.fidoo.user.activity.MainActivity.Companion.tempProductList
import com.fidoo.user.ordermodule.model.MyOrdersModel
import kotlinx.android.synthetic.main.orders_item_list.view.*
import kotlinx.android.synthetic.main.store_product.view.*

class OrderItemAdapter(
    var con: Context,var arraylist: ArrayList<MyOrdersModel.Item>
    ) : RecyclerView.Adapter<OrderItemAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.orders_item_list, parent, false)
    )

    override fun getItemCount()  = arraylist.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val index = arraylist[position]

        try {
             holder.itemView.itemName.text=index.productName
             holder.itemView.itemdiscription.text=index.product_desc

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}