package com.fidoo.user.ordermodule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.CallResponseModel
import com.fidoo.user.data.model.DeleteModel
import com.fidoo.user.data.model.LocationResponseModel
import com.fidoo.user.data.model.ProceedToOrder
import com.fidoo.user.ordermodule.model.CustomerCallMerchantModel
import com.fidoo.user.ordermodule.model.Feedback
import com.fidoo.user.ordermodule.model.TrackPackageOrderModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackViewModel(application: Application) : AndroidViewModel(application) {

    var getLocationResponse: MutableLiveData<LocationResponseModel>? = null
    var cancelOrderResponse: MutableLiveData<DeleteModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    var callCustomerResponse: MutableLiveData<CallResponseModel>? = null
    var customerCallMerchantRes: MutableLiveData<CustomerCallMerchantModel>? = null
    var proceedToOrderResponse: MutableLiveData<ProceedToOrder>? = null
    var trackPackageOrderModelRes: MutableLiveData<TrackPackageOrderModel>? = null
    var orderFeedback: MutableLiveData<Feedback>? = null

    init {
        getLocationResponse = MutableLiveData<LocationResponseModel>()
        cancelOrderResponse = MutableLiveData<DeleteModel>()
        failureResponse = MutableLiveData<String>()
        callCustomerResponse = MutableLiveData<CallResponseModel>()
        customerCallMerchantRes = MutableLiveData<CustomerCallMerchantModel>()
        proceedToOrderResponse = MutableLiveData<ProceedToOrder>()
        trackPackageOrderModelRes = MutableLiveData<TrackPackageOrderModel>()
        orderFeedback = MutableLiveData<Feedback>()
    }


    fun cancelOrderApi(accountId: String, accessToken: String, orderId: String) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cancelOrderApi(
            accountId = accountId, accessToken = accessToken, order_id = orderId
        ).enqueue(object : Callback<DeleteModel> {
            override fun onResponse(call: Call<DeleteModel>, response: Response<DeleteModel>) {
                    cancelOrderResponse?.value = response.body()
                }

                override fun onFailure(call: Call<DeleteModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun getLocationApi(
        accountId: String,
        accessToken: String,
        order_id: String,
        user_type: String
    ) {
        Log.d("getLocation__", accountId + "--" + accessToken + "--" + order_id + "--" + user_type)
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getLocationApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            user_type = user_type
        )
            .enqueue(object : Callback<LocationResponseModel> {

                override fun onResponse(
                    call: Call<LocationResponseModel>,
                    response: Response<LocationResponseModel>
                ) {
                    // progressDialog?.value = false
                    getLocationResponse?.value = response.body()

                }

                override fun onFailure(call: Call<LocationResponseModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun proceedToOrder(accountId: String, accessToken: String, orderId: String) {
        Log.d("proceedToOrder___", "$accountId--$accessToken--$orderId")
        WebServiceClient.client.create(BackEndApi::class.java).proceedToOrder(
            accountId = accountId,
            accessToken = accessToken,
            orderId = orderId
        ).enqueue(object : Callback<ProceedToOrder> {

            override fun onResponse(
                call: Call<ProceedToOrder>,
                response: Response<ProceedToOrder>
            ) {

                proceedToOrderResponse?.value = response.body()

            }

            override fun onFailure(call: Call<ProceedToOrder>, t: Throwable) {
                Log.e("Error in making call", "--" + t.toString())
            }
        })
    }

    fun callCustomerApi(
        accountId: String,
        accessToken: String,
        customer_phone: String,
        delivery_boy_phone: String
    ) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).callCustomerApi(
            accountId = accountId,
            accessToken = accessToken,
            customer_phone = customer_phone,
            delivery_boy_phone = delivery_boy_phone
        )!!.enqueue(object : Callback<CallResponseModel> {
            override fun onResponse(
                call: Call<CallResponseModel>,
                response: Response<CallResponseModel>
            ) {
                //  progressDialog?.value = false
                callCustomerResponse?.value = response?.body()

            }

            override fun onFailure(call: Call<CallResponseModel>, t: Throwable) {
                // progressDialog?.value = false

                Log.e("Error in making call", "--" + t.toString())
            }
        })
    }

    fun customerCallMerchantApi(
        accountId: String,
        accessToken: String,
        customer_phone: String,
        merchant_phone: String
    ) {
        Log.d("customerCallMerchant__","$accountId-$accessToken-$customer_phone-$merchant_phone")
        WebServiceClient.client.create(BackEndApi::class.java).customerCallMerchantApi(
            accountId = accountId,
            accessToken = accessToken,
            customer_phone = customer_phone,
            merchant_phone = merchant_phone
        )!!.enqueue(object : Callback<CustomerCallMerchantModel> {
            override fun onResponse(
                call: Call<CustomerCallMerchantModel>,
                response: Response<CustomerCallMerchantModel>
            ) {
                customerCallMerchantRes?.value = response?.body()

               //  Log.e("customerCallMerchantRes__", "--")

            }

            override fun onFailure(call: Call<CustomerCallMerchantModel>, t: Throwable) {
                failureResponse!!.value = "error"
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
        Log.d(
            "addfeedbackApi", accountId + "-\n" + accessToken + "-\n" + order_id + "-\n" + star
                    + "-\n" + improvement + "-\n" + message + "-\n" + type
        )
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
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun trackSendPackagesOrderApi(accountId: String, accessToken: String, orderId: String) {
        Log.d("sendPAckages_Api", "$accountId---$accessToken--$orderId")

        WebServiceClient.client.create(BackEndApi::class.java).trackPackageOrderApi(
            accountId = accountId, accessToken = accessToken, order_id = orderId
        )
            .enqueue(object : Callback<TrackPackageOrderModel> {

                override fun onResponse(
                    call: Call<TrackPackageOrderModel>,
                    response: Response<TrackPackageOrderModel>
                ) {

                    trackPackageOrderModelRes?.value = response.body()

                }

                override fun onFailure(call: Call<TrackPackageOrderModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }


}