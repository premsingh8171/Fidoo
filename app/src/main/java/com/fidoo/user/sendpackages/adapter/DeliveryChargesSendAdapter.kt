package com.fidoo.user.sendpackages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.cartview.model.CartModel
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.restaurants.listener.AdapterCartAddRemoveClick
import com.fidoo.user.restaurants.model.StoreDetailsModel
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.sendpackages.activity.SendPackageOrderDetail
import com.fidoo.user.sendpackages.model.SendPackagesModel
import kotlinx.android.synthetic.main.grocery_cat_header_layout.view.*
import kotlinx.android.synthetic.main.grocery_cat_header_layout.view.cat_tv_header
import kotlinx.android.synthetic.main.item_delivery_charges.view.*

@Suppress("DEPRECATION")
class DeliveryChargesSendAdapter(
    var context: Context,
    var list:ArrayList<SendPackagesModel.DeliveryCharges>
   ): RecyclerView.Adapter<DeliveryChargesSendAdapter.ViewHolder>(){


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_delivery_charges, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.charges_Txt.text = list.get(position).distanceRange
        holder.itemView.charges_PriceTxt.text = context.resources.getString(R.string.ruppee)+list.get(position).deliveryCharges
    }

    override fun getItemCount(): Int {
        return list.size
    }
}