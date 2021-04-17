package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.SendPackagesModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendPackagesViewModel(application: Application) : AndroidViewModel(application), Callback<com.fidoo.user.data.model.PackageCatResponseModel> {

    var getcatResponse: MutableLiveData<com.fidoo.user.data.model.PackageCatResponseModel>? = null
    var getPackageResponse: MutableLiveData<com.fidoo.user.data.model.SendPackagesModel>? = null
    var sendPackagesResponse: MutableLiveData<com.fidoo.user.data.model.SendPackageOrderDetailModel>? = null
    var paymentResponse: MutableLiveData<com.fidoo.user.data.model.PaymentModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        getcatResponse = MutableLiveData<com.fidoo.user.data.model.PackageCatResponseModel>()
        getPackageResponse = MutableLiveData<com.fidoo.user.data.model.SendPackagesModel>()
        sendPackagesResponse = MutableLiveData<com.fidoo.user.data.model.SendPackageOrderDetailModel>()
        paymentResponse = MutableLiveData<com.fidoo.user.data.model.PaymentModel>()
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
            .enqueue(object : Callback<com.fidoo.user.data.model.PaymentModel> {
                override fun onResponse(call: Call<com.fidoo.user.data.model.PaymentModel>, response: Response<com.fidoo.user.data.model.PaymentModel>) {
                    // progressDialog?.value = false
                    paymentResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.PaymentModel>, t: Throwable) {
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

                override fun onResponse(call: Call<SendPackagesModel>, response: Response<com.fidoo.user.data.model.SendPackagesModel>) {
                    // progressDialog?.value = false
                    getPackageResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.SendPackagesModel>, t: Throwable) {
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
        package_delivery_time: String) {

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
            package_delivery_time = package_delivery_time

        )
            .enqueue(object : Callback<com.fidoo.user.data.model.SendPackageOrderDetailModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.SendPackageOrderDetailModel>, response: Response<com.fidoo.user.data.model.SendPackageOrderDetailModel>) {
                    // progressDialog?.value = false
                    sendPackagesResponse?.value = response.body()
                    Log.e("RESPONSE", sendPackagesResponse?.value.toString())

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.SendPackageOrderDetailModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong"
                }
            })
    }


    override fun onResponse(call: Call<com.fidoo.user.data.model.PackageCatResponseModel>?, response: Response<com.fidoo.user.data.model.PackageCatResponseModel>?) {
        getcatResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.PackageCatResponseModel>?, t: Throwable?) {
        Log.e("error",call.toString())
        Log.e("error 2",t!!.message.toString())
        failureResponse?.value="Something went wrong"
    }

}