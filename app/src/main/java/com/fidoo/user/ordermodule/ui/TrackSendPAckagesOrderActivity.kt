package com.fidoo.user.ordermodule.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.timerStatus
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.roomdb.database.PrescriptionDatabase
import com.fidoo.user.chatbot.ui.Chatbotui
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.NotiCheck
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.services.OrderBackgroundgService.Companion.bgServicOrderId
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.MY_PERMISSIONS_REQUEST_CODE
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.makesense.labs.curvefit.Curve
import com.makesense.labs.curvefit.interfaces.OnCurveClickListener
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import com.prudhvir3ddy.rideshare.utils.AnimationUtils
import com.prudhvir3ddy.rideshare.utils.MapUtils
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_sendpackages_additem.*
import kotlinx.android.synthetic.main.activity_track_order.*
import kotlinx.android.synthetic.main.activity_track_sendpackages_order.*
import kotlinx.android.synthetic.main.activity_track_sendpackages_order.customer_care_fmL
import kotlinx.android.synthetic.main.activity_track_sendpackages_order.orderTrackBack_fmL
import kotlinx.android.synthetic.main.activity_track_sendpackages_order.tv_order_id
import kotlinx.android.synthetic.main.review_popup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class TrackSendPAckagesOrderActivity : BaseActivity(), OnMapReadyCallback, OnCurveDrawnCallback,
    OnCurveClickListener, NotiCheck,
    LocationListener {
    private var mMap: GoogleMap? = null
    private var timer: CountDownTimer? = null
    private var timerr: CountDownTimer? = null

    //for map
    var rider_LatLng: LatLng? = null
    var rider_LatLngOrg: LatLng? = null
    var rider_LatLng2: LatLng? = null
    var merchantLatLng: LatLng? = null
    var user_LatLng: LatLng? = null

    var rider_LatLngStr: String? = ""
    var merchantLatLngStr: String? = ""
    var user_LatLngStr: String? = ""

    private var movingBikeMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null

    //var timerStatus =  true
    var userName: String? = ""
    var driverMobileNo: String? = ""
    var userMobileNo: String? = ""
    var viewmodel: TrackViewModel? = null
    var orderViewModel: OrderDetailsViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var currentOrderId: String? = ""
    var orderId: String? = ""
    var star: String? = "1"
    var improvement: String? = ""
    var improvementArray: ArrayList<String>? = null
    var checkStatusOfReview: Int = 0
    var reviewpopup: Int = 0
    var Delivery_Time: Int = 0
    var Delivery_experience: Int = 0
    var App_interface: Int = 0
    var Item_packaging: Int = 0
    private var locationManager: LocationManager? = null
    private val REQUEST_CHECK_SETTINGS = 0x1
    var order_cancel_Diolog: Dialog? = null
    private lateinit var prescriptionDatabase: PrescriptionDatabase
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var call_Diolog: Dialog? = null

    private var mMixpanel: MixpanelAPI? = null
    var latLngList: ArrayList<LatLng> = ArrayList()

    var responseHandle = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    var store_phone: String? = ""
    var rider_phone: String? = ""
    companion object {
        var trackOrderContext: Context? = null
        val notiInterface = TrackSendPAckagesOrderActivity()

        var rider_assignTimeStr: String = ""
        var rider_picLocTimeStr: String = ""
        var packageOnWay_TimeStr: String = ""
        var package_arrived: String = ""
        var OrderId_str: String = ""

        var order_status_for_track = ""
        var check_gMap1 = 0
        var check_gMap2 = 0
        var check_gMap3 = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_track_sendpackages_order)
        improvementArray = ArrayList()
        viewmodel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        orderViewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)
        userName = intent.getStringExtra("delivery_boy_name")
        OrderId_str = intent.getStringExtra("orderId").toString()
        timerStatus = true
        trackOrderContext = this
        deleteAllPrecription()
        behavior = BottomSheetBehavior.from(bottomSheetBtn_)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        if (intent.getStringExtra("orderId") != "") {
            bgServicOrderId = intent.getStringExtra("orderId")!!
            tv_order_id.text = intent.getStringExtra("orderId")!!
            //   startService(Intent(applicationContext, OrderBackgroundgService::class.java))
        }

        viewmodelusertrack?.customerActivityLog(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).mobileno, "OrderTrack Screen",
            SplashActivity.appversion, "", SessionTwiclo(this).deviceToken
        )


        displayLocationSettingsRequest(this)

        checkPermission()

        getLocation()
