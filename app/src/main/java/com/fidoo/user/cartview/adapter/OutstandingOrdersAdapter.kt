package com.fidoo.user.cartview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.cartview.model.CartModel

class OutstandingOrdersAdapter(
    val context: Context,
    val outstandingOrderItems: MutableList<CartModel.BalanceOrder.BalanceItem>,
    val ruppee: String
): RecyclerView.Adapter<OutstandingOrdersAdapter.OutstandingOrdersViewHolder>() {

    class OutstandingOrdersViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val balance_itemDetail = view.findViewById<TextView>(R.id.detail_balance_order_item)
        val balance_itemPrice = view.findViewById<TextView>(R.id.price_balance_order_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OutstandingOrdersViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.outstanding_order_item, parent, false)
    )

    override fun onBindViewHolder(holder: OutstandingOrdersViewHolder, position: Int) {
        holder.balance_itemDetail.text = (position+1).toString()+". " + outstandingOrderItems[position].product_name +" x "+outstandingOrderItems[position].quantity
        holder.balance_itemPrice.text = ruppee + (outstandingOrderItems[position].price.toDouble()*outstandingOrderItems[position].quantity.toDouble())
            .toString()
    }

    override fun getItemCount() = outstandingOrderItems.size

}