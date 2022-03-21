package com.fidoo.user.chatbot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.OrderStatusResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderStatusViewModel (application: Application) : AndroidViewModel(application) {

    var orderStatusViewModel: MutableLiveData<OrderStatusResponse>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        orderStatusViewModel =  MutableLiveData<OrderStatusResponse>()
        failureResponse =  MutableLiveData<String>()

    }


    fun cancelOrderApi(accountId: String, accessToken: String, orderId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (orderId != null) {
                WebServiceClient.client.create(BackEndApi::class.java).oredrStatusApi(
                    accountId = accountId,
                    accessToken = accessToken,
                    orderId = orderId

                )
                    .enqueue(object : Callback<OrderStatusResponse> {

                        override fun onResponse(
                            call: Call<OrderStatusResponse>,
                            response: Response<OrderStatusResponse>
                        ) {
                            orderStatusViewModel?.value = response.body()


                        }

                        override fun onFailure(call: Call<OrderStatusResponse>, t: Throwable) {

                            failureResponse?.value = "Something went wrong"
                        }
                    })
            }
        }
    }

}
