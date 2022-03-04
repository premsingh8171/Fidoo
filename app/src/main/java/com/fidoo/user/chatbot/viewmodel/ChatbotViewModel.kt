package com.fidoo.user.chatbot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Models.cancelStatus.ChatCancelModel
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.DeleteModel


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatbotViewModel (application: Application) : AndroidViewModel(application) {

    var cancelOrderResponse:MutableLiveData<DeleteModel>? = null
    var failureResponse:MutableLiveData<String>? = null

    init {
        cancelOrderResponse =  MutableLiveData<DeleteModel>()
        failureResponse =  MutableLiveData<String>()

    }


    fun cancelOrderApiChatBot(accountId: String, accessToken: String, orderId: String) {

        WebServiceClient.client.create(BackEndApi::class.java).cancelOrderApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = orderId

        )
            .enqueue(object : Callback<DeleteModel> {

                override fun onResponse(call: Call<DeleteModel>, response: Response<DeleteModel>) {
                    cancelOrderResponse?.value = response.body()

                }

                override fun onFailure(call: Call<DeleteModel>, t: Throwable) {

                    failureResponse?.value="Something went wrong"
                }
            })
    }

}
