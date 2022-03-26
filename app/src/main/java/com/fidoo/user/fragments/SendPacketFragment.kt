package com.fidoo.user.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fidoo.user.BuildConfig
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.addressmodule.activity.SavedAddressesActivityNew
import com.fidoo.user.sendpackages.model.SendPackagesModel
import com.fidoo.user.sendpackages.viewmodel.SendPackagesViewModel
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_send_package.ed_notes
import kotlinx.android.synthetic.main.activity_send_package.tv_address_from
import kotlinx.android.synthetic.main.activity_send_package.tv_address_to
import kotlinx.android.synthetic.main.fragment_send_packet.view.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class SendPacketFragment : com.fidoo.user.utils.BaseFragment(),
    AdapterClick, PaymentResultListener {

    lateinit var mView: View


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
        var other_taxes_and_charges: String = ""

    }

    override fun provideYourFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater!!.inflate(R.layout.fragment_send_packet, parent, false)



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
        Checkout.preload(requireContext())
        //co.setKeyID("rzp_live_iceNLz5pb15jtP")
        var payley=BuildConfig.pay_key.toString()
        co.setKeyID(payley)


        // showIOSProgress()
        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))

        } else {
            if (com.fidoo.user.data.session.SessionTwiclo(requireContext()).isLoggedIn) {
                viewmodel?.getPackageCatApi(
                    com.fidoo.user.data.session.SessionTwiclo(
                        requireContext()
                    ).loggedInUserDetail.accountId,
                    com.fidoo.user.data.session.SessionTwiclo(
                        requireContext()
                    ).loggedInUserDetail.accessToken
                )
            } else {
                viewmodel?.getPackageCatApi(
                    "",
                    ""
                )
            }
            viewmodel?.getPackageCatApi(
                com.fidoo.user.data.session.SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
                com.fidoo.user.data.session.SessionTwiclo(requireContext()).loggedInUserDetail.accessToken
            )
        }

        mView.tv_address_title.setOnClickListener {
            findNavController().navigate(R.id.itemsBottomSheetFragment)
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
            com.fidoo.user.data.session.SessionTwiclo(requireContext()).storeId = ""
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

        mView.from_address_lay.setOnClickListener {
            addressType = "from"
            startActivity(
                Intent(requireContext(), SavedAddressesActivityNew::class.java).putExtra(
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

        mView.to_address_lay.setOnClickListener {
            addressType = "to"
            startActivity(
                Intent(requireContext(), SavedAddressesActivityNew::class.java).putExtra(
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


        viewmodel?.sendPackagesResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cat response", Gson().toJson(user))
            /*   if (paymentRadioGroup.checkedRadioButtonId.equals(R.id.onlineRadioBtn)) {
                sendPackagesModel=user
                      launchPayUMoneyFlow(user.paymentAmt)
            }
            else
            {*/
           /* startActivity(Intent(requireContext(), SendPackageOrderDetail::class.java)
                .putExtra("base_distance", user.base_distance)
                .putExtra("additional_distance", user.additional_distance)
                .putExtra("distance", user.distance)
                .putExtra("time", user.time)
                .putExtra("orderId", user.orderId)
                .putExtra("base_price", user.base_price)
                .putExtra("additional_charges", user.additional_charges)
                .putExtra("tax", user.tax)
                .putExtra("paymentAmt", user.paymentAmt)
            )*/
           /* buyPopup(
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
            ) *///   }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        mView.back_action.setOnClickListener {
            requireActivity().finish()
        }

//        lifecycleScope.launchWhenResumed {
//
//        }

//        if (ItemsBottomSheetFragment.catId != null)
//        {
//
//            ItemsBottomSheetFragment.catId!!.observe(viewLifecycleOwner) {
//
//                Toast.makeText(requireContext(), "" + it, Toast.LENGTH_SHORT).show()
//
//            }
//        }


        mView.tv_placeOrder.setOnClickListener {
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))

            } else {

//                if (ItemsBottomSheetFragment.catId != "" || ItemsBottomSheetFragment.catId.isNotEmpty()) {
//                    catId = ItemsBottomSheetFragment.catId
//                }

                when {
                    catId == "" -> {
                        showToast("Please select type of package")
                    }

//                    parcelValue.text.toString().equals("") -> {
//                        showToast("Please enter parcel value")
//                    }


//                    catId != "" -> {
//                        if (ItemsBottomSheetFragment.catId != "" || ItemsBottomSheetFragment.catId.isNotEmpty()) {
//                            catId = ItemsBottomSheetFragment.catId
//
//                            if (catId == "1") {
//                                mView.tv_address_title.text = "Food or Meals"
//                            } else if (catId == "2") {
//                                mView.tv_address_title.text = "Cloth"
//                            } else {
//                                mView.tv_address_title.text = "Otherrs"
//
//                            }
//                            // mView.tv_address_title.text=catId
//                        }
//                    }

                    tv_address_from.text.toString().length < 10 -> {
                        showToast("Please enter from address")
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

        return mView
    }


    private fun sendPackageInfo() {

    }

    private fun calculateStoreCustomerDistance() {
        val source =
            selectedfromLat.toString() + "," + selectedfromLng.toString()
        val destination =
            selectedtoLat.toString() + "," + selectedtoLng.toString()
        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=$source&destination=$destination&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
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


                   /* if (distance.isNotEmpty() || distance != "") {
                        viewmodel?.sendPackagesApi(
                            SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
                            SessionTwiclo(requireContext()).loggedInUserDetail.accessToken,
                            tv_address_from.text.toString(),
                            "",
                            selectedfromLat.toString(),
                            selectedfromLng.toString(),
                            tv_address_to.text.toString(),
                            ed_notes.text.toString().trim(),
                            selectedtoLat.toString(),
                            selectedtoLng.toString(),
                            "",
                            fromName,
                            fromNumber,
                            toName,
                            toNumber,
                            "online",
                            distance
                        )
                    } else {
                        showToast("There is some issue in Service, please try after sometime")
                    }*/
                }
            }, Response.ErrorListener { _ -> }) {

        }
        val requestQueue = Volley.newRequestQueue(requireContext())
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
        /*val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.buy_popup, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
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
        mDialogView.total_price.text = resources.getString(R.string.ruppee) + totalPrice*/

    }

    override fun onItemClick(
        productId: String?,
        type: String?,
        count: String?,
        offerPrice: String?,
        customize_count: Int?,
        productType: String?,
        cart_id: String?
    ) {
        catId = productId.toString()
        Toast.makeText(requireContext(), catId, Toast.LENGTH_SHORT).show()
    }


    private fun startPayment(paymentAmt: String) {
        /*
         * Instantiate Checkout
         */
        val activity: Activity = requireActivity()
        val co = Checkout()
        //var razorpayId = "qwerty"

        viewmodel?.sendPackagesResponse?.observe(this, Observer {
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
                prefill.put("email", com.fidoo.user.data.session.SessionTwiclo(
                    requireContext()
                ).profileDetail.account.emailid)
                prefill.put(
                    "contact",
                    com.fidoo.user.data.session.SessionTwiclo(
                        requireContext()
                    ).profileDetail.account.country_code + com.fidoo.user.data.session.SessionTwiclo(
                        requireContext()
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
            Toast.makeText(
                requireContext(),
                "Payment failed $errorCode \n $response",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: java.lang.Exception) {
            Log.e("onError", "Exception in onPaymentSuccess", e)
        }
    }


    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try {
            //Toast.makeText(this, "Payment Successful $razorpayPaymentId", Toast.LENGTH_LONG).show()
            viewmodel?.paymentApi(
                com.fidoo.user.data.session.SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
                com.fidoo.user.data.session.SessionTwiclo(requireContext()).loggedInUserDetail.accessToken,
                sendPackagesModel!!.orderId,
                razorpayPaymentId!!,
                "",
                "online",other_taxes_and_charges
            )


        } catch (e: java.lang.Exception) {
            Log.e("onSuccess", "Exception in onPaymentSuccess", e)
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d("kb", "on resume")
        tv_address_from.text = selectedFromAddress
        tv_address_to.text = selectedToAddress
    }


    override fun onPause() {
        super.onPause()
        Log.d("kb", "on pause")
    }


}