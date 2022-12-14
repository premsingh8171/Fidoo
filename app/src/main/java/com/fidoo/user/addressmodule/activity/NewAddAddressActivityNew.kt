package com.fidoo.user.addressmodule.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.checkAddressSavedFromWhichActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.addressmodule.activity.ChangeAddressActivity.Companion.value_current_loc
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.addressmodule.viewmodel.AddressViewModel
import com.fidoo.user.constants.useconstants
import com.fidoo.user.constants.useconstants.navigateFromCart
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.profile.ui.ProfileFragment
import com.fidoo.user.sendpackages.activity.SendPackageActivity
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.maps.model.GeocoderModel
import com.fidoo.user.utils.showAlertDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import com.skyfishjy.library.RippleBackground
import kotlinx.android.synthetic.main.activity_new_add_address_new.*
import kotlinx.android.synthetic.main.content_map.*
import java.util.*

@Suppress("DEPRECATION")
open class NewAddAddressActivityNew : BaseActivity(), OnMapReadyCallback, LocationListener{

    companion object {
        val MY_PERMISSIONS_REQUEST_CODE = 123

        var checkCount = 0
    }
    var handleOtherButtonAddress : Boolean = false
    var onMapNoNetDiolog: Dialog? = null
    private lateinit var saveBtn : Button
    private lateinit var userAddress : TextInputEditText
    private var mMap: GoogleMap? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var placesClient: PlacesClient? = null
    private var predictionList: List<AutocompletePrediction>? = null
    var viewmodel: AddressViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null

    private var lat: Double? = 0.0
    private var lng: Double? = 0.0

    var userName : String = ""
    var userPhone : String = ""
    var addressType: String = "1"
    var defaultValue: String = "0"
    var namelength=0
    var popup_count=0
    var othernamelength=0
    var mobilenumberlength=0

    var AddressId:String= ""
    var tempAddressId: String = ""
    var where: String? = ""
    var contact_type: String? = ""

    private var mLastKnownLocation: Location? = null
    private var locationCallback: LocationCallback? = null

    //private var searchBar: searchBar? = null
    private var mapView: View? = null
    private var rippleBg: RippleBackground? = null
    private var DEFAULT_ZOOM = 18f
    private var locationManager: LocationManager? = null

