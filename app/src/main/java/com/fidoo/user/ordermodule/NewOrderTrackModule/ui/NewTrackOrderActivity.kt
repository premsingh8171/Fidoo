package com.fidoo.user.ordermodule.ui.NewOrderTrackModule.ui

import android.Manifest
import android.R.array
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
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
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.airbnb.lottie.LottieDrawable
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
import com.fidoo.user.activity.MainActivity.Companion.onBackpressHandle
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.adapter.ItemsAdapterTrackScreen
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.cartview.roomdb.database.PrescriptionDatabase
import com.fidoo.user.chatbot.ui.Chatbotui
import com.fidoo.user.constants.useconstants
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.NotiCheck
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.Message
import com.fidoo.user.ordermodule.model.Feedback
import com.fidoo.user.ordermodule.model.OrderDetailsModel
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.NewTrackViewModel.NewOrderViewModel
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.adapter.NewOrderTrackAdapter
import com.fidoo.user.ordermodule.ui.OrderRejectedActivity
import com.fidoo.user.ordermodule.ui.OrdersFragment
import com.fidoo.user.ordermodule.ui.ReviewItemsActivity
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.services.OrderBackgroundgService
import com.fidoo.user.services.OrderBackgroundgService.Companion.bgServicOrderId
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.CommonUtils
import com.fidoo.user.utils.MY_PERMISSIONS_REQUEST_CODE
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
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
import kotlinx.android.synthetic.main.activity_track_order_new.*
import kotlinx.android.synthetic.main.review_popup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
class NewTrackOrderActivity : BaseActivity(), OnMapReadyCallback, OnCurveDrawnCallback,
    OnCurveClickListener, NotiCheck,
    LocationListener {
    //private var curveManager: CurveManager? = null
    private var mMap: GoogleMap? = null
    //private var timer: CountDownTimer? = null
   // private var timerr: CountDownTimer? = null

    private var timer: CountDownTimer? = null
    private var timerr: CountDownTimer? = null

    var product_Update: Int? = 0 //api hit handle on resume

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

    //var timerStatus =  true
    var userName: String? = ""
    var driverMobileNo: String? = ""
    var viewmodel: TrackViewModel? = null
    var orderViewModel: OrderDetailsViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    //for new tracking Screen//
    var newOrderViewModel : NewOrderViewModel? = null
    lateinit var mainAdapter: NewOrderTrackAdapter
    var firstMsgListData = ArrayList<Message>()

//for dialog
var mPresImg: String = ""
    var items: MutableList<OrderDetailsModel.Item>? = null
    var callBack = 0
    var mContext: Context? = null
    var order_Dialog: Dialog? = null
    var order_Dialog1: Dialog? = null
    var viewmodel1: OrderDetailsViewModel? = null
    var finalprice = " "

    var currentOrderId: String? = ""
    var orderId: String? = ""
    var star: String? = "1"
    var improvement: String? = ""
    var store_phone: String? = ""
    var rider_phone: String? = ""
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
    var call_Diolog: Dialog? = null
    private lateinit var prescriptionDatabase: PrescriptionDatabase
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var hit: Long = 5
    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var trackOrderContext: Context? = null
        val notiInterface = NewTrackOrderActivity()
        var trackOrderActivity: NewTrackOrderActivity? = null
        var order_status_for_track = ""
        var check_gMap2 = 0
        var check_gMap3 = 0
    }

    var check_gMap1 = 0

    private var movingCabMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var originMarker: Marker? = null
    private var blackPolyLine: Polyline? = null
    private var greyPolyLine: Polyline? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null

    private var grayPolyline: Polyline? = null
    private var blackPolyline: Polyline? = null
    var latLngList: ArrayList<LatLng> = ArrayList()
    var check = 0
    var handleClick = 0
    var handleCounter = 0
    var heightImg: Int=0
    var heightBottomSheet: Int=0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_track_order_new)
        improvementArray = ArrayList()
        viewmodel = ViewModelProviders.of(this).get(TrackViewModel::class.java)
        orderViewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)
        userName = intent.getStringExtra("delivery_boy_name")

        //  continueTxt.setText(intent.getStringExtra("delivery_boy_mobile"))
        behavior = BottomSheetBehavior.from(bottomSheetBtn)



        bottomSheetBtn.requestLayout();
        val metrics: DisplayMetrics = this.getResources().getDisplayMetrics()
        heightBottomSheet = ((metrics.heightPixels)*(.4)).roundToInt()

        heightImg = ((metrics.heightPixels)*(.65)).roundToInt()

        behavior = BottomSheetBehavior.from(bottomSheetBtn)
        bottomSheetBtn.requestLayout()
        behavior.peekHeight = heightBottomSheet


        val params: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            heightImg
        )
        giflayout.layoutParams = params


   //     ordrePlaceGif.setLayoutParams(LayoutParams(500,gifsize ))



        //For new Order Track Screen api Call code by sumit rai (UC)

        newOrderViewModel = ViewModelProvider(this).get(NewOrderViewModel::class.java)
        newOrderViewModel?.NewTrackScreenApiCall(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!
        )
        newOrderViewModel?.newTrackViewModel?.observe(this) {
            // Log.d("sddffsddsds", Gson().toJson(it)
            firstMsgListData = it.messages as ArrayList<Message>

            useconstants.merchantName = it.merchantDetails.name
            useconstants.dBoyName = it.riderDetails.name
            useconstants.orderStatusMain = it.orderStatus
            useconstants.callAllow = it.callToMerchant
            firstMsgListData = it.messages as ArrayList<Message>

            bottomSheetCallBack()
            useconstants.pos = 0

            if (!it.gif.isEmpty()) {
                ordrePlaceGif.playAnimation()
                ordrePlaceGif.setAnimationFromUrl(it.gif);
                lottieAnimation2.setAnimationFromUrl(it.gif);
                //OrderplaceGif1.setAnimationFromUrl(it.gif)
                // OrderplaceGif12.setAnimationFromUrl(it.gif)

            }
            var url = it.riderBtmIcon
            GlideToVectorYou.init().with(this).load(Uri.parse(url), callStore)
            Glide.with(this)
                .load(it.riderDetails.image)
                .into(store_img_onOrder)


            if (it.orderStatus == "10" || it.orderStatus == "16") {
                changegif.visibility = View.VISIBLE
            }

            if (it.orderStatus.equals("13") || it.orderStatus.equals("1")) {
                cancelBtn1.visibility = View.VISIBLE
            } else {
                cancelBtn1.visibility = View.GONE
            }

            if (it.orderStatus.equals("3")) {
                map12.visibility = View.GONE
            }


            if (it.callToRider) {
                callStore.setOnClickListener {
                    if (!store_phone.equals("")) {
                        onCallPopUp(0)
                        if (sessionInstance.profileDetail != null) {
                            viewmodel?.customerCallMerchantApi(
                                SessionTwiclo(this).loggedInUserDetail.accountId,
                                SessionTwiclo(this).loggedInUserDetail.accessToken,
                                sessionInstance.profileDetail.account.userName,
                                store_phone!!
                            )
                        } else {
                            viewmodel?.customerCallMerchantApi(
                                SessionTwiclo(this).loggedInUserDetail.accountId,
                                SessionTwiclo(this).loggedInUserDetail.accessToken,
                                sessionInstance.loginDetail.phoneNumber,
                                store_phone!!
                            )
                        }
                    }
                }

                callStore.setOnClickListener {

                    onCallPopUp(1)

                    if (sessionInstance.profileDetail != null) {
                        viewmodel?.callCustomerApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            sessionInstance.profileDetail.account.userName,
                            driverMobileNo!!
                        )
                    } else {
                        viewmodel?.callCustomerApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            sessionInstance.loginDetail.phoneNumber,
                            driverMobileNo!!
                        )
                    }
                }
