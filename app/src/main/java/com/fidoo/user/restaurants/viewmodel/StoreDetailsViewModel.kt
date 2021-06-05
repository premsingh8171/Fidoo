package com.fidoo.user.restaurants.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.*
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.restaurants.model.StoreDetailsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreDetailsViewModel(application: Application) : AndroidViewModel(application),
    Callback<StoreDetailsModel> {

    var getStoreDetailsApi: MutableLiveData<StoreDetailsModel>? = null
    var getSearchResponse: MutableLiveData<StoreDetailsModel>? = null
    var addToCartResponse: MutableLiveData<AddToCartModel>? = null
    var clearCartResponse: MutableLiveData<ClearCartModel>? = null
    var customizeProductResponse: MutableLiveData<CustomizeProductResponseModel>? = null
    var cartCountResponse: MutableLiveData<CartCountModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    var addRemoveCartResponse: MutableLiveData<AddRemoveCartModel>? = null

    init {
        addRemoveCartResponse = MutableLiveData<AddRemoveCartModel>()
        addToCartResponse = MutableLiveData<AddToCartModel>()
        getStoreDetailsApi = MutableLiveData<StoreDetailsModel>()
        getSearchResponse = MutableLiveData<StoreDetailsModel>()
        clearCartResponse = MutableLiveData<ClearCartModel>()
        customizeProductResponse = MutableLiveData<CustomizeProductResponseModel>()
        cartCountResponse = MutableLiveData<CartCountModel>()
        failureResponse = MutableLiveData<String>()
    }

    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cart count params", accountId + ", " + accessToken)
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<CartCountModel> {

                override fun onResponse(
                    call: Call<CartCountModel>,
                    response: Response<CartCountModel>
                ) {

                    cartCountResponse?.value = response.body()

                }

                override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun addRemoveCartDetails(accountId: String, accessToken: String, product_id: String, addRemoveType: String, is_customize: String, product_customize_id: String, cart_id: String, customize_sub_cat_id: ArrayList<String>) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).addRemoveCartApi(
            accountId = accountId,
            accessToken = accessToken,
            product_id = product_id,
            addRemoveType = addRemoveType,
            is_customize = is_customize,
            product_customize_id = product_customize_id,
            cart_id = cart_id,
            customize_sub_cat_id = customize_sub_cat_id
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
            .enqueue(object : Callback<AddToCartModel> {

                override fun onResponse(
                    call: Call<AddToCartModel>,
                    response: Response<AddToCartModel>
                ) {
                    addToCartResponse?.value = response.body()
                }

                override fun onFailure(call: Call<AddToCartModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun clearCartApi(accountId: String, accessToken: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).clearCartApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<ClearCartModel> {

                override fun onResponse(
                    call: Call<ClearCartModel>,
                    response: Response<ClearCartModel>
                ) {

                    clearCartResponse?.value = response.body()

                }

                override fun onFailure(call: Call<ClearCartModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun customizeProductApi(accountId: String, accessToken: String, product_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).customizeProductApi(
            accountId = accountId, accessToken = accessToken, product_id = product_id
        )
            .enqueue(object : Callback<CustomizeProductResponseModel> {

                override fun onResponse(
                    call: Call<CustomizeProductResponseModel>,
                    response: Response<CustomizeProductResponseModel>
                ) {

                    customizeProductResponse?.value = response.body()

                }

                override fun onFailure(call: Call<CustomizeProductResponseModel>, t: Throwable) {
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
            .enqueue(object : Callback<StoreDetailsModel> {

                override fun onResponse(
                    call: Call<StoreDetailsModel>,
                    response: Response<StoreDetailsModel>
                ) {

                    getSearchResponse?.value = response.body()

                }

                override fun onFailure(call: Call<StoreDetailsModel>, t: Throwable) {
                    failureResponse?.value = "Something went wrong"
                }
            })
    }

    fun getStoreDetails(
        accountId: String,
        accessToken: String,
        store_id: String?,
        is_nonveg: String?,
        cat_id: String?,
        contains_egg: String?
    ) {
        // progressDialog?.value = true
        Log.d("storeDetail_value",accountId+"\n"+accessToken+"\n"+store_id+"\n"+cat_id+"\n"+contains_egg)
        WebServiceClient.client.create(BackEndApi::class.java).getStoreDetailsApi(
            accountId = accountId,
            accessToken = accessToken,
            store_id = store_id,
            is_nonveg = is_nonveg,
            cat_id = cat_id,
            contains_egg = contains_egg
        )
            .enqueue(this)

    }

    override fun onResponse(
        call: Call<StoreDetailsModel>?,
        response: Response<StoreDetailsModel>?
    ) {
        getStoreDetailsApi?.value = response?.body()

    }

    override fun onFailure(call: Call<StoreDetailsModel>?, t: Throwable?) {
        failureResponse?.value = "Something went wrong"
    }

}