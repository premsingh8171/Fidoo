package com.fidoo.user.store.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.CartCountModel
import com.fidoo.user.store.model.StoreListingModel
import com.google.gson.Gson
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
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(accountId = accountId, accessToken = accessToken)
            .enqueue(object : Callback<CartCountModel> {
                override fun onResponse(
                    call: Call<CartCountModel>,
                    response: Response<CartCountModel>
                ) {
                    cartCountResponse?.value = response.body()
                }

                override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun getStores(
        accountId: String,
        accessToken: String,
        service_id: String,
        latitude: String,
        longitude: String,
        distance_start: String?,
        distance_end: String?,
        sort_by: String?,
        cuisine_to_search: String?,
        page_count: String?
    ) {
        // progressDialog?.value = true
        Log.d(
            "storeList____",
            "$accountId--\n$accessToken--\n$service_id--\n$latitude--\n$longitude\n$distance_start--\n$distance_end--\n$sort_by--$cuisine_to_search--$page_count"
        )
        WebServiceClient.client.create(BackEndApi::class.java).getStoresApi(
            accountId = accountId,
            accessToken = accessToken,
            service_id = service_id,
            latitude = latitude,
            longitude = longitude,
            distance_start = distance_start,
            distance_end = distance_end,
            sort_by = sort_by,
            cuisine_to_search = cuisine_to_search,
            page_count = page_count

        )
            .enqueue(this)
    }

    override fun onResponse(call: Call<StoreListingModel>?, response: Response<StoreListingModel>?) {
        getStoresApi?.value = response?.body()
      //  Log.d("getStoresApi__",response?.body().toString())

    }

    override fun onFailure(call: Call<StoreListingModel>?, t: Throwable?) {
        failureResponse?.value = t.toString()
    }

//    fun listData(accountId: String,
//    accessToken: String,
//    service_id: String,
//    latitude: String,
//    longitude: String,
//    distance_start: String?,
//    distance_end: String?,
//    sort_by: String?,
//    cuisine_to_search: String?,
//    page_count: String?)
//    =
//        Pager(PagingConfig(pageSize = 6)) {
//            Store_dataSource(accountId, accessToken, service_id, latitude, longitude, distance_start, distance_end,
//            sort_by, cuisine_to_search)
//        }.flow.cachedIn(viewModelScope)


}