package com.fidoo.user.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel(application: Application) : AndroidViewModel(application), Callback<com.fidoo.user.data.model.CartModel> {
    var addToCartResponse: MutableLiveData<com.fidoo.user.data.model.AddToCartModel>? = null
    var cancelOrderResponse: MutableLiveData<com.fidoo.user.data.model.DeleteModel>? = null
    var getCartDetailsResponse: MutableLiveData<com.fidoo.user.data.model.CartModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    var addRemoveCartResponse: MutableLiveData<com.fidoo.user.data.model.AddRemoveCartModel>? = null
    var appplyPromoResponse: MutableLiveData<com.fidoo.user.data.model.ApplyPromoModel>? = null
    var orderPlaceResponse: MutableLiveData<com.fidoo.user.data.model.OrderPlaceModel>? = null
    var paymentResponse: MutableLiveData<com.fidoo.user.data.model.PaymentModel>? = null
    var deleteCartResponse: MutableLiveData<com.fidoo.user.data.model.DeleteModel>? = null
    var removeCouponResponse: MutableLiveData<com.fidoo.user.data.model.DeleteModel>? = null
    var uploadPrescriptionResponse: MutableLiveData<com.fidoo.user.data.model.UploadPresModel>? = null
    var customizeProductResponse: MutableLiveData<com.fidoo.user.data.model.CustomizeProductResponseModel>? = null
    var razorpayResponse: MutableLiveData<com.fidoo.user.data.model.PaymentModel>? = null
    var getLocationResponse: MutableLiveData<com.fidoo.user.data.model.LocationResponseModel>? = null
    init {
        addToCartResponse = MutableLiveData<com.fidoo.user.data.model.AddToCartModel>()
        cancelOrderResponse = MutableLiveData<com.fidoo.user.data.model.DeleteModel>()
        getCartDetailsResponse = MutableLiveData<com.fidoo.user.data.model.CartModel>()
        failureResponse = MutableLiveData<String>()
        addRemoveCartResponse = MutableLiveData<com.fidoo.user.data.model.AddRemoveCartModel>()
        appplyPromoResponse = MutableLiveData<com.fidoo.user.data.model.ApplyPromoModel>()
        orderPlaceResponse = MutableLiveData<com.fidoo.user.data.model.OrderPlaceModel>()
        paymentResponse = MutableLiveData<com.fidoo.user.data.model.PaymentModel>()
        deleteCartResponse = MutableLiveData<com.fidoo.user.data.model.DeleteModel>()
        removeCouponResponse = MutableLiveData<com.fidoo.user.data.model.DeleteModel>()
        customizeProductResponse = MutableLiveData<com.fidoo.user.data.model.CustomizeProductResponseModel>()
        uploadPrescriptionResponse = MutableLiveData<com.fidoo.user.data.model.UploadPresModel>()
        razorpayResponse = MutableLiveData<com.fidoo.user.data.model.PaymentModel>()
        getLocationResponse = MutableLiveData<com.fidoo.user.data.model.LocationResponseModel>()
    }

    fun getCartDetails(accountId: String, accessToken: String, userLat: String, userlong: String) {
        //  progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).getCartDetailsApi(
            accountId = accountId, accessToken = accessToken, userLat= userLat, userLong = userlong
        ).enqueue(this)
    }

    fun addToCartApi(accountId: String, accessToken: String, products: ArrayList<com.fidoo.user.data.model.AddCartInputModel>, cart_id: String) {

        var addCartInputModelFinal =
            com.fidoo.user.data.model.AddCartInputModelFinal()
        addCartInputModelFinal.accessToken = accessToken
        addCartInputModelFinal.accountId = accountId
        addCartInputModelFinal.cart_id = cart_id
        addCartInputModelFinal.products = ArrayList<com.fidoo.user.data.model.Product>()
        for (i in 0 until products.size) {
            var temp = com.fidoo.user.data.model.Product(
                products.get(i).productId,
                products.get(i).customizeSubCatId,
                products.get(i).isCustomize,
                products.get(i).message,
                products.get(i).quantity
            )

            addCartInputModelFinal.products.add(temp)
        }
        WebServiceClient.client.create(BackEndApi::class.java).addToCartApi(addCartInputModelFinal)
            .enqueue(object : Callback<com.fidoo.user.data.model.AddToCartModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.AddToCartModel>, response: Response<com.fidoo.user.data.model.AddToCartModel>) {

                    addToCartResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.AddToCartModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong in add to cart response"
                }
            })
    }
