package com.fidoo.user.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterCartAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.review_popup.view.*


class CartItemsAdapter(
        val con: Context,
        val cart: MutableList<com.fidoo.user.data.model.CartModel.Cart>,
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
        /*  holder.plusLay.setOnClickListener {

          }
          holder.minusLay.setOnClickListener {

          }*/
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
                var itemPrice: Int = cart.get(position).quantity.toString().toInt() * cart.get(position).offerPrice.toString().toInt()
                holder.priceTxt.text = con.resources.getString(R.string.ruppee) + itemPrice.toString()
            }
        }

        if (!items.equals("")) {
            holder.customitemName.text = items
            holder.customitemName.visibility = View.VISIBLE
        } else {
            holder.customitemName.visibility = View.GONE
        }
        Glide.with(con)
            .load(cart.get(position).productImage)
            .fitCenter()
            .into(holder.productImg)


        holder.deleteIcon.setOnClickListener {
            adapterClick.onItemClick(cart.get(position).productId, "", "", "",0,"", cart[position].cart_id)
        }

        holder.plusLay.setOnClickListener {

            if (cart[position].customizeItem != null) {

                if (cart[position].customizeItem.size != 0) {
                    adapterCartAddRemoveClick.onAddItemClick(
                            cart[position].productId,
                            items,
                            cart[position].offerPrice,
                            cart[position].is_customize,
                            cart[position].customizeItem[0].productCustomizeId,
                            cart[position].cart_id
                    )} else{
                    adapterCartAddRemoveClick.onAddItemClick(
                            cart[position].productId,
                            items,
                            cart[position].offerPrice,cart.get(position).is_customize,"", cart[position].cart_id
                    )
                }
            }
            /*      var count: Int=holder.countValue.text.toString().toInt()
                     count++
                  holder.countValue.setText(count.toString())

                  var itemPrice: Int= count.toString().toInt() * cart.get(position).offerPrice.toString().toInt()

                  holder.priceTxt.text =
                      con.resources.getString(R.string.ruppee) + itemPrice.toString()*/
        }
        holder.minusLay.setOnClickListener {
            if (holder.countValue.text.toString().toInt() > 0) {

                if (cart.get(position).customizeItem != null) {

                    if (cart.get(position).customizeItem.size != 0) {
                        adapterCartAddRemoveClick.onRemoveItemClick(
                            cart.get(position).productId,
                            cart.get(position).quantity,
                            cart.get(position).is_customize,
                            cart.get(position).customizeItem.get(0).productCustomizeId,
                            cart[position].cart_id
                        )}
                    else{
                        adapterCartAddRemoveClick.onRemoveItemClick(
                                cart[position].productId,
                                cart[position].quantity,
                                cart[position].is_customize,
                                "",
                                cart[position].cart_id
                        )
                    }
                }

                var count: Int = holder.countValue.text.toString().toInt()

                count--
                holder.countValue.text = count.toString()
                if (count == 0) {
                    cart.removeAt(position)
                    notifyDataSetChanged()

                }

                if (count > 0) {
                    var itemPrice: Int =
                            count.toString().toInt() * cart.get(position).offerPrice.toString().toInt()

                    holder.priceTxt.text =
                            con.resources.getString(R.string.ruppee) + itemPrice.toString()
                }
            }

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
}