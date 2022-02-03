package com.fidoo.user.ordermodule.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterReviewClick
import com.fidoo.user.ordermodule.model.MyOrdersModel
import com.fidoo.user.ordermodule.ui.*
import kotlinx.android.synthetic.main.orders_adapter.view.*
import kotlinx.android.synthetic.main.review_popup.view.*


class OrdersAdapter(
    val con: Context?,
    val orders: MutableList<MyOrdersModel.Order>,
    val adapterReviewClick: AdapterReviewClick,
    val onOrderItemClick: OnOrderItemClick
) : RecyclerView.Adapter<OrdersAdapter.UserViewHolder>() {
    var orderItemAdapter: OrderItemAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.orders_adapter, parent, false)
    )

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        // holder.storeName.text=orders.get(position).storeName
        holder.orderIdValue.text = orders[position].orderId
        //updated by shobha
        holder.orderIdTxt.text = "Order ID: #"+orders[position].orderId
        holder.orderOnValue.text = orders[position].orderDate
        holder.locText.text = orders[position].storeAddress

        holder.totalPriceValue.text = con?.resources?.getString(R.string.ruppee) + orders.get(position).totalPrice

//        holder.totalPriceValue.text=""
//        dynamicText(holder.totalPriceValue,con?.resources?.getString(R.string.ruppee) + orders.get(position ).totalPrice,"paid: ")


        if (orders[position].serviceTypeId == "4") {
            holder.storeName.text = "Send Package"
            holder.locText.text = orders[position].fromAddress
            holder.locToText.text = orders[position].toAddress
            holder.locToText.visibility = View.VISIBLE
            holder.too.visibility = View.VISIBLE
            holder.storeImg.visibility = View.VISIBLE
            holder.buttonValue.visibility = View.VISIBLE
            holder.buttonValue.text = "Track Order"
            //  holder.buttonValue.setTextColor(Color.rgb(51, 147, 71))
            holder.loc_icon.visibility = View.GONE
            // holder.storeImg.layoutParams = ConstraintLayout.LayoutParams(2, 120)
            holder.itemView.itemListTxt.text = orders[position].package_item_name
            Glide.with(con!!)
                //.load(orders[position].package_item_image)
                .load(R.drawable.send_pac_)
                .fitCenter()
                .placeholder(R.drawable.send_pac_)
                .error(R.drawable.send_pac_)
                .into(holder.storeImg)
        } else {
            holder.too.visibility = View.GONE
            holder.locToText.visibility = View.GONE
            holder.storeImg.visibility = View.VISIBLE
            holder.buttonValue.visibility = View.VISIBLE
            holder.loc_icon.visibility = View.GONE
            holder.storeName.text = orders[position].storeName
            //    holder.storeImg.layoutParams = ConstraintLayout.LayoutParams(120, 120)
            Glide.with(con!!)
                .load(orders[position].storeImage)
                .fitCenter()
                .placeholder(R.drawable.default_store)
                .error(R.drawable.default_store)
                .into(holder.storeImg)
        }

        try {
            if (orders[position].items.isNotEmpty()) {
                holder.itemView.itemListTxt.text = ""

                for (i in 0 until orders[position].items.size) {
                    if (orders[position].items.size - 1 == i) {
                        holder.itemView.itemListTxt.append(
                            orders[position].items[i].productName + " x "
                                    + orders[position].items[i].quantity
                        )
                    } else {
                        holder.itemView.itemListTxt.append(
                            orders[position].items[i].productName + " x "
                                    + orders[position].items[i].quantity + ","
                        )
                    }
                }
                holder.itemView.Itemlabeltxt.visibility=View.VISIBLE
                holder.itemView.itemListTxt.visibility=View.VISIBLE

//                orderItemAdapter = OrderItemAdapter(con!!, orders[position].items as ArrayList)
//                holder.itemRecyclerview_.adapter = orderItemAdapter
            }
            else{
                //updated by shobha
                holder.itemView.Itemlabeltxt.visibility=View.GONE
                holder.itemView.itemListTxt.visibility=View.GONE
            }
        } catch (e: Exception) { }

        //  0:failed, 1:success, 2: cancel, 3 Delivered, 4: received, 5: in progress, 6: Out for delivery ,11:preparing

        if (orders[position].serviceTypeId == "4") {
            when {
                orders[position].orderStatus.equals("0") -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Failed"
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
                orders[position].orderStatus.equals("1") -> {
                    // holder.buttonValue.visibility = View.VISIBLE
                    // holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Your package is in progress"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                orders[position].orderStatus.equals("2") -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Cancelled"
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
                orders[position].orderStatus.equals("11") -> {
                    // holder.orderStatusTxt.text = "Your food is being prepared"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                orders[position].orderStatus.equals("3") -> {
                    if (orders[position].is_rate_to_driver.equals("1")) {
                        if (orders[position].serviceTypeId == "4") {
                            holder.buttonValue.visibility = View.VISIBLE
                        } else {
                            // holder.buttonValue.text = "Repeat Order"
                            holder.buttonValue.isClickable = false
                            holder.buttonValue.visibility = View.VISIBLE

                        }

                    } else {
                        holder.buttonValue.text = "Review"
                    }

                    holder.orderStatusTxt.text = "Delivered"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("5") -> {
                    //  holder.buttonValue.visibility = View.VISIBLE
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.buttonValue.text = "Track Order"
                    holder.orderStatusTxt.text = "Your package is being processed"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("6") -> {
                    // holder.buttonValue.visibility = View.VISIBLE

                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Your package is out for delivery"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("7") -> {
                    holder.buttonValue.visibility = View.VISIBLE

                    holder.orderStatusTxt.text = "Your package is accepted"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("9") -> {
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.buttonValue.text = "Track Order"
                    holder.orderStatusTxt.text = "Your package is being processed"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("10") -> {
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.orderStatusTxt.text = "Your package is out for delivery"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("8") -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Rejected"
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
            }
            if (orders[position].is_rate_to_driver == "1" || orders[position].is_rate_to_merchant == "1") {
                holder.buttonValue.isClickable = false
                //holder.buttonValue.text = "Repeat Order"
                holder.buttonValue.visibility = View.GONE
                holder.rating_txt_ll.visibility = View.VISIBLE
                holder.rating_txt_.visibility = View.VISIBLE
                holder.rating_txt_.text = orders[position].delivery_rating.toString()
            } else {
                holder.rating_txt_ll.visibility = View.GONE
                holder.rating_txt_.visibility = View.GONE
            }
        } else {
            when {
                orders[position].orderStatus.equals("0") -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Failed"
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
                orders[position].orderStatus.equals("1") -> {
                    // holder.buttonValue.visibility = View.VISIBLE
                    // holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Your order is in progress"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                orders[position].orderStatus.equals("2") -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Cancelled"
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
                orders[position].orderStatus.equals("11") -> {
                    holder.orderStatusTxt.text = "Your food is being prepared"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                orders[position].orderStatus.equals("3") -> {
                    if (orders[position].is_rate_to_driver.equals("1")) {
                        if (orders[position].serviceTypeId == "4") {
                            holder.buttonValue.visibility = View.GONE
                        } else {
                            holder.buttonValue.text = "Repeat Order"
                            holder.buttonValue.isClickable = false
                            holder.buttonValue.visibility = View.VISIBLE

                        }

                    } else {
                        holder.buttonValue.text = "Review"
                    }

                    holder.orderStatusTxt.text = "Delivered"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("5") -> {
                    //  holder.buttonValue.visibility = View.VISIBLE
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Your order is being processed"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("6") -> {
                    // holder.buttonValue.visibility = View.VISIBLE

                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Your order is out for delivery"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("7") -> {
                    holder.buttonValue.visibility = View.VISIBLE

                    holder.orderStatusTxt.text = "Your order is accepted"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("9") -> {
                    if (orders[position].serviceTypeId == "4") {
                        holder.buttonValue.visibility = View.GONE
                    } else {
                        holder.buttonValue.visibility = View.VISIBLE
                    }

                    holder.orderStatusTxt.text = "Your order is being processed"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("10") -> {
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.orderStatusTxt.text = "Your order is out for delivery"
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                orders[position].orderStatus.equals("8") -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = "Rejected"
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
            }

            if (orders[position].is_rate_to_driver == "1" || orders[position].is_rate_to_merchant == "1") {
                holder.buttonValue.isClickable = false
                holder.buttonValue.text = "Repeat Order"
                holder.rating_txt_ll.visibility = View.VISIBLE
                holder.rating_txt_.visibility = View.VISIBLE
                holder.rating_txt_.text = orders[position].merchant_rating.toString()
            } else {
                holder.rating_txt_ll.visibility = View.GONE
                holder.rating_txt_.visibility = View.GONE
            }
        }


        holder.buttonValue.setOnClickListener {
            if (orders[position].serviceTypeId == "4") {
                if (holder.itemLay.buttonValue.text.equals("Track Order")) {
                    if (con != null) {
                        con.startActivity(
                            Intent(con, TrackSendPAckagesOrderActivity::class.java)
                                .putExtra("orderId", orders[position].orderId)
                                .putExtra(
                                    "delivery_boy_name",
                                    orders[position].delivery_boy_name
                                )
                                .putExtra(
                                    "delivery_boy_mobile",
                                    orders[position].delivery_boy_mobile
                                )
                                .putExtra("order_status", holder.orderStatusTxt.text)
                        )
                    }
                } else if (holder.itemLay.buttonValue.text.equals("Review")) {
                    if (orders[position].is_rate_to_driver.equals("0")) {
                        con.startActivity(
                            Intent(con, ReviewOrderSendPackageActivity::class.java)
                                .putExtra("orderId", orders[position].orderId)
                        )
                    }
                }
            } else {
                if (holder.itemLay.buttonValue.text.equals("Repeat Order")) {
                    onOrderItemClick.onRepeatOrder(orders[position], position)
                } else {
                    if (orders[position].serviceTypeId == "4" && orders[position].orderStatus.equals(
                            "3"
                        )
                    ) {

                        if (orders[position].is_rate_to_driver.equals("0")) {
                            // buyPopup(adapterReviewClick, orders[position].orderId)
                            con.startActivity(
                                Intent(con, ReviewOrderSendPackageActivity::class.java)
                                    .putExtra("orderId", orders[position].orderId)
                            )
                        }

//                con.startActivity(Intent(con, ChatRoomActivity::class.java).putExtra("APIKey", "gS0Bcjk3qQye7BbQQt+TsO4uT3Ja8zBVGzaNTQRfmNY="))
                    } else if (orders[position].orderStatus.equals("3")) {
                        if (orders[position].is_rate_to_driver.equals("0")) {
                            //  buyPopup(adapterReviewClick, orders[position].orderId)
                            if (orders[position].serviceTypeId.equals("4")) {
                                if (orders[position].is_rate_to_driver.equals("0")) {
                                    con.startActivity(
                                        Intent(con, ReviewOrderSendPackageActivity::class.java)
                                            .putExtra("orderId", orders[position].orderId)
                                    )
                                }
                            } else {
                                if (con != null) {
                                    con.startActivity(
                                        Intent(con, ReviewOrderActivity::class.java)
                                            .putExtra("orderId", orders[position].orderId)
                                    )
                                }
                            }

                        } else {
                            con?.startActivity(
                                Intent(con, TrackOrderActivity::class.java)
                                    .putExtra("orderId", orders[position].orderId)
                                    .putExtra(
                                        "delivery_boy_name",
                                        orders[position].delivery_boy_name
                                    )
                                    .putExtra(
                                        "delivery_boy_mobile",
                                        orders[position].delivery_boy_mobile
                                    )
                                    .putExtra("order_status", holder.orderStatusTxt.text)
                            )
                        }
                    }
                    // adapterClick.onItemClick(orders.get(position).orderId,"","")
                    else {
                        con.startActivity(
                            Intent(con, TrackOrderActivity::class.java)
                                .putExtra("orderId", orders[position].orderId)
                                .putExtra("delivery_boy_name", orders[position].delivery_boy_name)
                                .putExtra(
                                    "delivery_boy_mobile",
                                    orders[position].delivery_boy_mobile
                                )
                                .putExtra("order_status", holder.orderStatusTxt.text)
                        )
                    }
                }
            }
        }

        holder.itemLay.setOnClickListener {
            // adapterClick.onItemClick(orders.get(position).orderId,"","")
            if (orders[position].serviceTypeId == "4") {


            } else if (orders[position].serviceTypeId == "0" || orders[position].serviceTypeId == "") {
                //Toast.makeText(con, "Invalid Request", Toast.LENGTH_LONG).show()
                val intent = Intent(con, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", orders[position].orderId)
                intent.putExtra("toaddress", orders[position].toAddress)
                con.startActivity(intent)
            } else if (orders[position].orderStatus == "0") {
                Toast.makeText(con, "No details found for failed order", Toast.LENGTH_LONG).show()
            }
        }


    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var buttonValue = view.buttonValue
        var storeImg = view.storeImg
        var storeName = view.storeName
        var locText = view.locText
        var locToText = view.locToText
        var loc_icon = view.loc_icon
        var too = view.too
        var orderOnValue = view.orderOnValue
        var totalPriceValue = view.totalPriceValue
        var orderIdValue = view.orderIdValue
        var orderStatusTxt = view.orderStatusTxt
        var itemLay = view.itemLay
        var itemRecyclerview_ = view.itemRecyclerview_
        var rating_txt_ll = view.rating_txt_ll
        var rating_txt_ = view.rating_txt_
        var orderIdTxt = view.orderIdTxt
    }


    private fun buyPopup(
        adapterReviewClick: AdapterReviewClick,
        orderId: String
    ) {

        val mDialogView = LayoutInflater.from(con).inflate(R.layout.rating_popup, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(con)
            .setView(mDialogView)
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
            adapterReviewClick.onReviewDoneClick(
                orderId,
                mDialogView.ratingStore.rating.toString(),
                mDialogView.reviewStore.text.toString(),
                mDialogView.ratingDriver.rating.toString(),
                mDialogView.reviewDriver.text.toString()
            )

        }

        mDialogView.cancelTxt.setOnClickListener {
            //dismiss dialog

            mAlertDialogg.dismiss()
        }

    }

    interface OnOrderItemClick {
        fun onCancelOrder(orders: MyOrdersModel.Order, pos: Int)
        fun onRepeatOrder(orders: MyOrdersModel.Order, pos: Int)
    }

    fun dynamicText(view: TextView, value: String, key: String) {
        view.text = ""
        val str = SpannableString(key)
        str.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            str.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(StyleSpan(Typeface.BOLD), 0, str.length, 0)
        view.append(str)
        view.append(value)

    }
}