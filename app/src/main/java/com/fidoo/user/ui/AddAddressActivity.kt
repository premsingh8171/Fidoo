package com.fidoo.user.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fidoo.user.R
import com.fidoo.user.data.model.GetAddressModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.viewmodels.AddressViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.skyfishjy.library.RippleBackground
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.content_map.*
import java.io.IOException
import java.util.*
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.gson.Gson
import java.lang.IndexOutOfBoundsException

class AddAddressActivity : BaseActivity(), OnMapReadyCallback {


    private var mMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var placesClient: PlacesClient? = null
    private var predictionList: List<AutocompletePrediction>? = null
    var viewmodel: AddressViewModel? = null

    private var lat: Double? = 0.0
    private var lng: Double? = 0.0


    var addressType: String = "1"
    var defaultValue: String = "0"

    var tempAddressId: String = ""

    private var mLastKnownLocation: Location? = null
    private var locationCallback: LocationCallback? = null

    //private var searchBar: searchBar? = null
    private var mapView: View? = null
    private var btnFind: Button? = null
    private var rippleBg: RippleBackground? = null
    private var DEFAULT_ZOOM = 15f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_add_address)

        viewmodel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(AddressViewModel::class.java)
        //emailValue.setText(model.phone_no)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mapView = mapFragment.view


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this, getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        val token = AutocompleteSessionToken.newInstance()



        searchBar!!.setOnSearchActionListener(object :
            MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence) {
                startSearch(text.toString(), true, null, true)
            }

            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    searchBar!!.disableSearch()
                }
            }
        })


        searchBar!!.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setCountry("IN")
                    .setSessionToken(token)
                    .setQuery(s.toString())
                    .build()
                placesClient!!.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse = task.result
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.autocompletePredictions
                                val suggestionsList: MutableList<String?> = ArrayList()
                                for (i in predictionList!!.indices) {
                                    val prediction = predictionList!![i]
                                    suggestionsList.add(prediction.getFullText(null).toString())
                                }
                                searchBar!!.updateLastSuggestions(suggestionsList)
                                if (!searchBar!!.isSuggestionsVisible) {
                                    searchBar!!.showSuggestionsList()
                                }
                            }
                        } else {
                            Log.i("mytag", "prediction fetching task unsuccessful")
                        }
                    }
            }

            override fun afterTextChanged(s: Editable) {}
        })



        searchBar!!.setSuggstionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemClickListener(position: Int, v: View) {
                if (position >= predictionList!!.size) {
                    return
                }
                val selectedPrediction = predictionList!![position]
                val suggestion = searchBar!!.lastSuggestions[position].toString()
                searchBar!!.text = suggestion
                Handler().postDelayed(Runnable { searchBar!!.clearSuggestions() }, 1000)
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    searchBar!!.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
                val placeId = selectedPrediction.placeId
                val placeFields: List<Place.Field> = Arrays.asList(Place.Field.LAT_LNG)
                val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient!!.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener { fetchPlaceResponse ->
                        val place: Place = fetchPlaceResponse.place
                        Log.i("mytag", "Place found: " + place.getName())
                        val latLngOfPlace: LatLng = place.getLatLng()!!
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLngOfPlace,
                                DEFAULT_ZOOM
                            )
                        )
                    }.addOnFailureListener { e ->
                        if (e is ApiException) {
                            val apiException = e as ApiException
                            apiException.printStackTrace()
                            val statusCode = apiException.statusCode
                            Log.i("mytag", "place not found: " + e.message)
                            Log.i("mytag", "status code: $statusCode")
                        }
                    }
            }

            override fun OnItemDeleteListener(position: Int, v: View) {}
        })

        if (radioGroup.checkedRadioButtonId.equals(R.id.homeRadioBtn)) {
            tv_address_title.text = "Home"
        } else
            if (radioGroup.checkedRadioButtonId.equals(R.id.officeRadioBtn)) {
                tv_address_title.text = "Office"
            } else
                if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
                    tv_address_title.text = "Other"
                }



        btn_continue.setOnClickListener {
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))

            } else {
                if (ed_name.text.toString().equals("")){
                    showToast("Please enter your name")

                } else if (ed_phone.text.toString().equals("")) {
                    showToast("Please enter your phone no.")

                } else
                    if (ed_address.text.toString().equals("")) {
                        showToast("Please enter your house number")

                    } else
                        if (ed_landmark.text.toString().equals("")) {
                            showToast("Please enter your landmark")

                        } else {

                            showIOSProgress()
                            if (radioGroup.checkedRadioButtonId.equals(R.id.homeRadioBtn)) {
                                addressType = "1"
                            } else
                                if (radioGroup.checkedRadioButtonId.equals(R.id.officeRadioBtn)) {
                                    addressType = "2"
                                } else
                                    if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
                                        addressType = "3"
                                    }


                            defaultValue = if (defaultCheckBox.isChecked) {
                                "1"
                            } else {
                                "0"
                            }

                            if (intent.hasExtra("data")) {
                                viewmodel?.editAddressDetails(
                                        SessionTwiclo(this).loggedInUserDetail.accountId,
                                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                                        ed_address.text.toString(),
                                        ed_address.text.toString(),
                                        tv_Address.text.toString(),
                                        ed_landmark.text.toString(),
                                        addressType,
                                        lat.toString(),
                                        lng.toString(),
                                        ed_name.text.toString(),
                                        "",defaultValue,
                                        ed_phone.text.toString(),
                                        tempAddressId

                                )

                            }
                            else
                            {

                                viewmodel?.addAddressDetails(
                                        SessionTwiclo(this).loggedInUserDetail.accountId,
                                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                                        ed_address.text.toString(),
                                        ed_address.text.toString(),
                                        tv_Address.text.toString(),
                                        ed_landmark.text.toString(),
                                        addressType,
                                        lat.toString(),
                                        lng.toString(),
                                        ed_name.text.toString(),
                                        "",
                                        defaultValue,
                                        ed_phone.text.toString()
                                )
                            }
                        }
            }
        }

        viewmodel?.addAddressResponse?.observe(this, {

            dismissIOSProgress()
            showToast("Address added successfully")
            finish()

        })

        viewmodel?.failureResponse?.observe(this,  {
            showToast("Something is wrong, please try again")
        })



    }

