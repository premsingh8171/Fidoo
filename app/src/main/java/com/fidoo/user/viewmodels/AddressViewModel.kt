package com.fidoo.user.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressViewModel(application: Application) : AndroidViewModel(application), Callback<GetAddressModel> {

      var getAddressesResponse: MutableLiveData<GetAddressModel>? = null
      var addAddressResponse: MutableLiveData<AddAddressModel>? = null
      var editAddressResponse: MutableLiveData<AddAddressModel>? = null
      var deleteAddressResponse: MutableLiveData<DeleteAddressModel>? = null
      var failureResponse: MutableLiveData<String>? = null

    init {
        getAddressesResponse = MutableLiveData<GetAddressModel>()
        addAddressResponse = MutableLiveData<AddAddressModel>()
        editAddressResponse = MutableLiveData<AddAddressModel>()
        deleteAddressResponse = MutableLiveData<DeleteAddressModel>()
        failureResponse = MutableLiveData<String>()

    }

     fun getAddressesApi(accountId: String, accessToken: String) {
   // progressDialog?.value = true
    WebServiceClient.client.create(BackEndApi::class.java).getAddressesApi(
        accountId = accountId, accessToken = accessToken
    )
        .enqueue(this)

   }


    fun addAddressDetails(accountId: String, accessToken: String, flat_no: String, building: String, location: String, landmark: String,
                          address_type: String, latitude: String,
                          longitude: String, name: String, email: String, is_default: String, phone_number: String) {

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
            phone_no = phone_number
        )
            .enqueue(object : Callback<AddAddressModel> {

                override fun onResponse(call: Call<AddAddressModel>, response: Response<AddAddressModel>) {
                   // progressDialog?.value = false
                    addAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<AddAddressModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }
    fun editAddressDetails(accountId: String, accessToken: String, flat_no: String, building: String, location: String, landmark: String,
                          address_type: String, latitude: String,
                          longitude: String, name: String, email: String, is_default: String, phone_no: String, address_id: String) {

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
            is_default = is_default
        )
            .enqueue(object : Callback<AddAddressModel> {

                override fun onResponse(call: Call<AddAddressModel>, response: Response<AddAddressModel>) {
                   // progressDialog?.value = false
                    editAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<AddAddressModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    fun deleteAddressApi(accountId: String, accessToken: String, address_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).deleteAddressApi(
            accountId = accountId, accessToken = accessToken, address_id = address_id
        )
            .enqueue(object : Callback<DeleteAddressModel> {

                override fun onResponse(call: Call<DeleteAddressModel>, response: Response<DeleteAddressModel>) {
                   // progressDialog?.value = false
                    deleteAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<DeleteAddressModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }
    override fun onResponse(call: Call<GetAddressModel>?, response: Response<GetAddressModel>?) {
        getAddressesResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<GetAddressModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"
    }

}