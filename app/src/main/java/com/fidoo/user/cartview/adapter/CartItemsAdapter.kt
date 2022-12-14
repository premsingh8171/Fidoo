package com.fidoo.user.cartview.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.cartview.model.CartModel
import com.fidoo.user.interfaces.AdapterClick
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.review_popup.view.*


class CartItemsAdapter(
    val con: Context,
    val cart: MutableList<CartModel.Cart>,
    private val adapterCartAddRemoveClick: AdapterCartAddRemoveClick,
    private val adapterClick: AdapterClick
) : RecyclerView.Adapter<CartItemsAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
    )

    override fun getItemCount() = cart.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //holder.storeName.text = cart.get(position).companyName
        holder.itemName.text = cart[position].productName
        holder.countValue.text = cart[position].quantity

        var items: String? = ""
        if (cart.get(position).customizeItem != null) {

            if (cart.get(position).customizeItem.size != 0) {
                var tempp: Double? = 0.0
                for (i in 0 until cart[position].customizeItem.size) {
                    items = cart.get(position).customizeItem.get(i).subCatName + ", " + items
                    tempp = tempp!! + cart.get(position).customizeItem.get(i).price.toDouble()
                }

                tempp = tempp!! + cart.get(position).offerPrice.toDouble()
                var itemPrice: Double = cart.get(position).quantity.toString().toDouble() * tempp
                holder.priceTxt.text = con.resources.getString(R.string.ruppee) + itemPrice.toString()
                Log.e("items", items.toString())
                items = items!!.substring(0, items.length - 2)

            } else{

                var itemPrice: Float = cart.get(position).quantity.toFloat() * cart.get(position).offerPrice.toFloat()
                holder.priceTxt.text = con.resources.getString(R.string.ruppee) + itemPrice.toString()

            }
        }

        if (!items.equals("")) {
            holder.customitemName.text = items
            holder.customitemName.visibility = View.VISIBLE
        } else {
            holder.customitemName.visibility = View.GONE
        }

        if (cart.get(position).is_customize.equals("1")){
            holder.itemView.iscustomizedOrnot.visibility=View.VISIBLE
        }else{
            holder.itemView.iscustomizedOrnot.visibility=View.GONE
        }


        try {
            if (cart.get(position).is_nonveg.equals("0")) {
                holder.productImg.setImageResource(R.drawable.veg)
                holder.productImg.visibility=View.VISIBLE
            }else if (cart.get(position).is_nonveg.equals("1")){
                if (cart[position].contains_egg.equals("1")) {
                    holder.productImg.setImageResource(R.drawable.egg_icon)
                    holder.productImg.visibility = View.VISIBLE
                } else {
                    holder.productImg.setImageResource(R.drawable.non_veg)
                    holder.productImg.visibility = View.VISIBLE
                }
            }else{
                holder.productImg.visibility=View.INVISIBLE
            }

        }catch (e:Exception){
            e.printStackTrace()
        }


        holder.deleteIcon.setOnClickListener {
            adapterClick.onItemClick(cart.get(position).productId, "", "", "",0,"", cart[position].cart_id)
        }

        holder.plusLay.setOnClickListener {
            if (checkForInternet(con)) {
                var count: Int = holder.countValue.text.toString().toInt()
                count++
                if (cart[position].customizeItem != null) {
                    if (cart[position].customizeItem.size != 0) {
                        adapterCartAddRemoveClick.onAddItemClick(
                            cart[position].productId,
                            items,
                            cart[position].offerPrice,
                            cart[position].is_customize,
                            cart[position].customizeItem[0].productCustomizeId,
                            cart[position].cart_id, count.toString()
                        )
                    } else {
                        adapterCartAddRemoveClick.onAddItemClick(
                            cart[position].productId,
                            items,
                            cart[position].offerPrice,
                            cart.get(position).is_customize,
                            "",
                            cart[position].cart_id,
                            count.toString()
                        )
                    }
                }
            }

            /*      var count: Int=holder.countValue.text.toString().toInt()
                     count++
                  holder.countValue.setText(count.toString())
                  var itemPrice: Int= count.toString().toInt() * cart.get(position).offerPrice.toString().toInt()
                  holder.priceTxt.text =  con.resources.getString(R.string.ruppee) + itemPrice.toString()*/
        }

        holder.minusLay.setOnClickListener {
            if (checkForInternet(con)) {

                if (holder.countValue.text.toString().toInt() > 0) {
                    var count: Int = holder.countValue.text.toString().toInt()
                    count--

                    if (cart.get(position).customizeItem != null) {

                        if (cart.get(position).customizeItem.size != 0) {
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                cart.get(position).productId,
                                cart.get(position).quantity,
                                cart.get(position).is_customize,
                                cart.get(position).customizeItem.get(0).productCustomizeId,
                                cart[position].cart_id,
                                count.toString()
                            )
                        } else {
                            adapterCartAddRemoveClick.onRemoveItemClick(
                                cart[position].productId,
                                cart[position].quantity,
                                cart[position].is_customize,
                                "",
                                cart[position].cart_id, count.toString()
                            )
                        }
                    }

                    holder.countValue.text = count.toString()

                    if (count == 0) {
                        cart.removeAt(position)
                        notifyDataSetChanged()
                    }

                    if (count > 0) {
                        var itemPrice: Float =
                            count.toString().toInt() * cart.get(position).offerPrice.toString()
                                .toFloat()


                        holder.priceTxt.text =
                            con.resources.getString(R.string.ruppee) + itemPrice.toString()
                    }

                }
            }

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

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //var storeName = view.storeName
        var itemName = view.productName
        var priceTxt = view.productPrice
        var customitemName = view.customitemName
        var productImg = view.productImage
        var countValue = view.tv_count
        var plusLay = view.btn_plus
        var minusLay = view.btn_minus
        var deleteIcon = view.dltBtn
    }

    private fun buyPopup() {

        val mDialogView = LayoutInflater.from(con).inflate(R.layout.review_popup, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(con).setView(mDialogView)
        /// .setTitle("Login Form")
        //show dialog
        val mAlertDialogg = mBuilder.show()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(mAlertDialogg.window!!.attributes)
        lp.gravity = Gravity.CENTER



        mAlertDialogg!!.window!!.attributes = lp
        mAlertDialogg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialogg.window!!.setGravity(Gravity.CENTER)
        //cancel button click of custom layout

        mDialogView.submitTxt.setOnClickListener {
            //dismiss dialog
            mAlertDialogg.dismiss()
        }

        mDialogView.cancelTxt.setOnClickListener {
            //dismiss dialog
            mAlertDialogg.dismiss()
        }

    }

    interface AdapterCartAddRemoveClick {
        fun onAddItemClick(
            productId: String?,
            items: String?,
            offerPrice: String?,
            isCustomize: String?,
            prodcustCustomizeId: String?,
            cart_id: String?,
            cart_quan: String?
        )

        fun onRemoveItemClick(
            productId: String?,
            quantity: String?,
            isCustomize: String?,
            prodcustCustomizeId: String?,
            cart_id: String?,
            cart_quan:String?
        )
    }


}