//    private fun getMyLocation() {
//        val latLng = LatLng(getLatitude().toDouble(), getLongitude().toDouble())
//        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
//        googleMap.animateCamera(cameraUpdate)
//    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mMap!!.isMyLocationEnabled = true

            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        if (mapView != null && mapView!!.findViewById<View?>("1".toInt()) != null) {
            val locationButton =
                    (mapView!!.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 40, 180)
        }

        mMap!!.setOnCameraIdleListener {
            val center = mMap!!.cameraPosition.target
            // Toast.makeText(applicationContext,""+center.latitude,Toast.LENGTH_SHORT).show()


            val address = getGeoAddressFromLatLong(center.latitude, center.longitude)
            tv_Address.text = address
            lat = center.latitude
            lng = center.longitude
        }

        //check if gps is enabled or not and then request user to enable it
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this@AddAddressActivity)
        val task: Task<LocationSettingsResponse> =
                settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this@AddAddressActivity,
                OnSuccessListener<LocationSettingsResponse?> { getDeviceLocation() })
        task.addOnFailureListener(this@AddAddressActivity, OnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@AddAddressActivity, 51)
                } catch (e1: SendIntentException) {
                    e1.printStackTrace()
                }
            }
        })
//        mMap!!.setOnMyLocationButtonClickListener {
//            if (searchBar!!.isSuggestionsVisible) searchBar!!.clearSuggestions()
//            if (searchBar!!.isSearchEnabled) searchBar!!.disableSearch()
//            false
//        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation()
            }
        }
    }


    private fun getDeviceLocation() {
        mFusedLocationProviderClient!!.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mLastKnownLocation = task.result
                        if (mLastKnownLocation != null) {
                            mMap!!.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                    mLastKnownLocation!!.latitude,
                                                    mLastKnownLocation!!.longitude
                                            ), DEFAULT_ZOOM
                                    )
                            )
                            val address = getGeoAddressFromLatLong(
                                    mLastKnownLocation!!.latitude,
                                    mLastKnownLocation!!.longitude
                            )

                            tv_Address.text = address
                        } else {
                            val locationRequest = LocationRequest.create()
                            locationRequest.interval = 10000
                            locationRequest.fastestInterval = 5000
                            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    super.onLocationResult(locationResult)
                                    mLastKnownLocation = locationResult.lastLocation

                                    mMap!!.moveCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                    LatLng(
                                                            mLastKnownLocation!!.latitude,
                                                            mLastKnownLocation!!.longitude
                                                    ), DEFAULT_ZOOM
                                            )
                                    )



                                    mFusedLocationProviderClient!!.removeLocationUpdates(
                                            locationCallback!!
                                    )
                                }
                            }
                            if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return@addOnCompleteListener
                            }
                            mFusedLocationProviderClient!!.requestLocationUpdates(
                                    locationRequest,
                                    locationCallback,
                                    null
                            )
                        }
                    } else {
                        Toast.makeText(
                                this@AddAddressActivity,
                                "unable to get last location",
                                Toast.LENGTH_SHORT
                        )
                                .show()
                    }
                }
    }

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
    override fun getGeoAddressFromLatLong(latitude: Double, longitude: Double): String? {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            //   String knownName = addresses.get(0).getFeatureName(); // Only if available else return
            address
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            ""
        }
    }

    override fun onResume() {
        super.onResume()

        getDeviceLocation()



    }





}