//        CoroutineScope(Dispatchers.IO).launch {
//            delay(30000)
//            customer_care_fmL.visibility = View.VISIBLE
//       }
        customer_care_fmL.setOnClickListener {

//            val dialIntent = Intent(Intent.ACTION_DIAL)
//            dialIntent.data = Uri.parse("tel:" + 9871322057)
//            startActivity(dialIntent)
            AppUtils.startActivityRightToLeft(this,Intent(this, Chatbotui::class.java)
                .putExtra("orderId", intent.getStringExtra("orderId")!!))
        }

        tv_delivery_boy_callNew.setOnClickListener {

//            val dialIntent = Intent(Intent.ACTION_DIAL)
//            dialIntent.data = Uri.parse("tel:" + driverMobileNo)
//            startActivity(dialIntent)

            if (userMobileNo!!.isNotEmpty()) {
                onCallPopUp(1)

                if (sessionInstance.profileDetail != null) {
                    viewmodel?.callCustomerApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        userMobileNo!!,
                        driverMobileNo!!
                    )
                } else {
                    viewmodel?.callCustomerApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        userMobileNo!!,
                        driverMobileNo!!
                    )
                }
            }
        }

        viewmodel?.trackSendPackagesOrderApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            OrderId_str
        )

        viewmodel?.getLocationApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            OrderId_str, "user"
        )



        orderTrackBack_fmL.setOnClickListener {
            if (intent.hasExtra("type")) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            } else {
                finish()
                AppUtils.finishActivityLeftToRight(this)
            }
        }

        viewmodel?.getLocationApi(
            SessionTwiclo(this@TrackSendPAckagesOrderActivity).loggedInUserDetail.accountId,
            SessionTwiclo(this@TrackSendPAckagesOrderActivity).loggedInUserDetail.accessToken,
            OrderId_str,
            "user"
        )

        viewmodel?.cancelOrderResponse?.observe(this, { user ->
            dismissIOSProgress()
            orderCancelPopUp()
            Log.e("cancelOrderResponse", Gson().toJson(user))
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        viewmodel?.getLocationResponse?.observe(this, { user ->
            try {
                Log.e("getLocationResponse___", Gson().toJson(user))
                Log.e("getLocationResponse___", timerStatus.toString())
                Log.e("check_gMap1", "$check_gMap1--$check_gMap2--$check_gMap3")
                userName = user.driver_name
                driverMobileNo = user.driver_mobile
                currentOrderId = user.orderId
                mMap?.clear()

            } catch (e: Exception) {
                e.printStackTrace()
                mMap?.clear()
            }

            try {
                if (user.driverLatitude.isNotEmpty()) {
                    rider_LatLng = LatLng(user.driverLatitude.toDouble(), user.driverLongitude.toDouble())
                    rider_LatLngOrg = LatLng(user.driverLatitude.toDouble(), user.driverLongitude.toDouble())
                    rider_LatLngStr = user.driverLatitude + "," + user.driverLongitude
                    Log.e("rider_LatLngStr", rider_LatLngStr.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (!user.merchantLatitude.isEmpty()) {
                    merchantLatLng =
                        LatLng(user.merchantLatitude.toDouble(), user.merchantLongitude.toDouble())
                    merchantLatLngStr = user.merchantLatitude + "," + user.merchantLongitude
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {
                if (user.userLatitude.isNotEmpty() && user.userLatitude.isNotEmpty()) {
                    user_LatLng =
                        LatLng(user.userLatitude.toDouble(), user.userLongitude.toDouble())
                    user_LatLngStr = user.userLatitude + "," + user.userLongitude

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            calculateEstimatedTime(rider_LatLngStr!!, user_LatLngStr!!)


            if (order_status_for_track.equals("")) {

                if (check_gMap1 == 0) {
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(merchantLatLng, 13f))
                    check_gMap1 = 1
                }

                if (merchantLatLng != null) {

                    mMap?.addMarker(
                        MarkerOptions()
                            .position(merchantLatLng!!)
                            //.icon(bitmapDescriptorFromVector_(this, R.drawable.pkg_icon))
                            .icon(bitmapDescriptorFromVector(this, R.drawable.pkg_icon))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(true)
                    )
                }

                if (user_LatLng != null) {

                    mMap?.addMarker(
                        MarkerOptions()
                            .position(user_LatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.home_locpin))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(true)
                    )

                    mMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            user_LatLng,
                            13f
                        ), 3000, null
                    )
                }

                createDashedLine(
                    merchantLatLng!!,
                    user_LatLng!!,
                    Color.parseColor("#C7C7C7")
                )


            } else if (order_status_for_track.equals("rider_assign")) {
                if (check_gMap2 == 0) {
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(merchantLatLng, 13f))
                    check_gMap2 = 1
                }

                drawRoute(rider_LatLngStr!!, merchantLatLngStr!!, "")

                if (rider_LatLng != null) {
//                    var rotation=0.0f
//
//                    try {
//                        if (latLngList.isNullOrEmpty()){
//                            var rider_LatLngNext= LatLng(latLngList[1].latitude, latLngList[1].longitude)
//                            rotation = MapUtils.getRotation(rider_LatLng!!, rider_LatLngNext).toFloat()
//                        }else {
//                            rotation = MapUtils.getRotation(rider_LatLng!!, user_LatLng!!).toFloat()
//                        }
//
//
//                    }catch (e:Exception){
//                        e.printStackTrace()
//                    }
//                    rotation = MapUtils.getRotation(rider_LatLng!!, rider_LatLng!!)
//                    if (!rotation.isNaN()) {
//                        Log.d("rotationf__", rotation.toString())
//                        val valueAnimator = AnimationUtils.cabAnimator()
//
//                        movingBikeMarker=   mMap?.addMarker(
//                            MarkerOptions()
//                                .position(rider_LatLng!!)
//                                .rotation(rotation)
//                                .icon(bitmapDescriptorFromVector_(this, R.drawable.rider))
//                                .rotation(rotation)
//                                .anchor(0.5f, .5f)
//                                .zIndex(20.0f)
//                            //	.draggable(true).flat(true)
//
//                        )
//                        valueAnimator.start()
//
//                    }
//
//                    mMap?.animateCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                            rider_LatLng,
//                            14f
//                        ), 3000, null
//                    )


                    if (rider_LatLng != null) {
                        //    updateCarLocation(rider_LatLng!!)
                        showDefaultLocationOnMap(rider_LatLng!!)
                    }

                }

                if (merchantLatLng != null) {

                    mMap?.addMarker(
                        MarkerOptions()
                            .position(merchantLatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.pkg_icon))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(true)
                    )

                }


            } else if (order_status_for_track.equals("order_picked")) {
                if (check_gMap3 == 0) {
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(user_LatLng, 13f))
                    check_gMap3 = 1
                }

//                if (rider_LatLng != null) {
//                    var rotation = 0.0f
//
//                    try {
//                        if (latLngList.isNullOrEmpty()) {
//                            var rider_LatLngNext =
//                                LatLng(latLngList[1].latitude, latLngList[1].longitude)
//                            rotation =
//                                MapUtils.getRotation(rider_LatLng!!, rider_LatLngNext).toFloat()
//                        } else {
//                            rotation = MapUtils.getRotation(rider_LatLng!!, user_LatLng!!).toFloat()
//                        }
//
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                    //  val rotation = MapUtils.getRotation(rider_LatLng!!, merchantLatLng!!)
//                    if (!rotation.isNaN()) {
//                        Log.d("rotationf__", rotation.toString())
//                        val valueAnimator = AnimationUtils.cabAnimator()
//
//                        mMap?.addMarker(
//                            MarkerOptions()
//                                .position(rider_LatLng!!)
//                                .rotation(rotation)
//                                .icon(bitmapDescriptorFromVector_(this, R.drawable.rider))
//                                .rotation(rotation)
//                                .anchor(0.5f, .5f)
//                                .zIndex(20.0f)
//                            //	.draggable(true).flat(true)
//
//                        )
//                        valueAnimator.start()
//
//                    }
//
//                    mMap?.animateCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                            rider_LatLng,
//                            14f
//                        ), 3000, null
//                    )
//
//                }

                if (user_LatLng != null) {

                    mMap?.addMarker(
                        MarkerOptions()
                            .position(user_LatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.home_locpin))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(true)
                    )

                }

                drawRoute(rider_LatLngStr!!, user_LatLngStr!!, "")
            }


        })

        viewmodel?.trackPackageOrderModelRes?.observe(this, {
            Log.e("trackPackageOrder_", Gson().toJson(it))
            if (it.error_code.equals("200")) {
                try {
                    tv_order_id.text = it.order_id
                    total_item_text_new.text =
                        "Total delivery charge :" + "₹" + it.total_delivery_charge

                    driverMobileNo = it.delivery_boy_phone
                    userMobileNo = it.customer_phone


                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    if (it.order_status != null) {
                        when {

                            it.order_status.equals("0") -> {
                                order_status_new.text = "Failed"
                            }

                            it.order_status.equals("1") -> {
                                //place order first time
                                tv_delivery_boy_new.text = it.rider_status
                                order_status_new.text = it.order_message
                                rider_assignTimeStr = it.updated_at
                                tv_riderAssignedTime.text = rider_assignTimeStr

                            }

                            it.order_status.equals("2") -> {

                                if (MainActivity.onBackpressHandle.equals("1")) {
                                    startActivity(Intent(this, MainActivity::class.java))
                                } else {
                                    finish()
                                }
                            }

                            it.order_status.equals("9") -> {
                                //assign rider
                                tv_delivery_boy_new.text = it.rider_status
                                order_status_new.text = it.order_message
                                rider_assignTimeStr = it.updated_at
                                rider_assignImg.setColorFilter(resources.getColor(R.color.primary_color))
                                tv_delivery_boy_callNew.setImageResource(R.drawable.call_fill)

                                vertical_line_btw_two_loc1.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )
                                tv_riderAssignedTime.text = rider_assignTimeStr

                                order_status_for_track = "rider_assign"

                            }

                            it.order_status.equals("10") -> {
                                //package on te way pickup
                                order_status_for_track = "order_picked"

                                tv_delivery_boy_new.text = it.rider_status
                                order_status_new.text = it.order_message
                                packageOnWay_TimeStr = it.updated_at
                                tv_Package_Onthe_WayTime.text = packageOnWay_TimeStr
                                rider_assignImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc1.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )
                                tv_delivery_boy_callNew.setImageResource(R.drawable.call_fill)


                                rider_pickup_locImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc2.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )

                                packagesOnWayImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc3.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )

                            }

                            it.order_status.equals("3") -> {
                                // packaged arried delivered

                                tv_delivery_boy_new.text = it.rider_status
                                order_status_new.text = it.order_message
                                package_arrived = it.updated_at
                                tv_Package_Onthe_WayTime.text = package_arrived
                                rider_assignImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc1.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )
                                tv_delivery_boy_callNew.setImageResource(R.drawable.call_fill)


                                rider_pickup_locImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc2.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )

                                packagesOnWayImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc3.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )

                                img_to_packetArrived.setColorFilter(resources.getColor(R.color.primary_color))
                                OrdersFragment.handleApiResponseForSendPackage = 1
                                finish()
                            }

                            it.order_status.equals("14") -> {
                                // at pickup location
                                tv_delivery_boy_new.text = it.rider_status
                                order_status_new.text = it.order_message
                                rider_picLocTimeStr = it.updated_at
                                tv_At_pickup_locTime.text = rider_picLocTimeStr
                                rider_assignImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc1.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )
                                tv_delivery_boy_callNew.setImageResource(R.drawable.call_fill)

