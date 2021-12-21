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
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Subcategory
import com.fidoo.user.grocerynewui.onclicklistener.ItemViewOnClickListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.add_img
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.add_itemll
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.grocery_item_img
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.grocery_item_tv
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.itme_LL
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.minusplus_ll
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.qua_txt
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.stock_status
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.subt_img
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.tv_price
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.tv_unit
import kotlinx.android.synthetic.main.item_viewall_subcat.view.*

class ViewAllSubCatAdapter(
    var context: Context,
    var list: ArrayList<Product>,
    var itemViewOnClickListener: ItemViewOnClickListener,
    private val storeID: String,
    var width: Int
) : RecyclerView.Adapter<ViewAllSubCatAdapter.ViewHolder>() {

    var count: Int = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_viewall_subcat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.itme_LL_viewAll.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT ,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (model.is_prescription == "1"){
            holder.itemView.medicine_prescription_lay_viewAll.visibility=View.VISIBLE
        }else{
            holder.itemView.medicine_prescription_lay_viewAll.visibility=View.GONE
        }

        holder.itemView.grocery_item_tv_viewAll.text = list[position].product_name
        holder.itemView.discription_ofPrd.text = list[position].description
        holder.itemView.qua_txt_viewAll.text = list[position].cart_quantity.toString()
        holder.itemView.tv_price_viewAll.text =
            context.resources.getString(R.string.ruppee) + " " + list[position].offer_price
        holder.itemView.tv_unit_viewAll.text = list[position].weight.toLowerCase() +" "+ list[position].unit.toLowerCase()
        holder.itemView.minusplus_ll_viewAll.visibility = View.GONE

        if (model.cart_quantity == 0) {
            holder.itemView.add_itemll_viewAll.visibility = View.VISIBLE
            holder.itemView.minusplus_ll_viewAll.visibility = View.GONE

        } else {

            holder.itemView.add_itemll_viewAll.visibility = View.GONE
            holder.itemView.minusplus_ll_viewAll.visibility = View.VISIBLE

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
            holder.itemView.qua_txt_viewAll.text = model.cart_quantity.toString()
        }

        Glide.with(context)
            .load(model.image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.grocery_item_img_viewAll)

        if (list[position].in_out_of_stock_status == "1"){
            holder.itemView.stock_status_viewAll.visibility = View.GONE
            holder.itemView.tv_price_viewAll.visibility = View.VISIBLE
            holder.itemView.tv_unit_viewAll.visibility = View.VISIBLE

            //add first time
            holder.itemView.add_itemll_viewAll.setOnClickListener {
                count= list[position].cart_quantity

                if (SessionTwiclo(context).isLoggedIn){
                    count++
                    holder.itemView.qua_txt_viewAll.text = count.toString()
                    //groceryItemClick.onItemClick(position,list.get(position))
                    holder.itemView.add_itemll_viewAll.visibility=View.GONE
                    holder.itemView.minusplus_ll_viewAll.visibility=View.VISIBLE
                    //count=1

                    if (SessionTwiclo(context).storeId.equals(storeID) || SessionTwiclo(context).storeId.equals("")
                    ) {
                        // Adapter Click
                        itemViewOnClickListener.addtoCart(
                            0,
                            list[position],
                            position,
                            storeID,
                            count.toString(),"add"
                        )
                        SessionTwiclo(context).storeId=storeID

                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Replace cart item!")
                        builder.setMessage("Do you want to discard the previous selection?")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                        builder.setPositiveButton("Yes") { _, _ ->
                            SessionTwiclo(context).storeId=storeID

                            itemViewOnClickListener.addtoCart(
                                0,
                                list[position],
                                position,
                                storeID,
                                count.toString(),"Replace"
                            )

                            //notifyDataSetChanged()
                        }

                        //performing negative action
                        builder.setNegativeButton("No") { _, _ ->
                            count = 0
                            holder.itemView.add_itemll_viewAll.visibility = View.VISIBLE
                            holder.itemView.minusplus_ll_viewAll.visibility = View.GONE
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

            //minus item onclick
            holder.itemView.subt_img_viewAll.setOnClickListener {
                count= list[position].cart_quantity

                if (count>=0) {
                    count--
                    holder.itemView.qua_txt_viewAll.text = count.toString()
                    if (count == 0) {
                        holder.itemView.add_itemll_viewAll.visibility=View.VISIBLE
                        holder.itemView.minusplus_ll_viewAll.visibility=View.GONE
                        itemViewOnClickListener.minusItemCart(
                            0,
                            list[position],
                            position,
                            storeID,
                            count.toString(),"remove"
                        )
                    } else {
                        itemViewOnClickListener.minusItemCart(0,
                            list[position],
                            position,
                            storeID,
                            count.toString(),"remove"
                        )
                    }

                }else{
                    holder.itemView.minusplus_ll_viewAll.visibility=View.GONE
                    holder.itemView.add_itemll_viewAll.visibility=View.VISIBLE
                }

                holder.itemView.qua_txt_viewAll.text = count.toString()
            }

            //add item click
            holder.itemView.add_img_viewAll.setOnClickListener {
                count= list[position].cart_quantity
                count++
                holder.itemView.qua_txt_viewAll.text = count.toString()

                itemViewOnClickListener.plusItemCart(0,
                    list[position],
                    position,
                    storeID,
                    count.toString(),"add"
                )

            }

        }else{

            holder.itemView.add_itemll_viewAll.visibility = View.GONE
            holder.itemView.minusplus_ll_viewAll.visibility = View.GONE
            holder.itemView.tv_price_viewAll.visibility = View.GONE
            holder.itemView.stock_status_viewAll.visibility = View.VISIBLE
            holder.itemView.tv_unit_viewAll.visibility = View.VISIBLE

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

    fun updateData(list_: ArrayList<Product>) {
        list = java.util.ArrayList<Product>()
        list.addAll(list_)
        notifyDataSetChanged()
    }
}