    private var mCircle: Circle? = null
    private var mMarker: Marker? = null
    private val REQUEST_CHECK_SETTINGS = 0x1

    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    var pref: SessionTwiclo? = null
    private var mMixpanel: MixpanelAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_new_add_address_new)
        pref = SessionTwiclo(this)
        saveBtn = findViewById(R.id.btn_continue)
        userAddress = findViewById(R.id.ed_address)
        userAddress.addTextChangedListener(saveAddressWatcher)
        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        userName = SessionTwiclo(this).loginDetail.account.name
        userPhone = SessionTwiclo(this).loginDetail.account.userName

        viewmodel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(AddressViewModel::class.java)
        val manager = this@NewAddAddressActivityNew.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            val permList = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            requestPermissions(permList, MY_PERMISSIONS_REQUEST_CODE)
        }

        Log.d("sddsdsd",SavedAddressesActivityNew.addAddressOrNot+"--"+where)

        viewmodelusertrack = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(UserTrackerViewModel::class.java)
        where = pref!!.guestLogin
        namelength= ed_address.text!!.length
        othernamelength= ed_name.text!!.length
        mobilenumberlength= ed_phone.text!!.length
        homeRadioBtn.setOnClickListener {
            ed_name.text!!.clear()
            ed_phone.text!!.clear()
            other_add_contact_details.visibility = View.GONE
            handleOtherButtonAddress = false
            namelength= ed_address.text!!.length

            othernamelength= ed_name.text!!.length
            mobilenumberlength= ed_phone.text!!.length

            if (namelength>0){
                btn_continue.isEnabled = true
            }else{
                btn_continue.isEnabled = false
            }
        }
        officeRadioBtn.setOnClickListener {
            other_add_contact_details.visibility = View.GONE
            ed_name.text!!.clear()
            ed_phone.text!!.clear()
            handleOtherButtonAddress = false
            namelength= ed_address.text!!.length
            othernamelength= ed_name.text!!.length
            mobilenumberlength= ed_phone.text!!.length

            if (namelength>0){
                btn_continue.isEnabled = true
            }else{
                btn_continue.isEnabled = false
            }
        }
        otherRadioBtn.setOnClickListener {
            other_add_contact_details.visibility = View.VISIBLE
            handleOtherButtonAddress = true
            namelength= ed_address.text!!.length
            othernamelength= ed_name.text!!.length
            mobilenumberlength= ed_phone.text!!.length
            if (namelength>0 && othernamelength>0 && mobilenumberlength>9) {
                btn_continue.isEnabled = true
            } else {
                btn_continue.isEnabled = false
            }
        }

        if (SessionTwiclo(this).isLoggedIn == true) {
            viewmodelusertrack?.customerActivityLog(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).mobileno,
                "AddNewAddress Screen",
                SplashActivity.appversion,
                StoreListActivity.serive_id_,
                SessionTwiclo(this).deviceToken
            )
        }

        if (useconstants.editAddress){
            ProfileFragment.addManages= "  "
            tv_SelectDeliveryAddress.visibility = View.GONE
            live_add_1.visibility = View.GONE
            btn_proceed.visibility = View.GONE
            tv_AddAddress.visibility = View.VISIBLE
            add_new_add_ll.visibility = View.VISIBLE
            btn_continue.visibility = View.VISIBLE
            iv_mapSlider.visibility  = View.GONE
            iv_emptyMap.visibility = View.VISIBLE

        }
        AddressId= intent.getStringExtra("Id").toString()


        // where = intent.getStringExtra("where")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView2) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        mapView = mapFragment.view

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(this, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        checkPermission()
        displayLocationSettingsRequest(this)
        getLocation()
        if (intent.hasExtra("data")) {
            val model: GetAddressModel.AddressList = Gson().fromJson(intent.getStringExtra("data"),
                GetAddressModel.AddressList::class.java
            )
            lat = model.latitude.toDouble()
            lng = model.longitude.toDouble()
            tv_Address.setText(model.location)
            tv_Address.setHorizontallyScrolling(true)
            tv_Address_1.setText(model.location)

            ed_name.setText(model.name)
            ed_phone.setText(model.phone_no)
            ed_address.setText(model.flatNo)
            //buildingValue.setText(model.building)
            ed_landmark.setText(model.landmark)
            tempAddressId = model.id

            when {
                model.addressType.equals("1") -> {
                    homeRadioBtn.isChecked = true
                }
                model.addressType.equals("2") -> {
                    officeRadioBtn.isChecked = true
                }
                else -> {
                    otherRadioBtn.isChecked = true
                }
            }
        }

        getDeviceLocation()

        if (radioGroup.checkedRadioButtonId.equals(R.id.homeRadioBtn)) {
            tv_address_title.text = "Home"
        }
        else if (radioGroup.checkedRadioButtonId.equals(R.id.officeRadioBtn)) {
            tv_address_title.text = "Office"
        }
        else if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
            tv_address_title.text = "Other"
        }

        add_address_frmLL.setOnClickListener {
            finish()
        }

        btn_proceed.setOnClickListener {
            tv_SelectDeliveryAddress.visibility = View.GONE
            live_add_1.visibility = View.GONE
            btn_proceed.visibility = View.GONE
            tv_AddAddress.visibility = View.VISIBLE
            add_new_add_ll.visibility = View.VISIBLE
            btn_continue.visibility = View.VISIBLE
            iv_mapSlider.visibility  = View.GONE
            iv_emptyMap.visibility = View.VISIBLE
        }

        ivPickContact.setOnClickListener {
            var i = Intent(Intent.ACTION_PICK)
            SavedAddressesActivityNew.lat_long=1
            i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(i,111)
        }
        ed_name.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {


                var search_value = s.toString()
                othernamelength= search_value.length

                namelength= ed_address.text!!.length

                mobilenumberlength= ed_phone.text!!.length

                if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
                    if (namelength>0 && othernamelength>0 && mobilenumberlength>9) {
                        btn_continue.isEnabled = true
                    } else {
                        btn_continue.isEnabled = false
                    }
                }else{
                    if (namelength>0){
                        btn_continue.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        ed_phone.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                var search_value = s.toString()
                mobilenumberlength= search_value.length
                namelength= ed_address.text!!.length
                othernamelength= ed_name.text!!.length



                if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
                    if (namelength>0 && othernamelength>0 && mobilenumberlength>9) {
                        btn_continue.isEnabled = true
                    } else {
                        btn_continue.isEnabled = false
                    }
                }else{
                    if (namelength>0){
                        btn_continue.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        ed_address.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                var search_value = s.toString()

                namelength= search_value.length

                if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
                    if (namelength>0 && othernamelength>0 && mobilenumberlength>9) {
                        btn_continue.isEnabled = true
                    } else {
                        btn_continue.isEnabled = false
                    }
                }else{
                    if (namelength>0){
                        btn_continue.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {



            }

        })

//        ed_address.doAfterTextChanged {
//            btn_continue.isEnabled = true
////            live_add_1.visibility = View.GONE
////            locationImage.visibility = View.GONE
//        }

        btn_continue.setOnClickListener {
            checkCount = 1
            checkAddressSavedFromWhichActivity = "fromNewAddressActivity"
            useconstants.addressTypeuser= true
            useconstants.showSavedActivity= false
            useconstants.fromprofile=false
            useconstants.addressListShow= true
            if (!isNetworkConnected) {
                showToast(resources.getString(R.string.provide_internet))
            }
            else {

                if (!ed_landmark.text.isNullOrBlank()) {
                    SessionTwiclo(this).userAddress =
                        ed_address.text.toString() + ", " + ed_landmark.text.toString() + ", " + tv_Address_1.text.toString()
                }else{
                    SessionTwiclo(this).userAddress =
                        ed_address.text.toString() + ", " + tv_Address_1.text.toString()
                }

                if (useconstants.sendpackage){
                    if(useconstants.tofromsendpackage.equals("from")){

                        if (ed_landmark.text.isNullOrBlank()){
                            SendPackageActivity.selectedFromAddress= "Building: "+ed_address.text.toString() + ", "+tv_Address_1.text.toString()

                        }else{
                            SendPackageActivity.selectedFromAddress= "Building: "+ed_address.text.toString() + ", Landmark: " + ed_landmark.text.toString()+", "+tv_Address_1.text.toString()

                        }

                        //  SendPackageActivity.selectedFromAddress = addressList[position].location
                        SendPackageActivity.selectedfromLat = lat!!
                        SendPackageActivity.selectedfromLng= lng!!
                        SendPackageActivity.fromName = ed_name.text.toString()
                        SendPackageActivity.fromNumber = ed_phone.text.toString()
                        useconstants.tofromsendpackage=""
                    }else if (useconstants.tofromsendpackage.equals("to")){
                        if (ed_landmark.text.isNullOrBlank()){
                            SendPackageActivity.selectedToAddress= "Building: "+ed_address.text.toString() + ", "+tv_Address_1.text.toString()

                        }else{
                            SendPackageActivity.selectedToAddress= "Building: "+ed_address.text.toString() + ", Landmark: " + ed_landmark.text.toString()+", "+tv_Address_1.text.toString()

                        }

                        //  SendPackageActivity.selectedFromAddress = addressList[position].location
                        SendPackageActivity.selectedtoLat = lat!!
                        SendPackageActivity.selectedtoLng= lng!!
                        SendPackageActivity.toName = ed_name.text.toString()
                        SendPackageActivity.toNumber = ed_phone.text.toString()
                        useconstants.tofromsendpackage=""
                    }
                }
                SessionTwiclo(this).userLat= lat.toString()
                SessionTwiclo(this).userLng= lng.toString()

                    if (where.equals("guest")) {
                        SessionTwiclo(this).userLat = lat.toString()
                        SessionTwiclo(this).userLng = lng.toString()
                        SessionTwiclo(this).userAddress = tv_Address.text.toString()
                        SavedAddressesActivity.savedAddressesActivity!!.finish().toString()
                        finish()
                    }
                    else {
                        if (lat != null || lat != 0.0 && lng != null || lng != 0.0) {
                            if (SavedAddressesActivity.addAddressOrNot.equals("new_add")) {
                                if (ed_address.text.toString().equals("")) {
                                    showToast("Please enter your house number")
                                } else if (tv_Address.equals("")) {
                                    showToast("Location not available")
                                } else {
                                    if (MainActivity.addEditAdd == "SendPackage") {
//                                if (ed_name.text.toString().equals("")) {
//                                    showToast("Please add contact details")
//                                } else {
                                        showIOSProgress()
                                        if (radioGroup.checkedRadioButtonId.equals(R.id.homeRadioBtn)) {
                                            addressType = "1"
                                        } else if (radioGroup.checkedRadioButtonId.equals(R.id.officeRadioBtn)) {
                                            addressType = "2"
                                        } else if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
                                            addressType = "3"
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
                                                "", defaultValue,
                                                ed_phone.text.toString(),
                                                tempAddressId, contact_type!!
                                            )
                                        } else {
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
                                                ed_phone.text.toString(), contact_type!!
                                            )
                                        }
                                        // }
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
                                                "", defaultValue,
                                                ed_phone.text.toString(),
                                                tempAddressId, contact_type!!
                                            )
                                        } else {
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
                                                ed_phone.text.toString(), contact_type!!
                                            )
                                        }
                                    }
                                }
                            } else {
                                if (ed_address.text.toString().equals("")) {
                                    showToast("Please enter your house number")
                                }

                                if (handleOtherButtonAddress == true) {
                                    if (ed_name.text.toString().equals("")) {
                                        showToast("Please add contact details")
                                    } else if (ed_phone.text.toString().equals("")) {
                                        showToast("Please add contact details")

                                    }else if (ed_phone.text!!.length>0 && ed_phone.text!!.length<10 ){
                                        showToast("Please enter valid contact details")
                                    }else {
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
                                                "", defaultValue,
                                                ed_phone.text.toString(),
                                                tempAddressId,
                                                contact_type!!
                                            )
                                        } else {
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
                                                ed_phone.text.toString(),
                                                contact_type!!
                                            )
                                        }
                                    }
                                } else {
                                    if (tv_Address.equals("")) {
                                        showToast("Location not available")
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
                                                "", defaultValue,
                                                ed_phone.text.toString(),
                                                tempAddressId,
                                                contact_type!!
                                            )
                                        } else {
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
                                                ed_phone.text.toString(),
                                                contact_type!!
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            Toast.makeText(this, "unable to get location", Toast.LENGTH_SHORT).show()
                        }
                    }


            }
            if (addressType.equals("1")) {
                SessionTwiclo(this).addressType = "Home"
            }else if(addressType.equals("2")){
                SessionTwiclo(this).addressType = "Office"
            }else if(addressType.equals("3")){
                SessionTwiclo(this).addressType = "Other"
            }

           if (useconstants.editmessage){
               viewmodel?.removeAddressApi(
                   SessionTwiclo(this).loggedInUserDetail.accountId,
                   SessionTwiclo(this).loggedInUserDetail.accessToken, useconstants.addressId_delete
               )

//               viewmodel?.addAddressDetails(
//                   SessionTwiclo(this).loggedInUserDetail.accountId,
//                   SessionTwiclo(this).loggedInUserDetail.accessToken,
//                   ed_address.text.toString(),
//                   ed_address.text.toString(),
//                   tv_Address.text.toString(),
//                   ed_landmark.text.toString(),
//                   addressType,
//                   lat.toString(),
//                   lng.toString(),
//                   ed_name.text.toString(),
//                   "",
//                   defaultValue,
//                   ed_phone.text.toString(),
//                   contact_type!!
//               )
               useconstants.editmessage= false

           }

        }

        change_txt.setOnClickListener {
            useconstants.showSavedActivity= true

            useconstants.editAddress= false
            useconstants.editmessage= true

            add_new_add_ll.visibility= View.GONE


            val intent = Intent(this,SavedAddressesActivityNew::class.java).putExtra("list_show", "no")
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
//            val intent = Intent(this,SavedAddressesActivityNew::class.java)
//            intent.putExtra("list_show", "no")
//            startActivity(intent)
            AppUtils.finishActivityLeftToRight(this)
            finish()
        }

        viewmodel?.editAddressResponse?.observe(this) {
            dismissIOSProgress()
            Log.e("editAddressResponse_", Gson().toJson(it))
            if (it.errorCode == 200) {
                SavedAddressesActivityNew.editAdd = 1
                showToast("Address Edited successfully")
                // savedAddressesActivity!!.finish()
                finish()
                AppUtils.finishActivityLeftToRight(this)
            } else if (it.errorCode == 101) {
                showAlertDialog(this)
            }
        }

        viewmodel?.addAddressResponse?.observe(this) {
            dismissIOSProgress()
            if (it.errorCode == 200) {
                if (useconstants.editmessage){
                    showToast("Address edited successfully")

                }else {
                    showToast("Address added successfully")
                }
                SavedAddressesActivityNew.editAdd = 1
                finish()
                AppUtils.finishActivityLeftToRight(this)
                if (useconstants.navigateFromNewAddressActivity == 1 && !useconstants.sendpackage) {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
               else if(navigateFromCart == 1){
//                    val intent = Intent(this, CartActivity::class.java)
//                    startActivity(intent)
                }
            } else if (it.errorCode == 101) {
                showAlertDialog(this)
            }
        }
        viewmodel?.failureResponse?.observe(this) {
            showToast("Something is wrong, please try again")
        }
    }



    private val saveAddressWatcher = object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
          val user_address = userAddress.text.toString().trim()
            saveBtn.isEnabled = user_address.isNotEmpty()
        }
        override fun afterTextChanged(p0: Editable?) {}
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
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            Log.d("resultt", result.toString())
            Log.d("statuss", status.toString())
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                }
                catch (e: SendIntentException) {
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getLocation()
        getDeviceLocation()
        if (radioGroup.checkedRadioButtonId.equals(R.id.otherRadioBtn)) {
            other_add_contact_details.visibility = View.VISIBLE
            handleOtherButtonAddress = true
        }
        if(!ed_address.text.toString().equals("") || !ed_phone.text.toString().equals("")){
            live_add_1.visibility = View.GONE
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@NewAddAddressActivityNew,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@NewAddAddressActivityNew, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@NewAddAddressActivityNew, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION //Manifest.permission.READ_PHONE_STATE
                    ),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@NewAddAddressActivityNew, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            }
        }
        else {
            getDeviceLocation()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

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
            layoutParams.setMargins(0, 0, 40, 320)
        }

        mMap!!.setOnCameraIdleListener {
            linear_progress_indicator_map.visibility = View.GONE
            val center = mMap!!.cameraPosition.target
            // Toast.makeText(applicationContext,""+center.latitude,Toast.LENGTH_SHORT).show()
            val address = getGeoAddressFromLatLong(center.latitude, center.longitude)
            if (address!!.isNotEmpty()) {
                //  tv_Address.text = address
                geocoderAddress(center.latitude!!.toString(), center.longitude!!.toString())
            } else {
                geocoderAddress(center.latitude!!.toString(), center.longitude!!.toString())

            }
            // tv_Address.text = address
            lat = center.latitude
            lng = center.longitude

            linear_progress_indicator_map.visibility = View.GONE
        }

        //check if gps is enabled or not and then request user to enable it
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this@NewAddAddressActivityNew)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            this@NewAddAddressActivityNew
        ) {

            if (intent.hasExtra("data")) {
                getDeviceLocation()
            } else {
                getDeviceLocation()
            }
        }

        task.addOnFailureListener(this@NewAddAddressActivityNew) { e ->

            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@NewAddAddressActivityNew, 51)
                } catch (e1: SendIntentException) {
                    e1.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111 && resultCode == Activity.RESULT_OK){
            var contactUri : Uri = data?.data ?: return
            var cols = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            var rs = contentResolver.query(contactUri,cols,null,null,null)
            if (rs?.moveToFirst()!!){
                var fixmobileno= ""
                Log.d("rsstring",rs.getString(0))
                if (rs.getString(0).length>10){

                    val fixlength= rs.getString(0).length-10
                    val numberarray= rs.getString(0).toCharArray()
                    for (i in fixlength-1 .. numberarray.size-1){

                        println(numberarray[i])
                        if (i!= fixlength+4) {
                            if (i == fixlength - 1) {
                                fixmobileno = numberarray[i].toString()
                            } else {
                                fixmobileno = fixmobileno + numberarray[i]
                            }
                        }
                      //  fixmobileno= fixmobileno+numberarray[i]
                    }
                }else{
                    fixmobileno= rs.getString(0)
                }
                ed_phone.setText(fixmobileno)
                ed_name.setText(rs.getString(1))

                namelength= ed_address.text!!.length
                othernamelength= ed_name.text!!.length
                mobilenumberlength= ed_phone.text!!.length
                if (namelength>0 && othernamelength>0 && mobilenumberlength==10) {
                    btn_continue.isEnabled = true
                } else {
                    btn_continue.isEnabled = false
                }
              //  btn_continue.isEnabled= true
            }
        }
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                if (intent.hasExtra("data")) {
                    getDeviceLocation()
                } else {
                    getDeviceLocation()
                }
            }
        }

        Log.d(
            "requestCode_____",
            requestCode.toString() + "=" + SECOND_ACTIVITY_REQUEST_CODE.toString()
        )

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                var result = data!!.getStringExtra("location")
                if (result != null) {
                    displayLocationSettingsRequest(this)

                    getLocation()

                    getGeoLocation(result)
                }
            }
            if (value_current_loc.equals("Current_location")) {
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
                Log.d(
                    "mLastKnownLocationc_",
                    mLastKnownLocation!!.latitude.toString() + "\n"
                            + mLastKnownLocation!!.longitude.toString()
                )
                if (address!!.isNotEmpty()) {
                    // tv_Address.text = address
                    geocoderAddress(
                        mLastKnownLocation!!.latitude!!.toString(),
                        mLastKnownLocation!!.longitude!!.toString()
                    )
                }
                else {
                    geocoderAddress(
                        mLastKnownLocation!!.latitude!!!!.toString(),
                        mLastKnownLocation!!.longitude!!.toString()
                    )
                }
            }
        }
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient!!.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mLastKnownLocation = task.result
                    if (mLastKnownLocation != null) {

                        if (intent.hasExtra("data")) {

                            mMap!!.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lat!!,
                                        lng!!
                                    ), DEFAULT_ZOOM
                                )
                            )
                            val address = getGeoAddressFromLatLong(
                                lat!!,
                                lng!!
                            )
                            if (address!!.isNotEmpty()) {
                                // tv_Address.text = address
                                geocoderAddress(lat!!.toString(), lng!!.toString())
                            }
                            else {
                                geocoderAddress(lat!!.toString(), lng!!.toString())
                            }
                        }
                        else {
                            if (SavedAddressesActivityNew.lat_long == 1) {
                                getGeoLocation(SavedAddressesActivityNew.Search_key)
                            }
                            else {
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
                                Log.d(
                                    "mLastKnownLocation_",
                                    mLastKnownLocation!!.latitude.toString() + "\n"
                                            + mLastKnownLocation!!.longitude.toString()
                                )
                                if (address!!.isNotEmpty()) {
                                    // tv_Address.text = address
                                    geocoderAddress(
                                        mLastKnownLocation!!.latitude!!.toString(),
                                        mLastKnownLocation!!.longitude!!.toString()
                                    )
                                }
                                else {
                                    geocoderAddress(
                                        mLastKnownLocation!!.latitude!!.toString(),
                                        mLastKnownLocation!!.longitude!!.toString()
                                    )
                                }
                            }
                        }
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return@addOnCompleteListener
                        }
                    }
                    else {
                        Toast.makeText(this@NewAddAddressActivityNew, "unable to get current location", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

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
            val address = addresses[0].getAddressLine(0)
            val sub_locality = addresses[0].subLocality
            val feature_Name = addresses[0].featureName
            if(sub_locality != null) {
                tv_Address_locality.text = "$feature_Name" + " " + "$sub_locality"
            }
            else{
                tv_Address_locality.text = "$feature_Name"
            }
            address
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            ""
        }
    }

    private fun getGeoLocation(location: String?) {
        var latLng: String = ""
        try {
            var address_list: List<Address>? = null
            if (location != null || location != "") {
                val geocoder = Geocoder(this)
                try {
                    address_list = geocoder.getFromLocationName(location, 1)
                } catch (e: java.lang.Exception) {
                }
                try {
                    val address_: Address = address_list!![0]

                    latLng = LatLng(address_.getLatitude(), address_.getLongitude()).toString()
                    var lat = address_.getLatitude()
                    var lng = address_.getLongitude()

                    mMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lat!!,
                                lng!!
                            ), DEFAULT_ZOOM
                        )
                    )
                    val address = getGeoAddressFromLatLong(
                        lat!!,
                        lng!!
                    )
                    tv_Address!!.setText(address)
                    tv_Address.setHorizontallyScrolling(true)
                    tv_Address_1!!.setText(address)
                    tv_Address_1.visibility = View.VISIBLE
                }
                catch (e: Exception) {
                }
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "No result found", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.finishActivityLeftToRight(this)
    }

    override fun onLocationChanged(location: Location) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1)
            val sydney = LatLng(location.getLatitude(), location.getLongitude())
            mMap!!.addMarker(MarkerOptions().position(sydney).title("India"))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f))
            var mApAddress = addresses[0].getAddressLine(0)
            addresses[0].postalCode
            Log.d("current_location", mApAddress)
            if (addresses[0].subLocality != null) {
                Log.d("subLocality__", addresses[0].subLocality)

            } else {
                Log.d("subLocality__", addresses[0].locality)
            }
            var pincodeStr = addresses[0].postalCode
            Log.d("pincodeStr___", pincodeStr)
        } catch (e: Exception) {

        }
    }

    fun geocoderAddress(lat: String, lng: String) {
        progressindicatorAdd.visibility = View.VISIBLE
        if (isNetworkConnected) {
            val geocodeUrl =
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
            Log.e("geocodeUrl", geocodeUrl)
            val geocodeRequest = object :
                StringRequest(
                    Request.Method.GET,
                    geocodeUrl,
                    Response.Listener<String> { response ->
                        dismissIOSProgress()
                        Log.e("geocoderes_", "gson- $response")
                        val gsonBuilder = GsonBuilder();
                        val gson = gsonBuilder.create()
                        var model = gson.fromJson(response.toString(), GeocoderModel::class.java)
                        if (model.status.equals("OK")) {
                            if (model.results.size != 0) {
                                tv_Address.setText(model.results[0].formattedAddress)
                                tv_Address.setHorizontallyScrolling(true)
                                tv_Address_1.setText(model.results[0].formattedAddress)
                                live_add_1.visibility = View.VISIBLE
                            }
                        } else {
                            var address_ = getGeoAddressFromLatLong(lat.toDouble(), lng.toDouble())
                            tv_Address.setText(address_)
                            tv_Address.setHorizontallyScrolling(true)
                            tv_Address_1.setText(address_)
                            live_add_1.visibility = View.VISIBLE
                        }
                        progressindicatorAdd.visibility = View.GONE

                    },
                    Response.ErrorListener {
                        dismissIOSProgress()
                        progressindicatorAdd.visibility = View.GONE
                    }) {
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(geocodeRequest)
        }
        else {
            if (popup_count==0) {
                onMapPopUp(lat, lng)
                progressindicatorAdd.visibility = View.GONE
                popup_count++
            }
        }
    }

    override fun onStop() {
        super.onStop()
        popup_count=0
    }

    private fun onMapPopUp(lat: String, lng: String) {
        onMapNoNetDiolog = Dialog(this@NewAddAddressActivityNew)
        onMapNoNetDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        onMapNoNetDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        onMapNoNetDiolog?.setContentView(R.layout.no_net_popup)
        onMapNoNetDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        onMapNoNetDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        onMapNoNetDiolog?.setCanceledOnTouchOutside(true)
        onMapNoNetDiolog?.show()
        val onMapCencelBtn = onMapNoNetDiolog?.findViewById<CardView>(R.id.onMapCencelBtn)
        val retryBtn = onMapNoNetDiolog?.findViewById<CardView>(R.id.retryBtn)

        onMapCencelBtn?.setOnClickListener {
            onMapNoNetDiolog?.dismiss()
            finish()
        }

        retryBtn?.setOnClickListener {
            geocoderAddress(lat, lng)
            onMapNoNetDiolog?.dismiss()
        }
    }
}


