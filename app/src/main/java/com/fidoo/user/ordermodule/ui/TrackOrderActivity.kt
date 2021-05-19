package com.fidoo.user.ordermodule.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.NotiCheck
import com.fidoo.user.ui.MainActivity
import com.fidoo.user.ui.MainActivity.Companion.timerStatus
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.statusBarTransparent
import com.fidoo.user.viewmodels.TrackViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.makesense.labs.curvefit.Curve
import com.makesense.labs.curvefit.impl.CurveManager
import com.makesense.labs.curvefit.interfaces.OnCurveClickListener
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback
import kotlinx.android.synthetic.main.activity_track_order.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class TrackOrderActivity : BaseActivity(), OnMapReadyCallback, OnCurveDrawnCallback, OnCurveClickListener, NotiCheck {

    private var curveManager: CurveManager? = null
    private var mMap: GoogleMap? = null
    private var timer: CountDownTimer? = null
    private var timerr: CountDownTimer? = null
    var origin: LatLng? = null
    var mid: LatLng? = null
    var destination: LatLng? = null
    //var timerStatus =  true
    var userName: String? = ""
    var driverMobileNo: String? = ""
    var viewmodel: TrackViewModel? = null
    var orderViewModel: OrderDetailsViewModel? = null
    var currentOrderId: String? = ""
    var orderId: String? = ""


    companion object {

        var trackOrderContext: Context? = null

        val notiInterface = TrackOrderActivity()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)
        statusBarTransparent()

        viewmodel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        orderViewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        userName = intent.getStringExtra("delivery_boy_name")
        timerStatus = true
        //  continueTxt.setText(intent.getStringExtra("delivery_boy_mobile"))

        trackOrderContext = this
        //Log.e("Timer Status", timerStatus.toString())
        tv_order_id.text = intent.getStringExtra("orderId")!!

        cancelBtn.visibility = View.GONE

        customer_care.setOnClickListener {

            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + 8047165887)
            startActivity(dialIntent)

        }


        tv_delivery_boy_call.setOnClickListener {

            viewmodel?.callCustomerResponse?.observe(this, androidx.lifecycle.Observer {
                val call_status = it.message
                showToast("" + call_status)
            })

            viewmodel?.callCustomerApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    sessionInstance.profileDetail.account.userName,
                    driverMobileNo!!
            )
        }






        viewmodel?.getLocationApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                intent.getStringExtra("orderId")!!, "user"
        )


        orderViewModel?.getOrderDetails(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!
        )

        //currentOrderId = intent.getStringExtra("orderId")!!

        //order_status.text = intent.getStringExtra("order_status")

        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //cancel()
                //waitingLay.visibility= View.VISIBLE
                Log.e("Location Timer", "seconds remaining: " + millisUntilFinished / 1000)
                //  mTextField.setText("seconds remaining: " + millisUntilFinished / 1000)
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                viewmodel?.getLocationApi(
                        SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accountId,
                        SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accessToken,
                        intent.getStringExtra("orderId")!!,
                        "user"
                )
                orderViewModel?.getOrderDetails(
                    SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accountId,
                    SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accessToken, intent.getStringExtra("orderId")
                )
                timer?.start()


                //mTextField.setText("done!")
            }

        }.start()

        backIconnTrack.setOnClickListener {
            if (intent.hasExtra("type")) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            } else {
                finish()
            }
        }

        orderDetailsTxt.setOnClickListener {
            startActivity(
                    Intent(this, OrderDetailsActivity::class.java).putExtra("orderId", intent.getStringExtra("orderId")!!
                    )
            )
        }

        cancelBtn!!.setOnClickListener {
            showIOSProgress()
            timerr?.cancel()
            viewmodel!!.cancelOrderApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    intent.getStringExtra("orderId")!!
            )

        }

        viewmodel?.getLocationApi(
                SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accountId,
                SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accessToken,
                intent.getStringExtra("orderId")!!,
                "user"
        )

        viewmodel?.cancelOrderResponse?.observe(this, { user ->
            dismissIOSProgress()
            Log.e("stores response", Gson().toJson(user))
            val mModelData: com.fidoo.user.data.model.DeleteModel = user
            showToast(mModelData.message)
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.proceedToOrderResponse?.observe(this, {
            timerr?.cancel()
            showToast("Your order has been placed")
            waitingLay.visibility = View.GONE
        })

        //val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as FragmentContainerView
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        viewmodel?.getLocationResponse?.observe(this, { user ->
            Log.e("timer status", timerStatus.toString())
            userName = user.driver_name
            tv_delivery_boy.text = userName
            driverMobileNo = user.driver_mobile
            currentOrderId = user.orderId
            mMap?.clear()
            // Create a CurveManager object and pass googleMaps reference to it
            // Create a CurveManager object and pass googleMaps reference to it
            //  curveManager = CurveManager(mMap)

            // Register a callback to be invoked after curve is drawn on map

            // Register a callback to be invoked after curve is drawn on map
            //  curveManager!!.setOnCurveDrawnCallback(this)

            // Set a listener for curve click events.

            // Set a listener for curve click events.
            //  curveManager!!.setOnCurveClickListener(this)



            if (user.driverLatitude.isNotEmpty()) {
                origin = LatLng(user.driverLatitude.toDouble(), user.driverLongitude.toDouble())

            }

            val route_source = user.driverLatitude + "," + user.driverLongitude



            if (!user.merchantLatitude.isEmpty()) {
                mid = LatLng(user.merchantLatitude.toDouble(), user.merchantLongitude.toDouble())

            }

            /* if (!user.merchantLatitude.isEmpty()) {
             mid =
                 LatLng(30.6720512, 76.7090212)

         }*/
            val waypoint = user.merchantLatitude + "," + user.merchantLongitude
            //  val waypoint = "30.6720512" + "," + "76.7090212"


            if (user.userLatitude.isNotEmpty() && user.userLatitude.isNotEmpty()) {
                destination = LatLng(user.userLatitude.toDouble(), user.userLongitude.toDouble())

            }

            val route_desti = user.userLatitude + "," + user.userLongitude
            /*              val origin =
                           LatLng(30.696176, 76.714442)
                       val mid =
                           LatLng(30.693431, 76.74706)
                       val destination =
                           LatLng(30.68044, 76.762169)*/
            // Create a CurveOptions object and add atleast two latlong points to it
            // You can set different options in CurveOptions object similar to PolyLineOptions

            // Create a CurveOptions object and add atleast two latlong points to it
            // You can set different options in CurveOptions object similar to PolyLineOptions


            if (origin != null && mid != null && destination != null) {
                /* val curveOptions = CurveOptions()
                 curveOptions.add(origin)
                 curveOptions.add(mid)
                 curveOptions.add(destination)
                 curveOptions.color(Color.DKGRAY)
                 curveOptions.alpha = 0.5f
                 curveOptions.width(12f)
                 val pattern: List<PatternItem> = Arrays.asList(Dash(30f), Gap(30f))
                 curveOptions.pattern(pattern)
                 curveOptions.geodesic(false)*/
                if (origin != null) {
                    mMap?.addMarker(
                            MarkerOptions()
                                    .position(origin!!)
                                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_delivery_bike))
                                    .anchor(0.5f, 1f)
                    )
                }
                if (mid != null) {
                    mMap?.addMarker(
                            MarkerOptions()
                                    .position(mid!!)
                                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_resturant))
                                    .anchor(0.5f, 1f)
                    )
                }


                if (destination != null) {
                    mMap?.addMarker(
                            MarkerOptions()
                                    .position(destination!!)
                                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_home))
                                    .anchor(0.5f, 1f)
                    )

                }

                if (origin != null) {
                    mMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(origin, 14f)
                    )
                }

                drawRoute(route_source, route_desti, waypoint)
                // Draws curve asynchronously
                // Draws curve asynchronously
                //   curveManager!!.drawCurveAsync(curveOptions)


            }











            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        orderViewModel?.OrderDetailsResponse?.observe(this, {

            if (timerStatus){

                if (it.orderStatus == "13"){

                    waitingLay.visibility = View.VISIBLE
                    timerr = object : CountDownTimer(10000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            cancelBtn.visibility = View.VISIBLE
                            cancelBtn.text = "Cancel (" + (millisUntilFinished / 1000) + ")"
                            Log.e("left", "seconds remaining: " + millisUntilFinished / 1000)
                            //  mTextField.setText("seconds remaining: " + millisUntilFinished / 1000)
                            //here you can have your logic to set text to edittext
                        }

                        override fun onFinish() {
                            waitingLay.visibility = View.GONE
                            cancelBtn.visibility = View.GONE
                            timerStatus = false
                            viewmodel?.proceedToOrder(
                                SessionTwiclo(trackOrderContext).loggedInUserDetail.accountId,
                                SessionTwiclo(trackOrderContext).loggedInUserDetail.accessToken,
                                currentOrderId.toString()
                            )

                            /* startActivity(
							 Intent(this@CartActivity, OrderDetailsActivity::class.java).putExtra(
								 "orderId",
								 user.orderId
							 ).putExtra("type", "")
						 )*/
                            //mTextField.setText("done!")
                        }
                    }.start()
                }
            }




        })

        orderViewModel?.OrderDetailsResponse?.observe(this, {
            tv_order_id.text = it.orderId

            when {
                it.orderStatus.equals("0") -> {
                    //holder.buttonValue.visibility = View.GONE
                    order_status.text = "Failed"
                }
                it.orderStatus.equals("1") -> {
                    // holder.buttonValue.visibility = View.VISIBLE
                    // holder.buttonValue.visibility = View.GONE
                    order_status.text = "Please wait while we confirm your order"
                    tv_delivery_boy_call.visibility = View.GONE
                }
                it.orderStatus.equals("2") -> {
                    //holder.buttonValue.visibility = View.GONE
                    order_status.text = "Your order is cancelled"
                }
                it.orderStatus.equals("11") -> {
                    order_status.text = "Your order is being prepared"
                    tv_order_confirmed.setTextColor(Color.rgb(51,147,71))
                    order_confirm_pointer.setColorFilter(Color.rgb(51,147,71))
                }
                it.orderStatus.equals("3") -> {
                    /*if (it.is_rate_to_driver.equals("1")) { // TODO
                        //holder.buttonValue.visibility = View.GONE
                    } else {
                        holder.buttonValue.text = "Review"
                    }*/

                    tv_order_rejection.text = "your order has been delivered. Enjoy!!"

                    order_status.text = "Your order is delivered"
                    address_details_lay.visibility = View.GONE
                    tv_order_rejection.visibility = View.GONE
                    tv_delivery_boy_call.visibility = View.GONE
                    separator_one.visibility = View.GONE
                    tv_order_id.visibility = View.GONE
                    tv_order_id_label.visibility = View.GONE
                    map.alpha = 0.0f

                }
                it.orderStatus.equals("5") -> {
                    //  holder.buttonValue.visibility = View.VISIBLE
                    //holder.buttonValue.visibility = View.GONE
                    order_status.text = "Order is in progress"
                }
                it.orderStatus.equals("6") -> {
                    // holder.buttonValue.visibility = View.VISIBLE

                    //holder.buttonValue.visibility = View.GONE
                    order_status.text = "Your order is out for delivery"
                    tv_order_delivery.setTextColor(Color.rgb(51,147,71))
                    img_to_location.setColorFilter(Color.rgb(51,147,71))
                    tv_delivery_boy_call.visibility = View.VISIBLE

                }
                it.orderStatus.equals("7") -> {
                    //holder.buttonValue.visibility = View.VISIBLE


                    order_status.text = it.storeName+" has accepted your order"
                    tv_order_confirmed.setTextColor(Color.rgb(51,147,71))
                    order_confirm_pointer.setColorFilter(Color.rgb(51,147,71))
                    tv_delivery_boy_call.visibility = View.GONE
                }
                it.orderStatus.equals("9") -> {
                    //holder.buttonValue.visibility = View.VISIBLE
                    order_status.text = "Your Order is ready and will soon be picked up by " + it.deliveryBoyName
                    tv_order_confirmed.setTextColor(Color.rgb(51,147,71))
                    order_confirm_pointer.setColorFilter(Color.rgb(51,147,71))
                    tv_order_picked.setTextColor(Color.rgb(51,147,71))
                    delivery_partner_confirmed_pointer.setColorFilter(Color.rgb(51,147,71))
                    tv_delivery_boy_call.visibility = View.VISIBLE
                }
                it.orderStatus.equals("10") -> {
                    //holder.buttonValue.visibility = View.VISIBLE
                    order_status.text = "Your order is out for delivery"
                    tv_order_delivery.setTextColor(Color.rgb(51,147,71))
                    img_to_location.setColorFilter(Color.rgb(51,147,71))
                    tv_delivery_boy_call.visibility = View.VISIBLE
                }
                it.orderStatus.equals("8") -> {
                    //holder.buttonValue.visibility = View.GONE

                    if (it.paymentMode == "online"){
                        order_status.text = "Your order is rejected. Don't worry, we have processed your refund and same will be credited to your account within 3-5 business days"
                    }else{
                        order_status.text = "Your order is rejected."
                    }


                    address_details_lay.visibility = View.GONE
                    tv_order_rejection.visibility = View.VISIBLE
                    tv_delivery_boy_call.visibility = View.GONE
                    separator_one.visibility = View.GONE
                    tv_order_id.visibility = View.GONE
                    tv_order_id_label.visibility = View.GONE
                    map.alpha = 0.0f
                    customer_care.visibility = View.GONE


                }
            }

        })
    }

    fun String.toDate(
        dateFormat: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

    override fun onBackPressed() {

        if (intent.hasExtra("type")) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }


    @SuppressLint("SimpleDateFormat")

    fun getDifferenceBetweenDates(created_datee: String): Long {
        //   var created_datee= created_date.toDate().formatTo("yyyy-MM-dd HH:mm:ss")


        var formatted: String = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            formatted = current.format(formatter)
            Log.d("answer", formatted)
        } else {
            val date = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formatted = formatter.format(date)
            Log.d("answer", formatted)
        }
        /*  val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val formatted = current.format(formatter)*/
        Log.e("created_datee", created_datee)
        Log.e("formatted", formatted)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        // 2021-01-06 10:35:56
        val date1 = simpleDateFormat.parse(formatted)
        val date2 = simpleDateFormat.parse(created_datee)

//        var dateCheck: Boolean = false
//        if (date1 > date2) {
//            dateCheck = false
//            println("Date 1 occurs after Date 2")
//        } else if (date1 < date2) {
//            dateCheck = false
//            println("Date 1 occurs before Date 2")
//        } else if (date1.compareTo(date2) === 0) {
//            dateCheck = true
//
//            println("Both dates are equal")
//        }

//        val difference : Long = date2!!.time - date1.time
//        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
//        var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
//        val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
//        val sec = difference / 1000
//        hours = if (hours < 0)
//            -hours else
//                hours
//        Log.i("======= Hours", " :: $hours")
//        Log.i("======= Mins", " :: $min")
//        Log.i("======= sec", " :: "+Math.abs(sec))
        val diff: Long = date1.time - date2.time
        val seconds = diff / 1000
//        val minutes = seconds / 60
//        val hourss = minutes / 60
        // val dayss = hours / 24
//        Log.i(" Hours", " :: $hourss")
//        Log.i(" Mins", " :: $minutes")
        Log.i(" secs", " ::  " + seconds)

        return seconds

    }

    /*fun getDifferenceBetweenDatess(created_datee: String): Long {

        var formatted: String = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            formatted  =  current.format(formatter)
            Log.d("answer",formatted)
        } else {
            var date = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formatted  = formatter.format(date)
            Log.d("answer",formatted)
        }
        Log.e("created_datee", created_datee.toString())
        Log.e("formatted", formatted.toString())
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        // 2021-01-06 10:35:56
        var date1 = simpleDateFormat.parse(formatted.toString())
        var date2 = simpleDateFormat.parse(created_datee)

        var dateCheck: Boolean = false
        if (date1.compareTo(date2) > 0) {
            dateCheck = false
            println("Date 1 occurs after Date 2")
        } else if (date1.compareTo(date2) < 0) {
            dateCheck = false
            println("Date 1 occurs before Date 2")
        } else if (date1.compareTo(date2) === 0) {
            dateCheck = true

            println("Both dates are equal")
        }
        val difference: Long = date2.time - date1.time
        var days = (difference / (1000 * 60 * 60 * 24)).toInt()
        var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        var min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
        var sec = difference / 1000
        hours = if (hours < 0) -hours else hours
        Log.i("======= Hours", " :: $hours")
        Log.i("======= Mins", " :: $min")
        Log.i("======= sec", " :: "+Math.abs(sec))


        val diff: Long = date1.time - date2.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hourss = minutes / 60
        val dayss = hours / 24
        Log.i("======= Hourss", " :: $hourss")
        Log.i("======= Minss", " :: $minutes")
        Log.i("======= secs", " ::  "+seconds)

        return difference

    }*/

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            mMap = p0
        }
    }


    override fun onCurveDrawn(curve: Curve?) {

    }

    override fun onCurveClick(curve: Curve?) {

    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int
    ): BitmapDescriptor? {
        val vectorDrawable =
            ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    fun drawRoute(source: String, destination: String, waypoint: String) {
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=" + source + "&destination=" +
                    destination + "&waypoints=via:" + waypoint + "&key=AIzaSyBvnYPa4tw9s5TSGwzePeWD4Kk7yulyy9c"
        val directionsRequest = object :
            StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)


                Log.e("res", "routes- $jsonResponse")
                // Get routes
                val routes = jsonResponse.getJSONArray("routes")


                if (routes.length() != 0) {


                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val steps = legs.getJSONObject(0).getJSONArray("steps")
                    for (i in 0 until steps.length()) {
                        val points =
                            steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                        path.add(decodePoly(points))
                    }
                    for (i in 0 until path.size) {
                        this.mMap?.addPolyline(
                            PolylineOptions().addAll(path[i]).color(Color.BLACK)
                        )
                    }

                }
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)


    }


    override fun onResume() {
        super.onResume()
        //mMap = GoogleMap() TODO
        //MainActivity.check = "yes"
        Log.e("upcoming resume", "yes")
        tv_order_id.text = intent.getStringExtra("orderId")!!
    }

    override fun onPause() {
        super.onPause()
        timer!!.cancel()
        MainActivity.check = "no"
        Log.e("upcoming Pause", "yes")
    }

    override fun onStop(){
        super.onStop()
        timer!!.cancel()
    }

    override fun notiStatus(orderStatus: String) {
        if (orderStatus == "accepted") {
            runOnUiThread {
                val customBuilder =
                    AlertDialog.Builder(trackOrderContext!!)
                customBuilder.setTitle("Twiclo")
                customBuilder.setMessage("Order is Accepted by merchant")
                customBuilder.setNegativeButton(
                    "OK"
                ) { _, _ -> // MyActivity.this.finish();
                    /*SessionTwiclo(context).clearSession()
                        startActivity(Intent(this, SplashActivity::class.java))
                        ActivityCompat.finishAffinity(this!!)*/
                    //waitingLay.visibility = View.GONE
                    /*viewmodel?.getLocationApi(
                            SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accountId,
                            SessionTwiclo(this@TrackOrderActivity).loggedInUserDetail.accessToken,
                            intent.getStringExtra("orderId")!!,
                            "user"
                        )*/

                }
                //customBuilder.setIcon(R.drawable.logo)
                customBuilder.setCancelable(false)
                val dialog = customBuilder.create()
                dialog.show()/* val toast =
                        Toast.makeText(trackOrderContext, "Order is rejected by merchant", Toast.LENGTH_SHORT)
                    toast.show()*/

            }
        } else {
            runOnUiThread {
                val customBuilder = AlertDialog.Builder(trackOrderContext!!)
                customBuilder.setTitle("Twiclo")
                customBuilder.setMessage("Order is Rejected by merchant")
                customBuilder.setNegativeButton("OK") { _, _ -> // MyActivity.this.finish();
                    /*SessionTwiclo(context).clearSession()
                        startActivity(Intent(this, SplashActivity::class.java))
                        ActivityCompat.finishAffinity(this!!)*/
                    trackOrderContext!!.startActivity(
                            Intent(trackOrderContext, MainActivity::class.java)
                    )
                    finishAffinity()
                }
                //customBuilder.setIcon(R.drawable.logo)
                customBuilder.setCancelable(false)
                val dialog = customBuilder.create()
                dialog.show()/* val toast =
                        Toast.makeText(trackOrderContext, "Order is rejected by merchant", Toast.LENGTH_SHORT)
                    toast.show()*/

            }
        }/*  trackOrderContext!!.startActivity(Intent(trackOrderContext, HomeActivity::class.java))
        finishAffinity()*/


    }
}
