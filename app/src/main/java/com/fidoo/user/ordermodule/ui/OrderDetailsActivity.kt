package com.fidoo.user.ordermodule.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.adapter.ItemsAdapter
import com.fidoo.user.ordermodule.model.OrderDetailsModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.viewprescription.ViewPrescriptionActivity
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_order_details.backIcon
import kotlinx.android.synthetic.main.activity_order_details.prescriptionLabel

@Suppress("DEPRECATION")
class OrderDetailsActivity : com.fidoo.user.utils.BaseActivity() {

    var viewmodel: OrderDetailsViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var mPresImg:String=""
    var items: MutableList<OrderDetailsModel.Item>? = null

    private var mMixpanel: MixpanelAPI? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_order_details)
        viewmodel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        backIcon.setOnClickListener {

            if(intent.hasExtra("type")) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }

            else {
                finish()
                AppUtils.finishActivityLeftToRight(this)
            }
        }

        prescriptionImageLay.setOnClickListener {
            startActivity(Intent(this, ViewPrescriptionActivity::class.java).putExtra("image",mPresImg))
        }

        supportCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
          //  dialIntent.data = Uri.parse("tel:" + 8047165887)
            dialIntent.data = Uri.parse("tel:" + 8047165893)
            startActivity(dialIntent)

            // Syrow API for chat Support
            //startActivity(Intent(this, ChatActivity::class.java))

        }


        // Inflate the layout for this fragment

        viewmodel?.OrderDetailsResponse?.observe(this, { user ->
            Log.e("orderDetails__",Gson().toJson(user))
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

                   // orderStatusValue.text = "Delivered"
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


            tv_order_id.text = "Order: #" +mModelData.orderId
            locText.text = mModelData.storeAddress
            orderOnValue.text = mModelData.delivered_at
            //itemTotal.text = items?.get(0)?.price_with_customization

            if (mModelData.discount == "" || mModelData.discount == "0"){
                label_cart_discount.visibility = View.GONE
                cart_discount.visibility = View.GONE
            }else{
                cart_discount.text = "-" + resources.getString(R.string.ruppee) + "" + mModelData.discount
                label_cart_discount.text = "Cart Discount (" + mModelData.coupon_name +" )"
            }


            val deliveryChargeWithTax = mModelData.deliveryCharge + mModelData.tax
            delivery_charge.text = resources.getString(R.string.ruppee) + "" + deliveryChargeWithTax
            delivery_coupon_label.text = "Delivery Discount (" +mModelData.delivery_coupon_name +")"

            if ( mModelData.delivery_discount.toFloat().toString().equals("0.0")){
                label_delivery_charge.visibility=View.GONE
                delivery_coupon.visibility=View.GONE
            }else {
                label_delivery_charge.visibility=View.VISIBLE
                delivery_coupon.visibility=View.VISIBLE
                delivery_coupon.text =
                    "-" + resources.getString(R.string.ruppee) + "" + mModelData.delivery_discount.toFloat()
            }

            grand_price.text = resources.getString(R.string.ruppee) + "" + mModelData.totalPrice
            sub_total.text = resources.getString(R.string.ruppee) + "" + mModelData.totalPrice
        })


        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cart response", Gson().toJson(user))
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(intent.hasExtra("type"))
        {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
        else {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }
    }


    override fun onResume() {
        super.onResume()
        showIOSProgress()
        viewmodel?.getOrderDetails(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId")
        )
        viewmodelusertrack?.customerActivityLog(SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).mobileno,"OrderDetails Screen",
            SplashActivity.appversion, StoreListActivity.serive_id_,SessionTwiclo(this).deviceToken
        )
    }


}
