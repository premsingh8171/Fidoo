package com.fidoo.user.addressmodule.activity

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fidoo.user.R
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.addressmodule.adapter.AddressSearchAdapter
import com.fidoo.user.addressmodule.adapter.AddressadapterNew
import com.fidoo.user.addressmodule.adapter.AddressesAdapter
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.addressmodule.viewmodel.AddressViewModel
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.cartview.activity.CartActivity.Companion.storeLat
import com.fidoo.user.cartview.activity.CartActivity.Companion.storeLong
import com.fidoo.user.constants.useconstants
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.profile.ui.ProfileFragment
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_saved_addresses.*
import kotlinx.android.synthetic.main.activity_saved_addresses.addDividerLl
import kotlinx.android.synthetic.main.activity_saved_addresses.add_addressfm
import kotlinx.android.synthetic.main.activity_saved_addresses.addressesRecyclerView
import kotlinx.android.synthetic.main.activity_saved_addresses.backIcon_saved_address
import kotlinx.android.synthetic.main.activity_saved_addresses.clear_searchImg
import kotlinx.android.synthetic.main.activity_saved_addresses.current_locLL
import kotlinx.android.synthetic.main.activity_saved_addresses.emptyLastTxt
import kotlinx.android.synthetic.main.activity_saved_addresses.emptyScren_ll
import kotlinx.android.synthetic.main.activity_saved_addresses.emptyTxt
import kotlinx.android.synthetic.main.activity_saved_addresses.headingaddItxt
import kotlinx.android.synthetic.main.activity_saved_addresses.locationViewll
import kotlinx.android.synthetic.main.activity_saved_addresses.searchAdd_cardView
import kotlinx.android.synthetic.main.activity_saved_addresses.searchEdt_new_fgmt
import kotlinx.android.synthetic.main.activity_saved_addresses.search_add_rv
import kotlinx.android.synthetic.main.activity_saved_addresses.search_iconImg
import kotlinx.android.synthetic.main.activity_saved_addresses_new.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*
import kotlin.collections.LinkedHashSet

class SavedAddressesActivity : BaseActivity() {

    var viewmodel: AddressViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var where: String? = ""
    var search_value: String? = ""
    var logoutDiolog: Dialog? = null
    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var savedAddressesActivity: SavedAddressesActivityNew? = null
        var editAdd: Int? = 0
        var addAddressOrNot: String? = ""
        var lat_long: Int = 0
        var Search_key: String = ""
    }

    private var placesClient: PlacesClient? = null
    private var predictionList: List<AutocompletePrediction>? = null
    val searchAddList: ArrayList<String> = ArrayList()
    var addressSearchAdapter: AddressSearchAdapter? = null
    var pref: SessionTwiclo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_saved_addresses)

        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        pref = SessionTwiclo(this)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
        //  mPlaceAutocompleteAdapter = PlaceAutocompleteAdapter(this, gac, LAT_LNG_BOUNDS, null)

        viewmodel = ViewModelProvider(this).get(AddressViewModel::class.java)
        viewmodelusertrack = ViewModelProvider(this).get(UserTrackerViewModel::class.java)
        //storeLat = intent.getStringExtra("store_lat").toString()
        //storeLong = intent.getStringExtra("store_long").toString()
        // where=intent.getStringExtra("where")
        where = pref!!.guestLogin
        Log.d("where_where", where!!)
//        if (navigateFromCart == 1){
//            addressesRecyclerView.visibility = View.GONE
//        }




        if (CartActivity.addNewAddressFromCart == 1){
            addressesRecyclerView.visibility = View.GONE
            CartActivity.addNewAddressFromCart = 0
        }
        if (where.equals("guest")) {
            //  linear_progress_indicator.visibility = View.GONE
        }
        else {
            Log.e("store lat", storeLat)
            if (isNetworkConnected) {
                showIOSProgress()

                if (intent.getStringExtra("type") == "order") {
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        storeLat,
                        storeLong
                    )
                }
                else {
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        "",
                        ""
                    )
                }
                viewmodelusertrack?.customerActivityLog(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).mobileno,
                    "SavedAddress Screen",
                    SplashActivity.appversion,
                    StoreListActivity.serive_id_,
                    SessionTwiclo(this).deviceToken
                )
            }
            else {
                showInternetToast()
            }
        }

