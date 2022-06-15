

package com.fidoo.user.restaurants.adapter

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
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.cartview.model.CartModel
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.restaurants.listener.AdapterCartAddRemoveClick
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.new_store_item_layout.view.*
import kotlinx.android.synthetic.main.new_store_item_layout.view.btn_minus
import kotlinx.android.synthetic.main.new_store_item_layout.view.btn_plus
import kotlinx.android.synthetic.main.new_store_item_layout.view.dltBtn
import kotlinx.android.synthetic.main.new_store_item_layout.view.iscustomizedOrnot
import kotlinx.android.synthetic.main.new_store_item_layout.view.productImage
import kotlinx.android.synthetic.main.new_store_item_layout.view.productName
import kotlinx.android.synthetic.main.new_store_item_layout.view.productPrice
import kotlinx.android.synthetic.main.new_store_item_layout.view.tv_count
import kotlinx.android.synthetic.main.review_popup.view.*


class StoreItemAdapter2(
    val con: Context,
    val cart: MutableList<CartModel.Cart>,
    private val adapterCartAddRemoveClick: AdapterCartAddRemoveClick,
    private val adapterClick: AdapterClick,
    private val adapterAddRemoveClick: AdapterAddRemoveClick
) : RecyclerView.Adapter<StoreItemAdapter2.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.new_store_item_layout, parent, false)
    )

    override fun getItemCount() = cart.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


//        if (cart.size == 1){
//            val layout = holder.itemView.searchAddDish
//            val params: ViewGroup.LayoutParams = layout.layoutParams
//            params.height = RecyclerView.LayoutParams.WRAP_CONTENT
//            params.width = RecyclerView.LayoutParams.MATCH_PARENT
//            layout.layoutParams = params
//        }
//        else{
//            val layout = holder.itemView.searchAddDish
//            val params: ViewGroup.LayoutParams = layout.layoutParams
//            params.height = RecyclerView.LayoutParams.WRAP_CONTENT
//            params.width = 1000
//            layout.layoutParams = params
//        }

        //holder.storeName.text = cart.get(position).companyName
        try {
            holder.itemName.text = cart[position].productName
            holder.countValue.text = cart[position].quantity
            holder.productDesc.text = cart[position].message
        }catch (e:Exception){}

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

//        if (!items.equals("")) {
//            holder.customitemName.text = items
//            holder.customitemName.visibility = View.VISIBLE
//        } else {
//            holder.customitemName.visibility = View.GONE
//        }

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

        Glide.with(con)
            .load(cart[position].productImage).thumbnail(0.05f)
            .fitCenter()
            .error(R.drawable.default_item)
            .into(holder.itemView.productImage)



        holder.plusLay.setOnClickListener {
            if (checkForInternet(con)) {
                var count: Int = holder.countValue.text.toString().toInt()
                count++
                if (cart[position].is_customize.equals("1")) {
                    if (cart[position].customizeItem.size != 0) {
                        adapterCartAddRemoveClick.onAddItemClick(
                            count.toString(),
                            cart[position].productId,
                            items,
                            cart[position].offerPrice,
                            cart[position].is_customize,
                            cart[position].customizeItem[0].productCustomizeId,
                            cart[position].cart_id
                        )
                    } else {
                        adapterCartAddRemoveClick.onAddItemClick(
                            cart[position].productId,
                            items,
                            cart[position].offerPrice,
                            cart[position].is_customize,
                            "",
                            cart[position].cart_id,
                            count.toString()
                        )
                    }
                } else {
                    holder.countValue.text = count.toString()
                    adapterAddRemoveClick.onItemAddRemoveClick(
                        cart[position].productId,
                        count.toString(),
                        "add",
                        cart[position].offerPrice, "",
                        cart[position].cart_id, 0
                    )
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

                var count: Int = holder.countValue.text.toString().toInt()
                if (cart[position].is_customize.equals("1")) {
                    if (holder.countValue.text.toString().toInt() > 0) {
                        count--
                        holder.countValue.text = count.toString()
                        adapterCartAddRemoveClick.onRemoveItemClick(
                            cart[position].productId,
                            count.toString(),
                            cart[position].is_customize,
                            cart[position].customizeItem[0].id,
                            cart[position].cart_id
                        )
                    }
                } else {
                    if (count > 0) {
                        count--
                        holder.countValue.text = count.toString()
                        adapterAddRemoveClick.onItemAddRemoveClick(
                            cart[position].productId,
                            count.toString(),
                            "remove",
                            cart[position].offerPrice,
                            "",
                            cart[position].cart_id, 0
                        )
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
        var customitemName = view.customitemName
        var priceTxt = view.productPrice
        var productImg = view.productImage
        var countValue = view.tv_count
        var plusLay = view.btn_plus
        var minusLay = view.btn_minus
        var deleteIcon = view.dltBtn
        var productDesc = view.product_desc_txt
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

    interface AdapterCartAddRemoveClick2 {
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