//                callStore.setBackgroundColor(Color.parseColor()


            }



            Log.d("sumitkumar", "onCreate:${it.riderBottomMsg[0]} ")
            Log.d("sumitkumar", "onCreate:${it.riderBottomMsg[1]} ")
            Log.d("sumitkumar", "onCreate:${it.riderBottomMsg[2]} ")



            Log.d("sddffsddskgjgjds", Gson().toJson(firstMsgListData))
            if (product_Update == 0) {
                SetRecyclerview()
            } else {
                firstMsgListData = it.messages
                mainAdapter.notifyDataSetChanged()
            }


//            if(((it.orderStatus.equals("14")) && (it.orderStatus.equals("9")) && (!it.orderStatus.equals("15")))){
//                changegif.visibility = View.GONE
//                eta_lay1.visibility = View.VISIBLE
//
//                }

            if (it.orderStatus.equals("3")) {
                if (reviewpopup == 0) {
                    buyPopup(it.orderId)
                    reviewpopup = 1
                }

            }
            if (it.orderStatus.equals("8")) {
                finish()
                AppUtils.startActivityRightToLeft(
                    this, Intent(this, OrderRejectedActivity::class.java)
                        .putExtra("orderId", intent.getStringExtra("orderId")!!)
                )

            }

//            if(it.orderStatus == "7" || it.orderStatus == "11"){
//                eta_lay1.visibility = View.VISIBLE
//            }


            if (!it.gif.isEmpty()) {
                ordrePlaceGif.setAnimationFromUrl(it.gif);
                lottieAnimation2.setAnimationFromUrl(it.gif);

            }
            Glide.with(this)
                .load(it.riderDetails.image)
                .into(store_img_onOrder)

//             if(it.orderStatus.equals("10") || it.orderStatus.equals("14") ){
//                 eta_lay1.visibility = View.VISIBLE
//             }

            if (useconstants.orderStatusMain.equals("10") || useconstants.orderStatusMain.equals("14")) {
                callStore.isEnabled = true
            }


            Log.d("sumitkumar", "onCreate:${it.riderBottomMsg[0]} ")
            Log.d("sumitkumar", "onCreate:${it.riderBottomMsg[1]} ")
            Log.d("sumitkumar", "onCreate:${it.riderBottomMsg[2]} ")



            Log.d("sddffsddskgjgjds", Gson().toJson(firstMsgListData))
            // SetRecyclerview()



