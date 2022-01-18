package com.fidoo.user.sendpackages.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fidoo.user.R
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.viewmodel.CartViewModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.ui.TrackSendPAckagesOrderActivity
import com.fidoo.user.sendpackages.model.SendPackagesModel
import com.fidoo.user.sendpackages.viewmodel.SendPackagesViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_send_package_order_detail.*
import kotlinx.android.synthetic.main.activity_send_package_order_detail.backIcon
import kotlinx.android.synthetic.main.activity_send_package_order_detail.cash_lay
import kotlinx.android.synthetic.main.activity_send_package_order_detail.img_cash
import kotlinx.android.synthetic.main.activity_send_package_order_detail.img_online
import kotlinx.android.synthetic.main.activity_send_package_order_detail.online_lay
import kotlinx.android.synthetic.main.activity_send_package_order_detail.payment_method_lay
import kotlinx.android.synthetic.main.activity_send_package_order_detail.tv_cash
import kotlinx.android.synthetic.main.activity_send_package_order_detail.tv_grand_total
import kotlinx.android.synthetic.main.activity_send_package_order_detail.tv_place_order
import org.json.JSONObject

class SendPackageOrderDetail : com.fidoo.user.utils.BaseActivity(), PaymentResultListener {

    var sendPackagesViewModel: SendPackagesViewModel? = null
    var sendPackagesModel: SendPackagesModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var paymentMode: String = "online"
    var payment_suc_Diolog: Dialog? = null
    var totalAmount: Double = 0.0
    var viewmodel: CartViewModel? = null
    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var sendpackagerder_id: String = ""
        var finalOrderId: String = ""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_send_package_order_detail)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        val paymentAmount = intent.getStringExtra("payment_amount")
        val paymentAmount2 = intent.getStringExtra("value_after_discount")
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
        //
        val tax = intent.getStringExtra("tax")
        val cat_name = intent.getStringExtra("cat_name")
        val cat_Id = intent.getStringExtra("cat_Id")
        val images = intent.getStringExtra("document_Id")
        val standard_charges = intent.getStringExtra("standard_charges")
        val charges_one = intent.getStringExtra("charges_one")
        val charges_two = intent.getStringExtra("charges_two")

        sendPackagesViewModel = ViewModelProvider(this).get(SendPackagesViewModel::class.java)
        viewmodelusertrack = ViewModelProvider(this).get(UserTrackerViewModel::class.java)
        viewmodel = ViewModelProvider(this).get(CartViewModel::class.java)

