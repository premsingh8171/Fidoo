package com.fidoo.user.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fidoo.user.R
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.utils.BaseActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.robin.locationgetter.EasyLocation
import com.sanojpunchihewa.updatemanager.UpdateManager
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), android.location.LocationListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {




    companion object {
        var tempProductList: ArrayList<com.fidoo.user.data.model.TempProductListModel>? = null
        var addCartTempList: ArrayList<com.fidoo.user.data.model.AddCartInputModel>? = null
        var check: String = ""
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        var isearchSuggestionsList: ArrayList<String>? = null


        var ll: Location? = null
        var gac: GoogleApiClient? = null
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private lateinit var locationRequest : LocationRequest
        private lateinit var locationCallback : LocationCallback
        val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private var locationManager : LocationManager? = null

        // Declare the UpdateManager
        var mUpdateManager: UpdateManager? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        FirebaseApp.initializeApp(this)
        //searchSuggestionsList = ArrayList<String>()





        // Initialize the Update Manager with the Activity and the Update Mode
        mUpdateManager = UpdateManager.Builder(this).mode(UpdateManagerConstant.IMMEDIATE)

        mUpdateManager?.start()



        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTransparent)

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
        checkLocation()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tempProductList = ArrayList()
        addCartTempList = ArrayList()

        //ToDO
//        if (SessionTwiclo(this).profileDetail != null && SessionTwiclo(this).profileDetail.account != null) {
//            if (SessionTwiclo(this).profileDetail.account.name.equals("")) {
//                startActivity(Intent(this, EditProfileActivity::class.java))
//            }
//        }

        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    // ...
                    Log.e(
                        "Location_lat_lng",
                        " latitude ${location.latitude} longitude ${location.longitude}"
                    )
                    SessionTwiclo(this@MainActivity).userAddress = getGeoAddressFromLatLong(
                        location.latitude,
                        location.longitude
                    )
                    SessionTwiclo(this@MainActivity).userLat = location.latitude.toString()
                    SessionTwiclo(this@MainActivity).userLng = location.longitude.toString()
                    userAddress?.text = SessionTwiclo(
                        this@MainActivity
                    ).userAddress

                }
            }
        }


        if (SessionTwiclo(this).userAddress.equals("")) {
            try {
                // Request location updates
                //locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            } catch (ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }

            //define the listener
            val locationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    userAddress.text = ("" + location.longitude + ":" + location.latitude)
                }
                fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                fun onProviderEnabled(provider: String) {}
                fun onProviderDisabled(provider: String) {}
            }


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
                    SessionTwiclo(this@MainActivity).userAddress = getGeoAddressFromLatLong(
                        location.latitude,
                        location.longitude
                    )
                    SessionTwiclo(this@MainActivity).userLat = location.latitude.toString()
                    SessionTwiclo(this@MainActivity).userLng = location.longitude.toString()
                    userAddress?.text = SessionTwiclo(
                        this@MainActivity
                    ).userAddress
                }
            })

        } else {

            userAddress?.text = SessionTwiclo(
                this@MainActivity
            ).userAddress
        }








       /* FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            // val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, token)
            //  Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })*/
    }

    private fun checkLocation(){
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()
    }
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

    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f //170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //according to your app
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    /*val location = locationResult.lastLocation
                    Log.e("location", location.toString())*/
                    val addresses: List<Address>?
                    val geoCoder = Geocoder(applicationContext, Locale.getDefault())
                    addresses = geoCoder.getFromLocation(
                        locationResult.lastLocation.latitude,
                        locationResult.lastLocation.longitude,
                        1
                    )
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address: String = addresses[0].getAddressLine(0)
                        val city: String = addresses[0].locality
                        val state: String = addresses[0].adminArea
                        val country: String = addresses[0].countryName
                        val postalCode: String = addresses[0].postalCode
                        val knownName: String = addresses[0].featureName
                        Log.e("location", "$address $city $state $postalCode $country $knownName")
                    }
                }
            }
        }
    }

    // Start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null

        )
    }

    // Stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }



    override fun onResume() {
        super.onResume()
        if (SessionTwiclo(this).userAddress == ""){
            startLocationUpdates()
        }


        /* if (SessionTwiclo(this).userAddress.equals("")) {
        Log.e("ddd","ddddd")
            initLocation()
        } else {


        }*/
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



                    try {
                        fusedLocationClient.lastLocation
                                .addOnSuccessListener {
                                    // Got last known location. In some rare situations this can be null.
                                    Log.e("ll", ll.toString())
                                    SessionTwiclo(this).userAddress = getGeoAddressFromLatLong(ll!!.latitude, ll!!.longitude)
                                    userAddress?.text = SessionTwiclo(this).userAddress
                                }
                        ll = LocationServices.FusedLocationApi.getLastLocation(gac!!)
                        if (ll != null) {
                            Log.e("ll", ll.toString())
                            SessionTwiclo(
                                    this
                            ).userAddress = getGeoAddressFromLatLong(
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



                } else {
                    finishAffinity()
                    // initLocation()
                }
                return
            }
        }
    }


    fun initLocation() {
        if (isNetworkConnected()) {

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
    }

    fun getVisibleFragment(): Fragment? {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragments: List<Fragment> = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.isVisible) return fragment
        }
        return null
    }

    /*   override fun onBackPressed() {
           super.onBackPressed()

           Log.e("fedfver",getVisibleFragment().toString())
       }
   */


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
            Log.d(TAG, "LastLocation: " + (ll?.toString() ?: "NO LastLocation")
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

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onLocationChanged(p0: Location) {
        p0.let {
            Log.v("onLocationChanged", p0.latitude.toString())
            /* if (!uiUpdated) {

                     updateUI(it)
                 }*/

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
    // ToDO
//    private fun showLoginDialog(message: String){
//        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
//        //set title for alert dialog
//        builder.setTitle("Alert")
//        //set message for alert dialog
//        builder.setMessage(message)
//        // builder.setIcon(android.R.drawable.ic_dialog_alert)
//
//        //performing positive action
//        builder.setPositiveButton("Login") { dialogInterface, which ->
//            startActivity(
//                Intent(this, SplashActivity::class.java)
//            )
//
//
//        }
//
//        //performing negative action
//        builder.setNegativeButton("Cancel") { _, _ ->
//
//        }
//        // Create the AlertDialog
//        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
//        // Set other dialog properties
//        alertDialog.setCancelable(true)
//        alertDialog.show()
//    }

    /*    public void showProgress(boolean cancelable) {
        closeProgress();
        _progressDlg = new ProgressDialog(_context, R.style.MyTheme);
        _progressDlg.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        _progressDlg.setCancelable(cancelable);
        _progressDlg.show();

    }*/
    /* //  public void showProgress() {
        showProgress(false);
    }*/

}