//                                rider_pickup_locImg.setColorFilter(resources.getColor(R.color.primary_color))
//
//                                vertical_line_btw_two_loc2.background.setTint(
//                                    getResources().getColor(
//                                        R.color.primary_color
//                                    )
//                                )
                                order_status_for_track = "rider_assign"

                            }

                            it.order_status.equals("15") -> {
                                // at pickup location
                                tv_delivery_boy_new.text = it.rider_status
                                order_status_new.text = it.order_message
                                rider_picLocTimeStr = it.updated_at
                                tv_At_pickup_locTime.text = rider_picLocTimeStr
                                rider_assignImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc1.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )
                                tv_delivery_boy_callNew.setImageResource(R.drawable.call_fill)

                                rider_pickup_locImg.setColorFilter(resources.getColor(R.color.primary_color))
                                vertical_line_btw_two_loc2.background.setTint(
                                    getResources().getColor(
                                        R.color.primary_color
                                    )
                                )
                                order_status_for_track = "rider_assign"

                            }

                            it.order_status.equals("16") -> {
                                order_status_for_track = "order_picked"
                            }

                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })

    }


    private fun bitmapDescriptorFromVector_(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, 50, 75)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
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
            AppUtils.finishActivityLeftToRight(this)
        }
    }


    @SuppressLint("SimpleDateFormat")

    fun getDifferenceBetweenDates(created_datee: String): Long {
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

        Log.e("created_datee", created_datee)
        Log.e("formatted", formatted)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        // 2021-01-06 10:35:56
        val date1 = simpleDateFormat.parse(formatted)
        val date2 = simpleDateFormat.parse(created_datee)

        val diff: Long = date1.time - date2.time
        val seconds = diff / 1000

        Log.i(" secs", " ::  " + seconds)

        return seconds

    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            mMap = p0
            mMap!!.getUiSettings().setCompassEnabled(false);

        }
    }

    override fun onCurveDrawn(curve: Curve?) {}

    override fun onCurveClick(curve: Curve?) {}

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


    fun drawRoute(rider_latLong: String, destination: String, type: String) {
        val path: MutableList<List<LatLng>> = ArrayList()

        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=" + rider_latLong + "&destination=" +
                    destination + "&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
        Log.e("urlDirections", "routes- $urlDirections---$type")

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

                    val options = PolylineOptions().width(5f)
                        .color(Color.BLACK)
                        .geodesic(true).zIndex(5.0f)


                    if (type.equals("")) {

                    } else if (type.equals("rider_assign")) {

                    } else if (type.equals("order_picked")) {

                    }

                    for (i in 0 until path.size) {
                        var markerRiderLat =
                            steps.getJSONObject(i).getJSONObject("start_location")
                                .getString("lat")
                        var markerRiderIng =
                            steps.getJSONObject(i).getJSONObject("start_location")
                                .getString("lng")
                        options.addAll(path[i]).color(Color.BLACK).zIndex(5.0f)
                        latLngList.add(
                            LatLng(
                                markerRiderLat.toDouble(),
                                markerRiderIng.toDouble()
                            )
                        )
                    }
                    this?.mMap!!.addPolyline(options)
                    responseHandle = 0
                  //  updateCarLocation(latLngList[0])
                    updateCarLocationNew(path)
                    // responseHandle=1



                    //showPath(latLngList)
                }
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }


    fun createDashedLine(latLngOrig: LatLng, latLngDest: LatLng, color: Int) {
        if (mMap != null) {
            val difLat = latLngDest.latitude - latLngOrig.latitude
            val difLng = latLngDest.longitude - latLngOrig.longitude
            val zoom = mMap!!.cameraPosition.zoom.toDouble()
            val divLat = difLat / (zoom * 2)
            val divLng = difLng / (zoom * 2)
            var tmpLatOri = latLngOrig
            var i = 0
            while (i < zoom * 2) {
                var loopLatLng = tmpLatOri
                if (i > 0) {
                    loopLatLng = LatLng(
                        tmpLatOri.latitude + divLat * 0.25f,
                        tmpLatOri.longitude + divLng * 0.25f
                    )
                }
                val polyline = mMap!!.addPolyline(
                    PolylineOptions()
                        .add(loopLatLng)
                        .add(LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                        .color(color)
                        .width(5f)
                )
                tmpLatOri = LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng)
                i++
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //mMap = GoogleMap()
        //MainActivity.check = "yes"
        Log.e("upcoming_resume", "yes")
        tv_order_id.text = intent.getStringExtra("orderId")!!
        viewmodel?.getLocationApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!, "user"
        )

        timer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                Log.e("_Timer", "seconds remaining: " + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                viewmodel?.trackSendPackagesOrderApi(
                    SessionTwiclo(this@TrackSendPAckagesOrderActivity).loggedInUserDetail.accountId,
                    SessionTwiclo(this@TrackSendPAckagesOrderActivity).loggedInUserDetail.accessToken,
                    OrderId_str
                )

                viewmodel?.getLocationApi(
                    SessionTwiclo(this@TrackSendPAckagesOrderActivity).loggedInUserDetail.accountId,
                    SessionTwiclo(this@TrackSendPAckagesOrderActivity).loggedInUserDetail.accessToken,
                    OrderId_str,
                    "user"
                )
                //trackApi call

                timer?.start()
            }

        }.start()


    }

    override fun onPause() {
        super.onPause()
        timer!!.cancel()
        MainActivity.check = "no"
        Log.e("upcoming Pause", "yes")
        if (call_Diolog != null) {
            call_Diolog!!.dismiss()
        }
    }

    override fun onStop() {
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
                ) { _, _ ->
                }
                customBuilder.setCancelable(false)
                val dialog = customBuilder.create()
                dialog.show()

            }
        } else {
            runOnUiThread {
                val customBuilder = AlertDialog.Builder(trackOrderContext!!)
                customBuilder.setTitle("Twiclo")
                customBuilder.setMessage("Order is Rejected by merchant")
                customBuilder.setNegativeButton("OK") { _, _ -> // MyActivity.this.finish();
                    trackOrderContext!!.startActivity(
                        Intent(trackOrderContext, MainActivity::class.java)
                    )
                    finishAffinity()
                }
                customBuilder.setCancelable(false)
                val dialog = customBuilder.create()
                dialog.show()

            }
        }
        /*  trackOrderContext!!.startActivity(Intent(trackOrderContext, HomeActivity::class.java))
            finishAffinity()*/


    }

    private fun buyPopup(orderId: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.review_popup, null)
        //AlertDialogBuilder
        val mBuilder = android.app.AlertDialog.Builder(this)
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

        mDialogView.poor_icon_select.setBackgroundResource(R.drawable.rectangle_border)

        mDialogView.cancel_reviewPopUp.setOnClickListener { mAlertDialogg.dismiss() }

        mDialogView.poor_icon_select.setOnClickListener {
            star = "1"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.poor)
            mDialogView.reviewName_txt.text = "Very bad"
            mDialogView.selection_ques_txt.setText(R.string.poor_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.rectangle_border)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.bad_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)

        }

        mDialogView.bad_icon_select.setOnClickListener {
            star = "2"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.bad)
            mDialogView.reviewName_txt.text = "Bad"
            mDialogView.selection_ques_txt.setText(R.string.poor_experince)
            mDialogView.bad_icon_select.setBackgroundResource(R.drawable.rectangle_border)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)

        }

        mDialogView.ok_icon_select.setOnClickListener {
            star = "3"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.ok)
            mDialogView.reviewName_txt.text = "Average"
            mDialogView.selection_ques_txt.setText(R.string.ok_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.rectangle_border)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.bad_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
        }

        mDialogView.good_icon_select.setOnClickListener {
            star = "4"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.good)
            mDialogView.reviewName_txt.text = "Good"
            mDialogView.selection_ques_txt.setText(R.string.good_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.rectangle_border)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.bad_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
        }

        mDialogView.excellent_icon_select.setOnClickListener {
            star = "5"
            mDialogView.img_review_img.setBackgroundResource(R.drawable.excellent)
            mDialogView.reviewName_txt.text = "loveed it!"
            mDialogView.selection_ques_txt.setText(R.string.excellent_experince)
            mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.good_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.bad_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
            mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.rectangle_border)
        }

        //improvement
        mDialogView.itemPackaging_txt_selection.setOnClickListener {
            if (Item_packaging == 0) {
                improvementArray!!.add("'Item packaging'")
                mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
                Item_packaging = 1
            } else {
                improvementArray!!.remove("'Item packaging'")
                mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
                Item_packaging = 0
            }
            //improvement="Item packaging"
//            mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
//            mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
//            mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)

        }

        mDialogView.delivery_txt_selection.setOnClickListener {
            if (Delivery_Time == 0) {
                improvementArray!!.add("'Delivery Time'")
                mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
                Delivery_Time = 1
            } else {
                improvementArray!!.remove("'Delivery Time'")
                mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
                Delivery_Time = 0
            }
            // improvement="Delivery Time"
            //  mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            //  mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            //  mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
        }

        mDialogView.appInterface_txt_selection.setOnClickListener {
            if (App_interface == 0) {
                improvementArray!!.add("'App interface'")
                mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
                App_interface = 1
            } else {
                improvementArray!!.remove("'App interface'")
                mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
                App_interface = 0
            }

            //   improvement="App interface"
            //   mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            //   mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            //   mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
        }

        mDialogView.deliveryExperience_txt_selection.setOnClickListener {
            if (Delivery_experience == 0) {
                improvementArray!!.add("'Delivery experience'")
                mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_solid)
                Delivery_experience = 1
            } else {
                improvementArray!!.remove("'Delivery experience'")
                mDialogView.deliveryExperience_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
                Delivery_experience = 0
            }

            //  improvement="Delivery experience"
            //   mDialogView.itemPackaging_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            //   mDialogView.delivery_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
            //   mDialogView.appInterface_txt_selection.setBackgroundResource(R.drawable.black_rounded_empty)
        }

        mDialogView.submitTextBtn.setOnClickListener {
            mAlertDialogg.dismiss()
            showIOSProgress()
            checkStatusOfReview = 1
            val s: Set<String> = LinkedHashSet<String>(improvementArray)
            improvementArray!!.clear()
            improvementArray!!.addAll(s)

            improvement = improvementArray.toString()
            Log.d("improvement---", improvement.toString())
            viewmodel?.addfeedbackApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                orderId,
                star,
                improvement,
                mDialogView.remark_txt.text.toString(),
                "add"
            )

        }

        mDialogView.cancelTxt.setOnClickListener {
            //dismiss dialog
            mAlertDialogg.dismiss()
        }

    }

    fun calculateEstimatedTime(source: String, destination: String) {
        var urlEstimatedTime = ""
        if (order_status_for_track.equals("order_picked")) {
            urlEstimatedTime =
                "https://maps.googleapis.com/maps/api/directions/json?rider_LatLng=$source&destination=$destination&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
        } else {
            urlEstimatedTime =
                "https://maps.googleapis.com/maps/api/directions/json?origin=" + source + "&destination=" + destination + "&waypoints=via:" + merchantLatLngStr + "&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
        }

        Log.e("urlEstimatedTime", urlEstimatedTime)

        val directionsRequest = object :
            StringRequest(Method.GET, urlEstimatedTime, Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                Log.e("res", "routes- $jsonResponse")
                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() != 0) {
                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val estimatedTime =
                        legs.getJSONObject(0).getJSONObject("duration").get("text").toString()

                    if (estimatedTime.isNotEmpty()) {
                        eta_lay_new.visibility = View.VISIBLE
                        Log.e("estimated_time_new___", estimatedTime)
                        estimated_time_new.text = estimatedTime + " ETA"
                        //  estimated_time_new.text = estimatedTime
                    } else {
                        eta_lay_new.visibility = View.GONE

                        //showToast("There is some issue in Service, please try after sometime")
                    }
                }
            }, Response.ErrorListener { }) {

        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)

        //return packageDistance!!


    }

    private fun getLocation() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        } catch (e: SecurityException) {
        }
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())

        result.setResultCallback { result ->
            val status = result.status
            Log.d("resultt", result.toString())
            Log.d("statuss", status.toString())
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION //Manifest.permission.READ_PHONE_STATE
                    ),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            //  Toast.makeText(this@AddAddressActivity, "Permissions already granted", Toast.LENGTH_SHORT).show();
        }
    }


    override fun onLocationChanged(p0: Location) {}

    fun orderCancelPopUp() {
        order_cancel_Diolog = Dialog(this)
        order_cancel_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        order_cancel_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        order_cancel_Diolog?.setContentView(R.layout.order_cancel_popup)
        order_cancel_Diolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        // order_cancel_Diolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        order_cancel_Diolog?.setCanceledOnTouchOutside(true)
        order_cancel_Diolog?.show()
        val order_cancelImg = order_cancel_Diolog?.findViewById<ImageView>(R.id.order_cancelImg)

        Glide.with(this).load(R.drawable.order_cancel)
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
            .placeholder(R.drawable.order_cancel)
            .error(R.drawable.order_cancel).into(order_cancelImg!!)

        Handler().postDelayed({
            order_cancel_Diolog?.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }, 5000)
    }

    //delete all
    private fun deleteAllPrecription() {
        Thread {
            try {
                prescriptionDatabase = Room.databaseBuilder(
                    applicationContext,
                    PrescriptionDatabase::class.java, PrescriptionDatabase.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                prescriptionDatabase!!.prescriptionDao()!!.deleteAllItem()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    private fun updateCarLocation(latLng: LatLng) {
        //   if (movingBikeMarker==null) {
        movingBikeMarker = addCarMarkerAndGet(latLng)
        //  }
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
            movingBikeMarker?.position = currentLatLng
            movingBikeMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLng!!)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            val valueAnimator = AnimationUtils.cabAnimator()
            valueAnimator.addUpdateListener { va ->
                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                        multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingBikeMarker?.position = nextLocation
                    val rotation = MapUtils.getRotation(previousLatLng!!, nextLocation)
                    Log.d("dfddf",rotation.toString())
                    if (!rotation.isNaN()) {
                        movingBikeMarker?.rotation = rotation
                    }
                    movingBikeMarker?.setAnchor(0.5f, 0.5f)
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }

    private fun updateCarLocationNew(path: MutableList<List<LatLng>>) {

        var index = -1
        var next = 0

        val handler = Handler()
        handler.postDelayed(Runnable {
            if (index<path.size-1){
                index++
                next=index+1

            }else{
                next=1
            }
            if (index<path.size-1){
                rider_LatLng= LatLng( latLngList[index].latitude.toDouble(), latLngList[index].longitude.toDouble())
                rider_LatLng2= LatLng( latLngList[next].latitude.toDouble(), latLngList[next].longitude.toDouble())
            }

            movingBikeMarker = addCarMarkerAndGet(rider_LatLngOrg!!)
            movingBikeMarker?.setAnchor(0.5f, 0.5f)
            movingBikeMarker!!.position=rider_LatLngOrg
            animateCamera(rider_LatLngOrg!!)

            val valueAnimator = AnimationUtils.cabAnimator()
            valueAnimator.addUpdateListener {
                var v =valueAnimator.animatedFraction
                val Lng=v*rider_LatLng!!.longitude+(1-v)*rider_LatLng2!!.longitude
                var lat=v*rider_LatLng!!.latitude+(1-v)*rider_LatLng2!!.latitude
                var newPos=LatLng(lat,Lng)
//                movingBikeMarker!!.position=newPos

                val rotation = MapUtils.getRotation(rider_LatLng!!, newPos)
                Log.d("dfddf",rotation.toString())
                if (!rotation.isNaN()) {
                    movingBikeMarker?.rotation = rotation
                }

            }
            valueAnimator.start()

        }, 600)
    }


    private fun moveCamera(latLng: LatLng) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addCarMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(this))
        return mMap!!.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap())

        return mMap!!.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun showDefaultLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)

    }

    private fun onCallPopUp(type: Int) {
        call_Diolog = Dialog(this)
        call_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        call_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        call_Diolog?.setContentView(R.layout.call_popup)
        call_Diolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        call_Diolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        call_Diolog?.setCanceledOnTouchOutside(true)
        call_Diolog?.show()

        val callTypeTxt = call_Diolog?.findViewById<TextView>(R.id.callTypeTxt)
        val regImg = call_Diolog?.findViewById<ImageView>(R.id.regImg)
        val cancelDialogConstL =
            call_Diolog?.findViewById<ConstraintLayout>(R.id.cancelDialogConstL)

        if (regImg != null) {
            Glide.with(this)
                .load(R.drawable.call_wait)
                .fitCenter()
                .error(R.drawable.default_item)
                .into(regImg)
        }

            callTypeTxt!!.setText("Just a minute, connecting with the rider in a bit.")

        cancelDialogConstL?.setOnClickListener {
            call_Diolog?.dismiss()
        }
    }

}
