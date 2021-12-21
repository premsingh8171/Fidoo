package com.fidoo.user.grocerynewui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import com.fidoo.user.grocerynewui.onclicklistener.ItemViewOnClickListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.*

class GroceryItemAdapter2(
    var context: Context,
    val list: ArrayList<Product>,
    var itemViewOnClickListener: ItemViewOnClickListener,
    private val storeID: String,
    var width: Int
) : RecyclerView.Adapter<GroceryItemAdapter2.ViewHolder>() {

    var count: Int = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.grocery_new_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.itme_LL.layoutParams = LinearLayout.LayoutParams(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (model.is_prescription == "1") {
            holder.itemView.medicine_prescription_laynew.visibility = View.VISIBLE
        } else {
            holder.itemView.medicine_prescription_laynew.visibility = View.GONE
        }

        holder.itemView.grocery_item_tv.text = list[position].product_name
        holder.itemView.qua_txt.text = list[position].cart_quantity.toString()
        holder.itemView.tv_price.text =
            context.resources.getString(R.string.ruppee) + " " + list[position].offer_price

//        holder.itemView.actual_price.text = context.resources.getString(R.string.ruppee) + "" + list[position].price
//        holder.itemView.actual_price.paintFlags = holder.itemView.actual_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        holder.itemView.tv_unit.text = list[position].weight.toLowerCase() +" "+ list[position].unit.toLowerCase()
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

            count = list[position].cart_quantity

            holder.itemView.qua_txt.text = model.cart_quantity.toString()
        }

        Glide.with(context)
            .load(model.image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.grocery_item_img)

        if (list[position].in_out_of_stock_status == "1") {
            holder.itemView.stock_status.visibility = View.GONE
            holder.itemView.tv_price.visibility = View.VISIBLE
            holder.itemView.tv_unit.visibility = View.VISIBLE

            //add first time
            holder.itemView.add_itemll.setOnClickListener {
                count = list[position].cart_quantity

                if (SessionTwiclo(context).isLoggedIn) {
                    count++
                    holder.itemView.qua_txt.text = count.toString()
                    //groceryItemClick.onItemClick(position,list.get(position))
                    holder.itemView.add_itemll.visibility = View.GONE
                    holder.itemView.minusplus_ll.visibility = View.VISIBLE
                    //count=1

                    if (SessionTwiclo(context).storeId.equals(storeID) || SessionTwiclo(context).storeId.equals(
                            ""
                        )
                    ) {
                        // Adapter Click
                        itemViewOnClickListener.addtoCart(
                            0,
                            list[position],
                            position,
                            storeID,
                            count.toString(), "add"
                        )
                        SessionTwiclo(context).storeId = storeID

                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Replace cart item!")
                        builder.setMessage("Do you want to discard the previous selection?")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                        builder.setPositiveButton("Yes") { _, _ ->
                            SessionTwiclo(context).storeId = storeID

                            itemViewOnClickListener.addtoCart(
                                0,
                                list[position],
                                position,
                                storeID,
                                count.toString(), "Replace"
                            )

                            //notifyDataSetChanged()
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

                } else {
                    showLoginDialog("Please login to proceed")
                }

            }

            //minus item onclick
            holder.itemView.subt_img.setOnClickListener {
                count = list[position].cart_quantity

                if (count >= 0) {
                    count--
                    holder.itemView.qua_txt.text = count.toString()
                    if (count == 0) {
                        holder.itemView.add_itemll.visibility = View.VISIBLE
                        holder.itemView.minusplus_ll.visibility = View.GONE
                        itemViewOnClickListener.minusItemCart(
                            0,
                            list[position],
                            position,
                            storeID,
                            count.toString(), "remove"
                        )
                    } else {
                        itemViewOnClickListener.minusItemCart(
                            0,
                            list[position],
                            position,
                            storeID,
                            count.toString(), "remove"
                        )
                    }

                } else {
                    holder.itemView.add_itemll.visibility = View.VISIBLE
                    holder.itemView.minusplus_ll.visibility = View.GONE
                }

                holder.itemView.qua_txt.text = count.toString()
            }

            //add item click
            holder.itemView.add_img.setOnClickListener {
                count = list[position].cart_quantity
                count++
                holder.itemView.qua_txt.text = count.toString()

                itemViewOnClickListener.plusItemCart(
                    0,
                    list[position],
                    position,
                    storeID,
                    count.toString(), "add"
                )

            }

        } else {

            holder.itemView.add_itemll.visibility = View.GONE
            holder.itemView.minusplus_ll.visibility = View.GONE
            holder.itemView.tv_price.visibility = View.GONE
            holder.itemView.stock_status.visibility = View.VISIBLE
            holder.itemView.tv_unit.visibility = View.VISIBLE

        }

        if (list.size - 1 == position) {
            holder.itemView.spaceView_.visibility = View.VISIBLE
        } else {
            holder.itemView.spaceView_.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun showLoginDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Login") { _, _ ->
            context.startActivity(Intent(context, AuthActivity::class.java))
        }
        builder.setNegativeButton("Cancel") { _, _ -> }
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }


}