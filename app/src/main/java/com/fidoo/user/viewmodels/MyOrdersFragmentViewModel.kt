package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyOrdersFragmentViewModel(application: Application) : AndroidViewModel(application),
    Callback<com.fidoo.user.data.model.MyOrdersModel> {


    var myOrdersResponse: MutableLiveData<com.fidoo.user.data.model.MyOrdersModel>? = null
    var reviewResponse: MutableLiveData<com.fidoo.user.data.model.ReviewModel>? = null
    var uploadPrescriptionResponse: MutableLiveData<com.fidoo.user.data.model.UploadPresModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    init {
        failureResponse = MutableLiveData<String>()
        myOrdersResponse = MutableLiveData<com.fidoo.user.data.model.MyOrdersModel>()
        reviewResponse = MutableLiveData<com.fidoo.user.data.model.ReviewModel>()
        uploadPrescriptionResponse = MutableLiveData<com.fidoo.user.data.model.UploadPresModel>()
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
            .enqueue(object : Callback<com.fidoo.user.data.model.ReviewModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.ReviewModel>, response: Response<com.fidoo.user.data.model.ReviewModel>) {

                    reviewResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.ReviewModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }
    fun getMyOrders(accountId: String, accessToken: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getMyOrdersApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(this)

    }

    override fun onResponse(call: Call<com.fidoo.user.data.model.MyOrdersModel>?, response: Response<com.fidoo.user.data.model.MyOrdersModel>?) {

        myOrdersResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.MyOrdersModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"

    }


    fun uploadPrescriptionImage(accountId: RequestBody?, accessToken: RequestBody?, order_id: RequestBody?,
                                image: MultipartBody.Part?) {

        val call: Call<com.fidoo.user.data.model.UploadPresModel> = WebServiceClient.client.create(BackEndApi::class.java).uploadPrescriptionImage(accountId,accessToken,image)
        call.enqueue(object : Callback<com.fidoo.user.data.model.UploadPresModel> {
            override fun onResponse(
                call: Call<com.fidoo.user.data.model.UploadPresModel>,
                response: Response<com.fidoo.user.data.model.UploadPresModel>
            ) {
                uploadPrescriptionResponse?.value = response.body()
            }

            override fun onFailure(
                call: Call<com.fidoo.user.data.model.UploadPresModel>,
                t: Throwable
            ) {
                Log.e("ddddd",t.message.toString())
            }
        })


    }




}

