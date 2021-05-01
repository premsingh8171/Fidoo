package com.fidoo.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.data.model.CustomizeProductResponseModel
import com.fidoo.user.interfaces.CustomCartAddRemoveClick
import kotlinx.android.synthetic.main.custom_sub_items_adapter.view.*


class StoreSubCustomItemsAdapter(
    val con: Context,
    val maxSelectionCount: String,
    val subCat: MutableList<CustomizeProductResponseModel.SubCat>,
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

        var tempSelectionCount = 0

        Log.e("dd", subCat[position].subCatName)
        holder.itemLabel.setOnClickListener {
            if(holder.itemLabel.isChecked)
            {
                tempSelectionCount++
                if (tempSelectionCount==1){
                    tempSelectionCount++
                }

                Toast.makeText(con, tempSelectionCount.toString(), Toast.LENGTH_SHORT).show()

                //!holder.itemLabel.isChecked
                customCartAddRemoveClick.onIdSelected(subCat[position].id,"select",
                    subCat[position].price, tempSelectionCount)
            }
            else
            {

                tempSelectionCount--
                Toast.makeText(con, tempSelectionCount.toString(), Toast.LENGTH_SHORT).show()
                customCartAddRemoveClick.onIdSelected(subCat[position].id,"unselect",
                    subCat[position].price, tempSelectionCount)
            }
            if (tempSelectionCount == maxSelectionCount.toInt()){
                holder.itemLabel.isClickable = false

            }else{

            }
        }
        holder.itemLabel.text= subCat[position].subCatName+" ("+con.resources.getString(R.string.ruppee)+ subCat[position].price+")"
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //  var offerPrice = view.offerPrice
        var itemLabel = view.itemLabel
    }
}