package com.fidoo.user


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.adapter.StoreAdapter
import com.fidoo.user.data.model.*
import com.fidoo.user.data.session.*
import com.fidoo.user.utils.showAlertDialog
import com.fidoo.user.viewmodels.StoreListingViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_store_list.*


class StoreListActivity : com.fidoo.user.utils.BaseActivity() {

    var storeListingViewModel: StoreListingViewModel? = null
    var storeList: MutableList<StoreListingModel.StoreList>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_list)
        val mainText = "<span style=\"color:#339347\">Choose </span> <span>Your <br/>Favorite</span> <span style=\"color:#339347\">Store</span></string>"
        tv_choose_your_fav_store.text = HtmlCompat.fromHtml(
            mainText,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        storeListingViewModel = ViewModelProviders.of(this).get(StoreListingViewModel::class.java)


        backIcon.setOnClickListener { finish() }

        intent.getStringExtra("serviceId")?.let {
            if (isNetworkConnected) {
                if (SessionTwiclo(this).isLoggedIn){
                    storeListingViewModel!!.getStores(
                       SessionTwiclo(this).loggedInUserDetail.accountId,
                       SessionTwiclo(this).loggedInUserDetail.accessToken,
                        it,
                       SessionTwiclo(this).userLat,
                       SessionTwiclo(this).userLng,
                        "",
                        ""
                    )
                }else{
                    storeListingViewModel!!.getStores(
                        "",
                        "",
                        it,
                       SessionTwiclo(this).userLat,
                       SessionTwiclo(this).userLng,
                        "",
                        ""
                    )
                }

            } else {
                showInternetToast()
            }

        }


        storeListingViewModel?.getStoresApi?.observe(this, Observer { user ->
            linear_progress_indicator.visibility = View.GONE

            if (!user.error) {
                //val storeLat = user.storeList[0].storeLat
                //val storeLong = user.storeList[0].storeLong
                //calculateEstimatedDeliveryTime(storeLat, storeLong)

                Log.e("stores response", Gson().toJson(user))
                val mModelData: StoreListingModel = user
                //mDistanceStartTemp = mModelData.distance_start
                //mDistanceEndTemp = mModelData.distance_end
                storeList = mModelData.storeList
                val adapter = StoreAdapter(this, storeList!!)
                storesRecyclerView.layoutManager = LinearLayoutManager(this)
                storesRecyclerView.setHasFixedSize(true)
                storesRecyclerView.adapter = adapter

            } else {
                if (user.errorCode == 101) {
                    showAlertDialog(this)
                }
            }
            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })
    }
}