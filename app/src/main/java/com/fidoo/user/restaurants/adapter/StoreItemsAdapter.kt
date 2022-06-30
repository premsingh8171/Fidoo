package com.fidoo.user.restaurants.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.tempProductList
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.constants.useconstants
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.restaurants.activity.NewDBStoreItemsActivity
import com.fidoo.user.restaurants.listener.AdapterCartAddRemoveClick
import com.fidoo.user.restaurants.model.StoreDetailsModel
import com.fidoo.user.restaurants.model.vegDiffUtil
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity
import kotlinx.android.synthetic.main.store_product.view.*
import java.util.concurrent.Executors

class StoreItemsAdapter(
    val con: Context,
    private val adapterClick: AdapterClick,
    private var productList: ArrayList<StoreItemProductsEntity>,
    var fssai: String,
    var restaurantName: String,
    var service_id: String,
    var restaurantAddress: String,
    var adapterAddRemoveClick: AdapterAddRemoveClick,
    private val adapterCartAddRemoveClick: AdapterCartAddRemoveClick,
    var total_item_count: Int,
    private val storeID: String
    ) : RecyclerView.Adapter<StoreItemsAdapter.UserViewHolder>() {

    var arraylist: ArrayList<StoreDetailsModel.Product> = ArrayList()
    var count: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.store_product, parent, false)
    )

    override fun getItemCount()  = productList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val index = productList[position]

         Log.d("indexindex", restaurantName + "--" + restaurantAddress)

        holder.fssaitxt.text = fssai
        holder.restaurant_nametxt.text = restaurantName
        holder.restaurant_addtxt.text = restaurantAddress

       //  try {
       // Log.d("indexindex", index.price!! + "--" + index.offerPrice)

        if (!index.price!!.equals(index.offerPrice!!)) {

            holder.priceTxt.paintFlags =
                holder.priceTxt.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            holder.priceTxt.text = con.resources.getString(R.string.ruppee) + "" + index.price

            holder.offerPrice.text =
                con.resources.getString(R.string.ruppee) + "" + index.offerPrice
            holder.priceTxt.visibility=View.VISIBLE
        } else {
            if(index.offerPrice!!< 0.toString()){
                holder.offerPrice.text =
                    con.resources.getString(R.string.ruppee) + 0
            }else{
                holder.offerPrice.text =
                    con.resources.getString(R.string.ruppee) + index.offerPrice!!
            }

//            holder.offerPrice.text =
//                con.resources.getString(R.string.ruppee) + "" + index.offerPrice
//            holder.priceTxt.visibility=View.INVISIBLE

        }


