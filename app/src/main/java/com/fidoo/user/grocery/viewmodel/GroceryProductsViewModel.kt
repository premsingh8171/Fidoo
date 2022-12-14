package com.fidoo.user.grocery.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.model.CartCountModel
import com.fidoo.user.data.model.*
import com.fidoo.user.grocery.model.getGroceryProducts.GroceryProductsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroceryProductsViewModel(application: Application) : AndroidViewModel(application),
    Callback<GroceryProductsResponse> {

    var GroceryProductsResponse: MutableLiveData<GroceryProductsResponse>? = null
    var failureResponse: MutableLiveData<String>? = null
    var addToCartResponse: MutableLiveData<AddToCartModel>? = null
    var cartCountResponse: MutableLiveData<CartCountModel>? = null
    var clearCartResponse: MutableLiveData<ClearCartModel>? = null
    var addRemoveCartResponse: MutableLiveData<AddRemoveCartModel>? = null

    init {
        GroceryProductsResponse = MutableLiveData<GroceryProductsResponse>()
        failureResponse = MutableLiveData<String>()
        addToCartResponse = MutableLiveData<AddToCartModel>()
        clearCartResponse = MutableLiveData<ClearCartModel>()
        addRemoveCartResponse = MutableLiveData<AddRemoveCartModel>()
        cartCountResponse= MutableLiveData<CartCountModel>()
    }

    fun getGroceryProductsFun(
        accountId: String,
        accessToken: String,
        store_id: String?,
        category_id: String?,
        subcategory_id: String?
    ) {
        // progressDialog?.value = true
        Log.d("value__", "$store_id--\n$category_id--\n$subcategory_id")
        WebServiceClient.client.create(BackEndApi::class.java).getGroceryProducts(
                accountId = accountId,
                accessToken = accessToken,
                store_id = store_id,
                category_id = category_id,
                subcategory_id = subcategory_id
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

    fun addToCartApi(accountId: String, accessToken: String, products: ArrayList<AddCartInputModel>, cart_id: String) {
        var addCartInputModelList = ArrayList<AddCartInputModelFinal>()

        var addCartInputModelFinal = AddCartInputModelFinal()
        addCartInputModelFinal.accessToken = accessToken
        addCartInputModelFinal.accountId = accountId
        addCartInputModelFinal.products = ArrayList<Product>()
        addCartInputModelFinal.cart_id = cart_id

        //for (i in 0..products.size-1) {
        try {
            var temp = Product(
                products.get(0).productId,
                products.get(0).customizeSubCatId,
                products.get(0).isCustomize,
                products.get(0).message,
                products.get(0).quantity
            )
            addCartInputModelFinal.products.add(temp)
        }catch (e:Exception){
            e.printStackTrace()
        }

        //}
        addCartInputModelList.add(addCartInputModelFinal)
        WebServiceClient.client.create(BackEndApi::class.java).addToCartApi(addCartInputModelFinal)
            .enqueue(object : Callback<AddToCartModel> {

                override fun onResponse(
                    call: Call<AddToCartModel>,
                    response: Response<AddToCartModel>
                ) {
                    products.clear()
                    addCartInputModelList.clear()
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

    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cart_count_params", accountId + ", " + accessToken)
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


    override fun onResponse(call: Call<GroceryProductsResponse>,response: Response<GroceryProductsResponse>) {
        GroceryProductsResponse?.value = response.body()

    }

    override fun onFailure(call: Call<GroceryProductsResponse>, t: Throwable) {
        failureResponse?.value = "Something went wrong"
    }

}