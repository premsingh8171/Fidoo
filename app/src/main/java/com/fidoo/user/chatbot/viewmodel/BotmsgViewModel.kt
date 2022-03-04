package com.fidoo.user.chatbot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Models.cancelStatus.orderStatusMSg.OrderStatusMsg
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BotmsgViewModel (application: Application) : AndroidViewModel(application) {

    var botMsgViewModel: MutableLiveData<List<String>>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        botMsgViewModel =  MutableLiveData<List<String>>()
        failureResponse =  MutableLiveData<String>()

    }


    fun botmsgapi(accountId: String, accessToken: String, orderId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            WebServiceClient.client.create(BackEndApi::class.java).botStatusMsgApi(
                accountId = accountId,
                accessToken = accessToken,
                orderId = orderId

            )
                .enqueue(object : Callback<OrderStatusMsg> {

                    override fun onResponse(
                        call: Call<OrderStatusMsg>,
                        response: Response<OrderStatusMsg>
                    ) {
                        botMsgViewModel?.value = response.body()?.messages


                    }

                    override fun onFailure(call: Call<OrderStatusMsg>, t: Throwable) {

                        failureResponse?.value = "Something went wrong"
                    }
                })
        }
    }
}
