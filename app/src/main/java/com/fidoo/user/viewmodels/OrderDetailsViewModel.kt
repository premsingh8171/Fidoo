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

class OrderDetailsViewModel(application: Application) : AndroidViewModel(application), Callback<com.fidoo.user.data.model.OrderDetailsModel> {


    var OrderDetailsResponse: MutableLiveData<com.fidoo.user.data.model.OrderDetailsModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    init {
        failureResponse = MutableLiveData<String>()
        OrderDetailsResponse = MutableLiveData<com.fidoo.user.data.model.OrderDetailsModel>()
    }


    fun getOrderDetails(accountId: String, accessToken: String, order_id: String?) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).orderDetailsApi(
                accountId = accountId, accessToken = accessToken, order_id = order_id
        ).enqueue(this)

    }

    override fun onResponse(call: Call<com.fidoo.user.data.model.OrderDetailsModel>?, response: Response<com.fidoo.user.data.model.OrderDetailsModel>?) {

        OrderDetailsResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.OrderDetailsModel>?, t: Throwable?) {

        failureResponse?.value="Something went wrong"

        Log.e("ERROR",""+t.toString())
    }

}