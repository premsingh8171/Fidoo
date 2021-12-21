package com.fidoo.user.addressmodule.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.addressmodule.model.AddAddressModel
import com.fidoo.user.addressmodule.model.DeleteAddressModel
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.addressmodule.model.RemoveAddressModel
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.ordermodule.model.GetOrderStatusModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressViewModel(application: Application) : AndroidViewModel(application),
    Callback<GetAddressModel> {
    var getAddressesResponse: MutableLiveData<GetAddressModel>? = null
    var addAddressResponse: MutableLiveData<AddAddressModel>? = null
    var editAddressResponse: MutableLiveData<AddAddressModel>? = null
    var deleteAddressResponse: MutableLiveData<DeleteAddressModel>? = null
    var removeAddressResponse: MutableLiveData<RemoveAddressModel>? = null
    var orderStatusModelResponse: MutableLiveData<GetOrderStatusModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        getAddressesResponse = MutableLiveData<GetAddressModel>()
        addAddressResponse = MutableLiveData<AddAddressModel>()
        editAddressResponse = MutableLiveData<AddAddressModel>()
        deleteAddressResponse = MutableLiveData<DeleteAddressModel>()
        removeAddressResponse = MutableLiveData<RemoveAddressModel>()
        orderStatusModelResponse = MutableLiveData<GetOrderStatusModel>()
        failureResponse = MutableLiveData<String>()
    }

    fun getAddressesApi(
        accountId: String,
        accessToken: String,
        storeLatitude: String,
        storeLongitude: String
    ) {
        Log.d("getAddressesApi", "$accountId--$accessToken--$storeLatitude--$storeLongitude")
        WebServiceClient.client.create(BackEndApi::class.java).getAddressesApi(
            accountId = accountId,
            accessToken = accessToken,
            storeLatitude = storeLatitude,
            storeLongitude = storeLongitude
        ).enqueue(this)
    }


    fun addAddressDetails(
        accountId: String,
        accessToken: String,
        flat_no: String,
        building: String,
        location: String,
        landmark: String,
        address_type: String,
        latitude: String,
        longitude: String,
        name: String,
        email: String,
        is_default: String,
        phone_number: String,
        contact_type: String
    ) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).addAddressApi(
            accountId = accountId,
            accessToken = accessToken,
            flat_no = flat_no,
            building = building,
            location = location,
            landmark = landmark,
            address_type = address_type,
            latitude = latitude,
            longitude = longitude,
            name = name,
            email_id = email,
            is_default = is_default,
            phone_no = phone_number,
            contact_type = contact_type
        )
            .enqueue(object : Callback<AddAddressModel> {

                override fun onResponse(
                    call: Call<AddAddressModel>,
                    response: Response<AddAddressModel>
                ) {
                    // progressDialog?.value = false
                    addAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<AddAddressModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun editAddressDetails(
        accountId: String,
        accessToken: String,
        flat_no: String,
        building: String,
        location: String,
        landmark: String,
        address_type: String,
        latitude: String,
        longitude: String,
        name: String,
        email: String,
        is_default: String,
        phone_no: String,
        address_id: String,
        contact_type: String
    ) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).editAddressApi(
            accountId = accountId,
            accessToken = accessToken,
            flat_no = flat_no,
            building = building,
            location = location,
            landmark = landmark,
            address_type = address_type,
            latitude = latitude,
            longitude = longitude,
            name = name,
            phone_no = phone_no,
            email_id = email,
            address_id = address_id,
            is_default = is_default,
            contact_type = contact_type
        )
            .enqueue(object : Callback<AddAddressModel> {

                override fun onResponse(
                    call: Call<AddAddressModel>,
                    response: Response<AddAddressModel>
                ) {
                    // progressDialog?.value = false
                    editAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<AddAddressModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun deleteAddressApi(accountId: String, accessToken: String, address_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).deleteAddressApi(
            accountId = accountId, accessToken = accessToken, address_id = address_id
        )
            .enqueue(object : Callback<DeleteAddressModel> {

                override fun onResponse(
                    call: Call<DeleteAddressModel>,
                    response: Response<DeleteAddressModel>
                ) {
                    // progressDialog?.value = false
                    deleteAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<DeleteAddressModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }


    fun removeAddressApi(accountId: String, accessToken: String, address_id: String) {
        WebServiceClient.client.create(BackEndApi::class.java).removeAddressApi(
            accountId = accountId, accessToken = accessToken, address_id = address_id
        )
            .enqueue(object : Callback<RemoveAddressModel> {
                override fun onResponse(
                    call: Call<RemoveAddressModel>,
                    response: Response<RemoveAddressModel>
                ) {
                    removeAddressResponse?.value = response.body()
                }

                override fun onFailure(call: Call<RemoveAddressModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }


    fun getOrderStatusApi(accountId: String, accessToken: String, order_id: String) {
        WebServiceClient.client.create(BackEndApi::class.java).getOrderStatusApi(
            accountId = accountId, accessToken = accessToken, order_id = order_id
        )
            .enqueue(object : Callback<GetOrderStatusModel> {
                override fun onResponse(
                    call: Call<GetOrderStatusModel>,
                    response: Response<GetOrderStatusModel>
                ) {
                    orderStatusModelResponse?.value = response.body()
                }

                override fun onFailure(call: Call<GetOrderStatusModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }


    override fun onResponse(call: Call<GetAddressModel>?, response: Response<GetAddressModel>?) {
        getAddressesResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<GetAddressModel>?, t: Throwable?) {
        failureResponse?.value = "Something went wrong"
    }

}