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
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.interfaces.CustomCartAddRemoveClick
import kotlinx.android.synthetic.main.custom_sub_items_adapter.view.*


class StoreSubCustomItemsAdapter(
        val con: Context,
        val maxSelectionCount: String,
        val subCat: MutableList<CustomizeProductResponseModel.SubCat>,
        val customCartAddRemoveClick: CustomCartAddRemoveClick,
        var typeview: String? = "",
        val adapterCustomRadioClick: AdapterCustomRadioClick
) :
    RecyclerView.Adapter<StoreSubCustomItemsAdapter.UserViewHolder>() {

    var clickPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_sub_items_adapter, parent, false)
    )

    override fun getItemCount() = subCat.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //  holder.offerPrice.setPaintFlags(holder.offerPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        /* holder.addToCartButton.setOnClickListener {
            con.startActivity(Intent(con,SingleProductActivity::class.java))
        }*/

        if(typeview.equals("CheckBox")) {
            holder.itemView.mainLayyRadioGroup.visibility=View.GONE
            holder.itemView.mainLayy.visibility=View.VISIBLE
            var tempSelectionCount = 0
            Log.e("dd", subCat[position].subCatName)
            holder.itemLabel.setOnClickListener {
                if (holder.itemLabel.isChecked) {
                    tempSelectionCount++
                    if (tempSelectionCount == 1) {
                        tempSelectionCount++
                    }

                    Toast.makeText(con, tempSelectionCount.toString(), Toast.LENGTH_SHORT).show()

                    //!holder.itemLabel.isChecked
                    customCartAddRemoveClick.onIdSelected(subCat[position].id, "select",
                            subCat[position].price, tempSelectionCount)
                } else {

                    tempSelectionCount--
                    Toast.makeText(con, tempSelectionCount.toString(), Toast.LENGTH_SHORT).show()
                    customCartAddRemoveClick.onIdSelected(subCat[position].id, "unselect",
                            subCat[position].price, tempSelectionCount)
                }
                if (tempSelectionCount == maxSelectionCount.toInt()) {
                    holder.itemLabel.isClickable = false

                } else {

                }
            }
            holder.itemLabel.text = subCat[position].subCatName + " (" + con.resources.getString(R.string.ruppee) + subCat[position].price + ")"
        }else{

            holder.itemView.mainLayyRadioGroup.visibility=View.VISIBLE
            holder.itemView.mainLayy.visibility=View.GONE

            holder.radioBtn_.text = subCat[position].subCatName+" ("+con.resources.getString(R.string.ruppee)+ subCat[position].price+")" ////setting text of second radio button
            holder.radioBtn_.id = subCat[position].id.toInt()


            holder.radioBtn_.setOnClickListener {
                clickPosition=position
                adapterCustomRadioClick.onCustomRadioClick(subCat[position].id.toInt().toString(), position.toString())
                notifyItemRangeChanged(0, subCat.size);
                notifyDataSetChanged()

            }

            if (clickPosition==position){
               holder.radioBtn_.isChecked=true
            }else{
                holder.radioBtn_.isChecked=false
            }
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //  var offerPrice = view.offerPrice
        var itemLabel = view.itemLabel
        var radioBtn_ = view.radioBtn
    }
}