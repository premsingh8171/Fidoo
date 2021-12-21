package com.fidoo.user.referral.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.referral.model.ReferralModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReferralViewModel(application: Application) : AndroidViewModel(application) {
    var referralModelRes: MutableLiveData<ReferralModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        referralModelRes = MutableLiveData<ReferralModel>()
        failureResponse = MutableLiveData<String>()
    }

    fun referralApi(accountId: String, accessToken: String) {

        WebServiceClient.client.create(BackEndApi::class.java).getReferralApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<ReferralModel> {

                override fun onResponse(
                    call: Call<ReferralModel>,
                    response: Response<ReferralModel>
                ) {
                    referralModelRes?.value = response.body()

                }

                override fun onFailure(call: Call<ReferralModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }
}