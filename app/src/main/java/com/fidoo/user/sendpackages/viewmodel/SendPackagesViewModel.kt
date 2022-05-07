package com.fidoo.user.sendpackages.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.PackageCatResponseModel
import com.fidoo.user.data.model.PaymentModel
import com.fidoo.user.sendpackages.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendPackagesViewModel(application: Application) : AndroidViewModel(application),
    Callback<PackageCatResponseModel> {

    var getcatResponse: MutableLiveData<PackageCatResponseModel>? = null
    var uploadSendPackagesResponse: MutableLiveData<SendPackagesUploadImageModel>? = null
    var getPackageResponse: MutableLiveData<SendPackagesModel>? = null
    var sendPackagesResponse: MutableLiveData<SendPackageOrderDetailModel>? = null
    var paymentResponse: MutableLiveData<PaymentModel>? = null
    var catResponse: MutableLiveData<SendPackagesCatModel>? = null
    var deleteSendPackagesResponse: MutableLiveData<DeleteSendPackagesImgModel>? = null
    var sendPackagePaymentModeResponse: MutableLiveData<SendPackagePaymentModeModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        uploadSendPackagesResponse = MutableLiveData<SendPackagesUploadImageModel>()
        getcatResponse = MutableLiveData<PackageCatResponseModel>()
        getPackageResponse = MutableLiveData<SendPackagesModel>()
        sendPackagesResponse = MutableLiveData<SendPackageOrderDetailModel>()
        paymentResponse = MutableLiveData<PaymentModel>()
        catResponse = MutableLiveData<SendPackagesCatModel>()
        deleteSendPackagesResponse = MutableLiveData<DeleteSendPackagesImgModel>()
        sendPackagePaymentModeResponse = MutableLiveData<SendPackagePaymentModeModel>()
        failureResponse = MutableLiveData<String>()
    }

    fun getPackageCatApi(accountId: String, accessToken: String) {
        WebServiceClient.client.create(BackEndApi::class.java).packageCategoriesApi(
            accountId = accountId, accessToken = accessToken
        ).enqueue(this)
    }

    fun getPackageCatApi2(accountId: String, accessToken: String) {
        WebServiceClient.client.create(BackEndApi::class.java).packageCategoriesApi2(
            accountId = accountId, accessToken = accessToken
        ).enqueue(object : Callback<SendPackagesCatModel> {
            override fun onResponse(
                call: Call<SendPackagesCatModel>,
                response: Response<SendPackagesCatModel>
            ) {
                catResponse?.value = response.body()

            }

            override fun onFailure(call: Call<SendPackagesCatModel>, t: Throwable) {
                failureResponse?.value = "Something went wrong"
            }
        })
    }


    fun sendPackagePaymentModeApi(accountId: String, accessToken: String) {
        WebServiceClient.client.create(BackEndApi::class.java).getSendPackagePayModeStatusApi(
            accountId = accountId, accessToken = accessToken
        ).enqueue(object : Callback<SendPackagePaymentModeModel> {
            override fun onResponse(
                call: Call<SendPackagePaymentModeModel>,
                response: Response<SendPackagePaymentModeModel>
            ) {
                sendPackagePaymentModeResponse?.value = response.body()

            }

            override fun onFailure(call: Call<SendPackagePaymentModeModel>, t: Throwable) {
                failureResponse?.value = "Something went wrong"
            }
        })
    }

    fun paymentApi(
        accountId: String,
        accessToken: String,
        order_id: String,
        transaction_id: String,
        payment_bank: String,
        payment_mode: String,
        other_taxes_and_charges: String
    ) {
        Log.d(
            "paymentApi___",
            accountId + "\n" + accessToken + "\n" + order_id + "\n" + transaction_id + "\n" + payment_bank + "\n" + payment_mode+ "\n" + other_taxes_and_charges
        )
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).paymentApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            transaction_id = transaction_id,
            payment_bank = payment_bank,
            payment_mode = payment_mode,
            other_taxes_and_charges = other_taxes_and_charges
        )
            .enqueue(object : Callback<PaymentModel> {
                override fun onResponse(
                    call: Call<PaymentModel>,
                    response: Response<PaymentModel>
                ) {
                    // progressDialog?.value = false
                    paymentResponse?.value = response.body()

                }

                override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun getPackageDetails(accountId: String, accessToken: String, package_distance: String) {
        WebServiceClient.client.create(BackEndApi::class.java).getPackageDetails(
            accountId = accountId,
            accessToken = accessToken,
            package_distance = package_distance
        )
            .enqueue(object : Callback<SendPackagesModel> {
                override fun onResponse(
                    call: Call<SendPackagesModel>,
                    response: Response<SendPackagesModel>
                ) {
                    getPackageResponse?.value = response.body()
                }

                override fun onFailure(call: Call<SendPackagesModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun sendPackageApi(
        accountId: String,
        accessToken: String,
        from_address: String,
        note: String,
        to_address: String,
        from_name: String,
        from_phone: String,
        to_name: String,
        to_phone: String,
        payment_mode: String,
        package_distance: String,
        payment_amount: String,
        package_delivery_time: String, from_latitude: String,
        from_longitude: String, to_latitude: String, to_longitude: String,
        category_id: String, item_name: String, images: String, tax: String
    ) {

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
            to_longitude = to_longitude,
            category_id = category_id,
            item_name = item_name,
            images = images ,
            tax = tax

        ).enqueue(object : Callback<SendPackageOrderDetailModel> {

                override fun onResponse(
                    call: Call<SendPackageOrderDetailModel>,
                    response: Response<SendPackageOrderDetailModel>
                ) {
                    sendPackagesResponse?.value = response.body()
                    Log.e("RESPONSE___", sendPackagesResponse?.value.toString())
                }

                override fun onFailure(call: Call<SendPackageOrderDetailModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    override fun onResponse(
        call: Call<PackageCatResponseModel>?,
        response: Response<PackageCatResponseModel>?
    ) {
        getcatResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<PackageCatResponseModel>?, t: Throwable?) {
        Log.e("error", call.toString())
        Log.e("error 2", t!!.message.toString())
        failureResponse?.value = "Something went wrong"
    }

    fun uploadSendPackagesImage(
        accountId: RequestBody?,
        accessToken: RequestBody?,
        image: MultipartBody.Part?
    ) {
        Log.e("uploadSendPackagesImage", "$accountId---$accessToken---$image")

        val call: Call<SendPackagesUploadImageModel> =
            WebServiceClient.client.create(BackEndApi::class.java)
                .uploadSendPackagesImage(accountId, accessToken, image)

        call.enqueue(object : Callback<SendPackagesUploadImageModel> {
            override fun onResponse(
                call: Call<SendPackagesUploadImageModel>,
                response: Response<SendPackagesUploadImageModel>
            ) {
                uploadSendPackagesResponse?.value = response.body()
            }

            override fun onFailure(
                call: Call<SendPackagesUploadImageModel>,
                t: Throwable
            ) {
                failureResponse?.value = "Something went wrong in sendPackage image"
                Log.e("ddddd", t.message.toString())
            }
        })


    }

    fun deleteSendPackagesApi(accountId: String, accessToken: String, document_id: String) {
        Log.d("deleteSendPackagesApi", "$accountId--+$accessToken--$document_id")
        WebServiceClient.client.create(BackEndApi::class.java).deleteSendPackagesImgApi(
            accountId = accountId, accessToken = accessToken, document_id = document_id
        )
            .enqueue(object : Callback<DeleteSendPackagesImgModel> {

                override fun onResponse(
                    call: Call<DeleteSendPackagesImgModel>,
                    response: Response<DeleteSendPackagesImgModel>
                ) {
                    deleteSendPackagesResponse?.value = response.body()
                }

                override fun onFailure(call: Call<DeleteSendPackagesImgModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong in deleting cart"
                }
            })
    }
}