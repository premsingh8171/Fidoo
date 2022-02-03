package com.fidoo.user.cartview.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.cartview.model.CartModel
import com.fidoo.user.dashboard.model.CheckPaymentStatusModel
import com.fidoo.user.data.model.*
import com.fidoo.user.ordermodule.model.DeletePrescriptionModel
import com.fidoo.user.ordermodule.model.UploadPresModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel(application: Application) : AndroidViewModel(application), Callback<CartModel> {

    var addToCartResponse: MutableLiveData<AddToCartModel>? = null
    var cartCountResponse: MutableLiveData<CartCountModel>? = null
    var cancelOrderResponse: MutableLiveData<DeleteModel>? = null
    var getCartDetailsResponse: MutableLiveData<CartModel>? = null
    var failureResponse: MutableLiveData<String>? = null
    var addRemoveCartResponse: MutableLiveData<AddRemoveCartModel>? = null
    var appplyPromoResponse: MutableLiveData<ApplyPromoModel>? = null
    var orderPlaceResponse: MutableLiveData<OrderPlaceModel>? = null
    var paymentResponse: MutableLiveData<PaymentModel>? = null
    var paymentFailureResponse: MutableLiveData<PaymentModel>? = null
    var deleteCartResponse: MutableLiveData<DeleteModel>? = null
    var removeCouponResponse: MutableLiveData<DeleteModel>? = null
    var uploadPrescriptionResponse: MutableLiveData<UploadPresModel>? = null
    var deletePrescriptionResponse: MutableLiveData<DeletePrescriptionModel>? = null
    var customizeProductResponse: MutableLiveData<CustomizeProductResponseModel>? = null
    var razorpayResponse: MutableLiveData<PaymentModel>? = null
    var getLocationResponse: MutableLiveData<LocationResponseModel>? = null
    var proceedToOrderResponse: MutableLiveData<ProceedToOrder>? = null
    var checkPaymentStatusRes: MutableLiveData<CheckPaymentStatusModel>? = null

    init {
        addToCartResponse = MutableLiveData<AddToCartModel>()
        cancelOrderResponse = MutableLiveData<DeleteModel>()
        getCartDetailsResponse = MutableLiveData<CartModel>()
        failureResponse = MutableLiveData<String>()
        addRemoveCartResponse = MutableLiveData<AddRemoveCartModel>()
        appplyPromoResponse = MutableLiveData<ApplyPromoModel>()
        orderPlaceResponse = MutableLiveData<OrderPlaceModel>()
        paymentResponse = MutableLiveData<PaymentModel>()
        paymentFailureResponse = MutableLiveData<PaymentModel>()
        deleteCartResponse = MutableLiveData<DeleteModel>()
        removeCouponResponse = MutableLiveData<DeleteModel>()
        customizeProductResponse = MutableLiveData<CustomizeProductResponseModel>()
        uploadPrescriptionResponse = MutableLiveData<UploadPresModel>()
        deletePrescriptionResponse = MutableLiveData<DeletePrescriptionModel>()
        razorpayResponse = MutableLiveData<PaymentModel>()
        getLocationResponse = MutableLiveData<LocationResponseModel>()
        cartCountResponse = MutableLiveData<CartCountModel>()
        proceedToOrderResponse = MutableLiveData<ProceedToOrder>()
        checkPaymentStatusRes = MutableLiveData<CheckPaymentStatusModel>()

    }

    fun getCartDetails(accountId: String, accessToken: String, userLat: String, userlong: String) {
    //    try {
            Log.d("getCar_tDetails",accountId+"\n"+accessToken+"\n"+userLat+"\n"+userlong)
    //    }catch (e:Exception){}

        WebServiceClient.client.create(BackEndApi::class.java).getCartDetailsApi(
            accountId = accountId, accessToken = accessToken, userLat= userLat, userLong = userlong
        ).enqueue(this)
    }

    fun addToCartApi(accountId: String, accessToken: String, products: ArrayList<AddCartInputModel>, cart_id: String) {

        var addCartInputModelFinal = AddCartInputModelFinal()
        addCartInputModelFinal.accessToken = accessToken
        addCartInputModelFinal.accountId = accountId
        addCartInputModelFinal.cart_id = cart_id
        addCartInputModelFinal.products = ArrayList<Product>()
        for (i in 0 until products.size) {
            var temp = Product(
                products.get(i).productId,
                products.get(i).customizeSubCatId,
                products.get(i).isCustomize,
                products.get(i).message,
                products.get(i).quantity
            )

            addCartInputModelFinal.products.add(temp)
        }
        Log.d("addCartInputModelFinaldd", Gson().toJson(addCartInputModelFinal))
        WebServiceClient.client.create(BackEndApi::class.java).addToCartApi(addCartInputModelFinal)
            .enqueue(object : Callback<AddToCartModel> {

                override fun onResponse(call: Call<AddToCartModel>, response: Response<AddToCartModel>) {

                    addToCartResponse?.value = response.body()

                }

                override fun onFailure(call: Call<AddToCartModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong in add to cart response"
                }
            })
    }

    fun customizeProductApi(accountId: String, accessToken: String, product_id: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).customizeProductApi(
            accountId = accountId, accessToken = accessToken, product_id = product_id
        )
            .enqueue(object : Callback<CustomizeProductResponseModel> {

                override fun onResponse(call: Call<CustomizeProductResponseModel>, response: Response<CustomizeProductResponseModel>) {

                    customizeProductResponse?.value = response.body()

                }

                override fun onFailure(call: Call<CustomizeProductResponseModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong with customization"
                }
            })
    }

    fun cancelOrderApi(accountId: String, accessToken: String, orderId: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).cancelOrderApi(
            accountId = accountId, accessToken = accessToken, order_id = orderId
        )
            .enqueue(object : Callback<DeleteModel> {

                override fun onResponse(call: Call<DeleteModel>, response: Response<DeleteModel>) {

                    cancelOrderResponse?.value = response.body()

                }

                override fun onFailure(call: Call<DeleteModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
                }
            })
    }

    fun removePromoApi(accountId: String, accessToken: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).deleteCartCouponApi(
            accountId = accountId, accessToken = accessToken
        )
            .enqueue(object : Callback<DeleteModel> {

                override fun onResponse(call: Call<DeleteModel>, response: Response<DeleteModel>) {

                    removeCouponResponse?.value = response.body()

                }

                override fun onFailure(call: Call<DeleteModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong with Coupons"
                }
            })
    }

    fun addRemoveCartDetails(accountId: String, accessToken: String, product_id: String, addRemoveType: String, is_customize: String, product_customize_id: String, cart_id: String,  customize_sub_cat_id: ArrayList<String>) {
        Log.d("addRemoveCartDetails___","$accountId\n$accessToken\n$product_id\n$addRemoveType$\n$is_customize\n$product_customize_id\n$cart_id$\n$customize_sub_cat_id")

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
                    addRemoveCartResponse?.value = response.body()
                }

                override fun onFailure(call: Call<AddRemoveCartModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong add/remove"
                }

            })
    }

    fun deleteCartDetails(accountId: String, accessToken: String, product_id: String, cart_id: String) {

        Log.d("deleteCartDetails__",accountId+"\n"+accessToken+"\n"+product_id+"\n"+cart_id)
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).deleteCartApi(
            accountId = accountId, accessToken = accessToken, product_id = product_id, cart_id = cart_id
        ).enqueue(object : Callback<DeleteModel> {

                override fun onResponse(call: Call<DeleteModel>, response: Response<DeleteModel>) {
                    // progressDialog?.value = false
                    deleteCartResponse?.value = response.body()
                }

                override fun onFailure(call: Call<DeleteModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong in deleting cart"
                }
            })
    }
    
    fun paymentApi(accountId: String, accessToken: String, order_id: String, transaction_id: String, payment_bank: String, payment_mode: String) {
        Log.d("paymentApi__",accountId+"\n"+accessToken+"\n"+order_id+"\n"+transaction_id+"\n"+payment_bank+"\n"+payment_mode)
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).paymentApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id,
            transaction_id = transaction_id,
            payment_bank = payment_bank,
            payment_mode = payment_mode
        ).enqueue(object : Callback<PaymentModel> {
            override fun onResponse(call: Call<PaymentModel>, response: Response<PaymentModel>) {
                // progressDialog?.value = false
                paymentResponse?.value = response.body()
            }

            override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                //  progressDialog?.value = false
                failureResponse?.value = "Something went wrong with payment"
            }
        })
    }

    fun paymentFailureApi(accountId: String, accessToken: String, order_id: String) {
        Log.d("paymentApi__",accountId+"\n"+accessToken+"\n"+order_id)
        WebServiceClient.client.create(BackEndApi::class.java).paymentFailureApi(
            accountId = accountId,
            accessToken = accessToken,
            order_id = order_id
        ).enqueue(object : Callback<PaymentModel> {
            override fun onResponse(call: Call<PaymentModel>, response: Response<PaymentModel>) {
                paymentFailureResponse?.value = response.body()
            }

            override fun onFailure(call: Call<PaymentModel>, t: Throwable) {
                failureResponse?.value = "Something went wrong with payment"
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
    }
    */

    fun orderPlaceApi(accountId: String, accessToken: String, payment_amt: String, delivery_option: String, address_id: String, promo_id: String, delivery_instructions: String, payment_mode: String,merchant_instructions: String) {
        try {
            Log.d("orderPlaceApi__",accountId+"\n"+accessToken+"\n"+payment_amt+"\n"+delivery_option+"\n"+address_id+"\n"+promo_id+"\n"+delivery_instructions+"\n"+payment_mode+"\n"+merchant_instructions)
        }catch (e:Exception){}
        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).orderPlaceApi(
            accountId = accountId,
            accessToken = accessToken,
            payment_amt = payment_amt,
            delivery_option = delivery_option,
            address_id = address_id,
            promo_id = promo_id,
            delivery_instructions = delivery_instructions,
            payment_mode = payment_mode,
            merchant_instructions = merchant_instructions
        )
            .enqueue(object : Callback<OrderPlaceModel> {


                override fun onResponse(call: Call<OrderPlaceModel>, response: Response<OrderPlaceModel>) {
                    // progressDialog?.value = false
                    orderPlaceResponse?.value = response.body()

                }

                override fun onFailure(call: Call<OrderPlaceModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong while placing order"
                }
            })
    }

    fun uploadPrescriptionImage(accountId: RequestBody?, accessToken: RequestBody?,image: MultipartBody.Part?) {
        val call: Call<UploadPresModel> = WebServiceClient.client.create(BackEndApi::class.java).uploadPrescriptionImage(accountId,accessToken,image)

        call.enqueue(object : Callback<UploadPresModel> {
            override fun onResponse(
                call: Call<UploadPresModel>,
                response: Response<UploadPresModel>
            ) {
                uploadPrescriptionResponse?.value = response.body()
            }
            override fun onFailure(
                call: Call<UploadPresModel>,
                t: Throwable
            ) {
                failureResponse?.value="Something went wrong in prescription"
                Log.e("ddddd",t.message.toString())
            }
        })

    }

    fun deletePrescriptionApi(accountId: String, accessToken: String, document_id: String) {
        Log.d("deletePrescriptionApi_","$accountId--+$accessToken--$document_id")
        WebServiceClient.client.create(BackEndApi::class.java).deletePrescriptionApi(
            accountId = accountId, accessToken = accessToken, document_id = document_id
        )
            .enqueue(object : Callback<DeletePrescriptionModel> {

                override fun onResponse(call: Call<DeletePrescriptionModel>, response: Response<DeletePrescriptionModel>) {
                    deletePrescriptionResponse?.value = response.body()
                }
                override fun onFailure(call: Call<DeletePrescriptionModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong in deleting cart"
                }
            })
    }

    fun applyPromoApi(accountId: String, accessToken: String, promocode: String) {

        // progressDialog?.value = true
        WebServiceClient.client.create(BackEndApi::class.java).applyOffersApi(
            accountId = accountId, accessToken = accessToken, promocode = promocode
        )
            .enqueue(object : Callback<ApplyPromoModel> {

                override fun onResponse(call: Call<ApplyPromoModel>, response: Response<ApplyPromoModel>) {
                    // progressDialog?.value = false
                    appplyPromoResponse?.value = response.body()

                }

                override fun onFailure(call: Call<ApplyPromoModel>, t: Throwable) {
                    //  progressDialog?.value = false
                    failureResponse?.value="Something went wrong in applying coupons"
                }
            })
    }

    fun getCartCountApi(accountId: String, accessToken: String) {
        Log.e("cartCount_params","$accountId--$accessToken")
        WebServiceClient.client.create(BackEndApi::class.java).cartCountApi(
            accountId = accountId, accessToken = accessToken
        ).enqueue(object : Callback<CartCountModel> {

                override fun onResponse(call: Call<CartCountModel>, response: Response<CartCountModel>) {
                    cartCountResponse?.value = response.body()
                }

                override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                    failureResponse?.value="Something went wrong"
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

    override fun onResponse(call: Call<CartModel>?, response: Response<CartModel>?) {
        try {
            getCartDetailsResponse?.value = response?.body()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFailure(call: Call<CartModel>?, t: Throwable?) {
        failureResponse?.value="Something went wrong with Cart"
        Log.e("CART ERROR",t.toString())
    }


    fun checkPaymentStatusApi(accountId: String, accessToken: String) {
        Log.e("checkPaymentStatusApi__", "$accountId, $accessToken")
        WebServiceClient.client.create(BackEndApi::class.java).checkPaymentStatusApi(
            accountId = accountId, accessToken = accessToken
        ).enqueue(object : Callback<CheckPaymentStatusModel> {
            override fun onResponse(
                call: Call<CheckPaymentStatusModel>,
                response: Response<CheckPaymentStatusModel>
            ) {
                checkPaymentStatusRes?.value = response.body()
            }

            override fun onFailure(call: Call<CheckPaymentStatusModel>, t: Throwable) {
                failureResponse?.value = "Something went wrong"
            }
        })
    }

}