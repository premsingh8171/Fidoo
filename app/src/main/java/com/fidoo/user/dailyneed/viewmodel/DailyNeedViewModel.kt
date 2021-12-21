package com.fidoo.user.dailyneed.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.dailyneed.model.DailyNeedModel
import com.fidoo.user.dailyneed.model.prdmodel.ViewAllProduct
import com.fidoo.user.data.model.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DailyNeedViewModel(application: Application) : AndroidViewModel(application),
	Callback<DailyNeedModel> {
	var serviceDetailsPrdDrivenRes: MutableLiveData<DailyNeedModel>? = null
	var viewAllproductsProductDrivenRes: MutableLiveData<ViewAllProduct>? = null
	var failureResponse: MutableLiveData<String>? = null

	//start for add to cart
	var addToCartResponse: MutableLiveData<AddToCartModel>? = null
	var cartCountResponse: MutableLiveData<CartCountModel>? = null
	var clearCartResponse: MutableLiveData<ClearCartModel>? = null
	var addRemoveCartResponse: MutableLiveData<AddRemoveCartModel>? = null
	//end

	init {
		serviceDetailsPrdDrivenRes = MutableLiveData<DailyNeedModel>()
		viewAllproductsProductDrivenRes = MutableLiveData<ViewAllProduct>()
		failureResponse = MutableLiveData<String>()
		addToCartResponse = MutableLiveData<AddToCartModel>()
		clearCartResponse = MutableLiveData<ClearCartModel>()
		addRemoveCartResponse = MutableLiveData<AddRemoveCartModel>()
		cartCountResponse = MutableLiveData<CartCountModel>()
	}

	fun getserviceDetailsPrdDrivenApi(
		accountId: String,
		accessToken: String,
		service_id: String,
		user_lat: String,
		user_long: String
	) {
		Log.d("api_call", "$accountId--$accessToken--$service_id--$user_lat--$user_long")
		WebServiceClient.client.create(BackEndApi::class.java).getserviceDetailsProductDrivenApi(
			accountId = accountId,
			accessToken = accessToken,
			service_id = service_id,
			user_lat = user_lat,
			user_long = user_long,
		).enqueue(this)

	}

	override fun onResponse(call: Call<DailyNeedModel>, response: Response<DailyNeedModel>) {
		serviceDetailsPrdDrivenRes?.value = response.body()
	}

	override fun onFailure(call: Call<DailyNeedModel>, t: Throwable) {
		failureResponse?.value = "Something went wrong"
	}

	fun ViewAllProductApi(
		accountId: String,
		accessToken: String,
		category_id: String?,
		user_lat: String,
		user_long: String
	) {
		Log.d("ViewAllProductApi","$accountId--$accessToken--$category_id--$user_lat--$user_long")
		WebServiceClient.client.create(BackEndApi::class.java).viewAllproductsProductDriven(
			accountId = accountId,
			accessToken = accessToken,
			category_id = category_id,
			user_lat = user_lat,
			user_long = user_long
		).enqueue(object : Callback<ViewAllProduct> {

			override fun onResponse(
				call: Call<ViewAllProduct>,
				response: Response<ViewAllProduct>
			) {

				viewAllproductsProductDrivenRes?.value = response.body()

			}

			override fun onFailure(call: Call<ViewAllProduct>, t: Throwable) {
				failureResponse?.value = "Something went wrong"
			}
		})

	}

	fun addToCartApi(
		accountId: String,
		accessToken: String,
		products: ArrayList<AddCartInputModel>,
		cart_id: String
	) {
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
		} catch (e: Exception) {
			e.printStackTrace()
		}

		//}
		addCartInputModelList.add(addCartInputModelFinal)
		Log.d("add_cart", Gson().toJson(addCartInputModelFinal))
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
		WebServiceClient.client.create(BackEndApi::class.java)
			.clearCartApi(accountId = accountId, accessToken = accessToken)
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
		Log.e("cart_count_params", "$accountId--$accessToken")
		WebServiceClient.client.create(BackEndApi::class.java)
			.cartCountApi(accountId = accountId, accessToken = accessToken)
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

	fun addRemoveCartDetails(
		accountId: String,
		accessToken: String,
		product_id: String,
		addRemoveType: String,
		is_customize: String,
		product_customize_id: String,
		cart_id: String,
		customize_sub_cat_id: ArrayList<String>
	) {
		Log.d(
			"addRemoveCartDetails",
			"$accountId--$accessToken--$product_id--$addRemoveType--$is_customize--$product_customize_id--$cart_id--$customize_sub_cat_id"
		)
		WebServiceClient.client.create(BackEndApi::class.java).addRemoveCartApi(
			accountId = accountId,
			accessToken = accessToken,
			product_id = product_id,
			addRemoveType = addRemoveType,
			is_customize = is_customize,
			product_customize_id = product_customize_id,
			cart_id = cart_id,
			customize_sub_cat_id = customize_sub_cat_id
		).enqueue(object : Callback<AddRemoveCartModel> {
			override fun onResponse(
				call: Call<AddRemoveCartModel>,
				response: Response<AddRemoveCartModel>
			) {
				addRemoveCartResponse?.value = response.body()
			}

			override fun onFailure(call: Call<AddRemoveCartModel>, t: Throwable) {
				failureResponse?.value = "Something went wrong"
			}
		})
	}
}