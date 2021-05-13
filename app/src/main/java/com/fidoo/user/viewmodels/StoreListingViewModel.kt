package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreListingViewModel(application: Application) : AndroidViewModel(application), Callback<StoreListingModel> {

    var getStoresApi: MutableLiveData<StoreListingModel>? = null
    var cartCountResponse: MutableLiveData<CartCountModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        getStoresApi = MutableLiveData<StoreListingModel>()
        cartCountResponse = MutableLiveData<CartCountModel>()
        failureResponse = MutableLiveData<String>()
    }

    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cart count params", "$accountId, $accessToken")
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<CartCountModel> {

                override fun onResponse(call: Call<CartCountModel>, response: Response<CartCountModel>) {

                    cartCountResponse?.value = response.body()

                }

                override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    fun getStores(accountId: String, accessToken: String, service_id: String, latitude: String, longitude: String, distance_start: String?, distance_end: String?,sort_by:String?) {
        // progressDialog?.value = true
        //Log.d("storeList____",accountId+"--\n"+accessToken+"--\n"+service_id+"--\n"+latitude+"--\n"+distance_start+"--\n"+distance_end+"--\n"+sort_by)
        WebServiceClient.client.create(BackEndApi::class.java).getStoresApi(
            accountId = accountId,
            accessToken = accessToken,
            service_id = service_id,
            latitude = latitude,
            longitude = longitude,
            distance_start = distance_start,
            distance_end = distance_end,
            sort_by=sort_by

        )
            .enqueue(this)
    }

    override fun onResponse(call: Call<StoreListingModel>?, response: Response<StoreListingModel>?) {
        getStoresApi?.value = response?.body()

    }

    override fun onFailure(call: Call<StoreListingModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"
    }

}