package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreListingViewModel(application: Application) : AndroidViewModel(application), Callback<com.fidoo.user.data.model.StoreListingModel> {

    var getStoresApi: MutableLiveData<com.fidoo.user.data.model.StoreListingModel>? = null
    var cartCountResponse: MutableLiveData<com.fidoo.user.data.model.CartCountModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        getStoresApi = MutableLiveData<com.fidoo.user.data.model.StoreListingModel>()
        cartCountResponse = MutableLiveData<com.fidoo.user.data.model.CartCountModel>()
        failureResponse = MutableLiveData<String>()
    }


    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cart count params", "$accountId, $accessToken")
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.CartCountModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.CartCountModel>, response: Response<com.fidoo.user.data.model.CartCountModel>) {

                    cartCountResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.CartCountModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }


    fun getStores(accountId: String, accessToken: String, service_id: String, latitude: String, longitude: String, distance_start: String?, distance_end: String?) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getStoresApi(
            accountId = accountId,
            accessToken = accessToken,
            service_id = service_id,
            latitude = latitude,
            longitude = longitude,
            distance_start = distance_start,
            distance_end = distance_end
        )
            .enqueue(this)
    }

    override fun onResponse(call: Call<com.fidoo.user.data.model.StoreListingModel>?, response: Response<com.fidoo.user.data.model.StoreListingModel>?) {
        getStoresApi?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.StoreListingModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"
    }

}