package com.fidoo.user.dashboard.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.cartview.activity.CartActivity.Companion.accessToken
import com.fidoo.user.cartview.activity.CartActivity.Companion.accountId
import com.fidoo.user.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeNewUiFragmentRepo {
    var addrListModel : MutableLiveData<GetAddressModel>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        addrListModel = MutableLiveData<GetAddressModel>()
        progressDialog = SingleLiveEvent<Boolean>()
        failureResponse = MutableLiveData<String>()
    }
    fun getAddressList() : MutableLiveData<GetAddressModel>{
        WebServiceClient.client.create(BackEndApi::class.java).getAddressesApi(
            accountId = accountId,
            accessToken = accessToken,
            storeLatitude = "",
            storeLongitude = ""
        ).enqueue(object : Callback<GetAddressModel> {
            override fun onResponse(
                call: Call<GetAddressModel>,
                response: Response<GetAddressModel>
            ) {
          //      progressDialog?.value = false
                addrListModel?.postValue(response.body())
            }

            override fun onFailure(call: Call<GetAddressModel>, t: Throwable) {
                failureResponse?.value = "Something went wrong"
            }
        })

        return addrListModel!!
    }
}