package com.fidoo.user.ordermodule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.ordermodule.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailsViewModel(application: Application) : AndroidViewModel(application), Callback<OrderDetailsModel> {

    var OrderDetailsResponse: MutableLiveData<OrderDetailsModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    var reviewResponse: MutableLiveData<ReviewModel>? = null
    var sendPackageOrderDetailsRes: MutableLiveData<SendPackageOrderDetailsModel>? = null
    var productChangeRequestDetailsRes: MutableLiveData<ProductChangeRequestDetailsModel>? = null
    var approveProductChangeRequestRes: MutableLiveData<ApproveProductChangeRequestModel>? = null
    var sendPackageOrderDetailsMainModelRes: MutableLiveData<SendPackageOrderDetailsMainModel>? = null

    init {
        failureResponse = MutableLiveData<String>()
        OrderDetailsResponse = MutableLiveData<OrderDetailsModel>()
        reviewResponse= MutableLiveData<ReviewModel>()
        sendPackageOrderDetailsRes= MutableLiveData<SendPackageOrderDetailsModel>()
        productChangeRequestDetailsRes = MutableLiveData<ProductChangeRequestDetailsModel>()
        approveProductChangeRequestRes = MutableLiveData<ApproveProductChangeRequestModel>()
        sendPackageOrderDetailsMainModelRes = MutableLiveData<SendPackageOrderDetailsMainModel>()

    }


    fun getOrderDetails(accountId: String, accessToken: String, order_id: String?) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).orderDetailsApi(
                accountId = accountId, accessToken = accessToken, order_id = order_id
        ).enqueue(this)

    }

    override fun onResponse(call: Call<OrderDetailsModel>?, response: Response<OrderDetailsModel>?) {
        OrderDetailsResponse?.value = response?.body()
    }

    fun productChangeRequestDetailsApi(
        accountId: String,
        accessToken: String,
        request_id: String?
    ) {
        Log.e("sendPackageOrderDetailsApi_", "$accountId--$accessToken--$request_id")
        WebServiceClient.client.create(BackEndApi::class.java).productChangeRequestDetailsApi(
            accountId = accountId,
            accessToken = accessToken,
            request_id = request_id
        )
            .enqueue(object : Callback<ProductChangeRequestDetailsModel> {

                override fun onResponse(
                    call: Call<ProductChangeRequestDetailsModel>,
                    response: Response<ProductChangeRequestDetailsModel>
                ) {

                    productChangeRequestDetailsRes?.value = response.body()

                }

                override fun onFailure(call: Call<ProductChangeRequestDetailsModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun approveProductChangeRequestApi(
        accountId: String,
        accessToken: String,
        request_id: String?,
        order_id: String?
    ) {
        Log.e("approveProductChangeRequestApi", "$accountId--$accessToken--$request_id--$order_id")
        WebServiceClient.client.create(BackEndApi::class.java).approveProductChangeRequestApi(
            accountId = accountId,
            accessToken = accessToken,
            request_id = request_id,
            order_id = order_id
        )
            .enqueue(object : Callback<ApproveProductChangeRequestModel> {

                override fun onResponse(
                    call: Call<ApproveProductChangeRequestModel>,
                    response: Response<ApproveProductChangeRequestModel>
                ) {
                    approveProductChangeRequestRes?.value = response.body()
                }

                override fun onFailure(call: Call<ApproveProductChangeRequestModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }


    fun reviewSubmitApi(
        accountId: String,
        accessToken: String,
        order_id: String?,
        store_rating: String?,
        store_review: String?,
        delivery_rating: String?,
        delivery_review: String?
    ) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).reviewSubmitApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            store_rating = store_rating,
            store_review = store_review,
            delivery_rating = delivery_rating,
            delivery_review = delivery_review
        )
            .enqueue(object : Callback<ReviewModel> {

                override fun onResponse(call: Call<ReviewModel>, response: Response<ReviewModel>) {

                    reviewResponse?.value = response.body()

                }

                override fun onFailure(call: Call<ReviewModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    fun sendPackageOrderDetailsApi(
        accountId: String,
        accessToken: String,
        order_id: String?
    ) {
        Log.e("sendPackageOrderDetailsApi_","$accountId--$accessToken--$order_id")
        WebServiceClient.client.create(BackEndApi::class.java).sendPackageOrderDetailsApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id
        )
            .enqueue(object : Callback<SendPackageOrderDetailsModel> {

                override fun onResponse(call: Call<SendPackageOrderDetailsModel>, response: Response<SendPackageOrderDetailsModel>) {

                    sendPackageOrderDetailsRes?.value = response.body()

                }

                override fun onFailure(call: Call<SendPackageOrderDetailsModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    override fun onFailure(call: Call<OrderDetailsModel>?, t: Throwable?) {

        failureResponse?.value="Something went wrong"

        Log.e("ERROR",""+t.toString())
    }

}