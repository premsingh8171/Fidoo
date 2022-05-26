package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.ordermodule.NewOrderTrackModule.RestaurantsCase.NewTrackModel.NewTrackModel
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.sendPackageModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModelSendPackage (application: Application) : AndroidViewModel(application){

    var newTrackViewModel: MutableLiveData<sendPackageModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        newTrackViewModel =  MutableLiveData<sendPackageModel>()
        failureResponse =  MutableLiveData<String>()

    }


    fun sendpackageNewTrackScreenApiCall(accountId: String, accessToken: String, orderId: String) {
        Log.d("trackReapp", "accid-${accountId}   acctkn-${accessToken}   ordrid-${orderId}")

        WebServiceClient.client.create(BackEndApi::class.java).SendPackageNewTrackScreenApi(
            accountId = accountId,
            accessToken = accessToken,
            orderId = orderId

        )
            .enqueue(object : Callback<sendPackageModel> {

                override fun onResponse(
                    call: Call<sendPackageModel>,
                    response: Response<sendPackageModel>
                ) {
                    newTrackViewModel?.value = response.body()
                    Log.d("sddffsddskgjgjds", Gson().toJson(response.body()))


                }

                override fun onFailure(call: Call<sendPackageModel>, t: Throwable) {

                    failureResponse?.value = "Something went wrong"
                }
            })
    }

}

