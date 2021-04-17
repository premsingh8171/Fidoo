package com.fidoo.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.interfaces.CustomCartAddRemoveClick
import kotlinx.android.synthetic.main.custom_sub_items_adapter.view.*


class StoreSubCustomItemsAdapter(
    val con: Context,
    val subCat: MutableList<com.fidoo.user.data.model.CustomizeProductResponseModel.SubCat>,
    val customCartAddRemoveClick: CustomCartAddRemoveClick
) :
    RecyclerView.Adapter<StoreSubCustomItemsAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.custom_sub_items_adapter, parent, false)
    )

    override fun getItemCount() = subCat.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //  holder.offerPrice.setPaintFlags(holder.offerPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        /* holder.addToCartButton.setOnClickListener {
            con.startActivity(Intent(con,SingleProductActivity::class.java))
        }*/

        Log.e("dd",subCat.get(position).subCatName)
        holder.itemLabel.setOnClickListener {
            if(holder.itemLabel.isChecked)
            {

                //!holder.itemLabel.isChecked
                customCartAddRemoveClick.onIdSelected(subCat.get(position).id,"select",subCat.get(position).price)
            }
            else
            {
                customCartAddRemoveClick.onIdSelected(subCat.get(position).id,"unselect",subCat.get(position).price)
            }
        }
        holder.itemLabel.text=subCat.get(position).subCatName+" ("+con.resources.getString(R.string.ruppee)+subCat.get(position).price+")"
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //  var offerPrice = view.offerPrice
        var itemLabel = view.itemLabel
    }
}