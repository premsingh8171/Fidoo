package com.fidoo.user

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fidoo.user.data.model.SendPackagesModel
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.view.address.SavedAddressesActivity
import com.fidoo.user.viewmodels.SendPackagesViewModel
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_send_package.*
import kotlinx.android.synthetic.main.buy_popup.view.*
import org.json.JSONObject

class SendPackageActivity : com.fidoo.user.utils.BaseActivity(),
    AdapterClick, PaymentResultListener {


    var viewmodel: SendPackagesViewModel? = null
    var sendPackagesModel: SendPackagesModel? = null
    var addressType: String = ""
    var catId: String = ""
    private var fromLat: Double? = 0.0
    private var fromLng: Double? = 0.0
    private var toLat: Double? = 0.0
    private var toLng: Double? = 0.0
    private val co = Checkout()

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_color)
        setContentView(R.layout.activity_send_package)



        viewmodel = ViewModelProvider(this).get(SendPackagesViewModel::class.java)

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
        co.setKeyID("rzp_live_iceNLz5pb15jtP")

        // showIOSProgress()
        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))

        } else {
            if (com.fidoo.user.data.session.SessionTwiclo(this).isLoggedIn) {
                viewmodel?.getPackageCatApi(
                    com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accountId,
                    com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accessToken
                )
            } else {
                viewmodel?.getPackageCatApi(
                    "",
                    ""
                )
            }
            viewmodel?.getPackageCatApi(
                com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accountId,
                com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accessToken
            )
        }

        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cart response", Gson().toJson(user))
            showToast(user)
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        /*viewmodel?.getcatResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cat response", Gson().toJson(user))
            val adapter = PackageCategoriesAdapter(this, user.categoriesList, this)
            customItemsRecyclerview.layoutManager = LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL,
                false
            )
            customItemsRecyclerview.setHasFixedSize(true)
            customItemsRecyclerview.adapter = adapter
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })*/

        viewmodel?.paymentResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("payment response", Gson().toJson(user))
            showToast("Order placed successfully")
            // startActivity(Intent(this, OrderDetailsActivity::class.java).putExtra("orderId",user.orderId).putExtra("type",""))
            com.fidoo.user.data.session.SessionTwiclo(this).storeId = ""
            //showToast(user.message)
            /*AwesomeDialog.build(this)
                .title("Congratulations")
                .body("Order Id: " + user.orderId + "\n\nOrder Placed Successfully!")
                .icon(R.drawable.ic_congrts)
                .position(AwesomeDialog.POSITIONS.CENTER)
                .onPositive("Go to Home") {
                    startActivity(Intent(this,HomeActivity::class.java))
                    finishAffinity()
                }*/
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        from_address_lay.setOnClickListener {
            addressType = "from"
            startActivity(
                Intent(this, SavedAddressesActivity::class.java).putExtra(
                    "type",
                    "from"
                )

            )
            /*        val intent = VanillaPlacePicker.Builder(this)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable autocompelte, map or both
               // .withLocation(23.057582, 72.534458)
                .setPickerLanguage(PickerLanguage.ENGLISH) // Apply language to picker
              //  .setLocationRestriction(LatLng(23.0558088,72.5325067), LatLng(23.0587592,72.5357321)) // Restrict location bounds in map and autocomplete
                .setCountry("IN") // Only for Autocomplete
                .enableShowMapAfterSearchResult(true) // To show the map after selecting the place from place picker only for PickerType.MAP_WITH_AUTO_COMPLETE
                *//*
                 * Configuration for Map UI
                 *//*
                .setMapType(MapType.SATELLITE) // Choose map type (Only applicable for map screen)
                 //.setMapStyle(R.raw.style_json) // Containing the JSON style declaration for night-mode styling
                .setMapPinDrawable(android.R.drawable.ic_menu_mylocation) // To give custom pin image for map marker
            .build()
            startActivityForResult(intent, REQUEST_PLACE_PICKER)*/
            //onSearchCalled()
        }

        to_address_lay.setOnClickListener {
            addressType = "to"
            startActivity(
                Intent(this, SavedAddressesActivity::class.java).putExtra(
                    "type",
                    "to"
                )
            )
            /*
            val intent = VanillaPlacePicker.Builder(this)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable autocompelte, map or both
                // .withLocation(23.057582, 72.534458)
                .setPickerLanguage(PickerLanguage.ENGLISH) // Apply language to picker
                //  .setLocationRestriction(LatLng(23.0558088,72.5325067), LatLng(23.0587592,72.5357321)) // Restrict location bounds in map and autocomplete
                .setCountry("IN") // Only for Autocomplete
                .enableShowMapAfterSearchResult(true) // To show the map after selecting the place from place picker only for PickerType.MAP_WITH_AUTO_COMPLETE
                *//*
                 * Configuration for Map UI
                 *//*
                .setMapType(MapType.SATELLITE) // Choose map type (Only applicable for map screen)
                //.setMapStyle(R.raw.style_json) // Containing the JSON style declaration for night-mode styling
                .setMapPinDrawable(android.R.drawable.ic_menu_mylocation) // To give custom pin image for map marker
                .build()
            startActivityForResult(intent, REQUEST_PLACE_PICKER)*/
        }


        viewmodel?.getPackageResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cat response", Gson().toJson(user))
            /*   if (paymentRadioGroup.checkedRadioButtonId.equals(R.id.onlineRadioBtn)) {
                sendPackagesModel=user
                      launchPayUMoneyFlow(user.paymentAmt)
            }
            else
            {*/

            try {
                tv_base_charges.text = "Delivery charges starting from " +user.base_price + " for first" +user.base_distance+ " kms"
            }catch (e: NullPointerException){
                Log.e("Error Base distance", e.toString())
            }



            startActivity(Intent(this, SendPackageOrderDetail::class.java)
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
        }

        tv_placeOrder.setOnClickListener {
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))

            } else {
                when {
                    /*catId == "" -> {
                        showToast("Please select type of package")
                    }*/
//                    parcelValue.text.toString().equals("") -> {
//                        showToast("Please enter parcel value")
//                    }

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

    }

    private fun sendPackageInfo() {


    }

    fun calculateStoreCustomerDistance() {
        val source = selectedfromLat.toString() + "," + selectedfromLng.toString()
        val destination = selectedtoLat.toString() + "," + selectedtoLng.toString()
        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=$source&destination=$destination&key=AIzaSyBvnYPa4tw9s5TSGwzePeWD4Kk7yulyy9c"
        val directionsRequest = object :
            StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                Log.e("res", "routes- $jsonResponse")
                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() != 0) {
                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val distance =
                        legs.getJSONObject(0).getJSONObject("distance").get("value").toString()
                    //showToast(distance)
                    //packageDistance = distance

//                    var paymentMode = ""
//
//                    paymentMode = if (checkedRadioButtonId == R.id.onlineRadioBtn) {
//                        "Online"
//                    } else {
//                        "Cash"
//                    }


                    if (distance.isNotEmpty() || distance != "") {
                        viewmodel?.getPackageDetails(
                            com.fidoo.user.data.session.SessionTwiclo(
                                this
                            ).loggedInUserDetail.accountId,
                            com.fidoo.user.data.session.SessionTwiclo(
                                this
                            ).loggedInUserDetail.accessToken,
                            distance
                        )
                    } else {
                        showToast("There is some issue in Service, please try after sometime")
                    }
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

        viewmodel?.sendPackagesResponse?.observe(this,  {
            val razorpayOrderId = SendPackagesModel().razorPayOrderId
            //Log.e("RAZORPAY", razorpayOrderId)

            try {
                val options = JSONObject()
                options.put("name", "Twiclo Technologies")
                options.put("description", "Send Package Charges")
                //You can omit the image option to fetch the image from dashboard
                options.put("image", "https://twiclo.com/include/assets/front/assets/img/logo.png")
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
                prefill.put("email", com.fidoo.user.data.session.SessionTwiclo(
                    this
                ).profileDetail.account.emailid)
                prefill.put(
                    "contact",
                    com.fidoo.user.data.session.SessionTwiclo(this).profileDetail.account.country_code + com.fidoo.user.data.session.SessionTwiclo(
                        this
                    ).profileDetail.account.userName
                )

                options.put("prefill", prefill)
                co.open(activity, options)
            } catch (e: java.lang.Exception) {
                Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }


        })


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
                com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accountId,
                com.fidoo.user.data.session.SessionTwiclo(this).loggedInUserDetail.accessToken,
                sendPackagesModel!!.orderId,
                razorpayPaymentId!!,
                "",
                "online"
            )


        } catch (e: java.lang.Exception) {
            Log.e("onSuccess", "Exception in onPaymentSuccess", e)
        }
    }

