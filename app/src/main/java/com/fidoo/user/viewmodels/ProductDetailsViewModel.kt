package com.fidoo.user.viewmodels


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.CartCountModel
import com.fidoo.user.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailsViewModel(application: Application) : AndroidViewModel(application), Callback<ProductDetailsModel> {

    var getProductDetailsResponse: MutableLiveData<ProductDetailsModel>? = null
    var addToCartResponse: MutableLiveData<AddToCartModel>? = null
    var clearCartResponse: MutableLiveData<ClearCartModel>? = null
    var cartCountResponse: MutableLiveData<CartCountModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    init {

        failureResponse = MutableLiveData<String>()
        getProductDetailsResponse = MutableLiveData<ProductDetailsModel>()
        addToCartResponse = MutableLiveData<AddToCartModel>()
        clearCartResponse = MutableLiveData<ClearCartModel>()
        cartCountResponse = MutableLiveData<CartCountModel>()

    }

    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cart count params",accountId+", "+accessToken)
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
                accountId = accountId, accessToken = accessToken
        )
                .enqueue(object : Callback<CartCountModel> {

                    override fun onResponse(call: Call<CartCountModel>, response: Response<CartCountModel>) {

                        cartCountResponse?.value = response.body()

                    }

                    override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                        failureResponse?.value="Something went wrong"
                    }
                })
    }
    fun clearCartApi(accountId: String, accessToken: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).clearCartApi(
                accountId = accountId, accessToken = accessToken
        )
                .enqueue(object : Callback<ClearCartModel> {

                    override fun onResponse(call: Call<ClearCartModel>, response: Response<ClearCartModel>) {

                        clearCartResponse?.value = response.body()

                    }

                    override fun onFailure(call: Call<ClearCartModel>, t: Throwable) {
                        failureResponse?.value="Something went wrong"
                    }
                })
    }


    fun addToCartApi(
            accountId: String,
            accessToken: String,
            products: ArrayList<AddCartInputModel>

    ) {
        var addCartInputModelFinal = AddCartInputModelFinal()
        addCartInputModelFinal.accessToken = accessToken
        addCartInputModelFinal.accountId = accountId
        addCartInputModelFinal.products = ArrayList<Product>()
        for (i in 0..products.size - 1) {
            var temp = Product(
                    products.get(i).productId,
                    products.get(i).customizeSubCatId,
                    products.get(i).isCustomize,
                    products.get(i).message,
                    products.get(i).quantity
            )

            addCartInputModelFinal.products.add(temp)
        }
        WebServiceClient.client.create(BackEndApi::class.java).addToCartApi(addCartInputModelFinal)
                .enqueue(object : Callback<AddToCartModel> {

                    override fun onResponse(call: Call<AddToCartModel>, response: Response<AddToCartModel>) {

                        addToCartResponse?.value = response.body()

                    }

                    override fun onFailure(call: Call<AddToCartModel>, t: Throwable) {
                        failureResponse?.value="Something went wrong"
                    }
                })
    }/*
    fun addToCartApi(
        accountId: String,
        accessToken: String,
        product_id: String,
        quantity: String,
        message: String,
        is_customize: String
    ) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).addToCartApi(
            accountId = accountId,accessToken = accessToken,product_id = product_id,quantity = quantity,message = message,is_customize = is_customize,customize_sub_cat_id = ArrayList<String>())!!
            .enqueue(object : Callback<AddToCartModel> {

                override fun onResponse(call: Call<AddToCartModel>, response: Response<AddToCartModel>) {

                    addToCartResponse?.value = response?.body()

                }

                override fun onFailure(call: Call<AddToCartModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }*/
    fun getProductDetails(accountId: String, accessToken: String, product_id: String?) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getProductDetailsApi(
                accountId = accountId, accessToken = accessToken, product_id = product_id
        )
                .enqueue(this)

    }

    override fun onResponse(call: Call<ProductDetailsModel>?, response: Response<ProductDetailsModel>?) {
        getProductDetailsResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<ProductDetailsModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong"
    }

}