//        if (ProfileFragment.addManages.equals("add_manage")) {
//            tv_yourAddresses_New.visibility = View.GONE
//            locationViewll.visibility = View.GONE
//            searchAdd_cardView.visibility = View.GONE
//            addDividerLl.visibility = View.GONE
//            headingaddItxt.visibility = View.VISIBLE
//            add_addressfm.visibility = View.VISIBLE
//        }
//        else {
//            locationViewll.visibility = View.VISIBLE
//            searchAdd_cardView.visibility = View.VISIBLE
//            searchAdd_cardView.setOnClickListener {
//                tv_yourAddresses_New.visibility = View.GONE
//            }
//            headingaddItxt.visibility = View.GONE
//            add_addressfm.visibility = View.GONE
//            if (ProfileFragment.addManages.equals("  ")){
//                addressesRecyclerView.visibility= View.GONE
//            }
//
//        }

        add_addressfm.setOnClickListener {

            useconstants.showSavedActivity=true
//
//            tv_yourAddresses_New.visibility = View.GONE
//            add_addressfm.visibility= View.GONE
//            headingaddItxt.visibility = View.GONE
//            addDividerLl.visibility = View.VISIBLE
//            addressesRecyclerView.visibility= View.GONE
//            searchAdd_cardView.visibility = View.VISIBLE
//            locationViewll.visibility = View.VISIBLE


            AppUtils.startActivityRightToLeft(
                this,
                Intent(this, SavedAddressesActivityNew::class.java).putExtra("list_show", "no")
            )

//            addAddressOrNot = "new_add"
//            startActivityForResult(
//                Intent(this, NewAddAddressActivityNew::class.java)
//                    .putExtra("where", where), 1
//            )
        }
        backIcon_saved_address.setOnClickListener {

            finish()
            AppUtils.finishActivityLeftToRight(this)
        }

        viewmodel?.getAddressesResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("addresses_response", Gson().toJson(user))
            //  linear_progress_indicator.visibility = View.GONE
            if(user.errorCode==200) {
                if (user.addressList.isNullOrEmpty()) {
                    SessionTwiclo(this).userAddress = ""
//                emptyTxt.visibility = View.VISIBLE
//                emptyTxt.visibility = View.VISIBLE

                }
                else {
//                    emptyTxt.visibility = View.GONE
//                    emptyLastTxt.visibility = View.GONE
//                    emptyScren_ll.visibility = View.GONE
                }


                val adapter = AddressadapterNew(this, user.addressList, object : AddressadapterNew.SetOnDeteleAddListener {
                    override fun onDelete(
                        add_id: String,
                        addressList: GetAddressModel.AddressList
                    ) {
                        //hit api
                        // Log.e("adadd_id", add_id)
                        deleteAddDialog(add_id)

                    }
                },
                    intent.getStringExtra("type")
                )
                addressesRecyclerView?.layoutManager = GridLayoutManager(this, 1)
                addressesRecyclerView?.setHasFixedSize(true)
                addressesRecyclerView?.adapter = adapter


            }
            else if (user.errorCode==101){
                showAlertDialog(this)
            }
        })

        viewmodel?.removeAddressResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            if (isNetworkConnected) {
                Log.e("removeAddressResponse", Gson().toJson(user))
                showToast(user.message)
                viewmodel?.getAddressesApi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    "",
                    ""
                )
            } else {
                showInternetToast()
            }
        })

        viewmodel?.failureResponse?.observe(this, Observer { user ->
            dismissIOSProgress()

            Log.e("cart response", Gson().toJson(user))
            showToast(user)

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

//        backIcon.setOnClickListener {
//            finish()
//        }

//        tv_add_address.setOnClickListener {
//            addAddressOrNot = "new_add"
//            startActivityForResult(
//                Intent(this, NewAddAddressActivity::class.java)
//                    .putExtra("where", where), 1
//            )
//        }

        current_locLL.setOnClickListener {
            SavedAddressesActivityNew.addAddressOrNot = "current_location"
            // Search_key=""
            SavedAddressesActivityNew.lat_long = 0
            startActivityForResult(
                Intent(this, NewAddAddressActivityNew::class.java)
                    .putExtra("where", where), 1
            )
        }

        searchEdt_new_fgmt.setOnClickListener {
            searchEdt_new_fgmt.isCursorVisible= true
            showSoftKeyboard(searchEdt_new_fgmt)
        }




    }

    private fun searchAdd(searchValue: String, token: AutocompleteSessionToken) {
        searchAddList.clear()
        val predictionsRequest = FindAutocompletePredictionsRequest.builder()
            .setCountry("IN")
            .setSessionToken(token)
            .setQuery(searchValue)
            .build()
        placesClient!!.findAutocompletePredictions(predictionsRequest)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val predictionsResponse = task.result
                    if (predictionsResponse != null) {
                        predictionList = predictionsResponse.autocompletePredictions
                        for (i in predictionList!!.indices) {
                            Log.i(
                                "predictionList___",
                                predictionList!![i].getFullText(null).toString()
                            )
                            searchAddList.add(predictionList!![i].getFullText(null).toString())
                        }

                        try {
                            val s: Set<String> = LinkedHashSet<String>(searchAddList)
                            searchAddList.clear()
                            searchAddList.addAll(s)
                            //  Log.i("predictionList___d", searchAddList.get(0).toString())
                            searchAddRecyclerview(searchAddList)
                        }
                        catch (e: Exception) {

                        }
                    }
                } else {
                    Log.i("mytag", "prediction fetching task unsuccessful")
                }
            }
    }

    private fun searchAddRecyclerview(searchAddList: ArrayList<String>) {
        addressSearchAdapter =
            AddressSearchAdapter(this, searchAddList, object : AddressSearchAdapter.AddSearchClick {
                override fun onSelectAdd(pos: Int, value: String) {
                    if (pos >= searchAddList!!.size) {
                        return
                    }
                    hideKeyboard()
                    SavedAddressesActivityNew.Search_key = value
                    SavedAddressesActivityNew.lat_long = 1
                    Log.d("editAddf___", SavedAddressesActivityNew.Search_key)
                    openSomeActivityForResult()
//                startActivityForResult(Intent(this@SavedAddressesActivity, NewAddAddressActivity::class.java)
//                    .putExtra("where",where),1)
                    // getGeoLocation(value)
                }
            })
        search_add_rv?.adapter = addressSearchAdapter
    }

    fun openSomeActivityForResult() {
        val intent = Intent(this, NewAddAddressActivityNew::class.java)
        intent.putExtra("where", where)
        // resultLauncher.launch(intent)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                //val data: Intent? = result.data
            }
        }



    override fun onResume() {

        super.onResume()
        viewmodel?.getAddressesApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            "",
            ""
        )


        Log.d("editAdd___", SavedAddressesActivityNew.editAdd.toString())
        if (SavedAddressesActivityNew.editAdd == 1) {
            if (isNetworkConnected) {
                showIOSProgress()
                if (intent.getStringExtra("type") == "order") {
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        storeLat,
                        storeLong
                    )
                } else {
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        "",
                        ""
                    )
                }
                SavedAddressesActivityNew.editAdd = 0
            }
            else {
                showInternetToast()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("requestCode__", requestCode.toString() + "--" + resultCode.toString())
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (isNetworkConnected) {
                if (intent.getStringExtra("type") == "order") {
                    showIOSProgress()
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        storeLat,
                        storeLong
                    )
                } else {
                    try {
                        if (SavedAddressesActivityNew.editAdd == 1) {
                            if (SessionTwiclo(this).loggedInUserDetail.accountId.isNotEmpty()) {
                                showIOSProgress()

                                viewmodel?.getAddressesApi(
                                    SessionTwiclo(this).loggedInUserDetail.accountId,
                                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                                    "",
                                    ""
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else {
                showInternetToast()
            }
        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            var place: com.google.android.libraries.places.api.model.Place =
                Autocomplete.getPlaceFromIntent(data!!)
            ed_search.setText(place.address)

            var locationName = String.format("Locality Name : %s", place.name)
            var latLng = place.latLng
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            var status = Autocomplete.getStatusFromIntent(data!!)
            Toast.makeText(this, status.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteAddDialog(add_id: String) {
        logoutDiolog = Dialog(this)
        logoutDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        logoutDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        logoutDiolog?.setContentView(R.layout.logout_popup)
        logoutDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        logoutDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        logoutDiolog?.setCanceledOnTouchOutside(true)
        logoutDiolog?.show()
        val cencelBtn = logoutDiolog?.findViewById<CardView>(R.id.cencelBtn)
        val logoutBtn = logoutDiolog?.findViewById<CardView>(R.id.logoutBtn)
        val rll_logout = logoutDiolog?.findViewById<RelativeLayout>(R.id.rll_logout)
        val message_txt = logoutDiolog?.findViewById<TextView>(R.id.message_txt)
        val cancel_txt = logoutDiolog?.findViewById<TextView>(R.id.cancel_txt)
        val yes_txt = logoutDiolog?.findViewById<TextView>(R.id.yes_txt)

        message_txt!!.text = "Are you sure you want to delete this address?"
        cancel_txt!!.text = "No"
        yes_txt!!.text = "Yes"

        cencelBtn?.setOnClickListener {
            logoutDiolog?.dismiss()
        }
        rll_logout?.setOnClickListener {
            //    logoutDiolog?.dismiss()
        }

        logoutBtn?.setOnClickListener {
            showIOSProgress()
            viewmodel?.removeAddressApi(
                SessionTwiclo(this@SavedAddressesActivity).loggedInUserDetail.accountId,
                SessionTwiclo(this@SavedAddressesActivity).loggedInUserDetail.accessToken, add_id
            )
            logoutDiolog?.dismiss()

        }

    }


    override fun onBackPressed() {
        AppUtils.finishActivityLeftToRight(this)
    }

//     val builder = AlertDialog.Builder(this@SavedAddressesActivity)
//                            builder.setTitle("Delete")
//                            builder.setMessage("Are you sure you want to delete this address?")
//                            builder.setIcon(android.R.drawable.ic_dialog_alert)
//                            builder.setPositiveButton("Yes") { dialogInterface, which ->
//                                showIOSProgress()
//                                viewmodel?.removeAddressApi(
//                                    SessionTwiclo(this@SavedAddressesActivity).loggedInUserDetail.accountId,
//                                    SessionTwiclo(this@SavedAddressesActivity).loggedInUserDetail.accessToken, add_id
//                                )
//                            }
//
//                            builder.setNegativeButton("No") { dialogInterface, which ->
//                            }
//                            val alertDialog: AlertDialog = builder.create()
//                            alertDialog.setCancelable(false)
//                            alertDialog.show()

}
