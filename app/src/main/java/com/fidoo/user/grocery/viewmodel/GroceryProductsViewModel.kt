package com.fidoo.user.grocery.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.grocery.model.getGroceryProducts.GroceryProductsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//GroceryProductsResponse
class GroceryProductsViewModel(application: Application) : AndroidViewModel(application),
        Callback<GroceryProductsResponse> {

      var GroceryProductsResponse: MutableLiveData<GroceryProductsResponse>? = null
      var failureResponse: MutableLiveData<String>? = null

      init {
          GroceryProductsResponse = MutableLiveData<GroceryProductsResponse>()
          failureResponse = MutableLiveData<String>()

      }

    fun getGroceryProductsFun(
            accountId: String,
            accessToken: String,
            store_id: String?,
    ) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getGroceryProducts(
                accountId = accountId,
                accessToken = accessToken,
                store_id = store_id,
        ).enqueue(object : Callback<GroceryProductsResponse> {

                    override fun onResponse(
                            call: Call<GroceryProductsResponse>,
                            response: Response<GroceryProductsResponse>
                    ) {

                        GroceryProductsResponse?.value = response.body()

                    }

                    override fun onFailure(call: Call<GroceryProductsResponse>, t: Throwable) {
                        failureResponse?.value = "Something went wrong"
                    }
                })

    }


    override fun onResponse(call: Call<GroceryProductsResponse>,
                            response: Response<GroceryProductsResponse>) {
        GroceryProductsResponse?.value = response.body()

    }

    override fun onFailure(call: Call<GroceryProductsResponse>, t: Throwable) {
        failureResponse?.value = "Something went wrong"
    }
}