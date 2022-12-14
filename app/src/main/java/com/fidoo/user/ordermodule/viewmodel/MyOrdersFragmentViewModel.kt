package com.fidoo.user.ordermodule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.DeleteModel
import com.fidoo.user.ordermodule.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyOrdersFragmentViewModel(application: Application) : AndroidViewModel(application),
    Callback<MyOrdersModel> {

    var cancelOrderResponse: MutableLiveData<DeleteModel>? = null
    var repeatOrderResponse: MutableLiveData<RepeatOrderModel>? = null

    var orderFeedback: MutableLiveData<Feedback>? = null
    var myOrdersResponse: MutableLiveData<MyOrdersModel>? = null
    var reviewResponse: MutableLiveData<ReviewModel>? = null
    var uploadPrescriptionResponse: MutableLiveData<UploadPresModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    init {
        cancelOrderResponse = MutableLiveData<DeleteModel>()
        repeatOrderResponse = MutableLiveData<RepeatOrderModel>()

        orderFeedback = MutableLiveData<Feedback>()
        failureResponse = MutableLiveData<String>()
        myOrdersResponse = MutableLiveData<MyOrdersModel>()
        reviewResponse = MutableLiveData<ReviewModel>()
        uploadPrescriptionResponse = MutableLiveData<UploadPresModel>()
    }

    fun addfeedbackApi(
        accountId: String,
        accessToken: String,
        order_id: String?,
        star: String?,
        improvement: String?,
        message: String?,
        type: String?
    ) {
        WebServiceClient.client.create(BackEndApi::class.java).addFeedbackApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            star = star,
            improvement = improvement,
            message = message,
            type = type
        )
            .enqueue(object : Callback<Feedback> {

                override fun onResponse(call: Call<Feedback>, response: Response<Feedback>) {
                    orderFeedback?.value = response.body()

                }

                override fun onFailure(call: Call<Feedback>, t: Throwable) {
                    failureResponse?.value= "Something went wrong"
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
        Log.e("reviewSubmitApi__","$accountId--$accessToken--$order_id--$store_rating--$store_review--$delivery_rating--$delivery_review")
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




    fun getMyOrders(accountId: String, accessToken: String, page_count: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getMyOrdersApi(
            accountId = accountId, accessToken = accessToken, page_count = page_count
        )
            .enqueue(this)

    }

    override fun onResponse(call: Call<MyOrdersModel>?, response: Response<MyOrdersModel>?) {

        myOrdersResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<MyOrdersModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"

    }


    fun uploadPrescriptionImage(accountId: RequestBody?, accessToken: RequestBody?, order_id: RequestBody?,
                                image: MultipartBody.Part?) {

        val call: Call<UploadPresModel> = WebServiceClient.client.create(BackEndApi::class.java).uploadPrescriptionImage(accountId,accessToken,image)
        call.enqueue(object : Callback<UploadPresModel> {
            override fun onResponse(
                call: Call<UploadPresModel>,
                response: Response<UploadPresModel>
            ) {
                uploadPrescriptionResponse?.value = response.body()
            }

            override fun onFailure(
                call: Call<UploadPresModel>,
                t: Throwable
            ) {
                Log.e("ddddd",t.message.toString())
            }
        })


    }


    fun cancelOrderApi(accountId: String, accessToken: String, orderId: String) {

        WebServiceClient.client.create(BackEndApi::class.java).cancelOrderApi(
            accountId = accountId, accessToken = accessToken, order_id = orderId
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.DeleteModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.DeleteModel>, response: Response<com.fidoo.user.data.model.DeleteModel>) {

                    cancelOrderResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.DeleteModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }


    fun repeatOrderApi(accountId: String, accessToken: String, orderId: String) {
       Log.e("repeatorder_params","$accountId--$accessToken--$orderId")
        WebServiceClient.client.create(BackEndApi::class.java).repeatOrderApi(
            accountId = accountId, accessToken = accessToken, order_id = orderId
        )
            .enqueue(object : Callback<RepeatOrderModel> {

                override fun onResponse(call: Call<RepeatOrderModel>, response: Response<RepeatOrderModel>) {

                    repeatOrderResponse?.value = response.body()

                }

                override fun onFailure(call: Call<RepeatOrderModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }


}

