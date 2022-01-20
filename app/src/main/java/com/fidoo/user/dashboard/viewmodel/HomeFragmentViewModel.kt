package com.fidoo.user.dashboard.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.dashboard.model.CheckPaymentStatusModel
import com.fidoo.user.dashboard.model.HomeServicesModel
import com.fidoo.user.dashboard.model.newmodel.ServiceDetailsModel
import com.fidoo.user.data.model.BannerModel
import com.fidoo.user.data.model.CartCountModel
import com.fidoo.user.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragmentViewModel(application: Application) : AndroidViewModel(application),
    Callback<BannerModel> {

    var btnSelected: ObservableBoolean? = null
    var mobileNo: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var bannersResponse: MutableLiveData<BannerModel>? = null
    var homeServicesResponse: MutableLiveData<HomeServicesModel>? = null
    var homeDataResponse: MutableLiveData<ServiceDetailsModel>? = null
    var cartCountResponse: MutableLiveData<CartCountModel>? = null
    var checkPaymentStatusRes: MutableLiveData<CheckPaymentStatusModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        btnSelected = ObservableBoolean(false)
        progressDialog = SingleLiveEvent<Boolean>()
        failureResponse = MutableLiveData<String>()
        homeDataResponse = MutableLiveData<ServiceDetailsModel>()
        bannersResponse = MutableLiveData<BannerModel>()
        homeServicesResponse = MutableLiveData<HomeServicesModel>()
        checkPaymentStatusRes = MutableLiveData<CheckPaymentStatusModel>()
        cartCountResponse = MutableLiveData<CartCountModel>()
    }

    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        //   btnSelected?.set(Util.isEmailValid(s.toString()) && password?.get()!!.length >= 8)
    }

    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(s.toString().length >= 3)
    }

    fun getBanners(accountId: String, accessToken: String, isNewApp: String) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getBannersApi(
            accountId = accountId, accessToken = accessToken, isNewApp = isNewApp
        ).enqueue(this)
    }

    fun getHomeServices(accountId: String, accessToken: String) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getHomeServicesApi(
            accountId = accountId, accessToken = accessToken
        ).enqueue(object : Callback<HomeServicesModel> {
            override fun onResponse(
                    call: Call<HomeServicesModel>,
                    response: Response<HomeServicesModel>
                ) {
                    progressDialog?.value = false
                    homeServicesResponse?.value = response.body()
                }

                override fun onFailure(call: Call<HomeServicesModel>, t: Throwable) {
                    progressDialog?.value = false
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun getHomeDataApi(
        accountId: String,
        accessToken: String,
        user_lat: String,
        user_long: String
    ) {

       // Log.e("getHomeDataApi", "$accountId--$accessToken--$user_lat--$user_long")
        WebServiceClient.client.create(BackEndApi::class.java).gethomeDataApi(
            accountId = accountId,
            accessToken = accessToken,
            user_lat = user_lat,
            user_long = user_long
        ).enqueue(object : Callback<ServiceDetailsModel> {

                override fun onResponse(
                    call: Call<ServiceDetailsModel>,
                    response: Response<ServiceDetailsModel>
                ) {
                    progressDialog?.value = false
                    homeDataResponse?.value = response.body()

                }

                override fun onFailure(call: Call<ServiceDetailsModel>, t: Throwable) {
                    progressDialog?.value = false
                    failureResponse?.value = "Something went wrong"
                }
            })

    }

    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cart_count_params", "$accountId, $accessToken")
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<CartCountModel> {

                override fun onResponse(
                    call: Call<CartCountModel>,
                    response: Response<CartCountModel>
                ) {
                    progressDialog?.value = false
                    cartCountResponse?.value = response.body()

                }

                override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                    progressDialog?.value = false
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun checkPaymentStatusApi(accountId: String, accessToken: String) {
        Log.e("checkPaymentStatus__", "$accountId, $accessToken")
        WebServiceClient.client.create(BackEndApi::class.java).checkPaymentStatusApi(
            accountId = accountId, accessToken = accessToken
        ).enqueue(object : Callback<CheckPaymentStatusModel> {
            override fun onResponse(
                    call: Call<CheckPaymentStatusModel>,
                    response: Response<CheckPaymentStatusModel>
                ) {
                    progressDialog?.value = false
                    checkPaymentStatusRes?.value = response.body()
                }

                override fun onFailure(call: Call<CheckPaymentStatusModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    override fun onResponse(call: Call<BannerModel>?, response: Response<BannerModel>?) {
        progressDialog?.value = false
        bannersResponse?.value = response?.body()
    }

    override fun onFailure(call: Call<BannerModel>?, t: Throwable?) {
        progressDialog?.value = false
        failureResponse?.value = "Something went wrong"
    }

}