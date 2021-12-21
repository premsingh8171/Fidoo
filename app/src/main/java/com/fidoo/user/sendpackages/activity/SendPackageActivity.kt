package com.fidoo.user.sendpackages.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fidoo.user.BuildConfig
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.addressmodule.activity.SavedAddressesActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.sendpackages.model.SendPackagesModel
import com.fidoo.user.sendpackages.roomdb.database.SendPackagesDb
import com.fidoo.user.sendpackages.viewmodel.SendPackagesViewModel
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.currentlocation_library.GetAddFromLatLong
import com.premsinghdaksha.currentlocation_library.TrackGPSLocation
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_send_package.*
import kotlinx.android.synthetic.main.activity_sendpackages_additem.*
import kotlinx.android.synthetic.main.buy_popup.view.*
import kotlinx.android.synthetic.main.logout_popup.*
import org.json.JSONObject


class SendPackageActivity : com.fidoo.user.utils.BaseActivity(),
    AdapterClick, PaymentResultListener {
    private val SECOND_ACTIVITY_REQUEST_CODE = 10

    var sendPackagesDiolog: Dialog? = null

    var viewmodel: SendPackagesViewModel? = null
    var sendPackagesModel: SendPackagesModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var addressType: String = ""
    var where: String = ""
    var catId: String = ""
    var start_point: String = ""
    var end_point: String = ""

    private val co = Checkout()
    private val getAddFromLatLong: GetAddFromLatLong? = null
    private val trackGPSLocation: TrackGPSLocation? = null

    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var selectedfromLat: Double? = 0.0
        var selectedfromLng: Double? = 0.0
        var selectedtoLat: Double? = 0.0
        var selectedtoLng: Double? = 0.0
        var selectedFromAddress: String = ""
        var selectedToAddress: String = ""
        var fromName: String = ""
        var fromNumber: String = ""
        var toName: String = ""
        var toNumber: String = ""

        //for send packages
        var start_Lat: Double? = 0.0
        var start_Lng: Double? = 0.0
        var end_Lat: Double? = 0.0
        var end_Lng: Double? = 0.0

        var cat_idStr: String = ""
        var cat_nameStr: String = ""
        var documentId: String = ""
        var forSendPackageAddCheck: String = "0"
        var standard_charges: String = ""
    }

    var contsCardHeight = 0
    var height = 0
    var width = 0
    var isSelected:Int?=0
    private lateinit var sendPackagesDb: SendPackagesDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_color)
        setContentView(R.layout.activity_send_package)

        viewmodel = ViewModelProvider(this).get(SendPackagesViewModel::class.java)
        viewmodelusertrack = ViewModelProvider(this).get(UserTrackerViewModel::class.java)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        // Display size
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = Math.round(width * 0.5).toInt()

        height = Math.round(((displayMetrics.heightPixels * 40) / 100).toDouble()).toInt()

        contsCardHeight = height - 150

        sendPackagesConstll.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )

        val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.topToTop = 0
        params.setMargins(50, contsCardHeight, 50, 0)
        sendPackagesConstCard.layoutParams = params

        selectedfromLat = 0.0
        selectedfromLng = 0.0
        selectedtoLat = 0.0
        selectedtoLng = 0.0
        selectedFromAddress = ""
        selectedToAddress = ""
        //packageDistance = ""


        /*fromName = ""
        fromNumber = ""
        toName = ""
        toNumber = ""*/

        //For Faster checkout of RazorPay
        Checkout.preload(applicationContext)
        // co.setKeyID("rzp_live_iceNLz5pb15jtP")
        co.setKeyID(BuildConfig.pay_key)

        where = intent.getStringExtra("where")!!
        try {
            tv_address_title.text=intent.getStringExtra("cat_name")!!
            cat_idStr=intent.getStringExtra("cat_id")!!
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        where = intent.getStringExtra("where")!!

        if (where.equals("guest")) {

        } else {
            // showIOSProgress()
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))
            } else {
                if (SessionTwiclo(this).isLoggedIn) {
                    viewmodel?.getPackageCatApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                    viewmodelusertrack?.customerActivityLog(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).mobileno,
                        "SendPackage screen",
                        SplashActivity.appversion,
                        StoreListActivity.serive_id_,
                        SessionTwiclo(this).deviceToken
                    )
                    viewmodel?.getPackageCatApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken
                    )
                } else {
                    viewmodel?.getPackageCatApi(
                        "",
                        ""
                    )
                }

            }
        }

        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cart response", Gson().toJson(user))
            showToast(user)
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.paymentResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("payment response", Gson().toJson(user))
            showToast("Order placed successfully")
            // startActivity(Intent(this, OrderDetailsActivity::class.java).putExtra("orderId",user.orderId).putExtra("type",""))
            SessionTwiclo(this).storeId = ""
        })

        from_address_lay.setOnClickListener {
            addressType = "from"
            forSendPackageAddCheck="1"
            startActivity(
                Intent(this, SavedAddressesActivity::class.java)
                    .putExtra("type", "from")
                    .putExtra("where", where)
            )
            MainActivity.addEditAdd ="SendPackage"

        }

        to_address_lay.setOnClickListener {
            addressType = "to"
            forSendPackageAddCheck="1"

            startActivity(
                Intent(this, SavedAddressesActivity::class.java)
                    .putExtra("type", "to")
                    .putExtra("where", where)
            )
            MainActivity.addEditAdd ="SendPackage"

        }

        viewmodel?.getPackageResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cat_response", Gson().toJson(user))
            standard_charges = user.standard_charges

            try {
                tv_base_charges.text =
                    "Delivery charges starting from " + user.base_price + " for first" + user.base_distance + " kms"
            } catch (e: NullPointerException) {
                Log.e("Error Base distance", e.toString())
            }



            startActivity(
                Intent(this, SendPackageOrderDetail::class.java)
                    .putExtra("base_distance", user.base_distance)
                    .putExtra("additional_distance", user.additional_distance)
                    .putExtra("distance", user.distance)
                    .putExtra("time", user.time)
                    .putExtra("orderId", user.orderId)
                    .putExtra("base_price", user.base_price)
                    .putExtra("additional_charges", user.additional_charges)
                    .putExtra("tax", user.tax)
                    .putExtra("payment_amount", user.paymentAmt)
                    .putExtra("from_address", tv_address_from.text.toString())
                    .putExtra("to_address", tv_address_to.text.toString())
                    .putExtra("order_id", user.orderId)
                    .putExtra("from_name", fromName)
                    .putExtra("from_number", fromNumber)
                    .putExtra("to_name", toName)
                    .putExtra("to_number", toNumber)
                    .putExtra("notes", ed_notes.text.toString())
                    .putExtra("base_distance", user.base_distance)
                    .putExtra("base_charges", user.base_price)
                    .putExtra("cat_name", cat_nameStr)
                    .putExtra("cat_Id", cat_idStr)
                    .putExtra("document_Id",documentId)
                    .putExtra("discount",user.discount)
                    .putExtra("coupon_name",user.coupon_name)
                    .putExtra("value_after_discount",user.value_after_discount)
                    .putExtra("standard_charges",user.standard_charges)
                    .putExtra("charges_one",user.charges_one)
                    .putExtra("charges_two",user.charges_two)

            )

            /*buyPopup(
                user.base_distance,
                user.additional_distance,
                user.distance,
                user.time,
                user.orderId,
                "",
                "cash",
                user.base_price,
                user.additional_charges,
                user.tax,
                user.paymentAmt
            )*/ //   }
