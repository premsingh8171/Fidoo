package com.fidoo.user.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.profile.EditProfileModel
import com.fidoo.user.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application),
    Callback<EditProfileModel> {

    var btnSelected: ObservableBoolean? = null
    var mobileNo: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<EditProfileModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        btnSelected = ObservableBoolean(false)
        progressDialog = SingleLiveEvent<Boolean>()
        mobileNo = ObservableField("")
        failureResponse = MutableLiveData<String>()
        password = ObservableField("")
        userLogin = MutableLiveData<EditProfileModel>()
    }


    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        //   btnSelected?.set(Util.isEmailValid(s.toString()) && password?.get()!!.length >= 8)

    }

    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(s.toString().length >= 3)


    }

    fun login(countryCode: String, device_id: String) {


        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).login(
            username = mobileNo?.get(),
            country_code = countryCode,
            device_id = device_id,
            device_type = "android"
        ).enqueue(this)
    }


    override fun onResponse(call: Call<EditProfileModel>?, response: Response<EditProfileModel>?) {
        progressDialog?.value = false
        userLogin?.value = response?.body()

    }

    override fun onFailure(call: Call<EditProfileModel>?, t: Throwable?) {
        progressDialog?.value = false
        failureResponse?.value = "Something went wrong"

    }

}