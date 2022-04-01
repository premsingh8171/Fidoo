package com.fidoo.user.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
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
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.addressmodule.viewmodel.AddressViewModel
import com.fidoo.user.dashboard.viewmodel.HomeFragmentViewModel
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.fragments.SendPacketFragment
import com.fidoo.user.ordermodule.ui.TrackOrderActivity
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.maps.model.GeocoderModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.robin.locationgetter.EasyLocation
import com.sanojpunchihewa.updatemanager.UpdateManager
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home_newui.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), android.location.LocationListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    var viewmodel: AddressViewModel? = null
    var homeFragmentViewModel: HomeFragmentViewModel? = null
    private var timer: CountDownTimer? = null
    var orderId: String? = ""
    private var mMixpanel: MixpanelAPI? = null


    companion object {
        var addEditAdd: String? = ""
        var service_idStr: String? = ""
        var tempProductList: ArrayList<TempProductListModel>? = null
        var addCartTempList: ArrayList<AddCartInputModel>? = null
        var check: String = ""
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        var isearchSuggestionsList: ArrayList<String>? = null
        var timerStatus = true

        var ll: Location? = null
        var gac: GoogleApiClient? = null
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private lateinit var locationRequest: LocationRequest
        private lateinit var locationCallback: LocationCallback
        val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private var locationManager: LocationManager? = null
        val MY_PERMISSIONS_REQUEST_CODE = 123

        // Declare the UpdateManager
        var mUpdateManager: UpdateManager? = null
        var onBackpressHandle: String? = "0"
        var orderProcess: Int? = 0
        var orderSuccess: Int? = 0
        var handleTrackScreenOrderSuccess: Int = 1 //when order status 3

        var checkAddressSavedFromWhichActivity : String = ""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        FirebaseApp.initializeApp(this)
        //searchSuggestionsList = ArrayList<String>()

        viewmodel = ViewModelProvider(this).get(AddressViewModel::class.java)
        homeFragmentViewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        mUpdateManager = UpdateManager.Builder(this).mode(UpdateManagerConstant.IMMEDIATE)
        mUpdateManager?.start()
        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        // window.statusBarColor = ContextCompat.getColor(this, R.color.colorTransparent)

        val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                //This piece of code will be executed when you click on your item
                // Call your fragment...
                val sendPacketFragment: FragmentManager = supportFragmentManager
                sendPacketFragment.beginTransaction().add(R.id.sendPacketFragment, SendPacketFragment())

            }
        }

        this.registerReceiver(mBroadcastReceiver, IntentFilter("start_send_package_fragment"))
        val navController = findNavController(R.id.fragment4)
        bottomNavigationView.setupWithNavController(navController)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tempProductList = ArrayList()
        addCartTempList = ArrayList()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

       // getAddress()
