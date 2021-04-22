package com.fidoo.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.awesomedialog.*
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.viewmodels.SendPackagesViewModel
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_send_package_order_detail.*
import kotlinx.android.synthetic.main.activity_send_package_order_detail.cash_lay
import kotlinx.android.synthetic.main.activity_send_package_order_detail.img_cash
import kotlinx.android.synthetic.main.activity_send_package_order_detail.img_online
import kotlinx.android.synthetic.main.activity_send_package_order_detail.online_lay
import kotlinx.android.synthetic.main.activity_send_package_order_detail.tv_cash
import kotlinx.android.synthetic.main.activity_send_package_order_detail.tv_grand_total
import org.json.JSONObject

class SendPackageOrderDetail : com.fidoo.user.utils.BaseActivity(), PaymentResultListener {

    var sendPackagesViewModel: SendPackagesViewModel? = null
    var sendPackagesModel: SendPackagesModel?=null
    var paymentMode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_package_order_detail)
        paymentMode = ""

        val paymentAmount = intent.getStringExtra("payment_amount")
        val orderId = intent.getStringExtra("order_id")
        val fromAddress = intent.getStringExtra("from_address")
        val toAddress = intent.getStringExtra("to_address")
        val notes = intent.getStringExtra("notes")
        val fromName = intent.getStringExtra("from_name")
        val fromNumber = intent.getStringExtra("from_number")
        val toName = intent.getStringExtra("to_name")
        val toNumber = intent.getStringExtra("to_number")
        val distance = intent.getStringExtra("distance")
        val deliveryTime = intent.getStringExtra("time")
        val baseDistance = intent.getStringExtra("base_distance")
        val baseCharges = intent.getStringExtra("base_charges")

        sendPackagesViewModel = ViewModelProvider(this).get(SendPackagesViewModel::class.java)

        try {
            tv_base_charges.text = "Delivery charges starting from" +baseCharges + "for first" +baseDistance+ "kms"
        }catch (e: NullPointerException){
            Log.e("Error Base distance", e.toString())
        }





        cash_lay.setOnClickListener {
            paymentMode = "cash"
            cash_lay.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            img_cash.setColorFilter(resources.getColor(R.color.colorPrimary))
            tv_cash.setTextColor(resources.getColor(R.color.white))
            online_lay.background = ResourcesCompat.getDrawable(resources, R.drawable.black_rounded_solid, null)
            tv_online_send_package.setTextColor(resources.getColor(R.color.grey))
            img_online.setImageResource(R.drawable.online_pay_grey)
        }



        online_lay.setOnClickListener {
            paymentMode = "online"
            online_lay.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            img_online.setColorFilter(resources.getColor(R.color.colorPrimary))
            tv_online_send_package.setTextColor(resources.getColor(R.color.white))
            cash_lay.background = ResourcesCompat.getDrawable(resources, R.drawable.black_rounded_solid, null)
            tv_cash.setTextColor(resources.getColor(R.color.grey))
            img_cash.setImageResource(R.drawable.cash_icon_grey)
        }
        backIcon.setOnClickListener {
            finish()
        }

        payment_lay.setOnClickListener {

            if (paymentMode != ""){
                if (fromAddress != null) {
                    if (fromName != null) {
                        if (fromNumber != null) {
                            if (toName != null) {
                                if (toNumber != null) {
                                    if (deliveryTime != null) {
                                        if (distance != null) {
                                            if (notes != null) {
                                                if (toAddress != null) {
                                                    if (paymentAmount != null) {
                                                        sendPackagesViewModel?.sendPackageApi(
                                                            com.fidoo.user.data.session.SessionTwiclo(
                                                                this
                                                            ).loggedInUserDetail.accountId,
                                                            com.fidoo.user.data.session.SessionTwiclo(
                                                                this
                                                            ).loggedInUserDetail.accessToken,
                                                            fromAddress,
                                                            notes,
                                                            toAddress,
                                                            fromName,
                                                            fromNumber,
                                                            toName,
                                                            toNumber,
                                                            paymentMode,
                                                            distance,
                                                            paymentAmount,
                                                            deliveryTime
                                                        )
                                                    } else {
                                                        showToast("There is some issue in payment, please try after sometime")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                showToast("Please select payment mode")
            }




        }

        sendPackagesViewModel?.sendPackagesResponse?.observe(this, {

            Log.e("send package response", it.toString())

            val razorpayId = it.razorPayOrderId


            if (paymentMode == "online"){
                if (paymentAmount != null) {
                    startPayment(paymentAmount)
                }

            }else{
                /*sendPackagesViewModel?.paymentApi(
                    com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accountId,
                    com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accessToken,
                    it.orderId,
                    razorpayId,
                    "",
                    "cash"
                )*/

                showToast("Order placed successfully")
                // startActivity(Intent(this, OrderDetailsActivity::class.java).putExtra("orderId",user.orderId).putExtra("type",""))
                //showToast(user.message)
                AwesomeDialog.build(this)
                    .title("Congratulations")
                    .body("Order Id: " + it.orderId + "\n\nOrder Placed Successfully!",)
                    .icon(R.drawable.ic_congrts)
                    .position(AwesomeDialog.POSITIONS.CENTER)
                    .onPositive("Go to Home", buttonBackgroundColor = R.color.colorPrimary) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }


            }

        })









        sendPackagesViewModel?.paymentResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("payment response", Gson().toJson(user))
            showToast("Order placed successfully")
            // startActivity(Intent(this, OrderDetailsActivity::class.java).putExtra("orderId",user.orderId).putExtra("type",""))
            //showToast(user.message)
            AwesomeDialog.build(this)
                .title("Congratulations")
                .body("Order Id: " + user.orderId + "\n\nOrder Placed Successfully!",)
                .icon(R.drawable.ic_congrts)
                .position(AwesomeDialog.POSITIONS.CENTER)
                .onPositive("Go to Home", buttonBackgroundColor = R.color.colorPrimary) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()

        })


    }

    private fun startPayment(paymentAmt: String) {
        /*
         * Instantiate Checkout
         */
        val activity: Activity = this
        val co = Checkout()

        val razorpayOrderId = com.fidoo.user.data.model.SendPackagesModel().razorPayOrderId
        //Log.e("RAZORPAY", razorpayOrderId)

        try {
            val options = JSONObject()
            options.put("name", "Fidoo")
            options.put("description", "Package Delivery Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://fidoo.in/include/assets/front/img/logo_sticky.svg")
            options.put("theme.color", "#339347")
            options.put("currency", "INR")
            options.put("order_id", razorpayOrderId)
            //Log.e("RAZORPAY", "")
            var amount = 0.0
            try {
                Log.e("totalAmount", paymentAmt)
                amount = paymentAmt.toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            options.put("amount", amount*100)

            val prefill = JSONObject()
            prefill.put("email", com.fidoo.user.data.session.SessionTwiclo(
                this
            ).profileDetail.account.emailid)
            prefill.put("contact", com.fidoo.user.data.session.SessionTwiclo(
                this
            ).profileDetail.account.country_code+ com.fidoo.user.data.session.SessionTwiclo(
                this
            ).profileDetail.account.userName)

            options.put("prefill", prefill)
            co.open(activity, options)
        }catch (e: java.lang.Exception){
            //Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }



    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            //Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
        }catch (e: java.lang.Exception){
            Log.e("onError", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            //Toast.makeText(this, "Payment Successful $razorpayPaymentId", Toast.LENGTH_LONG).show()
            sendPackagesViewModel?.paymentApi(
                com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accountId,
                com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accessToken,
                sendPackagesModel!!.orderId,
                razorpayPaymentId!!,
                "",
                "online"
            )



        }catch (e: java.lang.Exception){
            Log.e("onSuccess", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onResume() {
        super.onResume()
        tv_from_address.text = intent.getStringExtra("from_address")
        tv_to_address.text = intent.getStringExtra("to_address")
        tv_total_distance.text = intent.getStringExtra("distance")
        tv_time.text = intent.getStringExtra("time")
        tv_grand_total.text = intent.getStringExtra("payment_amount")
        tv_item_note.text = intent.getStringExtra("notes")
    }


}