/*
 fun addToCartApi(accountId: String, accessToken: String, product_id: String, quantity: String, message: String, is_customize: String, customize_sub_cat_id: ArrayList<String>) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).addToCartApi(
            accountId = accountId,accessToken = accessToken,product_id = product_id,quantity = quantity,message = message,is_customize = is_customize,customize_sub_cat_id = customize_sub_cat_id)!!
            .enqueue(object : Callback<AddToCartModel> {

                override fun onResponse(call: Call<AddToCartModel>, response: Response<AddToCartModel>) {

                    addToCartResponse?.value = response?.body()

                }

                override fun onFailure(call: Call<AddToCartModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }
*/


    fun customizeProductApi(accountId: String, accessToken: String, product_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).customizeProductApi(
            accountId = accountId, accessToken = accessToken, product_id = product_id
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.CustomizeProductResponseModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.CustomizeProductResponseModel>, response: Response<com.fidoo.user.data.model.CustomizeProductResponseModel>) {

                    customizeProductResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.CustomizeProductResponseModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong with customization"
                }
            })
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

    fun removePromoApi(accountId: String, accessToken: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).deleteCartCouponApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.DeleteModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.DeleteModel>, response: Response<com.fidoo.user.data.model.DeleteModel>) {

                    removeCouponResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.DeleteModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong with Coupons"
                }
            })
    }


    fun addRemoveCartDetails(accountId: String, accessToken: String, product_id: String, addRemoveType: String, is_customize: String, product_customize_id: String, cart_id: String,  customize_sub_cat_id: ArrayList<String>) {

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
            .enqueue(object : Callback<com.fidoo.user.data.model.AddRemoveCartModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.AddRemoveCartModel>, response: Response<com.fidoo.user.data.model.AddRemoveCartModel>) {
                    // progressDialog?.value = false
                    addRemoveCartResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.AddRemoveCartModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong add/remove"
                }
            })
    }

    fun deleteCartDetails(accountId: String, accessToken: String, product_id: String, cart_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).deleteCartApi(
            accountId = accountId, accessToken = accessToken, product_id = product_id, cart_id = cart_id
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.DeleteModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.DeleteModel>, response: Response<com.fidoo.user.data.model.DeleteModel>) {
                    // progressDialog?.value = false
                    deleteCartResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.DeleteModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong in deleting cart"
                }
            })
    }


    fun paymentApi(accountId: String, accessToken: String, order_id: String, transaction_id: String, payment_bank: String, payment_mode: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).paymentApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            transaction_id = transaction_id,
            payment_bank = payment_bank,
            payment_mode = payment_mode
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.PaymentModel> {
                override fun onResponse(call: Call<com.fidoo.user.data.model.PaymentModel>, response: Response<com.fidoo.user.data.model.PaymentModel>) {
                    // progressDialog?.value = false
                    paymentResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.PaymentModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong with payment"
                }
            })
    }
    /*
    fun razorPayPaymentApi(accountId: String, accessToken: String, order_id: String, amount: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).razorPayPaymentApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            amount = amount
        )
            .enqueue(object : Callback<PaymentModel> {
                override fun onResponse(call: Call<PaymentModel>, response: Response<PaymentModel>) {
                    // progressDialog?.value = false
                    razorpayResponse?.value = response.body()

                }

                override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong"
                }
            })
    } */


    fun orderPlaceApi(accountId: String, accessToken: String, payment_amt: String, delivery_option: String, address_id: String, promo_id: String, delivery_instructions: String, payment_mode: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).orderPlaceApi(
            accountId = accountId,
            accessToken = accessToken,
            payment_amt = payment_amt,
            delivery_option = delivery_option,
            address_id = address_id,
            promo_id = promo_id,
            delivery_instructions = delivery_instructions,
            payment_mode = payment_mode
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.OrderPlaceModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.OrderPlaceModel>, response: Response<com.fidoo.user.data.model.OrderPlaceModel>) {
                    // progressDialog?.value = false
                    orderPlaceResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.OrderPlaceModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong while placing order"
                }
            })
    }


    fun uploadPrescriptionImage(accountId: RequestBody?, accessToken: RequestBody?,
                                image: MultipartBody.Part?) {

        val call: Call<com.fidoo.user.data.model.UploadPresModel> = WebServiceClient.client.create(BackEndApi::class.java).uploadPrescriptionImage(accountId,accessToken,image)
        call.enqueue(object : Callback<com.fidoo.user.data.model.UploadPresModel> {
            override fun onResponse(
                call: Call<com.fidoo.user.data.model.UploadPresModel>,
                response: Response<com.fidoo.user.data.model.UploadPresModel>
            ) {
                uploadPrescriptionResponse?.value = response.body()
            }

            override fun onFailure(
                call: Call<com.fidoo.user.data.model.UploadPresModel>,
                t: Throwable
            ) {
                failureResponse?.value="Something went wrong in prescription"
                Log.e("ddddd",t.message.toString())
            }
        })


    }



    fun applyPromoApi(accountId: String, accessToken: String, promocode: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).applyOffersApi(
            accountId = accountId, accessToken = accessToken, promocode = promocode
        )
            .enqueue(object : Callback<com.fidoo.user.data.model.ApplyPromoModel> {

                override fun onResponse(call: Call<com.fidoo.user.data.model.ApplyPromoModel>, response: Response<com.fidoo.user.data.model.ApplyPromoModel>) {
                    // progressDialog?.value = false
                    appplyPromoResponse?.value = response.body()

                }

                override fun onFailure(call: Call<com.fidoo.user.data.model.ApplyPromoModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong in applying coupons"
                }
            })
    }
    override fun onResponse(call: Call<com.fidoo.user.data.model.CartModel>?, response: Response<com.fidoo.user.data.model.CartModel>?) {
        getCartDetailsResponse?.value = response?.body()

    }

    override fun onFailure(call: Call<com.fidoo.user.data.model.CartModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong with Cart"
        Log.e("CART ERROR",t.toString())
    }

}