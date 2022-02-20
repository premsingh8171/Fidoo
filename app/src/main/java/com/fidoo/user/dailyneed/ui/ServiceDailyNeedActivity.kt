package com.fidoo.user.dailyneed.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.dailyneed.adapter.DailyNeedMainAdapter
import com.fidoo.user.dailyneed.model.Category
import com.fidoo.user.dailyneed.model.Data
import com.fidoo.user.dailyneed.model.Subcategory
import com.fidoo.user.dailyneed.onclicklistener.ItemOnClickListener
import com.fidoo.user.dailyneed.viewmodel.DailyNeedViewModel
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.AddToCartModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.newsearch.ui.NewSearchActivity
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.activity_service_daily_need.*
import kotlinx.android.synthetic.main.no_item_found.*
import java.math.RoundingMode

@Suppress("DEPRECATION")
class ServiceDailyNeedActivity : BaseActivity(), ItemOnClickListener {
	var viewmodel: DailyNeedViewModel? = null
	var dailyNeedMainAdapter: DailyNeedMainAdapter? = null
	var customIdsList: ArrayList<String>? = null
	private var slide_: Animation? = null
	var dataList: ArrayList<Data>? = null
	var updateList: ArrayList<Data>? = null

	//int variable
	var check = 0
	var height = 0
	var width = 0
	var count: Int = 1
	var total_cart_count: Int = 0

	//string variable
	var serviceId: String? = ""
	var serviceName: String? = ""
	var sessionTwiclo: SessionTwiclo? = null
	val SEARCH_RESULT_CODE = 105
	val VIEWALL_RESULT_CODE = 102
	var product_Update: Int? = 0 //api hit handle on resume

	private var mMixpanel: MixpanelAPI? = null

	companion object {
		var product_count: Int? = 0
		var product_Id: String? = ""
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_service_daily_need)
		viewmodel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
			.create(DailyNeedViewModel::class.java)
		val metrics: DisplayMetrics = this.getResources().getDisplayMetrics()
		width = metrics.widthPixels
		sessionTwiclo = SessionTwiclo(this)

		mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

		if (intent.getStringExtra("serviceId")!!.isNotEmpty()) {
			serviceId = intent.getStringExtra("serviceId")
			serviceName = intent.getStringExtra("serviceName")
			store_name_.setText(serviceName)
			Log.d("serviceId__", serviceId.toString())
		}

		customIdsList = ArrayList()