//        if (isNetworkConnected) {
//            if (SessionTwiclo(this).isLoggedIn){
//                try {
//                    viewmodel?.getAddressesApi(
//                        SessionTwiclo(this).loggedInUserDetail.accountId,
//                        SessionTwiclo(this).loggedInUserDetail.accessToken,
//                        "",
//                        ""
//                    )
//                }catch (e:Exception){
//                    e.printStackTrace()
//                }
//            }else{
//                getCurrentLocation()
//            }
//
//        } else {
//            showInternetToast()
//        }

        try {
            if (SessionTwiclo(this).orderId.isNotEmpty()){
                var img=SessionTwiclo(this).storeImg
                Log.e("imgimg_",img)
                 orderId=SessionTwiclo(this).orderId
                 loadImage(img)
                 orderStatus_fm.visibility=View.VISIBLE
                 viewmodel?.getOrderStatusApi( SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,orderId!!)

                timer = object : CountDownTimer(20000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {}

                    override fun onFinish() {
                        if (SessionTwiclo(this@MainActivity).orderId.isNotEmpty()) {
                            viewmodel?.getOrderStatusApi(
                                SessionTwiclo(this@MainActivity).loggedInUserDetail.accountId,
                                SessionTwiclo(this@MainActivity).loggedInUserDetail.accessToken,
                                orderId!!
                            )
                            timer?.start()
                        }
                    }
                }.start()

            }else{
                orderStatus_fm.visibility=View.GONE
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

        SessionTwiclo(this).setetvalue("")
        SessionTwiclo(this).setcatname("")

        orderStatus_fm.setOnClickListener {
           startActivity(
                Intent(this, TrackOrderActivity::class.java)
                    .putExtra("orderId", orderId)
                    .putExtra("delivery_boy_name", "")
                    .putExtra(
                        "delivery_boy_mobile",
                        ""
                    )
                    .putExtra("order_status", "")
           )
        }

//        try {
//            if (sessionInstance!!.isLoggedIn) {
//                homeFragmentViewModel?.checkPaymentStatusApi(
//                    sessionInstance.loggedInUserDetail.accountId,
//                    sessionInstance.loggedInUserDetail.accessToken
//                )
//            }
//        }catch (e:java.lang.Exception){
//            e.printStackTrace()
//        }
//
//
//        homeFragmentViewModel?.checkPaymentStatusRes?.observe(this, {
////            Log.d("checkPaymentStatusRes", Gson().toJson(it))
////            if (it.error_code == 200) {
////
////            }
//        })

        viewmodel?.orderStatusModelResponse?.observe(this, {
            Log.e("StatusMod_", Gson().toJson(it))
            try {
                if (it.order_status != null) {
                    when {
                        it.order_status.equals("0") -> {
                            showOrderStatusTxt.text = "Failed"
                        }

                        it.order_status.equals("1") -> {
                            showOrderStatusTxt.text = "Please wait while we confirm your order"
                        }

                        it.order_status.equals("2") -> {
                            showOrderStatusTxt.text = "Your order is cancelled"
                            SessionTwiclo(this).orderId=""
                            SessionTwiclo(this).storeImg=""
                            orderStatus_fm.visibility=View.GONE
                        }

                        it.order_status.equals("11") -> {
                            showOrderStatusTxt.text = "Your order is being prepared"
                        }

                        it.order_status.equals("3") -> {
                            showOrderStatusTxt.text = "Your order is delivered"
                            SessionTwiclo(this).orderId=""
                            SessionTwiclo(this).storeImg=""
                            orderStatus_fm.visibility=View.GONE
                        }

                        it.order_status.equals("5") -> {
                            showOrderStatusTxt.text = "Order is in progress"
                        }

                        it.order_status.equals("6") -> {
                            showOrderStatusTxt.text = "Your order is out for delivery"
                        }

                        it.order_status.equals("7") -> {
                            showOrderStatusTxt.text = SessionTwiclo(this).storeName + " has accepted your order"
                        }

                        it.order_status.equals("9") -> {
                            showOrderStatusTxt.text =
                                "Your Order is ready and will soon be picked up by driver"
                        //+ it.deliveryBoyName
                        }

                        it.order_status.equals("10") -> {
                            showOrderStatusTxt.text = "Your order is out for delivery"
                        }

                        it.order_status.equals("8") -> {
                            showOrderStatusTxt.text = "Your order is rejected."
                            SessionTwiclo(this).orderId = ""
                            SessionTwiclo(this).storeImg = ""
                            orderStatus_fm.visibility = View.GONE
                        }

                        it.order_status.equals("13") -> {
                            showOrderStatusTxt.text = "Please wait while we confirm your order"
                        }

                        it.order_status.equals("14") -> {
                            showOrderStatusTxt.text = "Delivery boy on the way to pickup your order"
                        }

                        it.order_status.equals("15") -> {
                            showOrderStatusTxt.text = "Delivery boy on the way to pickup your order"
                        }

                    }
                }
            } catch (e: Exception) { }
        })

        viewmodel?.getAddressesResponse?.observe(this,{user ->
            Log.e("homeaddRes", Gson().toJson(user))

            if (user.addressList != null) {
                val addressList: MutableList<GetAddressModel.AddressList> = user.addressList
                if (addressList != null) {
                    try {
                        for (i in addressList.indices) {
                            if (i == 0) {
                                if (SessionTwiclo(this).userAddress!=""){
                                    Log.d("default_add___","="+SessionTwiclo(this).userAddress)
                                }else {
                                    SessionTwiclo(this).userAddress = addressList.get(0).location
                                    SessionTwiclo(this).userLat = addressList.get(0).latitude
                                    SessionTwiclo(this).userLng = addressList.get(0).longitude
                                    userAddress?.text = SessionTwiclo(this).userAddress
                                    userAddress_newDesh?.text = SessionTwiclo(this).userAddress
                                    SessionTwiclo(this).userAddressId = addressList.get(0).id
                                }
                            }
                        }

                        if (addressList.size == 0) {
                            Log.d("addressList__size", "message")
                            getCurrentLocation()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }else{
                getCurrentLocation()
            }

        })

    }

    private fun getAddress() {
        if (isNetworkConnected) {
            if (SessionTwiclo(this).isLoggedIn){
                try {
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        "",
                        ""
                    )
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }else{
                getCurrentLocation()
            }

        } else {
            showInternetToast()
        }    }

    override fun onPause() {
        super.onPause()
    }
  
    private fun getCurrentLocation() {
        Log.e("Locationcall", "call")

        EasyLocation(this, object : EasyLocation.EasyLocationCallBack {
            override fun permissionDenied() {
                Log.e("Location", "permission  denied")
            }

            override fun locationSettingFailed() {
                Log.e("Location", "setting failed")
            }

            override fun getLocation(location: Location) {
                Log.e(
                    "Location_lat_lng",
                    " latitude ${location.latitude} longitude ${location.longitude}"
                )

                var address=getGeoAddressFromLatLong(location.latitude, location.longitude)

                if (address!!.isNotEmpty()) {
                    SessionTwiclo(this@MainActivity).userAddress = getGeoAddressFromLatLong(location.latitude, location.longitude)
                    SessionTwiclo(this@MainActivity).userLat = location.latitude.toString()
                    SessionTwiclo(this@MainActivity).userLng = location.longitude.toString()
                    userAddress?.text = SessionTwiclo(this@MainActivity).userAddress
                }else{
                    geocoderAddress(location.latitude.toString(),location.longitude.toString())
                }
            }
        })
    }

//    private fun checkLocation(){
//        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            showAlertLocation()
//        }
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        getLocationUpdates()
//    }

    private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Your location settings is set to Off, Please enable location to use this application")
        dialog.setPositiveButton("Settings") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
            finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

//    private fun getLocationUpdates() {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        locationRequest = LocationRequest()
//        locationRequest.interval = 50000
//        locationRequest.fastestInterval = 50000
//        locationRequest.smallestDisplacement = 170f //170 m = 0.1 mile
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //according to your app
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                locationResult ?: return
//                if (locationResult.locations.isNotEmpty()) {
//                    /*val location = locationResult.lastLocation
//                    Log.e("location", location.toString())*/
//                    val addresses: List<Address>?
//                    val geoCoder = Geocoder(applicationContext, Locale.getDefault())
//                    addresses = geoCoder.getFromLocation(
//                        locationResult.lastLocation.latitude,
//                        locationResult.lastLocation.longitude,
//                        1
//                    )
//                    if (addresses != null && addresses.isNotEmpty()) {
//                        val address: String = addresses[0].getAddressLine(0)
//                        val city: String = addresses[0].locality
//                        val state: String = addresses[0].adminArea
//                        val country: String = addresses[0].countryName
//                        val postalCode: String = addresses[0].postalCode
//                        val knownName: String = addresses[0].featureName
//                        Log.e("location", "$address $city $state $postalCode $country $knownName")
//                    }
//                }
//            }
//        }
//    }

    // Start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, null

        )
    }

    // Stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        //checkPermission()
        getAddress()
        if (SessionTwiclo(this).orderId.isNotEmpty()) {
            orderId=SessionTwiclo(this).orderId
            viewmodel?.getOrderStatusApi(
                SessionTwiclo(this@MainActivity).loggedInUserDetail.accountId,
                SessionTwiclo(this@MainActivity).loggedInUserDetail.accessToken, orderId!!
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(SessionTwiclo(this).userAddress!=null || !SessionTwiclo(this).userAddress.isEmpty()) {
                        try {
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener {
                                    // Got last known location. In some rare situations this can be null.
                                    Log.e("ll", ll.toString())
                                    SessionTwiclo(this).userAddress =
                                        getGeoAddressFromLatLong(ll!!.latitude, ll!!.longitude)
                                    userAddress?.text = SessionTwiclo(this).userAddress
                                }
                            ll = LocationServices.FusedLocationApi.getLastLocation(gac!!)
                            if (ll != null) {
                                Log.e("ll", ll.toString())
                                SessionTwiclo(this).userAddress = getGeoAddressFromLatLong(
                                    ll!!.latitude,
                                    ll!!.longitude
                                )
                                userAddress?.text = SessionTwiclo(
                                    this
                                ).userAddress
                                //updateUI(ll!!)
                            } else {
                                LocationServices.FusedLocationApi.requestLocationUpdates(
                                    gac!!, locationRequest, this
                                )
                            }

                        } catch (e: SecurityException) {
                            Toast.makeText(
                                this, "SecurityException:\n$e",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    finishAffinity()
                }

                return
            }
        }
    }

    fun initLocation() {
        if (isNetworkConnected) {
            isGooglePlayServicesAvailable()
            if (!isLocationEnabled()) showAlert()
            locationRequest = LocationRequest()
            locationRequest.interval = 2000
            locationRequest.fastestInterval = 2000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            gac = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        } else {
            showNetAlert()
        }

    }

    fun showNetAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("No internet connection. Please Connect you Internet and Try Again!")
        builder.setPositiveButton(
            "OK"
        ) { dialog: DialogInterface?, which: Int ->


            finish()
        }


        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            Log.v("tag", requestCode.toString())
            // initLocation()
        }
    }

    override fun onStart() {
        if (gac != null) {
            gac!!.connect()
        }

        super.onStart()
    }

    override fun onStop() {
        if (gac != null) {
            gac!!.disconnect()
        }
        super.onStop()
        //timer!!.cancel()

    }

    fun getVisibleFragment(): Fragment? {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragments: List<Fragment> = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.isVisible) return fragment
        }
        return null
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show()
            } else {
                Log.d(TAG, "This device is not supported.")
                finish()
            }
            return false
        }
        Log.d(TAG, "This device is supported.")
        return true
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("""Your Locations Settings is set to 'Off'.Please Enable Location to use this app""".trimIndent())
            .setPositiveButton("Location Settings") { _, _ ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                this.startActivityForResult(myIntent, 100) }
            .setNegativeButton("Cancel") { _, _ -> }
        dialog.show()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
            return
        } else {
            Log.d(TAG, "onConnected")
            ll = LocationServices.FusedLocationApi.getLastLocation(gac)
            Log.d(
                TAG, "LastLocation: " + (ll?.toString() ?: "NO LastLocation")
            )
            if (ll != null) {
                SessionTwiclo(this).userAddress = getGeoAddressFromLatLong(
                    ll!!.latitude,
                    ll!!.longitude
                )
                SessionTwiclo(this).userLat = ll!!.latitude.toString()
                SessionTwiclo(this).userLng = ll!!.longitude.toString()
                userAddress?.text = SessionTwiclo(this).userAddress


                //  updateUI(ll!!)
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this)
            }


        }

    }

    override fun onConnectionSuspended(p0: Int) {}

    override fun onLocationChanged(p0: Location) {
        p0.let {
            Log.v("onLocationChanged", p0.latitude.toString())
            /* if (!uiUpdated) {

                     updateUI(it)
                 }*/

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {}

    fun showLoginDialog(message: String){
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { _, _ ->
            startActivity(
                Intent(this, AuthActivity::class.java)
            )


        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
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
        }
    }

    fun geocoderAddress(lat:String,lng:String) {
        val geocodeUrl =
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
        Log.e("geocodeUrl", geocodeUrl)
        val geocodeRequest = object :
            StringRequest(Request.Method.GET, geocodeUrl, Response.Listener<String> { response ->
                dismissIOSProgress()
                Log.e("geocoderes_", "gson- $response")
                val gsonBuilder = GsonBuilder();
                val gson = gsonBuilder.create()
                var model =gson.fromJson(response.toString(), GeocoderModel::class.java)
                if(model.status.equals("OK")) {
                    if (model.results.size!=0) {
                        SessionTwiclo(this@MainActivity).userAddress = model.results[0].formattedAddress
                        SessionTwiclo(this@MainActivity).userLat =lat
                        SessionTwiclo(this@MainActivity).userLng = lng
                        if(SessionTwiclo(this@MainActivity).userAddress.isNotEmpty()) {
                            userAddress?.text = SessionTwiclo(this@MainActivity).userAddress
                        }else{
                            userAddress?.text= model.results[0].formattedAddress
                        }
                    }
                }

            }, Response.ErrorListener { dismissIOSProgress()}) {

        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(geocodeRequest)

    }

    private fun loadImage(url: String) {
        Glide.with(this).load(url)
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
            .placeholder(R.drawable.default_store)
            .error(R.drawable.default_store).into(store_statusImg)
    }

}