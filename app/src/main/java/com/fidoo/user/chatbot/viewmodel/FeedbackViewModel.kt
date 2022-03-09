package com.fidoo.user.chatbot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.chatbot.model.cancelWithoutRefundEmpty
import com.fidoo.user.chatbot.model.feedBackModel.FeedbackModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FeedbackViewModel (application: Application) : AndroidViewModel(application) {

    var FeedbackViewModelResponse: MutableLiveData<FeedbackModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        FeedbackViewModelResponse =  MutableLiveData<FeedbackModel>()
        failureResponse =  MutableLiveData<String>()

    }


    fun FeedbackViewModelMsg(accountId: String, accessToken: String, orderId: String, res : Int?) {


        if (res != null) {
            WebServiceClient.client.create(BackEndApi::class.java).FeedbackApi(
                accountId = accountId,
                accessToken = accessToken,
                orderId = orderId,
                res = res

            )
                .enqueue(object : Callback<FeedbackModel> {

                    override fun onResponse(
                        call: Call<FeedbackModel>,
                        response: Response<FeedbackModel>
                    ) {
                        FeedbackViewModelResponse?.value = response.body()


                    }

                    override fun onFailure(call: Call<FeedbackModel>, t: Throwable) {

                        failureResponse?.value = "Something went wrong"
                    }
                })
        }
    }
}