//
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        back_action.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }

        info_im.setOnClickListener {
            deliveryPopUp(0)
        }

//        info_LL.setOnClickListener {
//            deliveryPopUp(1)
//        }


        tv_placeOrder.setOnClickListener {
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))

            } else {
                when {
                    tv_address_from.text.toString().length < 10 -> {
                        showToast("Please enter from address")
                        Log.e("LENGTH FROM ", tv_address_from.text.toString().length.toString())
                    }
                    tv_address_to.text.toString().length < 10 -> {
                        showToast("Please enter to address")
                    }
                    tv_address_from.text.toString() == tv_address_to.text.toString() -> {
                        showToast("From address and to address can't be same")
                    }
                    ed_notes.text.toString().trim() == "" -> {
                        showToast("Please enter to notes")
                    }
                    else -> {

                        calculateStoreCustomerDistance()
                        if (tv_address_to.text.isNotEmpty() && tv_address_from.text.isNotEmpty()) {
                            //selectedfromLat.toString()+","+selectedfromLng.toString(), selectedtoLat.toString()+","+selectedtoLng.toString()

                            //val source = selectedfromLat.toString()+","+selectedfromLng.toString()
                            //val destination = selectedtoLat.toString()+","+selectedtoLng.toString()

                            //Log.e("DISTANCE1",packageDistance)
                            //val distance = packageDistance
                            showIOSProgress()
                            sendPackageInfo()
                        } else {
                            showToast("please enter from/to address")
                        }


                    }
                }
            }
            // buyPopup()

        }


        item_type_lay.setOnClickListener {
            if (selectedFromAddress.isNotEmpty()) {
                AppUtils.startActivityForResultRightToLeft(
                    this,
                    Intent(this@SendPackageActivity, SendPackagesAddItem::class.java),
                    SECOND_ACTIVITY_REQUEST_CODE
                )
            }
        }


        ed_notes.setOnClickListener {
            if (selectedFromAddress.isNotEmpty()) {
                AppUtils.startActivityForResultRightToLeft(
                    this,
                    Intent(this@SendPackageActivity, SendPackagesAddItem::class.java),
                    SECOND_ACTIVITY_REQUEST_CODE
                )
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(
            "requestCode_____",
            "$requestCode=$SECOND_ACTIVITY_REQUEST_CODE,$resultCode=$RESULT_OK"
        )

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                var result = data!!.getStringExtra("Item_details")
                if (result != null) {
                  ///  item_type_lay.visibility=View.GONE
                    infoll_bottom.visibility=View.GONE
                  //  ed_notes.visibility=View.VISIBLE
                    tv_placeOrder.visibility=View.VISIBLE
                    ed_notes.text=data!!.getStringExtra("Item_list")
                    item_icon.setColorFilter(getResources().getColor(R.color.primary_color));
                    tv_address_title.text=data!!.getStringExtra("cat_name")
                    cat_nameStr=data!!.getStringExtra("cat_name").toString()
                    cat_idStr= data!!.getStringExtra("cat_id").toString()
                    documentId=data!!.getStringExtra("document_id").toString().replace(" ","")
                    Log.e("result__","$cat_nameStr--$cat_idStr--$documentId")
                }
            }
        }
    }

    private fun sendPackageInfo() {}

    fun calculateStoreCustomerDistance() {
        val source = selectedfromLat.toString() + "," + selectedfromLng.toString()
        val destination = selectedtoLat.toString() + "," + selectedtoLng.toString()
        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=$source&destination=$destination&key=AIzaSyBvnYPa4tw9s5TSGwzePeWD4Kk7yulyy9c"
        Log.e("urlDirections", urlDirections)
        var dist_: Float = 0f
        val directionsRequest = object :
            StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                dismissIOSProgress()
                Log.e("res_routes", "routes- $jsonResponse")
                var status = jsonResponse.optString("status")
                if (!status.equals("ZERO_RESULTS")) {
                    val routes = jsonResponse.optJSONArray("routes")
                    if (routes.length() != 0) {
                        val legs = routes.optJSONObject(0).optJSONArray("legs")
                        val distance =
                            legs.optJSONObject(0).optJSONObject("distance").get("value").toString()
                        val distanceTex =
                            legs.optJSONObject(0).optJSONObject("distance").getInt("value")
//                        val dist: List<String> = distanceTex.split(" ")
//                        val distStr = dist[0]
//                        dist_ = distStr.toFloat()
//                        Log.e("distance_", dist_.toString())

                        start_Lat = legs.optJSONObject(0).optJSONObject("start_location").opt("lat")
                            .toString().toDouble()
                        start_Lng = legs.optJSONObject(0).optJSONObject("start_location").opt("lng")
                            .toString().toDouble()
                        getGeoAddressFromLatLong2(start_Lat!!, start_Lng!!)
                        end_Lat = legs.optJSONObject(0).optJSONObject("end_location").opt("lat")
                            .toString().toDouble()
                        end_Lng = legs.optJSONObject(0).optJSONObject("end_location").opt("lng")
                            .toString().toDouble()
                        getGeoAddressFromLatLong2(end_Lat!!, end_Lng!!)

                        end_point = city.toLowerCase()
                        start_point = city.toLowerCase()
                        Log.d("end_point__", start_point + "==" + end_point)

//                        if ((end_point.equals("gurugram") && start_point.equals("gurugram"))
//                            || (end_point.equals("gurgaon ") && start_point.equals("gurgaon"))
//                            || (end_point.equals("") && start_point.equals(""))
//                        ) {
                        if (distanceTex != null && distanceTex < 15000) {
                            viewmodel?.getPackageDetails(
                                SessionTwiclo(
                                    this
                                ).loggedInUserDetail.accountId,
                                SessionTwiclo(
                                    this
                                ).loggedInUserDetail.accessToken,
                                distanceTex.toString()
                            )
//                            } else {
//                                showToast("There is some issue in Service, please try after sometime")
//                            }
                        } else {
                            showToastLong("Service is available only Gurugram!")

                        }
                    }
                } else {
                    showToast("No result found")
                }
            }, Response.ErrorListener { }) {

        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)

        //return packageDistance!!


    }

    @SuppressLint("SetTextI18n")
    private fun buyPopup(
        baseDistance: String,
        additionalDistance: String,
        totalDistance: String,
        time: String,
        order_id: String,
        txnId: String,
        paymentMode: String,
        base_price: String,
        additional_price: String,
        tax: String,
        totalPrice: String
    ) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.buy_popup, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
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
        mDialogView.base_distance.text = baseDistance + "KM"
        mDialogView.additional_distance.text = additionalDistance + "KM"
        mDialogView.total_distance.text = totalDistance + "KM"
        mDialogView.timeValue.text = time
        mDialogView.base_price.text = resources.getString(R.string.ruppee) + base_price
        mDialogView.additional_price.text = resources.getString(R.string.ruppee) + additional_price
        mDialogView.tax.text = resources.getString(R.string.ruppee) + tax
        mDialogView.total_price.text = resources.getString(R.string.ruppee) + totalPrice
        mDialogView.proceedTxt.setOnClickListener {
            //dismiss dialog

//            if (paymentRadioGroup.checkedRadioButtonId.equals(R.id.onlineRadioBtn)) {

            startPayment(totalPrice)

//            }
//            else {
//                showIOSProgress()
//                /*viewmodel?.paymentApi(
//                    SessionTwiclo(this).loggedInUserDetail.accountId,
//                    SessionTwiclo(this).loggedInUserDetail.accessToken,
//                    order_id,
//                    txnId,
//                    "",
//                    paymentMode
//                )*/
//                AwesomeDialog.build(this)
//                    .title("Congratulations")
//                    .body("Order Id: " + order_id + "\n\nOrder Placed Successfully!")
//                    .icon(com.example.awesomedialog.R.drawable.ic_congrts)
//                    .position(AwesomeDialog.POSITIONS.CENTER)
//                    .onPositive("Go to Home") {
//                        startActivity(Intent(this,MainActivity::class.java))
//                        finishAffinity()
//                    }
//            }

            mAlertDialogg.dismiss()
        }

        mDialogView.closeIcon.setOnClickListener {
            //dismiss dialog

            mAlertDialogg.dismiss()
        }

    }


    override fun onItemClick(
        productId: String?,
        type: String?,
        count: String?,
        offerPrice: String?,
        customizeCount: Int?,
        productType: String?,
        cart_id: String
    ) {
        catId = productId.toString()
    }


    private fun startPayment(paymentAmt: String) {
        /*
         * Instantiate Checkout
         */
        val activity: Activity = this
        val co = Checkout()
        //var razorpayId = "qwerty"

        viewmodel?.sendPackagesResponse?.observe(this) {
            val razorpayOrderId = SendPackagesModel().razorPayOrderId
            //Log.e("RAZORPAY", razorpayOrderId)

            try {
                val options = JSONObject()
                options.put("name", "Twiclo Technologies")
                options.put("description", "Send Package Charges")
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "https://fidoo.in/include/assets/fidoo-logo.jpg")
                options.put("theme.color", "#ffffff");
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
                options.put("amount", amount * 100)

                val prefill = JSONObject()
                prefill.put(
                    "email", SessionTwiclo(
                        this
                    ).profileDetail.account.emailid
                )
                prefill.put(
                    "contact",
                    SessionTwiclo(this).profileDetail.account.country_code + SessionTwiclo(
                        this
                    ).profileDetail.account.userName
                )

                options.put("prefill", prefill)
                co.open(activity, options)
            } catch (e: java.lang.Exception) {
                Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }


        }


    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try {
            Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
        } catch (e: java.lang.Exception) {
            Log.e("onError", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try {
            //Toast.makeText(this, "Payment Successful $razorpayPaymentId", Toast.LENGTH_LONG).show()
            viewmodel?.paymentApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                sendPackagesModel!!.orderId,
                razorpayPaymentId!!,
                "",
                "online"
            )


        } catch (e: java.lang.Exception) {
            Log.e("onSuccess", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onResume() {
        super.onResume()
        deleteAllSendPackages()
        if (selectedFromAddress.isNotEmpty()){
            location_icon_from.setColorFilter(getResources().getColor(R.color.primary_color));
        }
        if (selectedToAddress.isNotEmpty()){
            location_icon_to.setColorFilter(getResources().getColor(R.color.primary_color));
        }
        tv_address_from.text = selectedFromAddress
        tv_address_to.text = selectedToAddress
        forSendPackageAddCheck="0"
    }

    private fun deliveryPopUp(vis:Int) {
        sendPackagesDiolog = Dialog(this)
        sendPackagesDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sendPackagesDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sendPackagesDiolog?.setContentView(R.layout.send_packages_additem_popup)
        sendPackagesDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        // sendPackagesDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        sendPackagesDiolog?.setCanceledOnTouchOutside(true)
        sendPackagesDiolog?.show()
        val disable_gradientRl = sendPackagesDiolog?.findViewById<RelativeLayout>(R.id.disable_gradientRl)
        val deliveryChargesLl = sendPackagesDiolog?.findViewById<LinearLayout>(R.id.deliveryChargesLl)
        val deliveryChargesLl2 = sendPackagesDiolog?.findViewById<LinearLayout>(R.id.deliveryChargesLl2)

        if (vis==0){
            deliveryChargesLl2!!.visibility=View.GONE
            deliveryChargesLl!!.visibility=View.VISIBLE
        }else{
            deliveryChargesLl2!!.visibility=View.VISIBLE
            deliveryChargesLl!!.visibility=View.GONE

        }

        disable_gradientRl?.setOnClickListener{
            sendPackagesDiolog?.dismiss()
        }

    }

    //delete all
    private fun deleteAllSendPackages() {
        Thread {
            try {
                sendPackagesDb = Room.databaseBuilder(
                    applicationContext,
                    SendPackagesDb::class.java, SendPackagesDb.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                sendPackagesDb!!.sendPackagesDao()!!.deleteAllItem()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.finishActivityLeftToRight(this)
    }

}