//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // Result Code is -1 send from Payumoney activity
//        Log.d(
//            "MainActivity",
//            "request code $requestCode resultcode $resultCode"
//        )
//        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
//            null
//        ) {
//
//            val transactionResponse: TransactionResponse? =
//                data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE)
//
//            // val resultModel: ResultModel? = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT)
//
//            // Check which object is non-null
//            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
//                if (transactionResponse.transactionStatus == TransactionResponse.TransactionStatus.SUCCESSFUL) {
//                    //Success Transaction
//                    // Response from Payumoney
//                    val payuResponse = transactionResponse.getPayuResponse()
//                    // Response from SURl and FURL
//                    val merchantResponse = transactionResponse.transactionDetails
//                    Log.e("payuResponse", Gson().toJson(payuResponse))
//                    Log.e("merchantResponse", Gson().toJson(merchantResponse))
//                    Log.e("merchantResponse", sendPackagesModel!!.orderId)
//                    showIOSProgress()
//                    viewmodel?.paymentApi(
//                        SessionTwiclo(this).loggedInUserDetail.accountId,
//                        SessionTwiclo(this).loggedInUserDetail.accessToken, sendPackagesModel!!.orderId,merchantResponse, "", "online"
//                    )/*  buyPopup(
//                        sendPackagesModel!!.distance,
//                        sendPackagesModel!!.paymentAmt,
//                        sendPackagesModel!!.time,
//                        sendPackagesModel!!.orderId,
//                        merchantResponse,
//                        "online"
//                    )*/
//
//                } else {
//                    //Failure Transaction
//                }
//
//
//            }
//        }
//        /*if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                val place = Autocomplete.getPlaceFromIntent(data!!)
//                Log.wtf(
//                    "SearchRide",
//                    "\nID: " + place.id + "\naddress:" + place.address + "\nName:" + place.name + "\nlatlong: " + place.latLng
//                )
//                // do query with address
//                val addresses: List<Address>
//                val geocoder = Geocoder(this, Locale.getDefault())
//                //  lat=place.latLng!!.latitude
//                // lng=place.latLng!!.latitude
//                addresses = geocoder.getFromLocation(
//                    place.latLng!!.latitude,
//                    place.latLng!!.longitude,
//                    1
//                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                if (addressType.equals("from")) {
//                    fromLat = place.latLng!!.latitude
//                    fromLng = place.latLng!!.longitude
//                    fromGoogleAddress.text = addresses.get(0).getAddressLine(0)
//                } else if (addressType.equals("to")) {
//                    toLat = place.latLng!!.latitude
//                    toLng = place.latLng!!.longitude
//                    toGoogleAddress.text = addresses.get(0).getAddressLine(0)
//                }
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) { // TODO: Handle the error.
//                val status: Status = Autocomplete.getStatusFromIntent(data!!)
//                Toast.makeText(this, "Error: " + status.statusMessage, Toast.LENGTH_LONG).show()
//                // Log.i(FragmentActivity.TAG, status.statusMessage)
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }*/
//
//
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            when (requestCode) {
//                KeyUtils.REQUEST_PLACE_PICKER -> {
//                    val vanillaAddress = VanillaPlacePicker.onActivityResult(data)
//                    if(vanillaAddress!!.countryName.equals("India"))
//                    {
//                        Log.e("vanillaAddress",Gson().toJson(vanillaAddress))
//                        if (addressType.equals("from")) {
//                            fromLat = vanillaAddress.latitude
//                            fromLng = vanillaAddress.longitude
//                            tv_address_from.text = vanillaAddress.formattedAddress
//                        } else if (addressType.equals("to")) {
//                            toLat = vanillaAddress.latitude
//                            toLng = vanillaAddress.longitude
//                            tv_address_to.text = vanillaAddress.formattedAddress
//                        }
//                    }
//                    else
//                    {
//                        Toast.makeText(
//                            this,
//                            "Please select address from india only",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//
//    }

    override fun onResume() {
        super.onResume()
        tv_address_from.text = selectedFromAddress
        tv_address_to.text = selectedToAddress
    }
}