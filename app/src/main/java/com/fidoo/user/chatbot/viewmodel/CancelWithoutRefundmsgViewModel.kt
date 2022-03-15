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

class CancelWithoutRefundmsgViewModel (application: Application) : AndroidViewModel(application) {

    var CancelWithoutRefundmsgResponse: MutableLiveData<cancelWithoutRefundEmpty>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        CancelWithoutRefundmsgResponse =  MutableLiveData<cancelWithoutRefundEmpty>()
        failureResponse =  MutableLiveData<String>()

    }


    fun cancelWithoutRefundmsg(accountId: String, accessToken: String, orderId: String) {



            WebServiceClient.client.create(BackEndApi::class.java).cancelWithoutRefundmsgApi(
                accountId = accountId,
                accessToken = accessToken,
                orderId = orderId,
                //res = res

            )
                .enqueue(object : Callback<cancelWithoutRefundEmpty> {

                    override fun onResponse(
                        call: Call<cancelWithoutRefundEmpty>,
                        response: Response<cancelWithoutRefundEmpty>
                    ) {
                        CancelWithoutRefundmsgResponse?.value = response.body()


                    }

                    override fun onFailure(call: Call<cancelWithoutRefundEmpty>, t: Throwable) {

                        failureResponse?.value = "Something went wrong"
                    }
                })
        }
        }

