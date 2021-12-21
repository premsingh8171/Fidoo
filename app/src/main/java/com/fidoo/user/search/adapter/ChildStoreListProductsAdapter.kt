package com.fidoo.user.search.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.data.model.SearchModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.search.ui.SearchFragment2.Companion.for_click_storeID
import kotlinx.android.synthetic.main.child_storelist_products_item_lay.view.*

class ChildStoreListProductsAdapter(
    var context: Context,
    var list: ArrayList<SearchModel.ProductList>,
    private val adapterClick: AdapterClick,
    private var storeID: String,
    var adapterAddRemoveClick: AdapterAddRemoveClick,
    var adapterCartAddRemoveClick: AdapterCartAddRemoveClick
) : RecyclerView.Adapter<ChildStoreListProductsAdapter.ViewHolder>() {
    var count: Int = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.child_storelist_products_item_lay, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(list.get(position).productImage)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.search_item_img)

        holder.itemView.search_item_tv.text = list[position]?.productName?.toString()
        holder.itemView.search_qua_txt.text = list[position]?.cartQuantity?.toString()
        holder.itemView.search_tv_unit.text =
            list[position]?.weight?.toString() + list[position]?.unit?.toString()

        holder.itemView.search_tv_price.text = "₹" + list.get(position).offerPrice

        if (list[position].cartQuantity == 0) {
            holder.itemView.search_add_itemll.visibility = View.VISIBLE
            holder.itemView.search_minusplus_ll.visibility = View.GONE

        } else {
            holder.itemView.search_add_itemll.visibility = View.GONE
            holder.itemView.search_minusplus_ll.visibility = View.VISIBLE
        }

        //for add item
        holder.itemView.search_add_itemll.setOnClickListener {
            storeID = list[position].storeId
            if (SessionTwiclo(context).isLoggedIn) {
                if (list[position].isCustomize.equals("1")) {
                    adapterClick.onItemClick(
                        list[position].productId,
                        "custom",
                        "1",
                        list[position].offerPrice,
                        list[position].isCustomizeQuantity,
                        "",
                        list[position].cartId
                    )
                    for_click_storeID = storeID
                } else {
                    count++
                    holder.itemView.search_qua_txt.text = count.toString()
                    holder.itemView.search_add_itemll.visibility = View.GONE
                    holder.itemView.search_minusplus_ll.visibility = View.VISIBLE

                    if (SessionTwiclo(context).storeId.equals(storeID) || SessionTwiclo(context).storeId.equals(
                            ""
                        )
                    ) {
                        adapterAddRemoveClick.onItemAddRemoveClick(
                            list[position].productId, count.toString(), "add",
                            list[position].offerPrice, list[position].storeId,
                            list[position].cartId, 0
                        )
                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Replace cart item!")
                        builder.setMessage("Do you want to discard the previous selection?")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                        //performing positive action
                        builder.setPositiveButton("Yes") { _, _ ->
                            adapterAddRemoveClick.clearCart()
                            adapterAddRemoveClick.onItemAddRemoveClick(
                                list[position].productId,
                                count.toString(),
                                "add",
                                list[position].offerPrice,
                                list[position].storeId,
                                list[position].cartId,
                                0
                            )

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
            } else {
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
                        var productCustomizeId_str:String = ""
                        try {
                            productCustomizeId_str =
                                list[position]?.customizeItem[0].productCustomizeId
                        } catch (e: Exception) {

                        }

                        adapterCartAddRemoveClick.onAddItemClick(
                            list[position]?.productId,
                            items,
                            list[position]?.offerPrice,
                            list[position]?.isCustomize,
                            productCustomizeId_str,
                            list[position].cartId
                        )
                    } else {
                        adapterCartAddRemoveClick.onAddItemClick(
                            list[position]?.productId,
                            items,
                            list[position]?.offerPrice,
                            list[position]?.isCustomize,
                            "",
                            list[position].cartId
                        )
                    }

                }

            } else {
                count++
                holder.itemView.search_qua_txt.text = count.toString()
                adapterAddRemoveClick.onItemAddRemoveClick(
                    list[position]?.productId,
                    count.toString(),
                    "add",
                    list[position]?.offerPrice, list[position]?.storeId,
                    list[position].cartId, 0
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
                                list[position].cartId
                            )
                        } else {
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                list[position].productId,
                                count.toString(),
                                list[position].isCustomize,
                                "",
                                list[position].cartId
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
                            list[position].cartId, 0
                        )
                    } else {
                        adapterAddRemoveClick.onItemAddRemoveClick(
                            list[position]?.productId,
                            count.toString(),
                            "remove",
                            list[position]?.offerPrice,
                            list[position]?.storeId,
                            list[position].cartId, 0
                        )
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun showLoginDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Login") { dialogInterface, which ->
            context.startActivity(Intent(context, AuthActivity::class.java))
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

//    private fun update(list_:ArrayList<SearchModel.ProductList>,position:Int?){
//        var model= SearchModel.ProductList()
//            model.productId=list_[position!!].productId
//            model.productName=list_[position!!].productName
//            model.productDesc=list_[position!!].productDesc
//            model.productImage=list_[position!!].productImage
//            model.price=list_[position!!].price
//            model.offerPrice=list_[position!!].offerPrice
//            model.companyName=list_[position!!].companyName
//            model.weight=list_[position!!].weight
//            model.unit=list_[position!!].unit
//            model.categoryName=list_[position!!].categoryName
//            model.storeId=list_[position!!].storeId
//            model.cartId=list_[position!!].cartId
//            model.storeName=list_[position!!].storeName
//            model.storeAddress=list_[position!!].storeAddress
//            model.storeImage=list_[position!!].storeImage
//            model.serviceName=list_[position!!].serviceName
//            model.isCustomize=list_[position!!].isCustomize
//            model.isPrescription=list_[position!!].isPrescription
//            model.isNonveg=list_[position!!].isNonveg
//            model.cartQuantity=count
//            model.isCustomizeQuantity=list_[position!!].isCustomizeQuantity
//            model.rating=list_[position!!].rating
//            model.deliveryTime=list_[position!!].deliveryTime
//            model.customizeItem=list_[position!!].customizeItem
//
//            if (model != null) {
//                list?.set(2!!,model!!)
//               // list.addAll(list)
//                notifyDataSetChanged()
//            }
//
//
//    }

}