//                val language = arrayOf(
//                    it.riderBottomMsgADR[0], it.riderBottomMsgADR[1],
//                    it.riderBottomMsgADR[2], it.riderBottomMsgADR[3]
//                )
                CoroutineScope(Dispatchers.Main).launch {
                    for (item in it.riderBottomMsgADR) {
                        delay(1000)
                        store_name_txt_info1.text =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(item, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(item)
                            }

                        
                    }





        }
            //lopp kaam nahi kr rha tha is liye ye lga diya
        }










        ///---------------------------------------------------------------------------------

        backBtnNew.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }



        cardMap12.setOnClickListener {

        giflayout.visibility = View.GONE
        giflayout1.visibility = View.VISIBLE
        lottieAnimation2.visibility = View.VISIBLE
        changegif3.visibility = View.VISIBLE

    }

    cardMap123.setOnClickListener {
        giflayout.visibility = View.VISIBLE
        giflayout1.visibility = View.GONE
        lottieAnimation2.visibility = View.GONE
        changegif.visibility = View.VISIBLE
    }




//        allitemlistlayout.animate()
//            .translationY(0F)
//            .alpha(0.0F)
//            .setListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    super.onAnimationEnd(animation)
//                    allitemlistlayout.visibility = View.GONE
//                }
//            })

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        trackOrderContext = this
        trackOrderActivity =this
        //Log.e("Timer Status", timerStatus.toString())
        deleteAllPrecription()

        if (intent.getStringExtra("orderId") != "") {
            bgServicOrderId = intent.getStringExtra("orderId")!!
            tv_order_id.text = intent.getStringExtra("orderId")!!
            startService(Intent(applicationContext, OrderBackgroundgService::class.java))
        }

        viewmodelusertrack?.customerActivityLog(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).mobileno, "OrderTrack Screen",
            SplashActivity.appversion, "", SessionTwiclo(this).deviceToken
        )



      displayLocationSettingsRequest(this)

        checkPermission()

       getLocation()



        orderDetailsTxt1.setOnClickListener {
            onpopup()
        }

//        customer_care_fmL.visibility = View.INVISIBLE /// INVISIBLE k liye
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(30000)
//            customer_care_fmL.visibility = View.VISIBLE
//        }

        customer_care_fmL.setOnClickListener {
//            val dialIntent = Intent(Intent.ACTION_DIAL)
//            dialIntent.data = Uri.parse("tel:" + 9871322057)
//            startActivity(dialIntent)
            // Log.d("hello", "hello")
            AppUtils.startActivityRightToLeft(this,Intent(this, Chatbotui::class.java)
                .putExtra("orderId", intent.getStringExtra("orderId")!!))
        }




        viewmodel?.customerCallMerchantRes?.observe(this) {
            Log.d("customerCallMerchantRes", Gson().toJson(it))
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map1) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        observeGetLocationResponse()
        observeOrderDetailResponse()
        observeOrderFeedback()



//        tv_delivery_boy_call.setOnClickListener {
//
//            onCallPopUp(1)
//
//            if (sessionInstance.profileDetail != null) {
//                viewmodel?.callCustomerApi(
//                    SessionTwiclo(this).loggedInUserDetail.accountId,
//                    SessionTwiclo(this).loggedInUserDetail.accessToken,
//                    sessionInstance.profileDetail.account.userName,
//                    driverMobileNo!!
//                )
//            } else {
//                viewmodel?.callCustomerApi(
//                    SessionTwiclo(this).loggedInUserDetail.accountId,
//                    SessionTwiclo(this).loggedInUserDetail.accessToken,
//                    sessionInstance.loginDetail.phoneNumber,
//                    driverMobileNo!!
//                )
//            }
//        }

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

        timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                if (hit == (millisUntilFinished / 1000)) {
//                    newOrderViewModel?.NewTrackScreenApiCall(
//                        SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accountId,
//                        SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accessToken,
//                        intent.getStringExtra("orderId")!!
//                    )

                }
            }

            override fun onFinish() {
                Log.e("_Timersfadfdfdsf", timer.toString())
                hit = 0
                newOrderViewModel?.NewTrackScreenApiCall(
                    SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accountId,
                    SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accessToken,
                    intent.getStringExtra("orderId")!!
                )
                viewmodel?.getLocationApi(
                    SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accountId,
                    SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accessToken,
                    intent.getStringExtra("orderId")!!, "user"
                )
                timer!!.start()
            }


        }.start()

        orderViewModel?.getOrderDetails(
            SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accountId,
            SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")
        )
        viewmodel?.getLocationApi(
            SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accountId,
            SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!,
            "user"
        )

//        orderTrackBack_fmL.setOnClickListener {
//            if (intent.hasExtra("type")) {
//                startActivity(Intent(this, MainActivity::class.java))
//                finishAffinity()
//            } else {
//                finish()
//                AppUtils.finishActivityLeftToRight(this)
//            }
//        }

