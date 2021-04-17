package com.fidoo.user.view

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
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.viewmodels.OrderDetailsViewModel
import com.fidoo.user.utils.statusBarTransparent
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_order_details.backIcon
import kotlinx.android.synthetic.main.activity_order_details.discountLabel
import kotlinx.android.synthetic.main.activity_order_details.discountValue
import kotlinx.android.synthetic.main.activity_order_details.grandPrice
import kotlinx.android.synthetic.main.activity_order_details.prescriptionLabel

class OrderDetailsActivity : com.fidoo.user.utils.BaseActivity() {

    var viewmodel: OrderDetailsViewModel? = null
    var mPresImg:String=""
    var address:String=""
    var items: MutableList<com.fidoo.user.data.model.OrderDetailsModel.Item>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        statusBarTransparent()
        viewmodel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        backIcon.setOnClickListener {


            if(intent.hasExtra("type"))
            {
                startActivity(Intent(this,MainActivity::class.java))
                finishAffinity()
            }
            else {

                finish()

            }
        }

        prescriptionImageLay.setOnClickListener {
            startActivity(Intent(this,ViewPrescriptionActivity::class.java).putExtra("image",mPresImg))
        }

        supportCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + com.fidoo.user.data.session.SessionTwiclo(
                this
            ).profileDetail.account.support_number)
            startActivity(dialIntent)

            // Syrow API for chat Support
            //startActivity(Intent(this,ChatRoomActivity::class.java)).putExtra("APIKey", "-----Key----")

        }


        showIOSProgress()
        viewmodel?.getOrderDetails(
            com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accountId,
            com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId")
        )

        // Inflate the layout for this fragment

        viewmodel?.OrderDetailsResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            val mModelData: com.fidoo.user.data.model.OrderDetailsModel = user
            items = mModelData.items
            Log.e("orders details Response", Gson().toJson(mModelData))


            if (user.orderStatus.equals("0")) {
                orderStatusValue.text = "Failed"
            } else if (user.orderStatus.equals("1")) {
                // holder.buttonValue.visibility = View.VISIBLE
                orderStatusValue.text  = "In Progress"
            } else if (user.orderStatus.equals("2")) {
                orderStatusValue.text= "Cancelled"
            } else if (user.orderStatus.equals("11")) {
                orderStatusValue.text = "Preparing"
            } else if (user.orderStatus.equals("3")) {

                orderStatusValue.text = "Delivered"
            } else if (user.orderStatus.equals("5")) {
                //  holder.buttonValue.visibility = View.VISIBLE

                orderStatusValue.text = "In Progress"
            } else
                if (user.orderStatus.equals("6")) {
                    // holder.buttonValue.visibility = View.VISIBLE

                    orderStatusValue.text = "Out for Delivery"
                } else
                    if (user.orderStatus.equals("7")) {
                        orderStatusValue.text = "Accepted"
                    } else
                        if (user.orderStatus.equals("9")) {
                            orderStatusValue.text = "In Progress"
                        }else
                            if (user.orderStatus.equals("10")) {
                                orderStatusValue.text = "Out of delivery"
                            } else
                                if (user.orderStatus.equals("8")) {
                                    orderStatusValue.text = "Rejected"
                                }

            if (intent.hasExtra("toaddress")){
                address= intent.getStringExtra("toaddress").toString()
                deliveryAddress.text = address

                Log.e("from intent ","addddresss")
            }


            val adapter = ItemsAdapter(this, mModelData.items)
            itemsRecyclerView?.layoutManager = GridLayoutManager(this, 1)
            itemsRecyclerView?.setHasFixedSize(true)
            itemsRecyclerView?.adapter = adapter

            if(mModelData.is_prescription.equals("1"))
            {
                prescriptionLabel.visibility= View.VISIBLE
                prescriptionImageLay.visibility= View.VISIBLE
                mPresImg=mModelData.prescription

                Glide.with(this)
                    .load(mModelData.prescription)
                    .fitCenter()
                    .into(presImg)

            }
            else
            {
                prescriptionLabel.visibility= View.GONE
                prescriptionImageLay.visibility= View.GONE
            }

            Glide.with(this)
                .load(mModelData.storeImage)
                .fitCenter()
                .into(storeImg)

            storeName.text = mModelData.storeName
            orderIdValue.text = mModelData.orderId
            locText.text = mModelData.storeAddress
            orderOnValue.text = mModelData.dateTime
            //itemTotal.text = items?.get(0)?.price_with_customization
            discountLabel.text = "Cart Discount (" + mModelData.coupon_name +")"
            val deliveryChargeWithTax = mModelData.deliveryCharge.toInt() + mModelData.tax
            deliveryValue.text = resources.getString(R.string.ruppee) + "" + deliveryChargeWithTax
            delivery_coupon_label.text = "Delivery Discount (" +mModelData.delivery_coupon_name +")"
            grandPrice.text = resources.getString(R.string.ruppee) + "" + mModelData.totalPrice
            totalPriceValue.text = resources.getString(R.string.ruppee) + "" + mModelData.totalPrice
            if(mModelData.discount.equals(""))
            {
                discountValue.visibility=View.GONE
                discountLabel.visibility=View.GONE
            }
            else {
                discountValue.visibility=View.VISIBLE
                discountLabel.visibility=View.VISIBLE
                discountValue.text = resources.getString(R.string.ruppee) + "" + mModelData.discount
            }
        })


        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            showToast(user)


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
}