//        }catch (e:Exception){
//            e.printStackTrace()
//        }

        if (index.offerPrice.equals("0")){

            holder.offerPrice.visibility=View.GONE
        }else{
            holder.offerPrice.visibility=View.VISIBLE
        }

        try {
            if (index.headerActiveornot.equals("1")) {
                if (position == 0) {
                    holder.devider_catLL.visibility = View.GONE
                    holder.category_nameheader.visibility = View.VISIBLE
                    holder.category_nameheader.text = index.subcategory_name.toString()

                } else {
                    holder.devider_catLL.visibility = View.VISIBLE
                    holder.category_nameheader.visibility = View.VISIBLE
                    holder.category_nameheader.text = index.subcategory_name.toString()
                }
            }else{
                if (position==0){
                }else {
                    holder.devider_catLL.visibility = View.GONE
                    holder.category_nameheader.visibility = View.GONE
                }
            }

            Log.d("subcategory_name____", index.subcategory_name.toString())

        } catch (e: Exception) {
        }

        holder.itemName.text = index.productName

        if (index.product_desc?.length!! > 50){
            holder.itemView.product_desc_txt.text= Html.fromHtml(index.product_desc!!.substring(0, 50)
            +"..."+ "<font color='black'><b>More</b></font>")
        }else {

            holder.itemView.product_desc_txt.text = index.product_desc
        }

        holder.itemView.product_desc_txt.setOnClickListener {


            if (index.product_desc?.length!! > 50) {

                if (holder.itemView.product_desc_txt.text.toString().endsWith("Less")) {
                    holder.itemView.product_desc_txt.text = Html.fromHtml(
                        index.product_desc!!.substring(0, 50)
                                + "..." + "<font color='black'><b>More </b></font>"
                    )
                } else {
                    holder.itemView.product_desc_txt.text = Html.fromHtml(
                        index.product_desc
                                + " " + "<font color='black'><b>Less</b></font>"
                    )
                }
            }


        }

        if (index.image!!.isNotEmpty()) {
//            if (index.image!!.startsWith("http")){
//               var replace= index.image!!.replace("http","https")
//                Glide.with(con)
//                    .load(replace).thumbnail(0.05f)
//                    .fitCenter()
//                    .error(R.drawable.default_item)
//                    .into(holder.storeImg)
//            }else{

                Glide.with(con)
                    .load(index.image).thumbnail(0.05f)
                    .fitCenter()
                    .error(R.drawable.default_item)
                    .into(holder.storeImg)

         //   }

            holder.storeImg.visibility = View.VISIBLE
        } else {
            holder.storeImg.visibility = View.GONE
        }

        Log.d("indeximage", index.image.toString())

        if (productList[position].isCustomize == "1") {
            holder.customizable.visibility = View.VISIBLE
        } else { holder.customizable.visibility = View.GONE }


        if (productList[position].isNonveg!!.equals("0")) {
            holder.vegIcon.setImageResource(R.drawable.veg)
            //holder.vegIcon.setColorFilter(Color.rgb(53, 156, 71))
        } else if (productList[position].isNonveg!!.equals("1")) {
                if (productList[position].contains_egg.equals("1")){
                    holder.vegIcon.setImageResource(R.drawable.egg_icon)
                }else{
                    holder.vegIcon.setImageResource(R.drawable.non_veg)
                }
        }

        if (index.cartQuantity == 0) {
            holder.add_new_lay.visibility = View.VISIBLE
            holder.add_remove_lay.visibility = View.GONE

        } else {
            tempProductList!!.clear()
            holder.add_new_lay.visibility = View.GONE
            holder.add_remove_lay.visibility = View.VISIBLE

//            val tempProductListModel = TempProductListModel()
//            tempProductListModel.productId = index.productId
//            tempProductListModel.price = index.offerPrice
//            tempProductListModel.quantity = index.cartQuantity.toString()
//            tempProductList!!.add(tempProductListModel)
//            val customIdsList: ArrayList<String>?
//
//            customIdsList = ArrayList()
//            val addCartInputModel = AddCartInputModel()
//            addCartInputModel.productId = index.productId
//            addCartInputModel.quantity = index.cartQuantity.toString()
//            addCartInputModel.message = "add product"
//            addCartInputModel.customizeSubCatId = customIdsList
//            addCartInputModel.isCustomize = "0"
//            MainActivity.addCartTempList!!.add(addCartInputModel)
//            Log.e("llll", Gson().toJson(tempProductList))

           // count = index.cartQuantity!!

            holder.countValue.text = index.cartQuantity.toString()
        }

        if (index.in_out_of_stock_status == "1") {

            holder.stock_status.visibility = View.GONE

            holder.add_new_lay.setOnClickListener {
                if (checkForInternet(con)) {
                    count = 0
                    if (SessionTwiclo(con).isLoggedIn) {
                        if (index.isCustomize!!.equals("1")) {
                            count++
                            holder.countValue.text = count.toString()

                            useconstants.offerPrice= index.offerPrice!!
                            adapterClick.onItemClick(
                                index.productId,
                                "custom",
                                "1",
                                index.offerPrice,
                                index.is_customize_quantity,
                                "",
                                productList[position].cartId
                            )

                            //SessionTwiclo(con).storeId = storeID
                            SessionTwiclo(con).serviceId = MainActivity.service_idStr
                        } else {
                            count++
                            useconstants.offerPrice= index.offerPrice!!
                            //Log.d("countcountcountcot",count.toString())
                            holder.countValue.text = count.toString()
                            holder.add_new_lay.visibility = View.GONE
                            holder.add_remove_lay.visibility = View.VISIBLE

                            if (SessionTwiclo(con).storeId.equals(storeID) || SessionTwiclo(con).storeId.equals(
                                    ""
                                )
                            ) {
                                adapterAddRemoveClick.onItemAddRemoveClick(
                                    index.productId, count.toString(), "add",
                                    index.offerPrice, "",
                                    productList[position].cartId, 0
                                )

                                SessionTwiclo(con).storeId = storeID
                                SessionTwiclo(con).serviceId = MainActivity.service_idStr
                            } else {

                                val builder = AlertDialog.Builder(con)
                                builder.setTitle("Replace cart item!")
                                builder.setMessage("Do you want to discard the previous selection?")
                                builder.setIcon(android.R.drawable.ic_dialog_alert)
                                builder.setPositiveButton("Yes") { _, _ ->
                                    adapterAddRemoveClick.clearCart()
                                    adapterAddRemoveClick.onItemAddRemoveClick(
                                        index.productId,
                                        count.toString(),
                                        "add",
                                        index.offerPrice,
                                        storeID,
                                        productList[position].cartId, 0
                                    )

                                    SessionTwiclo(con).storeId = storeID
                                    SessionTwiclo(con).serviceId = MainActivity.service_idStr

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

                        }
                    } else {
                        showLoginDialog("Please login to proceed")
                    }
                }
            }

            holder.plusLay.setOnClickListener {
                if (checkForInternet(con)) {

                    var count: Int = holder.countValue.text.toString().toInt()
                    if (index.isCustomize.equals("1")) {
                        var items: String? = ""
                        count++
                        if (index.customizeItemName != "") {
                            items = index.customizeItemName
                        }
                        adapterCartAddRemoveClick.onAddItemClick(
                            count.toString(),
                            index.productId,
                            items,
                            index.offerPrice,
                            index.isCustomize,
                            index.customizeItemId,
                            productList[position].cartId
                        )


                    } else {
                        count++
                        holder.countValue.text = count.toString()
                        adapterAddRemoveClick.onItemAddRemoveClick(
                            index.productId,
                            count.toString(),
                            "add",
                            index.offerPrice, "",
                            productList[position].cartId, 0
                        )
                    }
                }
            }

            holder.minusLay.setOnClickListener {
                if (checkForInternet(con)) {

                    var count: Int = holder.countValue.text.toString().toInt()
                    if (index.isCustomize.equals("1")) {
                        if (holder.countValue.text.toString().toInt() > 0) {
                            count--
                            holder.countValue.text = count.toString()
                            NewDBStoreItemsActivity.is_deleting_search = "0"
                            NewDBStoreItemsActivity.is_adding_search = "0"
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                index.productId,
                                count.toString(),
                                index.isCustomize,
                                index.customizeItemId,
                                productList[position].cartId
                            )
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
                                    productList[position].cartId,
                                    0
                                )
                            } else {
                                adapterAddRemoveClick.onItemAddRemoveClick(
                                    index.productId,
                                    count.toString(),
                                    "remove",
                                    index.offerPrice,
                                    "",
                                    productList[position].cartId, 0
                                )
                            }
                        }
                    }
                }
            }

        } else if (index.in_out_of_stock_status.equals("0")) {
            holder.add_new_lay.visibility = View.GONE
            holder.add_remove_lay.visibility = View.GONE
            holder.stock_status.visibility = View.VISIBLE
        }

        if (total_item_count - 1 == position) {
            holder.store_bottom_ll.visibility = View.VISIBLE
        } else {
            holder.store_bottom_ll.visibility = View.GONE
        }

    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
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
         //   con.startActivity(Intent(con, LoginActivity::class.java))
            con.startActivity(Intent( con, AuthActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))


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

    fun putvegdata(veglist:ArrayList<StoreItemProductsEntity>){
        val diffutil= vegDiffUtil(productList, veglist)
        val diffresult= DiffUtil.calculateDiff(diffutil)
        productList.clear()
        productList.addAll(veglist)
        diffresult.dispatchUpdatesTo(this)

    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var offerPrice = view.offerPrice
        var priceTxt = view.priceTxt
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
        var category_nameheader = view.category_nameheader
        var devider_catLL = view.devider_catLL
        var store_bottom_ll = view.store_bottom_ll
        var fssaitxt = view.fssaitxt
        var restaurant_nametxt = view.restaurant_nametxt
        var restaurant_addtxt = view.restaurant_addtxt
    }

    fun updateData(listData_: ArrayList<StoreItemProductsEntity>,total_item: Int) {
        Log.d("updateData__",listData_.size.toString()+"-"+total_item)
        productList = java.util.ArrayList<StoreItemProductsEntity>()
        productList.addAll(listData_)
        total_item_count=total_item
        notifyDataSetChanged()
    }

}