//        orderDetailsTxt.setOnClickListener {
//            startActivity(
//                Intent(this, OrderDetailsActivity::class.java).putExtra(
//                    "orderId",
//                    intent.getStringExtra("orderId")!!
//                )
//            )
//        }

        cancelBtn1!!.setOnClickListener {
            showIOSProgress()
            //timerr?.cancel()
            stopService(Intent(applicationContext, OrderBackgroundgService::class.java))
            viewmodel!!.cancelOrderApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken,
                intent.getStringExtra("orderId")!!
            )
        }

        viewmodel?.getLocationApi(
            SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accountId,
            SessionTwiclo(this@NewTrackOrderActivity).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!,
            "user"
        )

        viewmodel?.cancelOrderResponse?.observe(this) { user ->
            dismissIOSProgress()
            orderCancelPopUp()
            Log.e("cancelOrderResponse", Gson().toJson(user))
        }

        viewmodel?.proceedToOrderResponse?.observe(this) {
            //timerr?.cancel()
            showToast("Your order has been placed")
           // waitingLay.visibility = View.GONE
        }

        viewmodel?.callCustomerResponse?.observe(this) {
            Log.d("callCustomerResponse", Gson().toJson(it))
        }
    }
//for order details pop up
    private fun onpopup() {
        order_Dialog = Dialog(this)
        order_Dialog?.dismiss()


        order_Dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        order_Dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        order_Dialog?.setContentView(R.layout.accept_reject_order_popup)
        order_Dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,

        )
        order_Dialog?.getWindow()?.setGravity(Gravity.BOTTOM);
        order_Dialog?.window?.attributes?.windowAnimations = R.style.DialogIntertnet
        order_Dialog?.setCanceledOnTouchOutside(true)
        order_Dialog?.show()

        var itemsRecyclerView1 = order_Dialog?.findViewById<RecyclerView>(R.id.itemsRecyclerView1)
        var itemQuantityPrice1 = order_Dialog?.findViewById<TextView>(R.id.itemQuantityPrice)
        Log.d("ghdsfugfdsf", "$items")
        val adapter = ItemsAdapterTrackScreen(this,items as ArrayList)
        itemsRecyclerView1?.adapter = adapter
        itemQuantityPrice1?.text = finalprice
    }




    fun observeGetLocationResponse() {
        viewmodel?.getLocationResponse?.observe(this) { user ->
            try {
                Log.e("getLocationResponse___", Gson().toJson(user))
              //  Log.e("getLocationResponse___", timerStatus.toString())
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
                    rider_LatLng =
                        LatLng(user.driverLatitude.toDouble(), user.driverLongitude.toDouble())
                    rider_LatLngStr = user.driverLatitude + "," + user.driverLongitude
                    rider_LatLngOrg =
                        LatLng(user.driverLatitude.toDouble(), user.driverLongitude.toDouble())

                    Log.e("rider_LatLngStr", rider_LatLngStr.toString())
                }
            } catch (e: Exception) {
                Log.e("rider_LatLngStr", e.toString())
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
                Log.d("check_gMap1sfd", check_gMap1.toString())
                if (check_gMap1 == 0) {
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(merchantLatLng, 13f))
                }

                if (merchantLatLng != null) {
                    mMap?.addMarker(
                        MarkerOptions()
                            .position(merchantLatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.shop))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(false)
                    )
                }

                if (user_LatLng != null) {
                    mMap?.addMarker(
                        MarkerOptions()
                            .position(user_LatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.home_locpin))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(false)
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

                if (rider_LatLng != null) {
                    var rotation=0.0f

                    if (previousLatLng == null) {
                        currentLatLng = rider_LatLng
                        previousLatLng = currentLatLng
                    } else {
                        previousLatLng = currentLatLng
                        currentLatLng = rider_LatLng
                    }

                    if (check == 0) {
                        check = 1
                    } else {
                        check = 0
                    }

                    val nextLocation = LatLng(
                        currentLatLng!!.latitude * previousLatLng!!.latitude,
                        currentLatLng!!.longitude * previousLatLng!!.longitude
                    )

                    try {
                        if (latLngList.isNullOrEmpty()){
                            var rider_LatLngNext=
                                LatLng(latLngList[1].latitude, latLngList[1].longitude)
                            rotation = MapUtils.getRotation(rider_LatLng!!, rider_LatLngNext).toFloat()

                        }else {
                            rotation = MapUtils.getRotation(rider_LatLng!!, nextLocation!!).toFloat()
                        }


                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                    var marker: Marker? = mMap?.addMarker(
                        MarkerOptions()
                            .position(rider_LatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.rider))
                            .rotation(rotation)
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(
                                false
                            )

                    )

                    mMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            rider_LatLng,
                            14f
                        ), 3000, null
                    )

                    val angle = MapUtils.bearingBetweenLocations(previousLatLng!!, nextLocation!!)
                    if (marker != null) {
                        MapUtils.rotateMarker(marker, angle)
                    }
                }
                drawRoute(rider_LatLngStr!!, merchantLatLngStr!!, "")

                if (merchantLatLng != null) {
                    mMap?.addMarker(
                        MarkerOptions()
                            .position(merchantLatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.shop))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(false)
                    )
                }


            } else if (order_status_for_track.equals("order_picked")) {
                if (check_gMap3 == 0) {
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(user_LatLng, 13f))
                    check_gMap3 = 1
                }

                if (rider_LatLng != null) {
                    var rotation=0.0f

                    if (previousLatLng == null) {
                        currentLatLng = rider_LatLng
                        previousLatLng = currentLatLng
                    } else {
                        previousLatLng = currentLatLng
                        currentLatLng = rider_LatLng
                    }

                    val nextLocation = LatLng(
                        currentLatLng!!.latitude * previousLatLng!!.latitude,
                        currentLatLng!!.longitude * previousLatLng!!.longitude
                    )

                    try {
                        if (latLngList.isNullOrEmpty()){
                            var rider_LatLngNext=
                                LatLng(latLngList[1].latitude, latLngList[1].longitude)
                            rotation = MapUtils.getRotation(rider_LatLng!!, rider_LatLngNext).toFloat()

                        }else {
                            rotation = MapUtils.getRotation(rider_LatLng!!, nextLocation!!).toFloat()
                        }


                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                    var marker: Marker? = mMap?.addMarker(
                        MarkerOptions()
                            .position(rider_LatLng!!)
                            .rotation(rotation)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.rider))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(
                                false
                            )

                    )

                    mMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            rider_LatLng,
                            14f
                        ), 3000, null
                    )

                    val angle = MapUtils.bearingBetweenLocations(previousLatLng!!, nextLocation!!)
                   if (marker != null) {
                        MapUtils.rotateMarker(marker, angle)
                   }
                }

                if (user_LatLng != null) {
                    mMap?.addMarker(
                        MarkerOptions()
                            .position(user_LatLng!!)
                            .icon(bitmapDescriptorFromVector_(this, R.drawable.home_locpin))
                            .anchor(0.5f, .5f)
                            .zIndex(20.0f)
                            .draggable(true).flat(false)
                    )
                }
                drawRoute(rider_LatLngStr!!, user_LatLngStr!!, "")
            }
        }
    }

    fun observeOrderDetailResponse() {
        orderViewModel?.OrderDetailsResponse?.observe(this) {
            Log.e("OrderDetailsResponse_", Gson().toJson(it))

            //for itme price
            val mModelData: OrderDetailsModel = it
            items = mModelData.items

            finalprice = it.totalPrice
            //---------------------------------------
            Log.d("dgfyugef", "$mModelData.items")


            if (it.orderStatus == "13") {
                    //waitingLay.visibility = View.VISIBLE
                    Log.e("hit___", "hit--" + OrderBackgroundgService.timer_count!!)
                    startService(Intent(applicationContext, OrderBackgroundgService::class.java))
                    // ordstatus_lay_new.visibility = View.VISIBLE
                    //order_status.text = "Please wait while we confirm your order"

                    	if (handleCounter==0) {
                    timerr = object : CountDownTimer(OrderBackgroundgService.timer_count!!, 1000) {

                        override fun onTick(millisUntilFinished: Long) {
                           // cancelBtn1.visibility = View.VISIBLE
                            customer_care_fmL.visibility = View.INVISIBLE
//                            cancelBtn1.text =
//                                "Cancel Order (" + (millisUntilFinished / 1000) + ")"

                            if ((millisUntilFinished / 1000) < 1) {
                                //waitingLay.visibility = View.GONE
                               // cancelBtn1.visibility = View.GONE
                                customer_care_fmL.visibility = View.VISIBLE
                                // ordstatus_lay_new.visibility = View.VISIBLE
                               // order_status.text =
                                    "Please wait while we confirm your order"
                            }
                        }

                        override fun onFinish() {
                            if (handleClick == 0) {


                                timerr!!.cancel()
                                stopService(
                                    Intent(
                                        applicationContext,
                                        OrderBackgroundgService::class.java
                                    )
                                )
                                handleClick = 1

                                	OrderBackgroundgService.timer_count=30000

                            }
                        }

                    }.start()
                    handleCounter = 1
                    	}

					if (currentOrderId!!.isNotEmpty()) {
						if (CartActivity.proceedClick == 0) {
							CartActivity.proceedClick = 1
							viewmodel?.proceedToOrder(
								SessionTwiclo(trackOrderContext).loggedInUserDetail.accountId,
								SessionTwiclo(trackOrderContext).loggedInUserDetail.accessToken,
								currentOrderId.toString()
							)
						}
					}


            }

            try {
                if (it.request_id.equals("0") || it.request_id.equals("")) {
                } else {

                    AppUtils.startActivityRightToLeft(
                        this,
                        Intent(this, ReviewItemsActivity::class.java)
                            .putExtra("orderId", intent.getStringExtra("orderId"))
                            .putExtra("request_id", it.request_id)
                            .putExtra("payment_mode", it.paymentMode)
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {

                //tv_order_id.text = it.orderId
                // total_item_text.text = it.item_count + " Items, ₹ " + it.totalPrice.toString()
                // store_name_txt_info.text = it.storeName

                //status_store_txt
                store_phone = it.store_phone
                rider_phone = it.delivery_boy_phone

//                Glide.with(this)
//                    .load(it.storeImage).thumbnail(0.05f)
//                    .fitCenter()
//                    .into(store_img_onOrder)

                if (it.categoryId.equals("5") || it.categoryId.equals("7")) {
                    storeDetailsViewContsl.visibility = View.VISIBLE
                } else {
                    storeDetailsViewContsl.visibility = View.GONE
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

//			try {
//				if (it.allow_cancel.equals("0")) {
//					cancelBtn.visibility = View.GONE
//					Log.e("allow_cancel", "0")
//				} else {
//					cancelBtn.visibility = View.VISIBLE
//				}
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}

            try {
                if (it.orderStatus != null) {
                    when {

                        it.orderStatus.equals("0") -> {
                            //order_status.text = "Failed"
                            cancelBtn1.visibility = View.GONE
                        }

                        it.orderStatus.equals("1") -> {
                            // holder.buttonValue.visibility = View.VISIBLE
                            // holder.buttonValue.visibility = View.GONE

                            // order_status.text = "Please wait while we confirm your order"
                          //  order_status.text = "Waiting for merchant to accept the order"
                            //tv_delivery_boy_call.visibility = View.GONE
                            //driver_cardView.visibility = View.GONE
                           // cancelBtn1.visibility = View.GONE
                            // ordstatus_lay_new.visibility = View.VISIBLE
                        }

                        it.orderStatus.equals("2") -> {
                            //holder.buttonValue.visibility = View.GONE
                           // order_status.text = "Your order is cancelled"
                           // cancelBtn1.visibility = View.GONE

                            if (onBackpressHandle.equals("1")) {
                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                finish()
                            }

                        }

                        it.orderStatus.equals("3") -> {
                            /*if (it.is_rate_to_driver.equals("1")) {
                            //holder.buttonValue.visibility = View.GONE
                             } else {
                            holder.buttonValue.text = "Review"
                             }*/


                            map1.alpha = 0.0f

                            if (reviewpopup == 0) {
                                buyPopup(it.orderId)
                                reviewpopup = 1
                            }
                            timer!!.cancel()
                            cancelBtn1.visibility = View.GONE

                        }

                        it.orderStatus.equals("11") -> {

                            cancelBtn1.visibility = View.GONE
                            order_status_for_track = "rider_assign"
                            //tv_delivery_boy_call.visibility = View.VISIBLE
                           // driver_cardView.visibility = View.VISIBLE
                            //ordstatus_lay_new.visibility = View.VISIBLE

                        }

                        it.orderStatus.equals("5") -> {
                            //  holder.buttonValue.visibility = View.VISIBLE
                            //holder.buttonValue.visibility = View.GONE

                           // order_status.text = "Order is in progress"
                            cancelBtn1.visibility = View.GONE
                            //tv_delivery_boy_call.visibility = View.VISIBLE
                           // driver_cardView.visibility = View.VISIBLE

                        }

                        it.orderStatus.equals("6") -> {
                            // holder.buttonValue.visibility = View.VISIBLE
                            //holder.buttonValue.visibility = View.GONE
                            //tv_delivery_boy.text =
                                it.deliveryBoyName + " is on the way to deliver your order."
                           // order_status.text =
                                it.deliveryBoyName + " is on the way to deliver your order."

                           // cancelBtn1.visibility = View.GONE
                            //ordstatus_lay_new.visibility = View.VISIBLE

                        }

                        it.orderStatus.equals("7") -> {
                            //holder.buttonValue.visibility = View.VISIBLE


                            //driver_cardView.visibility = View.GONE
                            // ordstatus_lay_new.visibility = View.VISIBLE

                        }

//                        it.orderStatus.equals("9") -> {
//                            //holder.buttonValue.visibility = View.VISIBLE
//                            Log.d("deliveryBoyName___", it.deliveryBoyName)
//                            dynamicText(
//                                tv_delivery_boy,
//                                "Your order is ready and will soon be picked up by ",
//                                it.deliveryBoyName
//                            )
//                            tv_delivery_boy.text =  "Your Order is ready and will soon be picked up by " + it.deliveryBoyName
//
//                            // ordstatus_lay_new.visibility = View.VISIBLE
//
//                        }

                        it.orderStatus.equals("10") -> {
                            //holder.buttonValue.visibility = View.VISIBLE
                            //tv_delivery_boy.text =
                                it.deliveryBoyName + " is on the way to deliver your order."
                            //order_status.text =
                                it.deliveryBoyName + " is on the way to deliver your order."
                            //	order_status.text = "Your order is out for delivery"


                            order_status_for_track = "order_picked"
                            storeCardID.visibility = View.GONE
                           cancelBtn1.visibility = View.GONE
                            // ordstatus_lay_new.visibility = View.VISIBLE

                        }

                        it.orderStatus.equals("8") -> {
                            //holder.buttonValue.visibility = View.GONE
                            handleClick = 1

                            if (it.paymentMode == "online") {
                               // order_status.text =
                                    "Your order is rejected. Don't worry, we have processed your refund and same will be credited to your account within 3-5 business days"
                            } else {
                               // order_status.text = "Your order is rejected."
                            }

                            address_details_lay.visibility = View.GONE

                           // driver_cardView.visibility = View.GONE
                           // tv_delivery_boy_call.visibility = View.GONE
                            tv_order_id.visibility = View.GONE
                            tv_order_id_label.visibility = View.GONE
                            map1.alpha = 0.0f
                            customer_care_fmL.visibility = View.GONE
                           // cancelBtn1.visibility = View.GONE

                            if (onBackpressHandle.equals("1")) {
                                startActivity(Intent(this, OrderRejectedActivity::class.java))
                                finish()
                            } else {
                                finish()
                            }
                        }

                        it.orderStatus.equals("14") -> {
//                            order_status_for_track = "rider_assign"
//                            // ordstatus_lay_new.visibility = View.VISIBLE
                            cancelBtn1.visibility = View.GONE
//
//                            //tv_delivery_boy.text =
//                                it.deliveryBoyName + " is on the way to pick your order"
//                            order_status.text =
//                                it.deliveryBoyName + " is on the way to pick your order from " + it.storeName

                        }

                        it.orderStatus.equals("15") -> {
                            order_status_for_track = "rider_assign"
                            //	order_status.text = "It's ready. Just packing it."

                            //ordstatus_lay_new.visibility = View.VISIBLE

                        }

                        it.orderStatus.equals("16") -> {
                            order_status_for_track = "order_picked"
                            storeCardID.visibility = View.GONE
                           // tv_delivery_boy.text =
                                it.deliveryBoyName + " has reached to your location"
                            //order_status.text = it.deliveryBoyName + " has reached to your location"
                           // driver_cardView.visibility = View.VISIBLE
                           // tv_delivery_boy_call.visibility = View.VISIBLE

                            //cancelBtn1.visibility = View.GONE
                            // ordstatus_lay_new.visibility = View.VISIBLE

                        }

                    }
                }
            } catch (e: Exception) {
            }

        }
        }


    fun observeOrderFeedback() {
        viewmodel?.orderFeedback?.observe(this) { feedback ->
            dismissIOSProgress()
            if (checkStatusOfReview == 1) {
                CommonUtils.dismissIOSProgress()
                val model: Feedback = feedback
                OrdersFragment.handleApiResponse = 1
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                Toast.makeText(this, model.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bitmapDescriptorFromVector_(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, 55, 80)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
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

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            mMap = p0
            mMap!!.getUiSettings().setCompassEnabled(false);

            try {
                val success: Boolean = mMap!!.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.mapstyle
                    )
                )
                if (!success) {
                    Log.e("TAG_", "Style parsing failed.")
                }

            } catch (e: Resources.NotFoundException) {
                Log.e("TAG_", "Can't find style. Error: ", e)
            }

            try {
                if (order_status_for_track.equals("rider_assign")) {
                    if (merchantLatLng != null) {
                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(merchantLatLng, 13f))
                    }
                } else if (order_status_for_track.equals("order_picked")) {
                    if (check_gMap3 == 0) {
                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(user_LatLng, 13f))
                        check_gMap3 = 1
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCurveDrawn(curve: Curve?) {}

    override fun onCurveClick(curve: Curve?) {}

    fun drawRoute(rider_latLong: String, destination: String, type: String) {

        val path: MutableList<List<LatLng>> = ArrayList()

        val urlDirections =
            "https://maps.googleapis.com/maps/api/directions/json?origin=$rider_latLong&destination=$destination&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"

        Log.e("urlDirections", "routes- $urlDirections---$type")

        val directionsRequest = object :
            StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)

                latLngList.clear()
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
                            steps.getJSONObject(i).getJSONObject("start_location").getString("lat")
                        var markerRiderIng =
                            steps.getJSONObject(i).getJSONObject("start_location").getString("lng")
                        options.addAll(path[i]).color(Color.BLACK).zIndex(5.0f)
                        latLngList.add(LatLng(markerRiderLat.toDouble(), markerRiderIng.toDouble()))
                    }
                    this?.mMap!!.addPolyline(options)

                    updateCarLocationNew(path)

                    	//showPath(latLngList)

                }
            }, Response.ErrorListener {
            }) {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
    }

    private fun createDashedLine(latLngOrig: LatLng, latLngDest: LatLng, color: Int) {
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
///-------------------------------------------------------

        if((useconstants.orderStatusMain.equals("10")|| useconstants.orderStatusMain.equals("14"))){
            Glide.with(this)
                .load(R.drawable.callactivite)
                .into(callStore)
        }

        // Log.d("sddffsddsds", Gson().toJson(it)


        Log.e("upcoming resume", "yes")
        tv_order_id.text = intent.getStringExtra("orderId")!!

        viewmodel?.getLocationApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!, "user"
        )
        timer!!.start()

    }


        private fun SetRecyclerview() {
            ordrePlaceGif.playAnimation()
            ordrePlaceGif.loop(true)
            ordrePlaceGif.repeatCount = LottieDrawable.INFINITE;
            mainAdapter = NewOrderTrackAdapter(this,firstMsgListData)
            val linearLayoutManager= LinearLayoutManager(this)
            recyclerViewNewTrack.layoutManager=linearLayoutManager
            recyclerViewNewTrack.adapter= mainAdapter



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
                customBuilder.setNegativeButton("OK") { _, _ ->
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
    }

    private fun buyPopup(orderId: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.review_popup, null)
        //AlertDialogBuilder
        val mBuilder = android.app.AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog
        val mAlertDialogg = mBuilder.show()
        mAlertDialogg?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(mAlertDialogg.window!!.attributes)
        lp.gravity = Gravity.CENTER

        mAlertDialogg!!.window!!.attributes = lp
        mAlertDialogg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialogg.window!!.setGravity(Gravity.CENTER)
        mAlertDialogg.setCanceledOnTouchOutside(false)
        mDialogView.poor_icon_select.setBackgroundResource(R.drawable.rectangle_border)

        mDialogView.cancel_reviewPopUp.setOnClickListener {
            mAlertDialogg.dismiss()
            finish()
        }

        star = "4"
        mDialogView.img_review_img.setBackgroundResource(R.drawable.good)
        mDialogView.reviewName_txt.text = "Good"
        mDialogView.selection_ques_txt.setText(R.string.good_experince)
        mDialogView.poor_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
        mDialogView.ok_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
        mDialogView.good_icon_select.setBackgroundResource(R.drawable.rectangle_border)
        mDialogView.excellent_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)
        mDialogView.bad_icon_select.setBackgroundResource(R.drawable.white_rounded_empty)

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
            mDialogView.reviewName_txt.text = "loved it!"
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun calculateEstimatedTime(source: String, destination: String) {
        var urlEstimatedTime = ""
        if (order_status_for_track.equals("order_picked")) {
            urlEstimatedTime =
                "https://maps.googleapis.com/maps/api/directions/json?origin=$source&destination=$destination&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
        } else {
            urlEstimatedTime =
                "https://maps.googleapis.com/maps/api/directions/json?origin=" + source + "&destination=" + destination + "&waypoints=via:" + merchantLatLngStr + "&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
        }

        Log.e("urlEstimatedTime", urlEstimatedTime)
        Log.e("rider_LatLngStr", urlEstimatedTime)
        val directionsRequest = object :
            StringRequest(Method.GET, urlEstimatedTime, Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                Log.e("res", "routes- $jsonResponse")
                val routes = jsonResponse.getJSONArray("routes")
                if (routes.length() != 0) {
                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val estimatedTime =
                        legs.getJSONObject(0).getJSONObject("duration").get("text").toString()
                    if (estimatedTime.isNotEmpty() || estimatedTime != "") {
                        if (useconstants.orderStatusMain.equals("11") || useconstants.orderStatusMain.equals(
                                "15"
                            ) || useconstants.orderStatusMain.equals("10") || useconstants.orderStatusMain.equals("7") || useconstants.orderStatusMain.equals("16") || useconstants.orderStatusMain.equals("16")
                        ) {
                            eta_lay11.visibility = View.VISIBLE
                         //   Log.d("ssssss", )
                            Log.e("estimated_time___", estimatedTime)
                            estimated_time1.text = estimatedTime
                        }
                    }else {

                    }
                }
            }, Response.ErrorListener { }) {

        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(directionsRequest)
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
            finish()
        }, 5000)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_OUTSIDE) {
            println("TOuch outside the dialog ******************** ")
            call_Diolog?.setCancelable(true)
        }
        return false
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

        if (type == 0) {
            callTypeTxt!!.setText("Just a minute, connecting with the merchant in a bit.")
        } else {
            callTypeTxt!!.setText("Just a minute, connecting with the rider in a bit.")
        }

        cancelDialogConstL?.setOnClickListener {
            call_Diolog?.dismiss()
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
                Log.d("fdsdds","$rider_LatLng--$rider_LatLng2")
            }

            //movingBikeMarker = addCarMarkerAndGet(rider_LatLngOrg!!)
            movingBikeMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(rider_LatLngOrg!!)

            val valueAnimator = AnimationUtils.cabAnimator()
            valueAnimator.addUpdateListener {
                try {
                    var v =valueAnimator.animatedFraction
                    val Lng=v*rider_LatLng!!.longitude+(1-v)*rider_LatLng2!!.longitude
                    var lat=v*rider_LatLng!!.latitude+(1-v)*rider_LatLng2!!.latitude
                    var newPos=LatLng(lat,Lng)

                    movingBikeMarker!!.position=rider_LatLngOrg

                    val rotation = MapUtils.getRotation(rider_LatLng!!, newPos)
                    Log.d("rotation_",rotation.toString())
                    if (!rotation.isNaN()) {
                        movingBikeMarker?.rotation = rotation
                    }
                }catch (e:Exception){e.printStackTrace()}

            }
            valueAnimator.start()

        }, 600)
    }

    // slide the view from below itself to the current position



