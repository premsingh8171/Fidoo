package com.fidoo.user.addressmodule.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.addressmodule.adapter.AddressSearchAdapter
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.hideKeyboard
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.currentlocation_library.GetAddFromLatLong
import com.premsinghdaksha.currentlocation_library.TrackGPSLocation
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_change_address.*
import kotlinx.android.synthetic.main.activity_saved_addresses_new.*
import java.util.*
import kotlin.collections.LinkedHashSet


class ChangeAddressActivity : BaseActivity() {
    var search_value:String?=""
    companion object{  public  var value_current_loc:String?=""
    }
    private var placesClient: PlacesClient? = null
    private var predictionList: List<AutocompletePrediction>? = null
    val searchAddList: ArrayList<String> = ArrayList()
    var addressSearchAdapter: AddressSearchAdapter?=null
    private var getAddFromLatLong: GetAddFromLatLong? = null
    private var trackGPSLocation: TrackGPSLocation? = null
    private var mMixpanel: MixpanelAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_change_address)
        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
//        trackGPSLocation =  TrackGPSLocation(this);
//
//        if (getGeoAddressFromLatLong(trackGPSLocation!!.latitude,trackGPSLocation!!.longitude)!=null) {
//            value_current_loc =
//                getGeoAddressFromLatLong(trackGPSLocation!!.latitude, trackGPSLocation!!.longitude)
//        }

        backIcon_ChangeAdd.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }

        current_loc_ChangeAdd.setOnClickListener {
            value_current_loc="Current_location"
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }

        searchEdt__ChangeAdd?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                search_value=s.toString()
                if (search_value!!.length>0){
                    search_iconImg_changeAdd.visibility= View.GONE
                    clear_searchImg_changeAdd.visibility= View.VISIBLE
                    search_add_rv_change.visibility= View.VISIBLE
                    add_ui_rrl.visibility= View.VISIBLE
                    searchAdd(search_value!!,token)
                }else{
                    search_add_rv_change.visibility= View.GONE
                    clear_searchImg_changeAdd.visibility= View.GONE
                    add_ui_rrl.visibility= View.GONE
                    search_iconImg_changeAdd.visibility= View.VISIBLE
                    searchAddList.clear()
                }

            }
        })

        clear_searchImg_changeAdd.setOnClickListener {
            searchAddList.clear()
            searchEdt__ChangeAdd.getText().clear()
            search_add_rv_change.visibility= View.GONE
            search_iconImg_changeAdd.visibility= View.GONE
            clear_searchImg_changeAdd.visibility= View.VISIBLE
            add_ui_rrl.visibility= View.VISIBLE
            addressesRecyclerView.visibility= View.VISIBLE
            searchAdd("",token)
        }
    }

    private fun searchAdd(searchValue: String,token:AutocompleteSessionToken) {
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
                            Log.i("predictionList___", predictionList!![i].getFullText(null).toString())
                            searchAddList.add(predictionList!![i].getFullText(null).toString())
                        }

                        try {
                            val s: Set<String> = LinkedHashSet<String>(searchAddList)
                            searchAddList.clear()
                            searchAddList.addAll(s)
                            //  Log.i("predictionList___d", searchAddList.get(0).toString())
                            searchAddRecyclerview(searchAddList)
                        }catch (e:Exception){

                        }

                    }
                } else {
                    Log.i("mytag", "prediction fetching task unsuccessful")
                }
            }

    }

    private fun searchAddRecyclerview(searchAddList: ArrayList<String>) {
        addressSearchAdapter = AddressSearchAdapter(this,searchAddList,object :AddressSearchAdapter.AddSearchClick{
            override fun onSelectAdd(pos: Int, value: String) {
                if (pos >= searchAddList!!.size) {
                    return
                }
                value_current_loc=""
                hideKeyboard()
                val resultIntent = Intent()
                resultIntent.putExtra("location",value);
                setResult(RESULT_OK, resultIntent)
                finish()
                AppUtils.finishActivityLeftToRight(this@ChangeAddressActivity)

            }

        })
        search_add_rv_change?.adapter=addressSearchAdapter
    }




    override fun onBackPressed() {
        finish()
        AppUtils.finishActivityLeftToRight(this)
    }
}