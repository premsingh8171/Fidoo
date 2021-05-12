package com.fidoo.user.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.interfaces.AdapterReviewClick
import com.fidoo.user.view.OrderDetailsActivity
import com.fidoo.user.view.TrackOrderActivity
import kotlinx.android.synthetic.main.orders_adapter.view.*
import kotlinx.android.synthetic.main.review_popup.view.*


class OrdersAdapter(
    val con: Context?,
    val orders: MutableList<com.fidoo.user.data.model.MyOrdersModel.Order>,
    val adapterReviewClick: AdapterReviewClick,
    val adapterClick: AdapterClick
) : RecyclerView.Adapter<OrdersAdapter.UserViewHolder>() {
    var star:String?="1"
    var improvement:String?=""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.orders_adapter, parent, false)
    )

    override fun getItemCount() = orders.size
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        // holder.storeName.text=orders.get(position).storeName
        holder.orderIdValue.text = orders[position].orderId
        holder.orderOnValue.text = orders[position].orderDate
        holder.locText.text = orders[position].storeAddress
        holder.totalPriceValue.text = con?.resources?.getString(R.string.ruppee) + orders.get(
            position
        ).totalPrice



        if (orders[position].serviceTypeId == "4") {
            holder.storeName.text = "Send Package"
            holder.locText.text = orders[position].fromAddress
            holder.locToText.text = orders[position].toAddress
            holder.too.visibility = View.VISIBLE
            holder.storeImg.visibility = View.GONE
            holder.buttonValue.visibility = View.VISIBLE
            holder.buttonValue.text = "Support"
            holder.buttonValue.setTextColor(Color.rgb(51, 147, 71))
            holder.loc_icon.visibility = View.GONE
        } else{
            holder.too.visibility = View.GONE
            holder.locToText.visibility = View.GONE
            holder.storeImg.visibility = View.VISIBLE
            holder.buttonValue.visibility = View.VISIBLE
            holder.loc_icon.visibility = View.VISIBLE
            holder.storeName.text = orders[position].storeName
        }

        //  0:failed, 1:success, 2: cancel, 3 delivered, 4: received, 5: in progress, 6: Out for delivery ,11:preparing
        when {
            orders[position].orderStatus.equals("0") -> {
                holder.buttonValue.visibility = View.GONE
                holder.orderStatusTxt.text = "Failed"
            }
            orders[position].orderStatus.equals("1") -> {
                // holder.buttonValue.visibility = View.VISIBLE
                // holder.buttonValue.visibility = View.GONE
                holder.orderStatusTxt.text = "Your order is in progress"
            }
            orders[position].orderStatus.equals("2") -> {
                holder.buttonValue.visibility = View.GONE
                holder.orderStatusTxt.text = "your order is cancelled"
            }
            orders[position].orderStatus.equals("11") -> {
                holder.orderStatusTxt.text = "Your food is being prepared"
            }
            orders[position].orderStatus.equals("3") -> {
                if (orders[position].is_rate_to_driver.equals("1")) {
                    holder.buttonValue.visibility = View.GONE
                } else {
                    holder.buttonValue.text = "Review"
                }

                holder.orderStatusTxt.text = "Your order is delivered"
            }
            orders[position].orderStatus.equals("5") -> {
                //  holder.buttonValue.visibility = View.VISIBLE
                holder.buttonValue.visibility = View.GONE
                holder.orderStatusTxt.text = "Your order is being processed"
            }
            orders[position].orderStatus.equals("6") -> {
                // holder.buttonValue.visibility = View.VISIBLE

                holder.buttonValue.visibility = View.GONE
                holder.orderStatusTxt.text = "Your order is out for delivery"
            }
            orders[position].orderStatus.equals("7") -> {
                holder.buttonValue.visibility = View.VISIBLE


                holder.orderStatusTxt.text = "Your order is accepted"
            }
            orders[position].orderStatus.equals("9") -> {
                if (orders[position].serviceTypeId == "4"){
                    holder.buttonValue.visibility = View.GONE
                }else{
                    holder.buttonValue.visibility = View.VISIBLE
                }

                holder.orderStatusTxt.text = "Your order is being processed"
            }
            orders[position].orderStatus.equals("10") -> {
                holder.buttonValue.visibility = View.VISIBLE
                holder.orderStatusTxt.text = "Your order is out for delivery"
            }
            orders[position].orderStatus.equals("8") -> {
                holder.buttonValue.visibility = View.GONE
                holder.orderStatusTxt.text = "Your order is rejected"
            }
        }



        if (orders[position].is_rate_to_driver == "1"){
            holder.buttonValue.isClickable = false
            holder.buttonValue.text = "Reviewed"
        }

        //    holder.orderStatusTxt.text="In Progress"
        Glide.with(con!!)
            .load(orders[position].storeImage)
            .fitCenter()
            .into(holder.storeImg)

        if (position == 1) {
            //  holder.buttonValue.text = "Rating & Review"
        }

        holder.buttonValue.setOnClickListener {

            if (orders[position].serviceTypeId == "4" && orders[position].orderStatus.equals("3")){

                if (orders[position].is_rate_to_driver.equals("0")) {
                    buyPopup(adapterReviewClick, orders[position].orderId)
                }



//                con.startActivity(Intent(con, ChatRoomActivity::class.java).putExtra("APIKey", "gS0Bcjk3qQye7BbQQt+TsO4uT3Ja8zBVGzaNTQRfmNY="))
            }else if (orders[position].orderStatus.equals("3")) {
                if (orders[position].is_rate_to_driver.equals("0")) {
                    buyPopup(adapterReviewClick, orders[position].orderId)
                } else {
                    con.startActivity(
                        Intent(con, TrackOrderActivity::class.java)
                            .putExtra("orderId", orders[position].orderId)
                            .putExtra("delivery_boy_name", orders[position].delivery_boy_name)
                            .putExtra("delivery_boy_mobile", orders[position].delivery_boy_mobile)
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
                        .putExtra("delivery_boy_mobile", orders[position].delivery_boy_mobile)
                        .putExtra("order_status", holder.orderStatusTxt.text)
                )
            }
        }
        holder.itemLay.setOnClickListener {
            // adapterClick.onItemClick(orders.get(position).orderId,"","")
            if (orders[position].serviceTypeId == "4") {

            }else if(orders[position].serviceTypeId == "0" || orders[position].serviceTypeId == ""){
                //Toast.makeText(con, "Invalid Request", Toast.LENGTH_LONG).show()
                val intent =Intent(con, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", orders[position].orderId)
                intent.putExtra("toaddress", orders[position].toAddress)
                con.startActivity(intent)
            }else if (orders[position].orderStatus == "0"){
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
        var itemValue = view.itemValue
        var orderOnValue = view.orderOnValue
        var totalPriceValue = view.totalPriceValue
        var orderIdValue = view.orderIdValue
        var orderStatusTxt = view.orderStatusTxt
        var itemLay = view.itemLay
    }


    private fun buyPopup(
        adapterReviewClick: AdapterReviewClick,
        orderId: String
    ) {

        val mDialogView = LayoutInflater.from(con).inflate(R.layout.review_popup, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(con)
            .setView(mDialogView)
        /// .setTitle("Login Form")
        //show dialog
        val mAlertDialogg = mBuilder.show()
        mAlertDialogg?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(mAlertDialogg.window!!.attributes)
        lp.gravity = Gravity.CENTER



        mAlertDialogg!!.window!!.attributes = lp
        mAlertDialogg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialogg.window!!.setGravity(Gravity.CENTER)
        //cancel button click of custom layout

       // img_review_img,reviewName_txt,cancel_reviewPopUp
        //poor_icon_select
        //ok_icon_select
        //good_icon_select
        //excellent_icon_select
        //selection_ques_txt
        //itemPackaging_txt_selection
        //delivery_txt_selection
        //appInterface_txt_selection
        //deliveryExperience_txt_selection
        //remark_txt
        //submitTextBtn


        mDialogView.poor_icon_select.setBackgroundResource(R.drawable.rectangle_border)

        mDialogView.cancel_reviewPopUp.setOnClickListener { mAlertDialogg.dismiss() }

        mDialogView.poor_icon_select.setOnClickListener {
            star="1"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.poor)
            mDialogView.reviewName_txt.text="Poor"
            mDialogView.selection_ques_txt.setText(R.string.poor_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.rectangle_border)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)

        }

        mDialogView.ok_icon_select.setOnClickListener {
            star="2"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.ok)
            mDialogView.reviewName_txt.text="Oka'ish"
            mDialogView.selection_ques_txt.setText(R.string.ok_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.rectangle_border)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
        }

        mDialogView.good_icon_select.setOnClickListener {
            star="3"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.good)
            mDialogView.reviewName_txt.text="Good"
            mDialogView.selection_ques_txt.setText(R.string.good_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.rectangle_border)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
        }

        mDialogView.excellent_icon_select.setOnClickListener {
            star="4"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.excellent)
            mDialogView.reviewName_txt.text="Excellent"
            mDialogView.selection_ques_txt.setText(R.string.excellent_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.rectangle_border)
        }

        mDialogView.itemPackaging_txt_selection.setOnClickListener {
            improvement="Item packaging"
            mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
            mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)

        }

        mDialogView.delivery_txt_selection.setOnClickListener {
            improvement="Delivery Time"
            mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
            mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
        }

        mDialogView.appInterface_txt_selection.setOnClickListener {
            improvement="App interface"
            mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
            mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
        }

        mDialogView.deliveryExperience_txt_selection.setOnClickListener {
            improvement="Delivery experience"
            mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
        }

        mDialogView.submitTextBtn.setOnClickListener {
            mAlertDialogg.dismiss()
        }







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
}