//    private fun addCarMarkerAndGet(latLng: LatLng): Marker {
////        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(this))
//       // return mMap!!.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
//    }

    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(14.5f).build()
        mMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }

    private fun bottomSheetCallBack(){
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Do something for new state.
                //allitemlistlayout
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> stateCollapseddbm()
                    BottomSheetBehavior.STATE_EXPANDED -> stateExpanddbm()




                    //BottomSheetBehavior.STATE_COLLAPSED->



                  //  BottomSheetBehavior.STATE_DRAGGING -> Toast.makeText(
//                        this,
//                        "STATE_DRAGGING",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(
//                        this,
//                        "STATE_SETTLING",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(
//                        this,
//                        "STATE_HIDDEN",
//                        Toast.LENGTH_SHORT
//                    ).show()
                   // else ->

            }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset.

            }

        }
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }



   private fun stateCollapseddbm(){
       var  fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_out);

       allitemlistlayout.animation= fadeInAnimation
       useconstants.pos = 0
       allitemlistlayout?.visibility = View.GONE
       useconstants.isScroll = false
       SetRecyclerview()

    }
    fun stateExpanddbm(){


        allitemlistlayout?.visibility = View.VISIBLE

        var  fadeInAnimation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fadein);
        allitemlistlayout.animation= fadeInAnimation

        useconstants.isScroll=true
        SetRecyclerview()
    }

    override fun onStart() {

        super.onStart()
        useconstants.pos=0
        useconstants.isScroll = false
    }
    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }



}