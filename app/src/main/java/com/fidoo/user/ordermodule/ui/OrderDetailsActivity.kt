package com.fidoo.user.ordermodule.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.adapter.ItemsAdapter
import com.fidoo.user.ordermodule.model.OrderDetailsModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.utils.statusBarTransparent
import com.fidoo.user.addressmodule.ViewPrescriptionActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_order_details.backIcon
import kotlinx.android.synthetic.main.activity_order_details.prescriptionLabel

class OrderDetailsActivity : com.fidoo.user.utils.BaseActivity() {

    var viewmodel: OrderDetailsViewModel? = null
    var mPresImg:String=""
    var address:String=""
    var items: MutableList<OrderDetailsModel.Item>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        statusBarTransparent()
        viewmodel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)

        backIcon.setOnClickListener {


            if(intent.hasExtra("type")) {
                startActivity(Intent(this,MainActivity::class.java))
                finishAffinity()
            }

            else {

                finish()

            }
        }

        prescriptionImageLay.setOnClickListener {
            startActivity(Intent(this, ViewPrescriptionActivity::class.java).putExtra("image",mPresImg))
        }

        supportCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + 8047165887)
            startActivity(dialIntent)

            // Syrow API for chat Support
            //startActivity(Intent(this, ChatActivity::class.java))

        }




        // Inflate the layout for this fragment

        viewmodel?.OrderDetailsResponse?.observe(this, { user ->
            dismissIOSProgress()
            val mModelData: OrderDetailsModel = user
            items = mModelData.items
            tv_address.text = mModelData.deliveryAddress
            tv_payment_mode.text = "Payment Mode: " +mModelData.paymentMode
            Log.e("orders details Response", Gson().toJson(mModelData))


            when {
                user.orderStatus.equals("0") -> {
                    orderStatusValue.text = "Failed"
                }
                user.orderStatus.equals("1") -> {
                    // holder.buttonValue.visibility = View.VISIBLE
                    orderStatusValue.text  = "In Progress"
                }
                user.orderStatus.equals("2") -> {
                    orderStatusValue.text= "Cancelled"
                }
                user.orderStatus.equals("11") -> {
                    orderStatusValue.text = "Preparing"
                }
                user.orderStatus.equals("3") -> {

                    orderStatusValue.text = "Delivered"
                }
                user.orderStatus.equals("5") -> {
                    //  holder.buttonValue.visibility = View.VISIBLE

                    orderStatusValue.text = "In Progress"
                }
                user.orderStatus.equals("6") -> {
                    // holder.buttonValue.visibility = View.VISIBLE

                    orderStatusValue.text = "Out for Delivery"
                }
                user.orderStatus.equals("7") -> {
                    orderStatusValue.text = "Accepted"
                }
                user.orderStatus.equals("9") -> {
                    orderStatusValue.text = "In Progress"
                }
                user.orderStatus.equals("10") -> {
                    orderStatusValue.text = "Out for delivery"
                }
                user.orderStatus.equals("8") -> {
                    orderStatusValue.text = "Rejected"
                }
            }


            val adapter = ItemsAdapter(this, mModelData.items)
            itemsRecyclerView?.layoutManager = GridLayoutManager(this, 1)
            itemsRecyclerView?.setHasFixedSize(true)
            itemsRecyclerView?.adapter = adapter

            if(mModelData.is_prescription.equals("1")) {
                prescriptionLabel.visibility= View.VISIBLE
                prescriptionImageLay.visibility= View.VISIBLE
                mPresImg=mModelData.prescription

                Glide.with(this)
                    .load(mModelData.prescription)
                    .fitCenter()
                    .into(presImg)

            } else {
                prescriptionLabel.visibility= View.GONE
                prescriptionImageLay.visibility= View.GONE
            }

            Glide.with(this)
                    .load(mModelData.storeImage)
                    .fitCenter()
                    .into(storeImg)

            if (mModelData.storeName == ""){
                storeName.visibility = View.GONE
            }else{
                storeName.text = mModelData.storeName
            }


            tv_order_id.text = "Order ID: " +mModelData.orderId
            locText.text = mModelData.storeAddress
            orderOnValue.text = mModelData.dateTime
            //itemTotal.text = items?.get(0)?.price_with_customization

            if (mModelData.discount == "" || mModelData.discount == "0"){
                label_cart_discount.visibility = View.GONE
                cart_discount.visibility = View.GONE
            }else{
                cart_discount.text = mModelData.discount
                label_cart_discount.text = "Cart Discount (" + mModelData.coupon_name +")"
            }



            val deliveryChargeWithTax = mModelData.deliveryCharge + mModelData.tax
            delivery_charge.text = resources.getString(R.string.ruppee) + "" + deliveryChargeWithTax
            delivery_coupon_label.text = "Delivery Discount (" +mModelData.delivery_coupon_name +")"
            delivery_coupon.text = "-" +resources.getString(R.string.ruppee) + "" + mModelData.delivery_discount.toFloat()
            grand_price.text = resources.getString(R.string.ruppee) + "" + mModelData.totalPrice
            sub_total.text = resources.getString(R.string.ruppee) + "" + mModelData.totalPrice
        })


        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            //showToast(user)


            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(intent.hasExtra("type"))
        {
            startActivity(Intent(this,MainActivity::class.java))
            finishAffinity()
        }
        else {
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        showIOSProgress()
        viewmodel?.getOrderDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId")
        )
    }
}
