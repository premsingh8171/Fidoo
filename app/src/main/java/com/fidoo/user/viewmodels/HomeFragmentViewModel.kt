package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragmentViewModel(application: Application) : AndroidViewModel(application), Callback<com.fidoo.user.data.model.BannerModel> {

    var btnSelected: ObservableBoolean? = null
    var mobileNo: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var bannersResponse: MutableLiveData<com.fidoo.user.data.model.BannerModel>? = null
    var homeServicesResponse: MutableLiveData<com.fidoo.user.data.model.HomeServicesModel>? = null
    var cartCountResponse: MutableLiveData<com.fidoo.user.data.model.CartCountModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    init {
        btnSelected = ObservableBoolean(false)
        progressDialog = SingleLiveEvent<Boolean>()
        failureResponse = MutableLiveData<String>()

        bannersResponse = MutableLiveData<com.fidoo.user.data.model.BannerModel>()
        homeServicesResponse = MutableLiveData<com.fidoo.user.data.model.HomeServicesModel>()
        cartCountResponse = MutableLiveData<com.fidoo.user.data.model.CartCountModel>()
    }

    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
     //   btnSelected?.set(Util.isEmailValid(s.toString()) && password?.get()!!.length >= 8)


    }

    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set( s.toString().length >=3)


    }

    fun getBanners(accountId: String, accessToken: String) {

   // progressDialog?.value = true
    WebServiceClient.client.create(BackEndApi::class.java).getBannersApi(
        accountId = accountId, accessToken = accessToken
    )
        .enqueue(this)

   }
    fun getHomeServices(accountId: String, accessToken: String) {

   // progressDialog?.value = true
    WebServiceClient.client.create(BackEndApi::class.java).getHomeServicesApi(
        accountId = accountId, accessToken = accessToken
    )
        .enqueue(object : Callback<com.fidoo.user.data.model.HomeServicesModel> {

            override fun onResponse(call: Call<com.fidoo.user.data.model.HomeServicesModel>, response: Response<com.fidoo.user.data.model.HomeServicesModel>) {
                progressDialog?.value = false
                homeServicesResponse?.value = response.body()

            }

            override fun onFailure(call: Call<com.fidoo.user.data.model.HomeServicesModel>, t: Throwable) {
                progressDialog?.value = false
                failureResponse?.value="Something went wrong"
            }
        })
   }
    fun getCartCountApi(accountId: String, accessToken: String) {
Log.e("cart count params",accountId+", "+accessToken)
   // progressDialog?.value = true
    WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
        accountId = accountId, accessToken = accessToken
    )
        .enqueue(object : Callback<com.fidoo.user.data.model.CartCountModel> {

            override fun onResponse(call: Call<com.fidoo.user.data.model.CartCountModel>, response: Response<com.fidoo.user.data.model.CartCountModel>) {
                progressDialog?.value = false
                cartCountResponse?.value = response.body()

            }

            override fun onFailure(call: Call<com.fidoo.user.data.model.CartCountModel>, t: Throwable) {
                progressDialog?.value = false
                failureResponse?.value="Something went wrong"
            }
        })
   }

    override fun onResponse(call: Call<com.fidoo.user.data.model.BannerModel>?, response: Response<com.fidoo.user.data.model.BannerModel>?) {
        progressDialog?.value = false
        bannersResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.BannerModel>?, t: Throwable?) {
        progressDialog?.value = false
        failureResponse?.value="Something went wrong"

    }

}