//        if (standard_charges!!.isNotEmpty()) {
//            var value = standard_charges.split(",")
//            chargesSendPackageTxt1.text = value[0]
//            chargesSendPackageTxt2.text = "      ₹ " + value[1]
//        }

        if (charges_one!!.isNotEmpty()) {
            var charges_oneStr = charges_one.split(",")
            chargesSendPackageTxt1.text = charges_oneStr[0]
            chargesSendPackageTxt2.text = "      ₹ " + charges_oneStr[1]
        }

        if (charges_two!!.isNotEmpty()) {
            var charges_TwoStr = charges_two.split(",")
            charges2Txt1.text = charges_TwoStr[0]
            charges2Txt2.text = "   ₹ " + charges_TwoStr[1]
        }


        try {
            tv_base_charges.text =
                "Delivery charges starting from ₹" + baseCharges + " for first " + baseDistance + " kms"
        } catch (e: NullPointerException) {
            Log.e("Error Base distance", e.toString())
        }

        if (isNetworkConnected) {
            showIOSProgress()
            sendPackagesViewModel?.sendPackagePaymentModeApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken)

            viewmodelusertrack?.customerActivityLog(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).mobileno, "SendPackagerderDetail screen",
                SplashActivity.appversion, "", SessionTwiclo(this).deviceToken
            )
        } else {
            showInternetToast()
        }

        cash_lay.setOnClickListener {
            paymentMode = "cash"
            cash_lay.setBackgroundResource(R.drawable.black_rounded_solid)
            online_lay.setBackgroundResource(R.drawable.payment_nonactiveleft)

            img_cash.setColorFilter(resources.getColor(R.color.primary_color))
            tv_cash.setTextColor(resources.getColor(R.color.primary_color))

//            online_lay.background = ResourcesCompat.getDrawable(resources,
//                R.drawable.black_rounded_solid, null)

            tv_online_send_package.setTextColor(resources.getColor(R.color.grey))
            img_online.setImageResource(R.drawable.online_pay_grey)
            img_online.setColorFilter(resources.getColor(R.color.grey))

        }

        online_lay.setOnClickListener {
            paymentMode = "online"
            online_lay.setBackgroundResource(R.drawable.black_rounded_solid)
            cash_lay.setBackgroundResource(R.drawable.payment_nonactiveleft)

            img_online.setColorFilter(resources.getColor(R.color.primary_color))
            tv_online_send_package.setTextColor(resources.getColor(R.color.primary_color))

//            cash_lay.background = ResourcesCompat.getDrawable(resources,
//                R.drawable.black_rounded_solid, null)

            tv_cash.setTextColor(resources.getColor(R.color.grey))
            img_cash.setImageResource(R.drawable.cash_icon_grey)
            img_cash.setColorFilter(resources.getColor(R.color.grey))

        }

        backIcon.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }

        payment_lay.setOnClickListener {

            if (paymentMode != "") {
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
                                                            SessionTwiclo(
                                                                this
                                                            ).loggedInUserDetail.accountId,
                                                            SessionTwiclo(
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
                                                            deliveryTime,
                                                            SendPackageActivity.start_Lat.toString()!!,
                                                            SendPackageActivity.start_Lng.toString()!!,
                                                            SendPackageActivity.end_Lat.toString()!!,
                                                            SendPackageActivity.end_Lng.toString()!!,
                                                            cat_Id!!,notes,images!!,tax!!
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
            } else {
                showToast("Please select payment mode")
            }

        }

        chargesFmBgbottoms.setOnClickListener {
            chargesFmBgs.visibility=View.GONE
            chargesFms.visibility=View.GONE
            chargesFmBgbottoms.visibility=View.GONE
        }

        chargesFmBgs.setOnClickListener {
            chargesFmBgs.visibility=View.GONE
            chargesFms.visibility=View.GONE
            chargesFmBgbottoms.visibility=View.GONE
        }

        tv_deliveryCharges_label.setOnClickListener {
            chargesFmBgs.visibility=View.VISIBLE
            chargesFms.visibility=View.VISIBLE
            chargesFmBgbottoms.visibility=View.VISIBLE
        }

        sendPackagesViewModel?.sendPackagesResponse?.observe(this) {

            Log.e("send_package_response", it.toString())

            val razorpayId = it.orderId.toString()
            sendpackagerder_id = razorpayId
            Log.e("razorpayId__", sendpackagerder_id)

            val razorpayOrderId= it.razorPayOrderId.toString()

            if (paymentMode == "online") {
                if (paymentAmount != null) {
                    startPayment(paymentAmount,razorpayOrderId)
                }
            } else {
                Log.e("paymentMode___",paymentMode)
                sendPackagesViewModel?.paymentApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    sendpackagerder_id,
                   "",
                    "",
                    paymentMode
                )
                paySuccessPopUp()
            }

        }

        sendPackagesViewModel?.sendPackagePaymentModeResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("payment_response", Gson().toJson(user))
                if (user.error_code==200){
                    if (user.free==1){
                        payment_method_lay.visibility= View.GONE
                        //gone in case of free
                        tv_total_distance_label.visibility= View.GONE
                        tv_total_distance.visibility= View.GONE
                        tv_time_label.visibility= View.GONE
                        tv_time.visibility= View.GONE
                        paymentMode="free"

                    }else{
                        payment_method_lay.visibility= View.VISIBLE

                        tv_total_distance_label.visibility= View.GONE
                        tv_total_distance.visibility= View.GONE
                        tv_time_label.visibility= View.GONE
                        tv_time.visibility= View.GONE

                        if (user.cash==1){
                            paymentMode="cash"
                            cash_lay.visibility= View.VISIBLE
                        }else{
                            cash_lay.visibility= View.GONE
                        }

                        if (user.online==1){
                            paymentMode="online"
                            online_lay.visibility= View.VISIBLE
                        }else{
                            online_lay.visibility= View.GONE
                        }
                    }
                }
        })

        sendPackagesViewModel?.paymentResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("payment_response", Gson().toJson(user))
            finalOrderId = user.orderId
            viewmodel?.proceedToOrder(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                finalOrderId
            )
        })

        viewmodel?.proceedToOrderResponse?.observe(this,
            { orderProceed ->
                Log.e("proceedToOrderResponse", Gson().toJson(orderProceed))

            })

    }

    private fun startPayment(paymentAmt: String,razorpayOrderId: String?) {
        val activity: Activity = this
        val co = Checkout()
        //var razorpayId = "qwerty"

        //  val razorpayOrderId = OrderPlaceModel().razorPayOrderId

        try {
            val options = JSONObject()
            options.put("name", "FIDOO")
            options.put("description", "Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://fidoo.in/include/assets/fidoo-logo.jpg")
            options.put("theme.color", "#339347");
            options.put("currency", "INR")
            options.put("order_id", razorpayOrderId)
            //Log.e("RAZORPAY", "")
            var amount = 0.0f
            try {
                Log.e("totalAmount", paymentAmt.toString())
                amount = paymentAmt.toFloat()
                //showToast(""+amount)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.e("totalAmount_",(amount * 100).toString())

            options.put("amount", amount * 100)

            val prefill = JSONObject()
            if (SessionTwiclo(this).profileDetail != null) {
                prefill.put("email", SessionTwiclo(this).profileDetail.account.emailid)
                prefill.put(
                    "contact",
                    SessionTwiclo(this).profileDetail.account.country_code + SessionTwiclo(this).profileDetail.account.userName
                )
                options.put("prefill", prefill)
            } else {
                prefill.put("email", SessionTwiclo(this).loginDetail.account.emailid)
                prefill.put(
                    "contact",
                    "+91" + SessionTwiclo(this).loginDetail.account.userName
                )
                options.put("prefill", prefill)
            }

            co.open(activity, options)
        } catch (e: java.lang.Exception) {
            Toast.makeText(activity, "Error in payment, please try again", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }


    override fun onPaymentError(errorCode: Int, response: String?) {
        Log.e("onError__", "$response")

        try {
            viewmodelusertrack?.customerActivityLog(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).mobileno, "SendPackagerderDetail Screen payment failed ",
                SplashActivity.appversion, "", SessionTwiclo(this).deviceToken
            )
        } catch (e: java.lang.Exception) {
            Log.e("onError", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try {
            paySuccessPopUp()

            sendPackagesViewModel?.paymentApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                sendpackagerder_id,
                razorpayPaymentId!!,
                "",
                "online"
            )

            viewmodelusertrack?.customerActivityLog(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).mobileno, "SendPackagerderDetail Screen payment Successful ",
                SplashActivity.appversion, "", SessionTwiclo(this).deviceToken
            )


        } catch (e: java.lang.Exception) {
            Log.e("onSuccess", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onResume() {
        super.onResume()
        tv_from_address.text = intent.getStringExtra("from_address")
        tv_to_address.text = intent.getStringExtra("to_address")
        tv_total_distance.text = intent.getStringExtra("distance") + " km"
        tv_time.text = intent.getStringExtra("time") + "min"
        tv_grand_total.text = "₹ " + intent.getStringExtra("value_after_discount")
       // tv_grand_total.text = "₹ 0"
        tv_deliveryCharges_.text = "₹ " + intent.getStringExtra("payment_amount")
        tv_delivery_fee.text = "- ₹ " + intent.getStringExtra("discount")

        if (!intent.getStringExtra("coupon_name")!!.equals("")){
            tv_delivery_fee_label.text = "Discount ("+ intent.getStringExtra("coupon_name")+")"
            tv_delivery_fee_label.visibility=View.VISIBLE
            tv_delivery_fee.visibility=View.VISIBLE

        }else{
            tv_delivery_fee_label.visibility=View.GONE
            tv_delivery_fee.visibility=View.GONE
        }

        // tv_place_order.text = "Place Order |  " + intent.getStringExtra("payment_amount")
        tv_place_order.text = "Place Order"
        tv_item_note.text = intent.getStringExtra("notes")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.finishActivityLeftToRight(this)
    }

    fun paySuccessPopUp() {
        payment_suc_Diolog = Dialog(this)
        payment_suc_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        payment_suc_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        payment_suc_Diolog?.setContentView(R.layout.payment_success_popup)
        payment_suc_Diolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        payment_suc_Diolog?.setCanceledOnTouchOutside(true)
        payment_suc_Diolog?.show()
        val payment_successImg =
            payment_suc_Diolog?.findViewById<ImageView>(R.id.payment_successImg)

        Glide.with(this).load(R.drawable.pay_suc)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .placeholder(R.drawable.pay_suc)
            .error(R.drawable.pay_suc).into(payment_successImg!!)

        Handler().postDelayed({
//            startActivity(Intent(this, MainActivity::class.java))
            startActivity(
                Intent(this@SendPackageOrderDetail, TrackSendPAckagesOrderActivity::class.java)
                    .putExtra("orderId", finalOrderId)
                    .putExtra("delivery_boy_name", "orders[position].delivery_boy_name")
                    .putExtra("delivery_boy_mobile", "")
                    .putExtra("order_status", "")
                    .putExtra("type", "")
            )
            finishAffinity()
        }, 5500)
    }

}