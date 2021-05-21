package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.CallResponseModel
import com.fidoo.user.data.model.ProceedToOrder
import com.fidoo.user.ordermodule.model.Feedback
import com.fidoo.user.ordermodule.model.ReviewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackViewModel(application: Application) : AndroidViewModel(application)  {

    var getLocationResponse: MutableLiveData<com.fidoo.user.data.model.LocationResponseModel>? = null
    var cancelOrderResponse: MutableLiveData<com.fidoo.user.data.model.DeleteModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    var callCustomerResponse: MutableLiveData<com.fidoo.user.data.model.CallResponseModel>? = null
    var proceedToOrderResponse: MutableLiveData<ProceedToOrder>? = null
    var orderFeedback: MutableLiveData<Feedback>? = null

    init {


        getLocationResponse = MutableLiveData<com.fidoo.user.data.model.LocationResponseModel>()
        cancelOrderResponse = MutableLiveData<com.fidoo.user.data.model.DeleteModel>()
        failureResponse = MutableLiveData<String>()
        callCustomerResponse = MutableLiveData<CallResponseModel>()
        proceedToOrderResponse = MutableLiveData<ProceedToOrder>()
        orderFeedback = MutableLiveData<Feedback>()

    }



    fun cancelOrderApi(accountId: String, accessToken: String, orderId: String) {

        // progressDialog?.value = true
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

    fun getLocationApi(accountId: String, accessToken: String, order_id: String, user_type: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getLocationApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            user_type = user_type
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.LocationResponseModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.LocationResponseModel>, response: Response<com.fidoo.user.data.model.LocationResponseModel>) { // progressDialog?.value = false
                    getLocationResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.LocationResponseModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    fun proceedToOrder(accountId: String, accessToken: String, orderId: String){
        WebServiceClient.client.create(BackEndApi::class.java).proceedToOrder(
            accountId = accountId,
            accessToken = accessToken,
            orderId = orderId
        ).enqueue(object : Callback<ProceedToOrder> {

            override fun onResponse(call: Call<ProceedToOrder>, response: Response<ProceedToOrder>) {

                proceedToOrderResponse?.value = response.body()

            }
            override fun onFailure(call: Call<ProceedToOrder>, t: Throwable) {
                Log.e("Error in making call","--"+t.toString())
            }
        })
    }

    fun callCustomerApi(accountId: String, accessToken: String, customer_phone: String, delivery_boy_phone: String) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).callCustomerApi(
            accountId = accountId,
            accessToken = accessToken,
            customer_phone = customer_phone,
            delivery_boy_phone = delivery_boy_phone)!!.enqueue(object : Callback<com.fidoo.user.data.model.CallResponseModel> {
            override fun onResponse(call: Call<com.fidoo.user.data.model.CallResponseModel>, response: Response<com.fidoo.user.data.model.CallResponseModel>) {
                //  progressDialog?.value = false
                callCustomerResponse?.value = response?.body()

                Log.e("Call Success","--")



            }
            override fun onFailure(call: Call<CallResponseModel>, t: Throwable) {
                // progressDialog?.value = false

                Log.e("Error in making call","--"+t.toString())
            }
        })
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

}