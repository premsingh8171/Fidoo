package com.fidoo.user.addressmodule.address

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.fidoo.user.CartActivity.Companion.storeLat
import com.fidoo.user.CartActivity.Companion.storeLong
import com.fidoo.user.R
import com.fidoo.user.adapter.AddressesAdapter
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.ui.AddAddressActivity
import com.fidoo.user.utils.AUTOCOMPLETE_REQUEST_CODE
import com.fidoo.user.viewmodels.AddressViewModel
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.gson.Gson
import com.fidoo.user.utils.BaseActivity
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_saved_addresses.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class SavedAddressesActivity : BaseActivity(),
    AdapterClick {

    var viewmodel: AddressViewModel? = null
    //lateinit var mPlaceAutocompleteAdapter: PlaceAutocompleteAdapter
    var where:String?=""

    companion object{
        var savedAddressesActivity: SavedAddressesActivity?=null
        var editAdd: Int?=0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_addresses)
        //  statusBarTransparent()

        //  mPlaceAutocompleteAdapter = PlaceAutocompleteAdapter(this, gac, LAT_LNG_BOUNDS, null)

        viewmodel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
        savedAddressesActivity=this
        //storeLat = intent.getStringExtra("store_lat").toString()
        //storeLong = intent.getStringExtra("store_long").toString()
          where=intent.getStringExtra("where")
        if (where.equals("guest")){
            linear_progress_indicator.visibility = View.GONE

        }else{
            Log.e("store lat", storeLat)

            if (isNetworkConnected) {
                showIOSProgress()

                if(intent.getStringExtra("type") == "order"){
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        storeLat,
                        storeLong
                    )
                }else{
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        "",
                        ""
                    )
                }

            } else {
                showInternetToast()
            }
        }


        backIcon_saved_address.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }
//        tv_add_address.setOnClickListener {
//            var intent = Intent(applicationContext, AddAddressActivity::class.java)
//            startActivity(intent)
//        }

        viewmodel?.getAddressesResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("addresses response", Gson().toJson(user))
            linear_progress_indicator.visibility = View.GONE
            if (user.addressList.isNullOrEmpty()) {
                SessionTwiclo(this).userAddress = ""
                emptyTxt.visibility = View.VISIBLE
                emptyTxt.visibility = View.VISIBLE

            } else {
                emptyTxt.visibility = View.GONE
                emptyLastTxt.visibility = View.GONE
            }


            if (!user.addressList.isNullOrEmpty()) {
                val adapter = AddressesAdapter(
                    this,
                    user.addressList,
                    this,
                    intent.getStringExtra("type") )
                addressesRecyclerView?.layoutManager = GridLayoutManager(this, 1)
                addressesRecyclerView?.setHasFixedSize(true)
                addressesRecyclerView?.adapter = adapter
            }

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        viewmodel?.deleteAddressResponse?.observe(this, Observer { user ->

            if (isNetworkConnected) {

                dismissIOSProgress()
                Log.e("addresses response", Gson().toJson(user))
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

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
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

        tv_add_address.setOnClickListener {
            startActivityForResult(Intent(this, AddAddressActivity::class.java)
                .putExtra("where",where),1)
        }
    }

    override fun onResume() {
        super.onResume()
        if (editAdd==1){
            if (isNetworkConnected) {
                showIOSProgress()
                if(intent.getStringExtra("type") == "order"){
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        storeLat,
                        storeLong
                    )
                }else{
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        "",
                        ""
                    )
                }
                editAdd=0
            } else {
                showInternetToast()
            }
        }

    }

    override fun onItemClick(
        addressId: String?,
        type: String?,
        count: String?,
        offerPrice: String?,
        customizeCount: Int?,
        productType: String?,
        cart_id: String?
    ) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete this address?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            showIOSProgress()
            viewmodel?.deleteAddressApi(SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, addressId.toString()
            )
            //Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            //Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (isNetworkConnected) {
                showIOSProgress()
                if(intent.getStringExtra("type") == "order"){
                    viewmodel?.getAddressesApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        storeLat,
                        storeLong
                    )
                }else {
                    try {
                        if (SessionTwiclo(this).loggedInUserDetail.accountId.isNotEmpty()) {
                            viewmodel?.getAddressesApi(
                                SessionTwiclo(this).loggedInUserDetail.accountId,
                                SessionTwiclo(this).loggedInUserDetail.accessToken,
                                "",
                                ""
                            )
                        }
                    }catch (e:Exception){e.printStackTrace()}

                }

            } else {
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

    override fun onBackPressed() {
        AppUtils.finishActivityLeftToRight(this)
    }

}
