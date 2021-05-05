package com.fidoo.user.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.SearchModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.ui.SingleProductActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.store_sub_item_adapter.view.*


class SearchAdapter(
        val con: Context,
        private val productList: ArrayList<SearchModel.ProductList>,
        private val adapterClick: AdapterClick,
        private var storeID : String,
        var adapterAddRemoveClick: AdapterAddRemoveClick,
        var adapterCartAddRemoveClick: AdapterCartAddRemoveClick
) : RecyclerView.Adapter<SearchAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.store_sub_item_adapter, parent, false))

    override fun getItemCount() = productList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        storeID = productList[position].storeId
        var count:Int=0
        holder.qty.text = productList[position].weight + productList[position].unit
        holder.itemName.text = productList.get(position).productName
//        if(!productList.get(position).rating.equals("")) {
//            holder.ratingTxtValue.visibility=View.VISIBLE
//            holder.ratingTxtValue.text = productList.get(position).rating
//        }

        holder.offerPrice.paintFlags = holder.offerPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.offerPrice.text = con.resources.getString(R.string.ruppee) + productList.get(position).price
        holder.priceAfterDiscount.text = con.resources.getString(R.string.ruppee) + productList.get(position).offerPrice

        Glide.with(con)
                .load(productList[position].productImage)
                .fitCenter()
                .into(holder.storeImg)

        holder.customText.visibility = View.GONE
        if (productList[position].cartQuantity == 0) {
            holder.add_new_layy.visibility = View.VISIBLE
            holder.add_remove_layy.visibility = View.GONE

        } else {

            holder.add_new_layy.visibility = View.GONE
            holder.add_remove_layy.visibility = View.VISIBLE

            val tempProductListModel = TempProductListModel()
            tempProductListModel.productId = productList.get(position).productId
            tempProductListModel.price = productList.get(position).offerPrice
            tempProductListModel.quantity = productList.get(position).cartQuantity.toString()
            MainActivity.tempProductList!!.add(tempProductListModel)
            val customIdsList: ArrayList<String>?

            customIdsList = ArrayList()
            val addCartInputModel = AddCartInputModel()
            addCartInputModel.productId = productList[position].productId
            addCartInputModel.quantity = productList[position].cartQuantity.toString()
            addCartInputModel.message = "add product"
            addCartInputModel.customizeSubCatId = customIdsList
            addCartInputModel.isCustomize = "0"
            MainActivity.addCartTempList!!.add(addCartInputModel)
            Log.e("llll", Gson().toJson(MainActivity.tempProductList))
            // Toast.makeText(con, tempProductList, Toast.LENGTH_LONG).show()
            count = productList[position].cartQuantity
            holder.countValue.text = productList[position].cartQuantity.toString()
        }

        if(productList[position].isCustomize.equals("1")) {
            holder.add_remove_layy.visibility=View.GONE
            holder.add_new_layy.visibility=View.VISIBLE
            //holder.addToCartButton.visibility=View.VISIBLE
        } else {
            //holder.addToCartButton.visibility=View.VISIBLE
            holder.add_new_layy.visibility=View.VISIBLE
            holder.add_remove_layy.visibility=View.GONE
        }

        /*holder.addToCartButton.setOnClickListener {
            if (productList.get(position).isCustomize.equals("1")) {
                adapterClick.onItemClick(productList.get(position).productId,
                    productList.get(position).storeId,
                    holder.countValue.text.toString(),
                    productList.get(position).offerPrice,
                    productList.get(position).isCustomizeQuantity,
                    "custom",
                    "")
            } else {
                //   adapterClick.onItemClick(productList.get(position).productId, productList.get(position).storeId, holder.countValue.text.toString(),productList.get(position).offerPrice,0)
                adapterClick.onItemClick(productList.get(position).productId,
                    productList.get(position).storeId,
                    holder.countValue.text.toString(),
                    productList.get(position).offerPrice,
                    productList.get(position).isCustomizeQuantity,
                    "simple",
                    "")
            }
        }*/


        holder.mainLay.setOnClickListener {

            if (SessionTwiclo(con).isLoggedIn){
                /* con.startActivity(
                        Intent(con, SingleProductActivity::class.java).putExtra(
                                "productId",
                                productList.get(position).productId
                        ).putExtra(
                                "productCompany",
                                productList.get(position).companyName
                        ).putExtra(
                                "cartQyantity",
                                productList.get(position).cartQuantity
                        )
                )*/
            }else{
                showLoginDialog("Please login to proceed")
            }
        }


        holder.add_new_layy.setOnClickListener {
            if (SessionTwiclo(con).isLoggedIn){
                if (productList[position].isCustomize.equals("1")) {
                    adapterClick.onItemClick(
                            productList[position].productId,
                            "custom",
                            "1",
                            productList[position].offerPrice,
                            productList[position].isCustomizeQuantity,
                            "",
                            productList[position].cartId
                    )
                } else {
                    count++
                    holder.countValue.text = count.toString()
                    holder.add_new_layy.visibility=View.GONE
                    holder.add_remove_layy.visibility=View.VISIBLE

                    if (SessionTwiclo(con).storeId.equals(storeID) || SessionTwiclo(con).storeId.equals("")) {
                        adapterAddRemoveClick.onItemAddRemoveClick(
                                productList[position].productId, count.toString(), "add",
                                productList[position].offerPrice,"",
                                productList[position].cartId,0
                        )
                    }else{
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
                                productList[position].productId, count.toString(), "add",
                                productList[position].offerPrice,"", productList[position].cartId,0)

                            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                        }

                        //performing negative action
                        builder.setNegativeButton("No") { _, _ ->
                            count = 0
                            holder.countValue.text = count.toString()
                            holder.add_new_layy.visibility = View.VISIBLE
                            holder.add_remove_layy.visibility = View.GONE
                        }
                        // Create the AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                    //adapterAddRemoveClick.onItemAddRemoveClick(productList.get(position).productId,count.toString(),"add",productList.get(position).offerPrice)

                    /*adapterClick.onItemClick(
                        productList[position].productId,
                        productList.get(position).storeId,
                        holder.countValue.text.toString(),
                        productList[position].offerPrice,
                        productList[position].isCustomizeQuantity,
                        "simple",
                        "")*/
                }
            }else{
                showLoginDialog("Please login to proceed")
            }
        }


        holder.plusLay.setOnClickListener {
           // count++
          //  holder.countValue.text = count.toString()
            if (SessionTwiclo(con).isLoggedIn){
                if (productList[position].isCustomize.equals("1")) {
                    adapterClick.onItemClick(
                            productList[position].productId,
                            "custom",
                            "1",
                            productList[position].offerPrice,
                            productList[position].isCustomizeQuantity,
                            "",
                            ""
                    )
                } else {
                    count++
                    holder.countValue.text = count.toString()
                    holder.add_new_layy.visibility=View.GONE
                    holder.add_remove_layy.visibility=View.VISIBLE

                    if (SessionTwiclo(con).storeId.equals(storeID) || SessionTwiclo(con).storeId.equals("")) {
                        adapterAddRemoveClick.onItemAddRemoveClick(
                                productList[position].productId, count.toString(), "add",
                                productList[position].offerPrice,"",
                                "",0
                        )
                    }else{
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
                                    productList[position].productId, count.toString(), "add",
                                    productList[position].offerPrice,"", "",0)

                            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
                        }


                        //performing negative action
                        builder.setNegativeButton("No") { _, _ ->
                            count = 0
                            holder.countValue.text = count.toString()
                            holder.add_new_layy.visibility = View.VISIBLE
                            holder.add_remove_layy.visibility = View.GONE
                        }
                        // Create the AlertDialog
                        val alertDialog: AlertDialog = builder.create()
                        // Set other dialog properties
                        alertDialog.setCancelable(false)
                        alertDialog.show()
                    }
                    //adapterAddRemoveClick.onItemAddRemoveClick(productList.get(position).productId,count.toString(),"add",productList.get(position).offerPrice)

                    /*adapterClick.onItemClick(
                        productList[position].productId,
                        productList.get(position).storeId,
                        holder.countValue.text.toString(),
                        productList[position].offerPrice,
                        productList[position].isCustomizeQuantity,
                        "simple",
                        "")*/
                }
            }else{
                showLoginDialog("Please login to proceed")

        holder.plusLay.setOnClickListener {
            if (productList[position].isCustomize.equals("1")) {
                var items: String? = ""
                if (productList[position].customizeItem != null) {

                    if (productList[position].customizeItem.size != 0) {
                        var tempp: Double? = 0.0
                        for (i in 0 until productList[position].customizeItem.size) {
                            items = productList[position].customizeItem[i].subCatName + ", " + items
                            tempp = tempp!! + productList[position].customizeItem.get(i).price.toDouble()

                        }
                        tempp = tempp!! + productList[position].offerPrice.toDouble()

                        items = items!!.substring(0, items.length - 2)
                    } else {

                    }
                }


                if (productList[position].customizeItem != null) {
                    count++

                    if (productList[position].customizeItem.size != 0) {
                        adapterCartAddRemoveClick.onAddItemClick(
                                productList[position].productId,
                                items,
                                productList[position].offerPrice,
                                productList[position].isCustomize,
                                productList[position].customizeItem[position].productCustomizeId,
                                productList[position].cartId
                        )
                    } else {
                        adapterCartAddRemoveClick.onAddItemClick(
                                productList[position].productId,
                                items,
                                productList[position].offerPrice,
                                productList[position].isCustomize,
                                "",
                                productList[position].cartId
                        )
                    }
                }

            } else {
                count++
                holder.countValue.text = count.toString()
                adapterAddRemoveClick.onItemAddRemoveClick(
                        productList[position].productId,
                        count.toString(),
                        "add",
                        productList[position].offerPrice, "",
                        productList[position].cartId,0
                )

            }
        }


        holder.minusLay.setOnClickListener {

            if (productList[position].isCustomize == "1"){
                if (holder.countValue.text.toString().toInt() > 0){
                    var count: Int = holder.countValue.text.toString().toInt()
                    count--
                    holder.countValue.text = count.toString()

                    if (productList[position].customizeItem != null) {

                        if (productList[position].customizeItem.size != 0) {
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                    productList[position].productId,
                                    count.toString(),
                                    productList[position].isCustomize,
                                    productList[position].customizeItem[0].productCustomizeId,
                                    productList[position].cartId
                            )
                        } else {
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                    productList[position].productId,
                                    count.toString(),
                                    productList[position].isCustomize,
                                    "",
                                    productList[position].cartId
                            )
                        }
                    }


                }

            }else{
                if(count>=0)
                {
                    count--
                    holder.countValue.text = count.toString()
                    if (count == 0) {
                        holder.add_new_layy.visibility = View.VISIBLE
                        holder.add_remove_layy.visibility = View.GONE
                        adapterAddRemoveClick.onItemAddRemoveClick(
                                productList[position].productId,
                                count.toString(),
                                "remove",
                                productList[position].offerPrice,
                                "",
                                productList[position].cartId,0
                        )
                        adapterAddRemoveClick.clearCart()
                    }else{
                        adapterAddRemoveClick.onItemAddRemoveClick(
                                productList[position].productId,
                                count.toString(),
                                "remove",
                                productList[position].offerPrice,
                                "",
                                productList[position].cartId,0
                        )

                    }
                }

            }


        }

    }

    private fun showLoginDialog(message: String){
        val builder = androidx.appcompat.app.AlertDialog.Builder(con)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { dialogInterface, which ->
            con.startActivity(Intent(con, LoginActivity::class.java))


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

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var qty = view.qty
        var itemName = view.itemName
        var offerPrice = view.offerPrice
        var priceAfterDiscount = view.priceAfterDiscount
        var customText = view.tv_stock_status
        var mainLay = view.mainLay
        var storeImg = view.storeImg
        var plusLay = view.btn_plus
        var minusLay = view.btn_minus
        var countValue = view.tv_count
        var add_remove_layy = view.add_remove_lay
        //   var ratingTxtValue = view.ratingTxtValue
        var add_new_layy = view.add_item_lay

    }


}