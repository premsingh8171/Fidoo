package com.fidoo.user.restaurants.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.constants.useconstants
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.restaurants.listener.CustomCartPlusMinusClick
import kotlinx.android.synthetic.main.custom_sub_items_adapter.view.*


class StoreSubCustomItemsAdapter(
    val con: Context,
    val maxSelectionCount: String,
    val subCat: MutableList<CustomizeProductResponseModel.SubCat>,
    val customCartPlusMinusClick: CustomCartPlusMinusClick,
    var typeview: String? = "",
    val adapterCustomRadioClick: AdapterCustomRadioClick
) :
    RecyclerView.Adapter<StoreSubCustomItemsAdapter.UserViewHolder>() {

    var clickPosition = 0
    var selectionCount = 0
    var boolean:Boolean=false
    var boolean2:Boolean=false

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_sub_items_adapter, parent, false)
    )

    override fun getItemCount() = subCat.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        if(typeview.equals("CheckBox")) {
            holder.itemView.mainLayyRadioGroup.visibility=View.GONE
            holder.itemView.mainLayy.visibility=View.VISIBLE
            var tempSelectionCount = 0
            Log.e("dd___d", subCat[position].subCatName)

            holder.itemLabel.setOnClickListener {
                Log.e("maxSelectionCount__",selectionCount.toString()+"--"+ maxSelectionCount.toString())


                if (maxSelectionCount == "0"){

                    if (holder.itemLabel.isChecked) {
                        customCartPlusMinusClick.onIdSelected(
                            subCat[position].id, "select",
                            subCat[position].price,
                            subCat[position].subCatName,
                            tempSelectionCount
                        )
                    } else {
                        customCartPlusMinusClick.onIdSelected(
                            subCat[position].id, "unselect",
                            subCat[position].price,
                            subCat[position].subCatName,
                            tempSelectionCount
                        )
                    }

                }else if (selectionCount<maxSelectionCount.toInt()){
                    if (holder.itemLabel.isChecked) {
                        selectionCount++
                        tempSelectionCount++
                        if (tempSelectionCount == 1) {
                            tempSelectionCount++
                        }

                        customCartPlusMinusClick.onIdSelected(
                            subCat[position].id, "select",
                            subCat[position].price,
                            subCat[position].subCatName,
                            tempSelectionCount
                        )
                    } else {
                        selectionCount--
                        tempSelectionCount--
                        customCartPlusMinusClick.onIdSelected(
                            subCat[position].id, "unselect",
                            subCat[position].price,
                            subCat[position].subCatName,
                            tempSelectionCount
                        )
                    }
                } else{
                 if (!holder.itemLabel.isChecked){
                        selectionCount--
                        tempSelectionCount--
                        customCartPlusMinusClick.onIdSelected(
                            subCat[position].id, "unselect",
                            subCat[position].price,
                            subCat[position].subCatName,
                            tempSelectionCount
                        )
                    }else{
                        holder.itemLabel.isChecked = false
                     Toast.makeText(con, "You can select only $maxSelectionCount customization at once", Toast.LENGTH_LONG).show()
                 }

                }

//                if (tempSelectionCount == maxSelectionCount.toInt()) {
//                    holder.itemLabel.isClickable = false
//
//                }
            }
            if (subCat[position].price.toString().equals("0")){
                holder.itemLabel.text = subCat[position].subCatName
            }else{
                holder.itemLabel.text = subCat[position].subCatName + " ( + " + con.resources.getString(R.string.ruppee) + subCat[position].price + ")"
            }
        }else{

            holder.itemView.mainLayyRadioGroup.visibility=View.VISIBLE
            holder.itemView.mainLayy.visibility=View.GONE

            if (subCat[position].price.toString().equals("0")){
                holder.radioBtn_.text = subCat[position].subCatName ////setting text of second radio button
            }else {
                if (subCat[position].subCatName.toLowerCase().endsWith("gms")
                    || subCat[position].subCatName.toLowerCase().endsWith("kg")
                    || subCat[position].subCatName.toLowerCase().endsWith("pcs")
                    ||useconstants.offerPrice.equals("0")
                ) {
                    holder.radioBtn_.text =
                        subCat[position].subCatName + " (" + con.resources.getString(R.string.ruppee) + subCat[position].price + ")" ////setting text of second radio button
                } else {
                    holder.radioBtn_.text =
                        subCat[position].subCatName + " ( + " + con.resources.getString(R.string.ruppee) + subCat[position].price + ")" ////setting text of second radio button
                }
            }

            holder.radioBtn_.id = subCat[position].id.toInt()


            holder.radioBtn_.setOnClickListener {
                clickPosition=position
                adapterCustomRadioClick.onCustomRadioClick(subCat[position].id.toInt().toString(), position.toString())
                notifyItemRangeChanged(0, subCat.size)
                notifyDataSetChanged()

            }

            holder.radioBtn_.isChecked = clickPosition==position
        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //  var offerPrice = view.offerPrice
        var itemLabel = view.itemLabel
        var radioBtn_ = view.radioBtn
    }
}