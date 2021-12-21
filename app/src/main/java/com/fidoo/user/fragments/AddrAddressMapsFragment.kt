package com.fidoo.user.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.fidoo.user.R

@Suppress("DEPRECATION")
class AddrAddressMapsFragment : Fragment(),OnMapReadyCallback  {


    lateinit var mView: View
  //  lateinit var locationRequest: LocationRequest


    val REQUEST_CODE_LOCATION = 123
    val LOCATION_DIALOG_CODE = 555

    private var mGoogleMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocationMarker: Marker? = null
    private var currentLocation: Location? = null
    private var firstTimeFlag = true

//    companion object {
//        var _lat: Double? = -34.0
//        var _lng: Double? = 151.0
//    }

//    private val callback = OnMapReadyCallback { googleMap ->
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }

    private val clickListener =
        View.OnClickListener { view: View ->
            if (mGoogleMap != null && currentLocation != null) animateCamera(
                currentLocation!!
            )
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_addr_address_maps, container, false)
//        requestPermission()
//
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            getLastLocation()
//        } else {
//            requireContext().showToast(requireContext(), "Permission is Not Granted")
//        }


        return mView
    }


    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult.lastLocation == null) return
            currentLocation = locationResult.lastLocation
            if (firstTimeFlag && mGoogleMap != null) {
                animateCamera(currentLocation!!)
                firstTimeFlag = false
            }
            showMarker(currentLocation!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView2) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        if (mGoogleMap != null && currentLocation != null) animateCamera(
            currentLocation!!
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {
       mGoogleMap = googleMap
    }


    private fun startCurrentLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 505
                )
                return
            }
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (ConnectionResult.SUCCESS == status) return true else {
            if (googleApiAvailability.isUserResolvableError(status)) Toast.makeText(
                requireContext(),
                "Please Install google play services to use this application",
                Toast.LENGTH_LONG
            ).show()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 505) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) Toast.makeText(
                requireContext(),
                "Permission denied by uses",
                Toast.LENGTH_SHORT
            )
                .show() else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) startCurrentLocationUpdates()
        }
    }


    private fun animateCamera(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mGoogleMap!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                getCameraPositionWithBearing(
                    latLng
                )
            )
        )
    }

    private fun getCameraPositionWithBearing(latLng: LatLng): CameraPosition {
        return CameraPosition.Builder().target(latLng).zoom(16f).build()
    }

    private fun showMarker(currentLocation: Location) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        if (currentLocationMarker == null) currentLocationMarker = mGoogleMap!!.addMarker(
            MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng)
        ) else com.fidoo.user.utils.maps.MarkerAnimation.animateMarkerToGB(
            currentLocationMarker,
            latLng,
            com.fidoo.user.utils.maps.LatLngInterpolator.Spherical()
        )
    }

     override fun onStop() {
        super.onStop()
        if (fusedLocationProviderClient != null) fusedLocationProviderClient!!.removeLocationUpdates(
            mLocationCallback
        )
    }

     override fun onResume() {
        super.onResume()
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
            startCurrentLocationUpdates()
        }
    }

     override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient = null
        mGoogleMap = null
    }



//    fun requestPermission() {
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                var builder = AlertDialog.Builder(requireContext())
//                builder.setMessage("We need permission for location ")
//                builder.setCancelable(false)
//                builder.setPositiveButton("ok", object : DialogInterface.OnClickListener {
//                    override fun onClick(p0: DialogInterface?, p1: Int) {
//                        requestPermissions(
//                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                            REQUEST_CODE_LOCATION
//                        )
//                    }
//                }).show()
//            } else {
//                requestPermissions(
//                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                    REQUEST_CODE_LOCATION
//                )
//            }
//
//        } else {
//            getLastLocation()
//            //  requireContext().showToast("Permission Granted")
//        }
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == REQUEST_CODE_LOCATION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //Permission Granted
//
//                getLastLocation()
//            } else {
//                //Permission Not Granted
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                    REQUEST_CODE_LOCATION
//                )
//            }
//        } else {
//            //Permission Not Granted
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                // This Block here means Permanentaly  Denied Permission
//                var builder = AlertDialog.Builder(requireContext())
//                    .setMessage("You have permanently denied this permission, go to setting to enable location permission")
//                    .setPositiveButton("ok", object : DialogInterface.OnClickListener {
//                        override fun onClick(p0: DialogInterface?, p1: Int) {
//                            gotoApplicationSetting()
//                        }
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .setCancelable(false)
//                    .show()
//            } else {
//                //Permission Not Granted
//                requireContext().showToast(requireContext(), "Permission Not Granted")
//            }
//        }
//    }
//
//    fun gotoApplicationSetting() {
//        val intent = Intent()
//        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//        val uri = Uri.fromParts("package", requireContext().packageName, null)
//        intent.data = uri
//        startActivity(intent)
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun getLastLocation() {
//
//        showLocationDialog()
//    }
//
//
//    fun showLocationDialog() {
//        locationRequest = LocationRequest.create()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 5000
//        locationRequest.fastestInterval = 2000
//
//        var builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//
//        builder.setAlwaysShow(true)
//
//        var task: Task<LocationSettingsResponse> =
//            LocationServices.getSettingsClient(requireContext())
//                .checkLocationSettings(builder.build())
//
//        task.addOnCompleteListener {
//            try {
//                var response: LocationSettingsResponse? = it.getResult(ApiException::class.java)
//                //Gps is on
//                showLocationLatLng()
//            } catch (e: ApiException) {
//                when (e.statusCode) {
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
//                        try {
//                            var resolvableApiException: ResolvableApiException =
//                                e as ResolvableApiException
//
//                            //this line is used in fragment to get the result in onActivityResult
//                            startIntentSenderForResult(
//                                resolvableApiException.resolution.intentSender,
//                                LOCATION_DIALOG_CODE,
//                                null,
//                                0,
//                                0,
//                                0,
//                                null
//                            );
//
//                            //this line is used in Activity to get the result in onActivityResult
//                            //   resolvableApiException.startResolutionForResult(requireActivity(), 555)
//
//                        } catch (ex: IntentSender.SendIntentException) {
//
//                        }
//
//                    }
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                    }
//
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == LOCATION_DIALOG_CODE) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    showLocationLatLng()
//                }
//                Activity.RESULT_CANCELED -> {
//                    showLocationDialog()
//                }
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    fun showLocationLatLng() {
//
//        var locationRequestnew = LocationRequest()
//        locationRequestnew.interval = 10000
//        locationRequestnew.fastestInterval = 3000
//        locationRequestnew.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//
//        LocationServices.getFusedLocationProviderClient(requireContext())
//            .requestLocationUpdates(locationRequestnew, object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    LocationServices.getFusedLocationProviderClient(requireContext())
//                        .removeLocationUpdates(this)
//                    if (locationResult != null && locationResult.locations.size > 0) {
//                        var lastLocationIndex = locationResult.locations.size - 1
//                        _lat = locationResult.locations[lastLocationIndex].latitude
//                        _lng = locationResult.locations[lastLocationIndex].longitude
//                        requireContext().showToast(requireContext(), _lat.toString())
//                    }
//                }
//            }, Looper.getMainLooper())
//    }
//
//    override fun onMapReady(p0: GoogleMap?) {
//    }
}