package com.fidoo.user.ordermodule.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterReviewClick
import com.fidoo.user.ordermodule.model.MyOrdersModel
import com.fidoo.user.ordermodule.ui.*
import kotlinx.android.synthetic.main.orders_adapter.view.*


class OrdersAdapter(
    val context: Context?,
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
        holder.orderIdValue.text = orders[position].orderId
        holder.orderIdTxt.text = "Order ID: #" + orders[position].orderId
        holder.orderOnValue.text = orders[position].orderDate
        holder.locText.text = orders[position].storeAddress

        holder.totalPriceValue.text =
            context?.resources?.getString(R.string.ruppee) + orders.get(position).totalPrice

        if (orders[position].serviceTypeId == "4") {
          //  holder.storeName.text = context?.getString(R.string.send_package)
            holder.storeName.text = orders[position].deliverCatName
           // holder.locText.text = orders[position].fromAddress
            holder.locText.text = orders[position].toAddress
            holder.locToText.text = orders[position].toAddress
            holder.too.visibility = View.VISIBLE
            holder.storeImg.visibility = View.VISIBLE
            holder.buttonValue.visibility = View.VISIBLE
            holder.buttonValue.text = context?.getString(R.string.track_order)
            holder.loc_icon.visibility = View.GONE
            holder.too.visibility = View.GONE
            holder.locToText.visibility = View.GONE
            holder.itemView.itemListTxt.text = orders[position].package_item_name
            holder.itemView.itemListTxt.visibility = View.VISIBLE
            holder.itemView.Itemlabeltxt.visibility = View.VISIBLE

            Glide.with(context!!)
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

            Glide.with(context!!)
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
                holder.itemView.Itemlabeltxt.visibility = View.VISIBLE
                holder.itemView.itemListTxt.visibility = View.VISIBLE
            } else {
                holder.itemView.Itemlabeltxt.visibility = View.GONE
                holder.itemView.itemListTxt.visibility = View.GONE
            }
        } catch (e: Exception) {
        }

        //  0:failed, 1:success, 2: cancel, 3 Delivered, 4: received, 5: in progress, 6: Out for delivery ,11:preparing

        if (orders[position].serviceTypeId == "4") {
            when (orders[position].orderStatus) {
                "0" -> {
                    holder.view2.visibility = View.GONE
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = context.getString(R.string.failed)
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
                "1" -> {
                    holder.view2.visibility = View.VISIBLE

                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_package_is_in_progress)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "2" -> {
                    holder.view2.visibility = View.GONE
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text = context.getString(R.string.cancelled)
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
                "11" -> {
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "3" -> {
                    if (orders[position].is_rate_to_driver.equals("1")) {
                        if (orders[position].serviceTypeId == "4") {
                            holder.buttonValue.visibility = View.VISIBLE
                        } else {
                            holder.buttonValue.isClickable = false
                            holder.buttonValue.visibility = View.VISIBLE
                        }

                    } else {
                        holder.buttonValue.text = context.getString(R.string.review)
                    }
                    holder.view2.visibility = View.VISIBLE
                    holder.orderStatusTxt.text = context.getString(R.string.delivered)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "5" -> {
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.buttonValue.text = context.getString(R.string.track_order)
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_package_is_being_processed)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "6" -> {
                    holder.view2.visibility = View.VISIBLE
                    holder.buttonValue.visibility = View.GONE
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_package_is_out_for_delivery)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "7" -> {
                    holder.view2.visibility = View.VISIBLE
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_package_is_accepted)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "9" -> {
                    holder.view2.visibility = View.VISIBLE
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.buttonValue.text = context.getString(R.string.track_order)
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_package_is_being_processed)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "10" -> {
                    holder.view2.visibility = View.VISIBLE
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_package_is_out_for_delivery)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "8" -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.view2.visibility = View.GONE
                    holder.orderStatusTxt.text = context.getString(R.string.rejected)
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))
                }
            }
            if (orders[position].is_rate_to_driver == "1" || orders[position].is_rate_to_merchant == "1") {
                holder.buttonValue.isClickable = false
                holder.buttonValue.visibility = View.GONE
                holder.rating_txt_ll.visibility = View.VISIBLE
                holder.rating_txt_.visibility = View.VISIBLE
                holder.rating_txt_.text = orders[position].delivery_rating.toString()
            } else {
                holder.rating_txt_ll.visibility = View.GONE
                holder.rating_txt_.visibility = View.GONE
            }
        } else {
            when (orders[position].orderStatus) {
                "0" -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.view2.visibility = View.GONE
                    holder.orderStatusTxt.text = context.getString(R.string.failed)
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))
                }
                "1" -> {
                    holder.view2.visibility = View.VISIBLE
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_order_is_in_progress)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "2" -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.view2.visibility = View.GONE
                    holder.orderStatusTxt.text = context.getString(R.string.cancelled)
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))

                }
                "11" -> {
                    holder.view2.visibility = View.VISIBLE
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_food_is_being_prepared)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "3" -> {
                    if (orders[position].is_rate_to_driver.equals("1")) {
                        if (orders[position].serviceTypeId == "4") {
                            holder.buttonValue.visibility = View.GONE
                        } else {
                            holder.buttonValue.text = context.getString(R.string.repeat_order)
                            holder.buttonValue.isClickable = false
                            holder.buttonValue.visibility = View.VISIBLE
                        }

                    } else {
                        holder.buttonValue.text = context.getString(R.string.review)
                    }
                    holder.view2.visibility = View.VISIBLE
                    holder.orderStatusTxt.text = context.getString(R.string.delivered)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "5" -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.view2.visibility = View.VISIBLE

                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_order_is_being_processed)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "6" -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.view2.visibility = View.VISIBLE

                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_order_is_out_for_delivery)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))

                }
                "7" -> {
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.view2.visibility = View.VISIBLE

                    holder.orderStatusTxt.text = context.getString(R.string.your_order_is_accepted)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "9" -> {
                    if (orders[position].serviceTypeId == "4") {
                        holder.buttonValue.visibility = View.GONE
                    } else {
                        holder.buttonValue.visibility = View.VISIBLE
                    }
                    holder.view2.visibility = View.VISIBLE

                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_order_is_being_processed)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "10" -> {
                    holder.buttonValue.visibility = View.VISIBLE
                    holder.orderStatusTxt.text =
                        context.getString(R.string.your_order_is_out_for_delivery)
                    holder.orderStatusTxt?.setTextColor(Color.parseColor("#339347"))
                }
                "8" -> {
                    holder.buttonValue.visibility = View.GONE
                    holder.view2.visibility = View.GONE
                    holder.orderStatusTxt.text = context.getString(R.string.rejected)
                    holder.orderStatusTxt?.setTextColor(Color.rgb(240, 0, 0))
                }
            }

            if (orders[position].is_rate_to_driver == "1" || orders[position].is_rate_to_merchant == "1") {
                holder.buttonValue.isClickable = false
                holder.buttonValue.text = context.getString(R.string.repeat_order)
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
                if (holder.itemLay.buttonValue.text.equals(TRACK_ORDER)) {
                    if (context != null) {
                        context.startActivity(
                            Intent(context, TrackSendPAckagesOrderActivity::class.java)
                                .putExtra(ORDER_ID, orders[position].orderId)
                                .putExtra(
                                    DELIVERY_BOY_NAME,
                                    orders[position].delivery_boy_name
                                )
                                .putExtra(
                                    DELIVERY_BOY_MOBILE,
                                    orders[position].delivery_boy_mobile
                                )
                                .putExtra(ORDER_STATUS, holder.orderStatusTxt.text)
                        )
                    }
                } else if (holder.itemLay.buttonValue.text.equals(REVIEW)) {
                    if (orders[position].is_rate_to_driver.equals("0")) {
                        context.startActivity(
                            Intent(context, ReviewOrderSendPackageActivity::class.java)
                                .putExtra(ORDER_ID, orders[position].orderId)
                        )
                    }
                }
            } else {
                if (holder.itemLay.buttonValue.text.equals(REPEAT_ORDER)) {
                    onOrderItemClick.onRepeatOrder(orders[position], position)
                } else {
                    if (orders[position].serviceTypeId == "4" && orders[position].orderStatus.equals(
                            "3"
                        )
                    ) {
                        if (orders[position].is_rate_to_driver.equals("0")) {
                            context.startActivity(
                                Intent(context, ReviewOrderSendPackageActivity::class.java)
                                    .putExtra(ORDER_ID, orders[position].orderId)
                            )
                        }
                    } else if (orders[position].orderStatus.equals("3")) {
                        if (orders[position].is_rate_to_driver.equals("0")) {
                            if (orders[position].serviceTypeId.equals("4")) {
                                if (orders[position].is_rate_to_driver.equals("0")) {
                                    context.startActivity(
                                        Intent(context, ReviewOrderSendPackageActivity::class.java)
                                            .putExtra(ORDER_ID, orders[position].orderId)
                                    )
                                }
                            } else {
                                if (context != null) {
                                    context.startActivity(
                                        Intent(context, ReviewOrderActivity::class.java)
                                            .putExtra(ORDER_ID, orders[position].orderId)
                                    )
                                }
                            }

                        } else {
                            context?.startActivity(
                                Intent(context, TrackOrderActivity::class.java)
                                    .putExtra(ORDER_ID, orders[position].orderId)
                                    .putExtra(
                                        DELIVERY_BOY_NAME,
                                        orders[position].delivery_boy_name
                                    )
                                    .putExtra(
                                        DELIVERY_BOY_MOBILE,
                                        orders[position].delivery_boy_mobile
                                    )
                                    .putExtra(ORDER_STATUS, holder.orderStatusTxt.text)
                            )
                        }
                    } else {
                        context.startActivity(
                            Intent(context, TrackOrderActivity::class.java)
                                .putExtra(ORDER_ID, orders[position].orderId)
                                .putExtra(DELIVERY_BOY_NAME, orders[position].delivery_boy_name)
                                .putExtra(
                                    DELIVERY_BOY_MOBILE,
                                    orders[position].delivery_boy_mobile
                                )
                                .putExtra(ORDER_STATUS, holder.orderStatusTxt.text)
                        )
                    }
                }
            }
        }

        holder.itemLay.setOnClickListener {
            if (orders[position].serviceTypeId == "4") {

                val intent = Intent(context, OrderDetailsSendPackageActivity::class.java)
                intent.putExtra(ORDER_ID, orders[position].orderId)
                intent.putExtra(TO_ADDRESS, orders[position].toAddress)
                context.startActivity(intent)

            } else if (orders[position].serviceTypeId == "0" || orders[position].serviceTypeId == "") {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra(ORDER_ID, orders[position].orderId)
                intent.putExtra(TO_ADDRESS, orders[position].toAddress)
                context.startActivity(intent)
            } else if (orders[position].orderStatus == "0") {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_details_found_for_failed_order),
                    Toast.LENGTH_LONG
                )
                    .show()
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
        var rating_txt_ll = view.rating_txt_ll
        var rating_txt_ = view.rating_txt_
        var orderIdTxt = view.orderIdTxt
        var view2 = view.view2
    }

    interface OnOrderItemClick {
        fun onCancelOrder(orders: MyOrdersModel.Order, pos: Int)
        fun onRepeatOrder(orders: MyOrdersModel.Order, pos: Int)
    }

    companion object {
        private const val TO_ADDRESS = "toaddress"
        private const val ORDER_ID = "orderId"
        private const val ORDER_STATUS = "order_status"
        private const val DELIVERY_BOY_MOBILE = "delivery_boy_mobile"
        private const val DELIVERY_BOY_NAME = "delivery_boy_name"
        private const val REPEAT_ORDER = "Repeat Order"
        private const val REVIEW = "Review"
        private const val TRACK_ORDER = "Track Order"
    }
}