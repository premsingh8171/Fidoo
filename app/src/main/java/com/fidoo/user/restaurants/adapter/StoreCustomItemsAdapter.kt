package com.fidoo.user.restaurants.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.restaurants.model.CustomListModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.restaurants.listener.CustomCartPlusMinusClick
import kotlinx.android.synthetic.main.store_customization_adapter.view.*


class StoreCustomItemsAdapter(
    val con: Context,
    val category: MutableList<CustomizeProductResponseModel.Category>,
    val customCartPlusMinusClick: CustomCartPlusMinusClick,
    val categoryy: ArrayList<CustomListModel>?,
    val adapterCustomRadioClick: AdapterCustomRadioClick
) :
    RecyclerView.Adapter<StoreCustomItemsAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.store_customization_adapter, parent, false)
    )

    override fun getItemCount() = category.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       // Log.d("dsdsddsds___",category.get(position).subCat.size.toString())
        if(category[position].isMandatory.equals("0")) {
            holder.catName.text = category[position].catName
        } else{
            holder.catName.text = category[position].catName+"*"
        }

        if (category[position].isMultiple.equals("0")) {

            if (category[position].isMandatory.equals("0")){
                holder.descriptionTxt.text="Please select options"
                val adapter = StoreSubCustomItemsAdapter(
                    con,
                    category[position].maxSelectionCount,
                    category[position].subCat,
                    customCartPlusMinusClick,"CheckBox",object :AdapterCustomRadioClick{
                        override fun onCustomRadioClick(checkedId: String?, position: String?) {
                        }
                    }
                )
                holder.subItemsRecyclerview.layoutManager = LinearLayoutManager(con)
                holder.subItemsRecyclerview.setHasFixedSize(true)
                holder.subItemsRecyclerview.adapter = adapter

            }else {
                holder.descriptionTxt.text = "Please select any one option"
                val adapter = StoreSubCustomItemsAdapter(
                    con,
                    category[position].maxSelectionCount,
                    category[position].subCat,

                    customCartPlusMinusClick, "RadioGroup",
                    object : AdapterCustomRadioClick {
                        override fun onCustomRadioClick(checkedId: String?, position: String?) {
                            Log.d("foskgjk", checkedId + "----" + position)
                            adapterCustomRadioClick.onCustomRadioClick(checkedId, position)
                        }
                    }
                )
                holder.subItemsRecyclerview.layoutManager = LinearLayoutManager(con)
                holder.subItemsRecyclerview.setHasFixedSize(true)
                holder.subItemsRecyclerview.adapter = adapter
            }


        } else {
            holder.descriptionTxt.text="Please select options"
            Log.d("edff0", category[position].subCat.size.toString())
            val adapter = StoreSubCustomItemsAdapter(
                con,
                category[position].maxSelectionCount,
                category[position].subCat,
                customCartPlusMinusClick,"CheckBox",object :AdapterCustomRadioClick{
                    override fun onCustomRadioClick(checkedId: String?, position: String?) {
                        // adapterCustomRadioClick.onCustomRadioClick(checkedId,position)
                    }
                }
            )
            holder.subItemsRecyclerview.layoutManager = LinearLayoutManager(con)
            holder.subItemsRecyclerview.setHasFixedSize(true)
            holder.subItemsRecyclerview.adapter = adapter
        }

    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var subItemsRecyclerview = view.subItemsRecyclervieww
        var catName = view.catName
        var descriptionTxt = view.descriptionTxt
        var layoutt = view.layoutt
    }
}