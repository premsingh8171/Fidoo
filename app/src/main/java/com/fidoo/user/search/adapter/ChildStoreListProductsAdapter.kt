package com.fidoo.user.search.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.data.model.SearchModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.search.model.ProductsList
import com.fidoo.user.search.model.SearchListModel
import kotlinx.android.synthetic.main.child_storelist_products_item_lay.view.*
import kotlinx.android.synthetic.main.grocery_item_layout.view.*

class ChildStoreListProductsAdapter(
        var context: Context,
        var list:ArrayList<SearchModel.ProductList>,
        private val adapterClick: AdapterClick,
        private var storeID : String,
        var adapterAddRemoveClick: AdapterAddRemoveClick,
        var adapterCartAddRemoveClick: AdapterCartAddRemoveClick
): RecyclerView.Adapter<ChildStoreListProductsAdapter.ViewHolder>()  {
    var count:Int=0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.child_storelist_products_item_lay, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
                .load(list.get(position).productImage)
                .fitCenter()
                .placeholder(R.drawable.about_icon)
                .error(R.drawable.about_icon)
                .into(holder.itemView.search_item_img)

        holder.itemView.search_item_tv.text=list[position]?.productName?.toString()
        holder.itemView.search_qua_txt.text=list[position]?.cartQuantity?.toString()
        holder.itemView.search_tv_unit.text=   list[position]?.weight?.toString() +  list[position]?.unit?.toString()

        holder.itemView.search_tv_price.text = "₹" + list.get(position).price

        if (list[position].cartQuantity == 0) {
            holder.itemView.search_add_itemll.visibility = View.VISIBLE
            holder.itemView.search_minusplus_ll.visibility = View.GONE

        } else {
            holder.itemView.search_add_itemll.visibility = View.GONE
            holder.itemView.search_minusplus_ll.visibility = View.VISIBLE
        }

        //for add item
        holder.itemView.search_add_itemll.setOnClickListener {
            storeID=list[position].storeId
            if (SessionTwiclo(context).isLoggedIn){
                if (list[position].isCustomize.equals("1")) {
                    adapterClick.onItemClick(
                            list[position].productId,
                            "custom",
                            "1",
                            list[position].offerPrice,
                            list[position].isCustomizeQuantity,
                            "",
                            ""
                    )
                } else {
                    count++
                    holder.itemView.search_qua_txt.text = count.toString()
                    holder.itemView.search_add_itemll.visibility = View.GONE
                    holder.itemView.search_minusplus_ll.visibility = View.VISIBLE

                    if (SessionTwiclo(context).storeId.equals(storeID) || SessionTwiclo(context).storeId.equals("")) {
                        adapterAddRemoveClick.onItemAddRemoveClick(
                                list[position].productId, count.toString(), "add",
                                list[position].offerPrice,list[position].storeId,
                                "",0
                        )
                    }else{
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Replace cart item!")
                        builder.setMessage("Do you want to discard the previous selection?")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                        //performing positive action
                        builder.setPositiveButton("Yes") { _, _ ->
                            adapterAddRemoveClick.clearCart()
                            adapterAddRemoveClick.onItemAddRemoveClick(
                                    list[position].productId, count.toString(), "add",
                                    list[position].offerPrice, list[position].storeId, "",0)

                        }

                        //performing negative action
                        builder.setNegativeButton("No") { _, _ ->
                            count = 0
                            holder.itemView.search_qua_txt.text = count.toString()
                            holder.itemView.search_add_itemll.visibility = View.VISIBLE
                            holder.itemView.search_minusplus_ll.visibility = View.GONE
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                }
            }else{
                showLoginDialog("Please login to proceed")
            }
        }

        //for plus item
         holder.itemView.search_add_img.setOnClickListener {
             var count: Int = holder.itemView.search_qua_txt.text.toString().toInt()

             if (list[position]?.isCustomize.equals("1")) {
                 var items: String? = ""
                 if (list[position]?.customizeItem != null) {

                     if (list[position]?.customizeItem.size != 0) {
                         var tempp: Double? = 0.0
                         for (i in 0 until list[position]?.customizeItem.size) {
                             items = list[position]?.customizeItem[i].subCatName + ", " + items
                             tempp = tempp!! + list[position]?.customizeItem.get(i).price.toDouble()

                         }
                         tempp = tempp!! + list[position]?.offerPrice.toDouble()

                         items = items!!.substring(0, items.length - 2)
                     } else {

                     }
                 }


                 if (list[position]?.customizeItem != null) {
                     count++

                     if (list[position]?.customizeItem.size != 0) {
                         adapterCartAddRemoveClick.onAddItemClick(
                                 list[position]?.productId,
                                 items,
                                 list[position]?.offerPrice,
                                 list[position]?.isCustomize,
                                 list[position]?.customizeItem[position].productCustomizeId,
                                ""
                         )
                     } else {
                         adapterCartAddRemoveClick.onAddItemClick(
                                 list[position]?.productId,
                                 items,
                                 list[position]?.offerPrice,
                                 list[position]?.isCustomize,
                                 "",
                                ""
                         )
                     }
                 }

             }
             else {
                 count++
                 holder.itemView.search_qua_txt.text = count.toString()
                 adapterAddRemoveClick.onItemAddRemoveClick(
                         list[position]?.productId,
                         count.toString(),
                         "add",
                         list[position]?.offerPrice, list[position]?.storeId,
                         "",0
                 )
             }
        }

        //for minus item
        holder.itemView.search_subt_img.setOnClickListener {
            var count: Int = holder.itemView.search_qua_txt.text.toString().toInt()

            if (list[position].isCustomize.equals("1")) {
                if (holder.itemView.search_qua_txt.text.toString().toInt() > 0) {
                    count--
                    holder.itemView.search_qua_txt.text = count.toString()

                    if (list[position].customizeItem != null) {

                        if (list[position].customizeItem.size != 0) {
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                    list[position].productId,
                                    count.toString(),
                                    list[position].isCustomize,
                                    list[position].customizeItem[0].productCustomizeId,
                                   ""
                            )
                        } else {
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                    list[position].productId,
                                    count.toString(),
                                    list[position].isCustomize,
                                    "",
                                    ""
                            )
                        }
                    }
                }

            } else {

                if (count > 0) {
                    count--
                    holder.itemView.search_qua_txt.text = count.toString()
                    if (count == 0) {
                        holder.itemView.search_add_itemll.visibility = View.VISIBLE
                        holder.itemView.search_minusplus_ll.visibility = View.GONE
                        adapterAddRemoveClick.onItemAddRemoveClick(
                                list[position]?.productId,
                                count.toString(),
                                "remove",
                                list[position]?.offerPrice,
                                list[position]?.storeId,
                               "",0
                        )
                    } else {
                        adapterAddRemoveClick.onItemAddRemoveClick(
                                list[position]?.productId,
                                count.toString(),
                                "remove",
                                list[position]?.offerPrice,
                                list[position]?.storeId,
                                "",0
                        )
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
      return list.size
    }

    private fun showLoginDialog(message: String){
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { dialogInterface, which ->
            context.startActivity(Intent(context, LoginActivity::class.java))


        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

}