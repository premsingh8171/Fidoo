package com.fidoo.user.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.ui.MainActivity.Companion.tempProductList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.store_product.view.*

class StoreItemsAdapter(
    val con: Context,
    private val adapterClick: AdapterClick,
    private val productList: ArrayList<StoreDetailsModel.Product>,
    val catList: ArrayList<StoreDetailsModel.Category>,
    var weight: String,
    var unit: String,
    var storerating: String,
    var service_id: String,
    var adapterAddRemoveClick: AdapterAddRemoveClick,
    private val adapterCartAddRemoveClick: AdapterCartAddRemoveClick,
    val id: Int,
    private val storeID: String,
    private val cartId: String

) : RecyclerView.Adapter<StoreItemsAdapter.UserViewHolder>() {

    var arraylist: ArrayList<StoreDetailsModel.Product> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.store_product, parent, false)
    )

    override fun getItemCount()  = productList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


        val index = productList[position]
        //Log.e("CART ID", cartId)

        Log.d("kb cat postion", "" + productList[position])
//        Log.d("kb cat postion",""+categotyList[position].product[position])

        // holder.offerPrice.paintFlags = holder.offerPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        //holder.offerPrice.text = con.resources.getString(R.string.ruppee) + "" + product.get(position).price
        holder.priceAfterDiscount.text = con.resources.getString(R.string.ruppee) + "" + index.offerPrice
        //holder.qty.text = product[position].weight + product[position].unit
        //holder.storeName.isSelected = true
        //holder.ratingTxtValue.text = storerating
        holder.itemName.text = index.productName
        //  holder.itemName.visibility=View.INVISIBLE
        Glide.with(con)
            .load(index.image)
            .fitCenter()
            .into(holder.storeImg)

        if (index.image == "") {
            // TODO
        }

        if (productList[position].isCustomize == "1"){
            holder.customizable.visibility = View.VISIBLE
        }else{
            holder.customizable.visibility = View.GONE
        }

        var count: Int = 0

        if (productList[position].isNonveg.equals("0")) {
            holder.vegIcon.setImageResource(R.drawable.nonveg_icon)
            holder.vegIcon.setColorFilter(Color.rgb(53, 156, 71))
        } else if (productList[position].isNonveg.equals("1")) {
            holder.vegIcon.setImageResource(R.drawable.nonveg_icon)

        }


        if (index.cartQuantity == 0) {
            holder.add_new_lay.visibility = View.VISIBLE
            holder.add_remove_lay.visibility = View.GONE

        } else {

            holder.add_new_lay.visibility = View.GONE
            holder.add_remove_lay.visibility = View.VISIBLE

            val tempProductListModel = TempProductListModel()
            tempProductListModel.productId = index.productId
            tempProductListModel.price = index.offerPrice
            tempProductListModel.quantity = index.cartQuantity.toString()
            tempProductList!!.add(tempProductListModel)
            val customIdsList: ArrayList<String>?

            customIdsList = ArrayList()
            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = index.productId
            addCartInputModel.quantity = index.cartQuantity.toString()
            addCartInputModel.message = "add product"
            addCartInputModel.customizeSubCatId = customIdsList
            addCartInputModel.isCustomize = "0"
            MainActivity.addCartTempList!!.add(addCartInputModel)
            Log.e("llll", Gson().toJson(tempProductList))
            // Toast.makeText(con, tempProductList, Toast.LENGTH_LONG).show()

            count = index.cartQuantity

            /*for (i in 0 until tempProductList!!.size){
                if (tempProductList!![i].productId == productList[position].productId){
                    count = tempProductList!![i].quantity.toInt()
                }
            }*/

            //count = index.cartQuantity
            holder.countValue.text = index.cartQuantity.toString()
        }

        if (index.in_out_of_stock_status == "1") {
            holder.stock_status.visibility = View.GONE

            holder.plusLay.setOnClickListener {
                if (index.isCustomize.equals("1")) {
                    var items: String? = ""
                    if (index.customizeItem != null) {

                        if (index.customizeItem.size != 0) {
                            var tempp: Double? = 0.0
                            for (i in 0 until index.customizeItem.size) {
                                items = index.customizeItem[i].subCatName + ", " + items
                                tempp = tempp!! + index.customizeItem.get(i).price.toDouble()

                            }
                            tempp = tempp!! + index.offerPrice.toDouble()

                            items = items!!.substring(0, items.length - 2)
                        } else {

                        }
                    }


                    if (index.customizeItem != null) {
                        count++

                        if (index.customizeItem.size != 0) {
                            adapterCartAddRemoveClick.onAddItemClick(
                                index.productId,
                                items,
                                index.offerPrice,
                                index.isCustomize,
                                index.customizeItem[position].productCustomizeId,
                                productList[position].cartId
                            )
                        } else {
                            adapterCartAddRemoveClick.onAddItemClick(
                                index.productId,
                                items,
                                index.offerPrice,
                                index.isCustomize,
                                "",
                                productList[position].cartId
                            )
                        }
                    }

                } else {
                    count++
                    holder.countValue.text = count.toString()
                    adapterAddRemoveClick.onItemAddRemoveClick(
                            index.productId,
                            count.toString(),
                            "add",
                            index.offerPrice, "",
                        productList[position].cartId,0
                    )
                }
            }

            holder.add_new_lay.setOnClickListener {
                if (SessionTwiclo(con).isLoggedIn) {
                    if (index.isCustomize.equals("1")) {
                        count++
                        holder.countValue.text = count.toString()
                        adapterClick.onItemClick(
                            index.productId,
                            "custom",
                            "1",
                            index.offerPrice,
                            index.is_customize_quantity,
                            "",
                            productList[position].cartId
                        )
                    } else {
                        count++
                        holder.countValue.text = count.toString()
                        holder.add_new_lay.visibility = View.GONE
                        holder.add_remove_lay.visibility = View.VISIBLE

                        if (SessionTwiclo(con).storeId.equals(storeID) || SessionTwiclo(con).storeId.equals("")
                        ) {
                            adapterAddRemoveClick.onItemAddRemoveClick(
                                index.productId, count.toString(), "add",
                                index.offerPrice, "",
                                productList[position].cartId,0
                            )

                        } else {

                            val builder = AlertDialog.Builder(con)
                            //set title for alert dialog
                            builder.setTitle("Replace cart item!")
                            //set message for alert dialog
                            builder.setMessage("Do you want to discard the previous selection?")
                            builder.setIcon(android.R.drawable.ic_dialog_alert)
                            //performing positive action
                            builder.setPositiveButton("Yes") { _, _ ->
                                adapterAddRemoveClick.clearCart()
                                adapterAddRemoveClick.onItemAddRemoveClick(
                                     index.productId,
                                    count.toString(),
                                    "add",
                                    index.offerPrice,
                                    storeID,
                                    productList[position].cartId,0
                                )

                                SessionTwiclo(con).storeId=storeID
                                //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                            }

                            //performing negative action
                            builder.setNegativeButton("No") { _, _ ->
                                count = 0
                                holder.countValue.text = count.toString()
                                holder.add_new_lay.visibility = View.VISIBLE
                                holder.add_remove_lay.visibility = View.GONE
                            }
                            // Create the AlertDialog
                            val alertDialog: AlertDialog = builder.create()
                            // Set other dialog properties
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        }


                        /*adapterClick.onItemClick(
                            product.get(position).productId,
                            "simple",
                            holder.countValue.text.toString(),
                            product.get(position).offerPrice,
                            product.get(position).is_customize_quantity,
                            "",
                            ""
                        )*/


                    }
                } else {
                    showLoginDialog("Please login to proceed")
                }
            }

            holder.minusLay.setOnClickListener {

                if (index.isCustomize.equals("1")) {
                    if (holder.countValue.text.toString().toInt() > 0) {
                        var count: Int = holder.countValue.text.toString().toInt()
                        count--
                        holder.countValue.text = count.toString()

                        if (index.customizeItem != null) {

                            if (index.customizeItem.size != 0) {
                                adapterCartAddRemoveClick.onRemoveItemClick(
                                    index.productId,
                                    count.toString(),
                                    index.isCustomize,
                                    index.customizeItem[0].productCustomizeId,
                                    productList[position].cartId
                                )
                            } else {
                                adapterCartAddRemoveClick.onRemoveItemClick(
                                    index.productId,
                                    count.toString(),
                                    index.isCustomize,
                                    "",
                                    productList[position].cartId
                                )
                            }
                        }


                    }

                } else {

                    if (count > 0) {
                        count--
                        holder.countValue.text = count.toString()
                        if (count == 0) {
                            holder.add_new_lay.visibility = View.VISIBLE
                            holder.add_remove_lay.visibility = View.GONE
                            adapterAddRemoveClick.onItemAddRemoveClick(
                                index.productId,
                                count.toString(),
                                "remove",
                                index.offerPrice,
                                "",
                                productList[position].cartId,0
                            )
                            //adapterAddRemoveClick.clearCart() // clearing the cart if item quantity becomes zero
                        } else {
                            adapterAddRemoveClick.onItemAddRemoveClick(
                                index.productId,
                                count.toString(),
                                "remove",
                                index.offerPrice,
                                "",
                                productList[position].cartId,0
                            )
                        }
                    }
                }
            }

            /*holder.itemLay.setOnClickListener {
                if (product.get(position).isCustomize.equals("1")) {

                } else {
                    con.startActivity(
                        Intent(con, SingleProductActivity::class.java).putExtra(
                            "productId",
                            product.get(position).productId
                        ).putExtra(
                            "productCompany",
                            product.get(position).companyName
                        ).putExtra(
                            "cartQyantity",
                            product.get(position).cartQuantity
                        ).putExtra(
                            "service_id",
                            service_id
                        )
                    )
                }
            }*/

        } else if (index.in_out_of_stock_status.equals("0")) {
            holder.add_new_lay.visibility = View.GONE
            holder.add_remove_lay.visibility = View.GONE
            holder.stock_status.visibility = View.VISIBLE
        }

    }

    private fun showLoginDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(con)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { _, which ->
            con.startActivity(Intent(con, LoginActivity::class.java))


        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //var offerPrice = view.productPrice
        var priceAfterDiscount = view.productPrice
        var itemLay = view.store_item_lay
        //var qty = view.tv_quantity
        var itemName = view.productName
        var storeImg = view.productImage
        //var customText = view.customText
        var plusLay = view.btn_plus
        //var ratingTxtValue = view.ratingTxtValue
        var minusLay = view.btn_minus
        var countValue = view.tv_count
        var add_remove_lay = view.add_remove_lay
        var vegIcon = view.veg_icon
        var add_new_lay = view.add_item_lay
        var stock_status = view.tv_stock_status
        var customizable = view.tv_customizable
    }


}