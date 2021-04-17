package com.fidoo.user.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressViewModel(application: Application) : AndroidViewModel(application), Callback<com.fidoo.user.data.model.GetAddressModel> {

      var getAddressesResponse: MutableLiveData<com.fidoo.user.data.model.GetAddressModel>? = null
      var addAddressResponse: MutableLiveData<com.fidoo.user.data.model.AddAddressModel>? = null
      var editAddressResponse: MutableLiveData<com.fidoo.user.data.model.AddAddressModel>? = null
      var deleteAddressResponse: MutableLiveData<com.fidoo.user.data.model.DeleteAddressModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        getAddressesResponse = MutableLiveData<com.fidoo.user.data.model.GetAddressModel>()
        addAddressResponse = MutableLiveData<com.fidoo.user.data.model.AddAddressModel>()
        editAddressResponse = MutableLiveData<com.fidoo.user.data.model.AddAddressModel>()
        deleteAddressResponse = MutableLiveData<com.fidoo.user.data.model.DeleteAddressModel>()
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
            .enqueue(object : Callback<com.fidoo.user.data.model.AddAddressModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.AddAddressModel>, response: Response<com.fidoo.user.data.model.AddAddressModel>) {
                   // progressDialog?.value = false
                    addAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.AddAddressModel>, t: Throwable) {
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
            .enqueue(object : Callback<com.fidoo.user.data.model.AddAddressModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.AddAddressModel>, response: Response<com.fidoo.user.data.model.AddAddressModel>) {
                   // progressDialog?.value = false
                    editAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.AddAddressModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    fun deleteAddressApi(accountId: String, accessToken: String, address_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).deleteAddressApi(
            accountId = accountId, accessToken = accessToken, address_id = address_id
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.DeleteAddressModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.DeleteAddressModel>, response: Response<com.fidoo.user.data.model.DeleteAddressModel>) {
                   // progressDialog?.value = false
                    deleteAddressResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.DeleteAddressModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }
    override fun onResponse(call: Call<com.fidoo.user.data.model.GetAddressModel>?, response: Response<com.fidoo.user.data.model.GetAddressModel>?) {
        getAddressesResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.GetAddressModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"
    }

}