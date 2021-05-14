package com.fidoo.user.ordermodule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.ordermodule.model.OrderDetailsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailsViewModel(application: Application) : AndroidViewModel(application), Callback<OrderDetailsModel> {

    var OrderDetailsResponse: MutableLiveData<OrderDetailsModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    init {
        failureResponse = MutableLiveData<String>()
        OrderDetailsResponse = MutableLiveData<OrderDetailsModel>()
    }


    fun getOrderDetails(accountId: String, accessToken: String, order_id: String?) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).orderDetailsApi(
                accountId = accountId, accessToken = accessToken, order_id = order_id
        ).enqueue(this)

    }

    override fun onResponse(call: Call<OrderDetailsModel>?, response: Response<OrderDetailsModel>?) {

        OrderDetailsResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<OrderDetailsModel>?, t: Throwable?) {

        failureResponse?.value="Something went wrong"

        Log.e("ERROR",""+t.toString())
    }

}