package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.LoginModel
import com.fidoo.user.fragments.SignInFragment
import com.fidoo.user.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application),
    Callback<LoginModel> {

    var btnSelected: ObservableBoolean? = null
    var mobileNo: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<LoginModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        btnSelected = ObservableBoolean(false)
        progressDialog = SingleLiveEvent<Boolean>()
        mobileNo = ObservableField("")
        failureResponse = MutableLiveData<String>()
        password = ObservableField("")
        userLogin = MutableLiveData<LoginModel>()
    }


    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        //   btnSelected?.set(Util.isEmailValid(s.toString()) && password?.get()!!.length >= 8)

    }

    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(s.toString().length >= 3)


    }

    fun login(countryCode: String, device_id: String, referral_Id: String,mobileNo: String) {
        Log.d(
            "login_parameter",
            "countryCode-$countryCode,mobileNo-$mobileNo,device_id-$device_id,referral_id-$referral_Id"
        )
        Log.d(
            "mobileNo__",
            mobileNo.toString()
        )

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).login(
            username = mobileNo,
            country_code = countryCode,
            device_id = device_id,
            device_type = "android",
            referred_by = referral_Id
        ).enqueue(this)
    }


    override fun onResponse(call: Call<LoginModel>?, response: Response<LoginModel>?) {
        progressDialog?.value = false
        userLogin?.value = response?.body()

    }

    override fun onFailure(call: Call<LoginModel>?, t: Throwable?) {
        progressDialog?.value = false
        failureResponse?.value = "Something went wrong"

    }

}