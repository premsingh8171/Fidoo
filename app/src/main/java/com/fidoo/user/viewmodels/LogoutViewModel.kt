package com.fidoo.user.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.LogoutModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutViewModel(application: Application) : AndroidViewModel(application), Callback<LogoutModel> {

    var getlogoutResponse: MutableLiveData<LogoutModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {

        failureResponse = MutableLiveData<String>()
        getlogoutResponse = MutableLiveData<LogoutModel>()

    }

    fun logoutapi(accountId: String, accessToken: String) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).logoutApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(this)

    }

    override fun onResponse(call: Call<LogoutModel>?, response: Response<LogoutModel>?) {
        getlogoutResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<LogoutModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"
    }

}