package com.fidoo.user.chatbot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.chatbot.model.cancelWithoutRefundEmpty
import com.fidoo.user.chatbot.model.cancelWithoutRefundWithSendkey
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CancelWithoutRefundmsgwithKeyViewModel (application: Application) : AndroidViewModel(application) {

    var CancelWithoutRefundmsgResponseWithKey: MutableLiveData<cancelWithoutRefundWithSendkey>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        CancelWithoutRefundmsgResponseWithKey =  MutableLiveData<cancelWithoutRefundWithSendkey>()
        failureResponse =  MutableLiveData<String>()

    }


    fun cancelWithoutRefundmsgWithKey(accountId: String, accessToken: String, orderId: String, res : Int?) {


        if (res != null) {
            WebServiceClient.client.create(BackEndApi::class.java).cancelWithoutRefundmsgApiwithKey(
                accountId = accountId,
                accessToken = accessToken,
                orderId = orderId,
                res = res

            )
                .enqueue(object : Callback<cancelWithoutRefundWithSendkey> {

                    override fun onResponse(
                        call: Call<cancelWithoutRefundWithSendkey>,
                        response: Response<cancelWithoutRefundWithSendkey>
                    ) {
                        CancelWithoutRefundmsgResponseWithKey?.value = response.body()


                    }

                    override fun onFailure(call: Call<cancelWithoutRefundWithSendkey>, t: Throwable) {

                        failureResponse?.value = "Something went wrong"
                    }
                })
        }
    }
}


