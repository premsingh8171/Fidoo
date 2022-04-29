package com.fidoo.user.ordermodule.ui.NewOrderTrackModule.NewTrackViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.NewTrackModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewOrderViewModel (application: Application) : AndroidViewModel(application) {

    var newTrackViewModel: MutableLiveData<NewTrackModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        newTrackViewModel =  MutableLiveData<NewTrackModel>()
        failureResponse =  MutableLiveData<String>()

    }


    fun NewTrackScreenApiCall(accountId: String, accessToken: String, orderId: String) {

        WebServiceClient.client.create(BackEndApi::class.java).NewTrackScreenApi(
                accountId = accountId,
                accessToken = accessToken,
                orderId = orderId

            )
                .enqueue(object : Callback<NewTrackModel> {

                    override fun onResponse(
                        call: Call<NewTrackModel>,
                        response: Response<NewTrackModel>
                    ) {
                        newTrackViewModel?.value = response.body()
                        Log.d("sddffsddskgjgjds", Gson().toJson(response.body()))


                    }

                    override fun onFailure(call: Call<NewTrackModel>, t: Throwable) {

                        failureResponse?.value = "Something went wrong"
                    }
                })
        }

}
