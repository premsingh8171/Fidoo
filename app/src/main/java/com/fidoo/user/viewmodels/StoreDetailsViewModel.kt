package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreDetailsViewModel(application: Application) : AndroidViewModel(application),
    Callback<com.fidoo.user.data.model.StoreDetailsModel> {

    var getStoreDetailsApi: MutableLiveData<com.fidoo.user.data.model.StoreDetailsModel>? = null
    var getSearchResponse: MutableLiveData<com.fidoo.user.data.model.StoreDetailsModel>? = null
    var addToCartResponse: MutableLiveData<com.fidoo.user.data.model.AddToCartModel>? = null
    var clearCartResponse: MutableLiveData<com.fidoo.user.data.model.ClearCartModel>? = null
    var customizeProductResponse: MutableLiveData<com.fidoo.user.data.model.CustomizeProductResponseModel>? = null
    var cartCountResponse: MutableLiveData<com.fidoo.user.data.model.CartCountModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    var addRemoveCartResponse: MutableLiveData<com.fidoo.user.data.model.AddRemoveCartModel>? = null
    init {
        addRemoveCartResponse = MutableLiveData<com.fidoo.user.data.model.AddRemoveCartModel>()
        addToCartResponse = MutableLiveData<com.fidoo.user.data.model.AddToCartModel>()
        getStoreDetailsApi = MutableLiveData<com.fidoo.user.data.model.StoreDetailsModel>()
        getSearchResponse = MutableLiveData<com.fidoo.user.data.model.StoreDetailsModel>()
        clearCartResponse = MutableLiveData<com.fidoo.user.data.model.ClearCartModel>()
        customizeProductResponse = MutableLiveData<com.fidoo.user.data.model.CustomizeProductResponseModel>()
        cartCountResponse = MutableLiveData<com.fidoo.user.data.model.CartCountModel>()
        failureResponse = MutableLiveData<String>()
    }

    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cart count params", accountId + ", " + accessToken)
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.CartCountModel> {

                override fun onResponse(
                    call: Call<com.fidoo.user.data.model.CartCountModel>,
                    response: Response<com.fidoo.user.data.model.CartCountModel>
                ) {

                    cartCountResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.CartCountModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun addRemoveCartDetails(accountId: String, accessToken: String, product_id: String, addRemoveType: String, is_customize: String, product_customize_id: String, cart_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).addRemoveCartApi(
            accountId = accountId,
            accessToken = accessToken,
            product_id = product_id,
            addRemoveType = addRemoveType,
            is_customize = is_customize,
            product_customize_id = product_customize_id,
            cart_id = cart_id
        )
            .enqueue(object : Callback<AddRemoveCartModel> {

                override fun onResponse(call: Call<AddRemoveCartModel>, response: Response<AddRemoveCartModel>) {
                    // progressDialog?.value = false
                    addRemoveCartResponse?.value = response.body()

                }

                override fun onFailure(call: Call<AddRemoveCartModel>, t: Throwable) {
                    //  progressDialog?.value = false

                    failureResponse?.value="Something went wrong"
                }
            })
    }



    fun addToCartApi(accountId: String, accessToken: String, products: ArrayList<AddCartInputModel>, cart_id: String
    ) {

        var addCartInputModelFinal = AddCartInputModelFinal()
        addCartInputModelFinal.accessToken = accessToken
        addCartInputModelFinal.accountId = accountId
        addCartInputModelFinal.products = ArrayList<Product>()
        addCartInputModelFinal.cart_id = cart_id
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
        //    addCartInputModelFinal.products.addAll()
        WebServiceClient.client.create(BackEndApi::class.java).addToCartApi(
            addCartInputModelFinal
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.AddToCartModel> {

                override fun onResponse(
                    call: Call<com.fidoo.user.data.model.AddToCartModel>,
                    response: Response<com.fidoo.user.data.model.AddToCartModel>
                ) {
                    addToCartResponse?.value = response.body()
                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.AddToCartModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun clearCartApi(accountId: String, accessToken: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).clearCartApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.ClearCartModel> {

                override fun onResponse(
                    call: Call<com.fidoo.user.data.model.ClearCartModel>,
                    response: Response<com.fidoo.user.data.model.ClearCartModel>
                ) {

                    clearCartResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.ClearCartModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun customizeProductApi(accountId: String, accessToken: String, product_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).customizeProductApi(
            accountId = accountId, accessToken = accessToken, product_id = product_id
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.CustomizeProductResponseModel> {

                override fun onResponse(
                    call: Call<com.fidoo.user.data.model.CustomizeProductResponseModel>,
                    response: Response<com.fidoo.user.data.model.CustomizeProductResponseModel>
                ) {

                    customizeProductResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.CustomizeProductResponseModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun searchApi(
        accountId: String,
        accessToken: String,
        store_id: String?,
        searchTxt: String?,
        is_nonveg: String?
    ) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getSearchStoreProductApi(
            accountId = accountId,
            accessToken = accessToken,
            store_id = store_id,
            search = searchTxt,
            is_nonveg = is_nonveg
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.StoreDetailsModel> {

                override fun onResponse(
                    call: Call<com.fidoo.user.data.model.StoreDetailsModel>,
                    response: Response<com.fidoo.user.data.model.StoreDetailsModel>
                ) {

                    getSearchResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.StoreDetailsModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun getStoreDetails(
        accountId: String,
        accessToken: String,
        store_id: String?,
        is_nonveg: String?,
        cat_id: String?
    ) {
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getStoreDetailsApi(
            accountId = accountId,
            accessToken = accessToken,
            store_id = store_id,
            is_nonveg = is_nonveg,
            cat_id = cat_id
        )
            .enqueue(this)

    }

    override fun onResponse(
        call: Call<com.fidoo.user.data.model.StoreDetailsModel>?,
        response: Response<com.fidoo.user.data.model.StoreDetailsModel>?
    ) {
        getStoreDetailsApi?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.StoreDetailsModel>?, t: Throwable?) {
        failureResponse?.value = "Something went wrong"
    }

}