		onClick()
		fidooLoaderShow()
		apiHit()
		responseObserve()
		swipeRefreshLayDaily.setOnRefreshListener {
			apiHit()
		}

	}

	private fun apiHit() {
		if (isNetworkConnected) {
			//showIOSProgress()

			if (SessionTwiclo(this).isLoggedIn) {
				viewmodel?.getCartCountApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken
				)
				viewmodel!!.getserviceDetailsPrdDrivenApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken,
					serviceId!!,
					SessionTwiclo(this).userLat,
					SessionTwiclo(this).userLng
				)
			} else {
				viewmodel!!.getserviceDetailsPrdDrivenApi(
					"",
					"",
					serviceId!!,
					SessionTwiclo(this).userLat,
					SessionTwiclo(this).userLng
				)
			}
		} else {
			showInternetToast()
		}
	}

	private fun responseObserve() {

		viewmodel?.serviceDetailsPrdDrivenRes?.observe(this, {
			Log.d("serviceDetails__", Gson().toJson(it))
			//	dismissIOSProgress()
			dataList = it.data as ArrayList

			Log.d("serviceDetails__", dataList!!.size.toString())
			fidooLoaderCancel()
			swipeRefreshLayDaily.isRefreshing = false

			if (dataList!!.size != 0) {
				no_itemsFound_res.visibility = View.GONE
				no_item_foundll.visibility = View.GONE
				if (product_Update == 0) {
					rvList(dataList!!)
				} else {
					updateList = it.data
					dailyNeedMainAdapter!!.updateData(updateList!!)
				}
			} else {
				no_itemsFound_res.visibility = View.VISIBLE
				no_item_foundll.visibility = View.VISIBLE
			}

		})

		//for clear store item
		viewmodel?.clearCartResponse?.observe(this, { user ->
			Log.e("clearCartResponse", Gson().toJson(user))
			dismissIOSProgress()
			val addCartInputModel = AddCartInputModel()
			addCartInputModel.productId = product_Id
			addCartInputModel.quantity = product_count.toString()
			addCartInputModel.message = "add product"
			addCartInputModel.customizeSubCatId = customIdsList!!
			addCartInputModel.isCustomize = "0"
			MainActivity.addCartTempList!!.add(0, addCartInputModel)

			viewmodel!!.addToCartApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken,
				MainActivity.addCartTempList!!,
				""
			)
		})

		//first time add item
		viewmodel?.addToCartResponse?.observe(this, { user ->
			Log.e("addToCartResponse__", Gson().toJson(user))
			val mModelData: AddToCartModel = user
			MainActivity.addCartTempList!!.clear()
			check = 1
			viewmodel?.getCartCountApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken
			)
		})

		//for plus and minus of item
		viewmodel?.addRemoveCartResponse?.observe(this, { user ->
			Log.e("cart_response", Gson().toJson(user))
			if (isNetworkConnected) {
				check = 1
				viewmodel?.getCartCountApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken
				)
			} else {
				showInternetToast()
			}
		})

		//cartCount Response
		viewmodel?.cartCountResponse?.observe(this, { cartcount ->
			dismissIOSProgress()
			Log.d("cartCountResponse___", cartcount.toString())
			var count = cartcount.count
			var price = cartcount.price

			if (!cartcount.error) {
				if (count != "0") {
					itemQuan_text.text = count
					val rounded = price.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

					totalPrice_Text.text = "₹ " + rounded.toString()
					cartCountFm.visibility = View.VISIBLE
					if (total_cart_count == 0) {
						total_cart_count = 1
						slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
						cartCountFm?.startAnimation(slide_)
					}
				} else {
					SessionTwiclo(this).storeId = ""
					cartCountFm.visibility = View.GONE
					total_cart_count = 0
					slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
					cartCountFm?.startAnimation(slide_)
				}
			}

		})

	}

	private fun onClick() {

		backIcon_.setOnClickListener {
			if (check == 0) {
				AppUtils.finishActivityLeftToRight(this);
			} else {
				check = 0
				val returnIntent = Intent()
				setResult(RESULT_OK, returnIntent)
				finish()

			}
		}

		search_onDailyNeed_.setOnClickListener {
			AppUtils.startActivityForResultRightToLeft(
				this, Intent(this, NewSearchActivity::class.java)
					.putExtra("storeId", "").putExtra("service_id", serviceId), SEARCH_RESULT_CODE
			)
		}

		cartCountFm.setOnClickListener {

			if (SessionTwiclo(this).isLoggedIn) {
				AppUtils.startActivityForResultRightToLeft(
					this, Intent(this, CartActivity::class.java)
						.putExtra("storeId", SessionTwiclo(this).storeId), VIEWALL_RESULT_CODE
				)
//				startActivity(
//					Intent(this, CartActivity::class.java).putExtra(
//						"store_id", SessionTwiclo(this).storeId
//					)
//				)
			} else {
				showLoginDialog("Please login to proceed")
			}
		}
	}

	private fun rvList(arrayList: ArrayList<Data>) {
		dailyNeedMainAdapter = DailyNeedMainAdapter(
			this@ServiceDailyNeedActivity,
			arrayList,
			this,
			object : DailyNeedMainAdapter.ItemClick {
				override fun onItemClick(
					main_position: Int,
					pos: Int,
					category_id: String,
					model: Subcategory
				) {
					AppUtils.startActivityForResultRightToLeft(
						this@ServiceDailyNeedActivity,
						Intent(
							this@ServiceDailyNeedActivity,
							CategoryProductListActivity::class.java
						)
							.putExtra("category_id", category_id)
							.putExtra("subCategory_id", model.subcategory_id)
							.putExtra("service_id", serviceId)
							.putExtra("category_name", model.category_name), VIEWALL_RESULT_CODE
					)
				}
			},
			width
		)

		dailyneedMainRv?.adapter = dailyNeedMainAdapter
	}

	fun clearCart() {
		showIOSProgress()
		viewmodel?.clearCartApi(
			SessionTwiclo(this).loggedInUserDetail.accountId,
			SessionTwiclo(this).loggedInUserDetail.accessToken
		)
	}

	override fun onBackPressed() {
		if (check == 0) {
			AppUtils.finishActivityLeftToRight(this);
		} else {
			check = 0
			val returnIntent = Intent()
			setResult(RESULT_OK, returnIntent)
			finish()

		}
	}

	override fun addtoCart(
		main_position: Int,
		productModel: com.fidoo.user.dailyneed.model.Product,
		pos: Int,
		store_id: String,
		item_count: String,
		type: String
	) {
		product_Id = productModel.product_id
		product_count = item_count!!.toInt()
		Log.d("typetype", type + "-" + store_id)
		if (type.equals("Replace")) {
			clearCart()
			SessionTwiclo(this).storeId = store_id
		} else {
			val addCartInputModel = AddCartInputModel()
			addCartInputModel.productId = productModel.product_id
			addCartInputModel.quantity = item_count
			addCartInputModel.message = "add product"
			addCartInputModel.customizeSubCatId = customIdsList!!
			addCartInputModel.isCustomize = "0"
			MainActivity.addCartTempList!!.add(0, addCartInputModel)
			viewmodel!!.addToCartApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken,
				MainActivity.addCartTempList!!,
				""
			)
		}
		dataList?.get(main_position)!!.products[pos].cart_quantity = item_count
		Log.d("item_count", item_count)
	}

	override fun plusItemCart(
		main_position: Int,
		productModel: com.fidoo.user.dailyneed.model.Product,
		pos: Int,
		store_id: String,
		item_count: String,
		type: String
	) {
		Log.d("item_count", item_count)
		val addCartInputModel = AddCartInputModel()
		addCartInputModel.productId = productModel.product_id
		addCartInputModel.quantity = item_count
		addCartInputModel.message = "add product"
		addCartInputModel.customizeSubCatId = customIdsList!!
		addCartInputModel.isCustomize = "0"
		MainActivity.addCartTempList!!.add(0, addCartInputModel)
		viewmodel!!.addToCartApi(
			SessionTwiclo(this).loggedInUserDetail.accountId,
			SessionTwiclo(this).loggedInUserDetail.accessToken,
			MainActivity.addCartTempList!!,
			""
		)
		dataList?.get(main_position)!!.products[pos].cart_quantity = item_count
	}

	override fun minusItemCart(
		main_position: Int,
		productModel: com.fidoo.user.dailyneed.model.Product,
		pos: Int,
		store_id: String,
		item_count: String,
		type: String
	) {
		Log.d("item_count", item_count)
		dataList?.get(main_position)!!.products[pos].cart_quantity = item_count
		viewmodel?.addRemoveCartDetails(
			SessionTwiclo(this).loggedInUserDetail.accountId,
			SessionTwiclo(this).loggedInUserDetail.accessToken,
			productModel.product_id,
			"remove",
			productModel.is_customize,
			productModel.is_customize_quantity,
			"",
			customIdsList!!
		)
	}

	override fun onClickViewAll(main_position: Int, model: Data) {
		AppUtils.startActivityForResultRightToLeft(
			this, Intent(this, CategoryProductListActivity::class.java)
				.putExtra("category_id", model.category_id)
				.putExtra("category_name", model.category_name)
				.putExtra("service_id", serviceId)
				.putExtra("subCategory_id", model.subcategory_id), VIEWALL_RESULT_CODE
		)
	}

	override fun onClickCatView(main_position: Int, pos: Int, model: Category) {
		AppUtils.startActivityForResultRightToLeft(
			this, Intent(this, CategoryProductListActivity::class.java)
				.putExtra("category_id", model.category_id)
				.putExtra("category_name", model.category_name)
				.putExtra("service_id", serviceId)
				.putExtra("subCategory_id", ""), VIEWALL_RESULT_CODE

		)
	}

	private fun showLoginDialog(message: String) {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Alert")
		builder.setMessage(message)
		// builder.setIcon(android.R.drawable.ic_dialog_alert)
		builder.setPositiveButton("Login") { _, _ ->
			sessionTwiclo!!.clearSession()
			startActivity(
				Intent(
					this,
					SplashActivity::class.java
				).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
			)
		}

		builder.setNegativeButton("Cancel") { _, _ -> }
		val alertDialog: AlertDialog = builder.create()
		alertDialog.setCancelable(true)
		alertDialog.show()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		Log.e("search_result_coe", "$requestCode - $SEARCH_RESULT_CODE - $resultCode")

		if (requestCode == SEARCH_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				product_Update = 1
				//	dataList?.clear()
				showIOSProgress()
				apiHit()
				product_Update = 0
			}
		}

		if (requestCode == VIEWALL_RESULT_CODE) {

			if (resultCode == RESULT_OK) {
				product_Update = 1
				//dataList?.clear()
				showIOSProgress()
				apiHit()
				product_Update = 0
			}

			viewmodel?.getCartCountApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken
			)
		}

	}


}