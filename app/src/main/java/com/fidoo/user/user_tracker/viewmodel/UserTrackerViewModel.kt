package com.fidoo.user.user_tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.user_tracker.model.UserTrackerModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserTrackerViewModel (application: Application) : AndroidViewModel(application),
    Callback<UserTrackerModel> {

    var customerActivityLog: MutableLiveData<UserTrackerModel>? = null

    init {
        customerActivityLog= MutableLiveData<UserTrackerModel>()
    }

    fun customerActivityLog(accountId:String?,user_name:String?,screen_name:String?,app_version :String?,service_type :String?,deviceId :String?){
        WebServiceClient.client.create(BackEndApi::class.java).customerActivityLogApi(
            accountId = accountId,
            user_name = user_name,
            screen_name = screen_name,
            app_version = app_version,
            service_type = service_type,
            deviceId = deviceId
        )
            .enqueue(this)
    }

    override fun onResponse(call: Call<UserTrackerModel>, response: Response<UserTrackerModel>) {
        customerActivityLog!!.value=response.body()
    }

    override fun onFailure(call: Call<UserTrackerModel>, t: Throwable) {}
}