package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendPackagesViewModel(application: Application) : AndroidViewModel(application), Callback<PackageCatResponseModel> {

    var getcatResponse: MutableLiveData<PackageCatResponseModel>? = null
    var getPackageResponse: MutableLiveData<SendPackagesModel>? = null
    var sendPackagesResponse: MutableLiveData<SendPackageOrderDetailModel>? = null
    var paymentResponse: MutableLiveData<PaymentModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        getcatResponse = MutableLiveData<PackageCatResponseModel>()
        getPackageResponse = MutableLiveData<SendPackagesModel>()
        sendPackagesResponse = MutableLiveData<SendPackageOrderDetailModel>()
        paymentResponse = MutableLiveData<PaymentModel>()
        failureResponse = MutableLiveData<String>()

    }

    fun getPackageCatApi(accountId: String, accessToken: String) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).packageCategoriesApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(this)

    }

    fun paymentApi(accountId: String, accessToken: String, order_id: String, transaction_id: String, payment_bank: String, payment_mode: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).paymentApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            transaction_id = transaction_id,
            payment_bank = payment_bank,
            payment_mode = payment_mode
        )
            .enqueue(object : Callback<PaymentModel> {
                override fun onResponse(call: Call<PaymentModel>, response: Response<PaymentModel>) {
                    // progressDialog?.value = false
                    paymentResponse?.value = response.body()

                }

                override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong"
                }
            })
    }
    fun getPackageDetails(accountId: String, accessToken: String, package_distance: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getPackageDetails(
            accountId = accountId,
            accessToken = accessToken,
            package_distance = package_distance
        )
            .enqueue(object : Callback<SendPackagesModel> {

                override fun onResponse(call: Call<SendPackagesModel>, response: Response<SendPackagesModel>) {
                    // progressDialog?.value = false
                    getPackageResponse?.value = response.body()

                }

                override fun onFailure(call: Call<SendPackagesModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    fun sendPackageApi(
        accountId: String,
        accessToken: String,
        from_address: String,
        note: String,to_address: String,
        from_name: String,
        from_phone: String,
        to_name: String,
        to_phone: String ,
        payment_mode: String,
        package_distance: String,
        payment_amount: String,
        package_delivery_time: String,from_latitude: String,
        from_longitude: String,to_latitude: String,to_longitude: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).sendPackageApi(
            accountId = accountId,
            accessToken = accessToken,
            from_address = from_address,
            from_note = note,
            to_address = to_address,
            from_name = from_name,
            from_phone = from_phone,
            to_name = to_name,
            to_phone = to_phone,
            payment_mode = payment_mode,
            package_distance = package_distance,
            payment_amount = payment_amount,
            package_delivery_time = package_delivery_time,
            from_latitude = from_latitude,
            from_longitude = from_longitude,
            to_latitude = to_latitude,
            to_longitude = to_longitude

        )
            .enqueue(object : Callback<SendPackageOrderDetailModel> {

                override fun onResponse(call: Call<SendPackageOrderDetailModel>, response: Response<SendPackageOrderDetailModel>) {
                    // progressDialog?.value = false
                    sendPackagesResponse?.value = response.body()
                    Log.e("RESPONSE___", sendPackagesResponse?.value.toString())

                }

                override fun onFailure(call: Call<SendPackageOrderDetailModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong"
                }
            })
    }


    override fun onResponse(call: Call<PackageCatResponseModel>?, response: Response<PackageCatResponseModel>?) {
        getcatResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<PackageCatResponseModel>?, t: Throwable?) {
        Log.e("error",call.toString())
        Log.e("error 2",t!!.message.toString())
        failureResponse?.value="Something went wrong"
    }

}