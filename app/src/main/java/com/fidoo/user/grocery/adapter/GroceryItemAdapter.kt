package com.fidoo.user.grocery.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.model.getGroceryProducts.GroceryProductsResponse
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.ui.MainActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.grocery_item_layout.view.*

class GroceryItemAdapter(
    var context: Context,
    val list: ArrayList<Product>,
    var adapterAddRemoveClick: AdapterAddRemoveClick,
    private val adapterCartAddRemoveClick: AdapterCartAddRemoveClick,
    val id: Int,
    private val storeID: String,
    private val cartId: String): RecyclerView.Adapter<GroceryItemAdapter.ViewHolder>() {

    var count:Int =0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grocery_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("storeID___",storeID)
        val model = list[position]
        holder.itemView.grocery_item_tv.text = list[position].product_name
        holder.itemView.qua_txt.text = list[position].cart_quantity.toString()
        holder.itemView.tv_price.text = context.resources.getString(R.string.ruppee) + "" + list[position].offer_price
        holder.itemView.tv_unit.text = list[position].weight + list[position].unit
        holder.itemView.minusplus_ll.visibility = View.GONE



        if (model.cart_quantity == 0) {
            holder.itemView.add_itemll.visibility = View.VISIBLE
            holder.itemView.minusplus_ll.visibility = View.GONE

        } else {

            holder.itemView.add_itemll.visibility = View.GONE
            holder.itemView.minusplus_ll.visibility = View.VISIBLE

            val tempProductListModel = TempProductListModel()
            tempProductListModel.productId = list[position].product_id
            tempProductListModel.price = list[position].offer_price
            tempProductListModel.quantity = list[position].cart_quantity.toString()
            MainActivity.tempProductList!!.add(tempProductListModel)
            val customIdsList: ArrayList<String>?

            customIdsList = ArrayList()
            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = list[position].product_id
            addCartInputModel.quantity = list[position].cart_quantity.toString()
            addCartInputModel.message = "add product"
            addCartInputModel.customizeSubCatId = customIdsList
            addCartInputModel.isCustomize = "0"
            MainActivity.addCartTempList!!.add(addCartInputModel)
            Log.e("llll", Gson().toJson(MainActivity.tempProductList))
            // Toast.makeText(con, tempProductList, Toast.LENGTH_LONG).show()

            count = list[position].cart_quantity

            /*for (i in 0 until tempProductList!!.size){
                if (tempProductList!![i].productId == productList[position].productId){
                    count = tempProductList!![i].quantity.toInt()
                }
            }*/

            //count = index.cartQuantity
            holder.itemView.qua_txt.text = model.cart_quantity.toString()
        }

        Glide.with(context)
            .load(model.image)
            .fitCenter()
            .placeholder(R.drawable.about_icon)
            .error(R.drawable.about_icon)
            .into(holder.itemView.grocery_item_img)

        if (list[position].in_out_of_stock_status == "1"){
            holder.itemView.stock_status.visibility = View.GONE

            //add first time click
            holder.itemView.add_itemll.setOnClickListener {
                count= list[position].cart_quantity

                if (SessionTwiclo(context).isLoggedIn){
                    count++
                    holder.itemView.qua_txt.text = count.toString()
                    //groceryItemClick.onItemClick(position,list.get(position))
                    holder.itemView.add_itemll.visibility=View.GONE
                    holder.itemView.minusplus_ll.visibility=View.VISIBLE
                    //count=1

                    if (SessionTwiclo(context).storeId.equals(storeID) || SessionTwiclo(context).storeId.equals("")
                    ) {
                        // Adapter Click
                        adapterAddRemoveClick.onItemAddRemoveClick(
                            list[position].product_id,
                            count.toString(),
                            "add",
                            list[position].offer_price,
                            storeID,
                            "",position
                        )

                    } else {

                        val builder = AlertDialog.Builder(context)
                        //set title for alert dialog
                        builder.setTitle("Replace cart item!")
                        //set message for alert dialog
                        builder.setMessage("Do you want to discard the previous selection?")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                        //performing positive action
                        builder.setPositiveButton("Yes") { _, _ ->
                            adapterAddRemoveClick.clearCart()
                            adapterAddRemoveClick.onItemAddRemoveClick(
                                list[position].product_id,
                                count.toString(),
                                "add",
                                list[position].offer_price,
                                storeID,
                                "",position
                            )
                            SessionTwiclo(context).storeId=storeID

                            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                        }

                        //performing negative action
                        builder.setNegativeButton("No") { _, _ ->
                            count = 0
                            holder.itemView.add_itemll.visibility = View.VISIBLE
                            holder.itemView.minusplus_ll.visibility = View.GONE
                        }
                        // Create the AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }


                }else{
                    showLoginDialog("Please login to proceed")
                }

            }

            //subtraction click
            holder.itemView.subt_img.setOnClickListener {
                count= list[position].cart_quantity

                if (count>=0) {
                    count--
                    holder.itemView.qua_txt.text = count.toString()
                    if (count == 0) {
                        holder.itemView.add_itemll.visibility=View.VISIBLE
                        holder.itemView.minusplus_ll.visibility=View.GONE
                        adapterAddRemoveClick.onItemAddRemoveClick(
                            list[position].product_id,
                            count.toString(),
                            "remove",
                            list[position].offer_price,
                            "",
                            model.cart_id,position
                        )
                        //adapterAddRemoveClick.clearCart() // clearing the cart if item quantity becomes zero
                    } else {
                        adapterAddRemoveClick.onItemAddRemoveClick(
                            list[position].product_id,
                            count.toString(),
                            "remove",
                            list[position].offer_price,
                            "",
                            model.cart_id,position
                        )
                    }

                }else{
                    holder.itemView.minusplus_ll.visibility=View.GONE
                    holder.itemView.add_itemll.visibility=View.VISIBLE

                }

                holder.itemView.qua_txt.text = count.toString()
                //groceryItemClick.onItemSub(position,count,list.get(position))
            }

            //add item click
            holder.itemView.add_img.setOnClickListener {
                count= list[position].cart_quantity
                count++

                holder.itemView.qua_txt.text = count.toString()
                //groceryItemClick.onItemAdd(position,count, list[position])

                adapterAddRemoveClick.onItemAddRemoveClick(
                    list[position].product_id,
                    count.toString(),
                    "add",
                    list[position].offer_price, "",
                    model.cart_id,position
                )

            }

        }else{

            holder.itemView.add_itemll.visibility = View.GONE
            holder.itemView.minusplus_ll.visibility = View.GONE
            holder.itemView.stock_status.visibility = View.VISIBLE

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface GroceryItemClick {
        fun onItemClick(productId: String , type: String, count: String, offerPrice: String, customize_count: Integer ,productType: String,  cart_id: String)

        fun onItemSub(pos: Int,itemcount:Int,grocery:Product)

        fun onItemAdd(pos: Int,itemcount:Int,grocery:Product)
    }

    private fun showLoginDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { _, _ ->
            context.startActivity(Intent(context, LoginActivity::class.java))


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


}