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
import androidx.viewpager.widget.ViewPager
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.dailyneed.adapter.pageradapter.SliderAdapterProduct
import com.fidoo.user.dailyneed.adapter.prdadapter.CircularTabAdapter
import com.fidoo.user.dailyneed.adapter.prdadapter.TabAdapter
import com.fidoo.user.dailyneed.model.prdmodel.Data
import com.fidoo.user.dailyneed.model.prdmodel.Product
import com.fidoo.user.dailyneed.onclicklistener.ItemOnCatClickListener
import com.fidoo.user.dailyneed.viewmodel.DailyNeedViewModel
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.newsearch.ui.NewSearchActivity
import com.fidoo.user.utils.BaseActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_category_product_list.*
import kotlinx.android.synthetic.main.no_item_found.*
import org.json.JSONObject

@Suppress("DEPRECATION")
class CategoryProductListActivity : BaseActivity(), ItemOnCatClickListener {
	var viewmodel: DailyNeedViewModel? = null
	var circularTabAdapter: CircularTabAdapter? = null
	var tabAdapter: TabAdapter? = null
	var sliderAdapterProduct: SliderAdapterProduct? = null
	var mainDataList: ArrayList<Data>? = null
	var customIdsList: ArrayList<String>? = null
	var total_cart_count: Int = 0
	private var slide_: Animation? = null
	var check = 0
	var width = 0
	var width2 = 0
	var height = 0
	var selectedTab = 0
	var category_id = 0
	val SEARCH_RESULT_CODE = 107
	val VIEWALL_RESULT_CODE = 107
	var product_Update = 0
	var sessionTwiclo: SessionTwiclo? = null
	var category_idStr: String? = ""
	var subCategory_id: String? = ""
	var category_name: String? = ""
	var service_id: String? = ""
	var scrollDown = false
	private val props = JSONObject()

	private var mMixpanel: MixpanelAPI? = null

	companion object {
		var product_count: String? = ""
		var product_Id: String? = ""
	}

	private var barOffset = 0
	private var fabVisible = true

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_category_product_list)
		viewmodel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
			.create(DailyNeedViewModel::class.java)
		mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
		mMixpanel?.track("Daily Needs")
		mainDataList = ArrayList()
		customIdsList = ArrayList()
		val metrics: DisplayMetrics = this.resources.displayMetrics
		width = metrics.widthPixels
		width2 = (metrics.widthPixels / 2) - 20
		height = (metrics.heightPixels) - 120

//		viewPagerBanner_!!.layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)

		sessionTwiclo = SessionTwiclo(this)

		if (intent.getStringExtra("category_id")!!.isNotEmpty()) {
			category_idStr = intent.getStringExtra("category_id")
			subCategory_id = intent.getStringExtra("subCategory_id")
			category_name = intent.getStringExtra("category_name")
			service_id = intent.getStringExtra("service_id")
			Log.d("serviceId__", "$category_idStr--$category_name---$subCategory_id")
			store_name_cat.text = category_name
		}

		viewPagerBanner_!!.setClipToPadding(false)

		val viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
			object : ViewPager.OnPageChangeListener {
				override fun onPageSelected(position: Int) {
					selectedTab = position
					Log.d("selectedTab___", selectedTab.toString())
					circularTabAdapter?.customNotifyDataChanged(mainDataList!!, selectedTab)
					tabAdapter?.customNotifyDataChanged(mainDataList!!, selectedTab)
					circularHeaderRv?.smoothScrollToPosition(selectedTab)
					tabHeaderRv?.smoothScrollToPosition(selectedTab)
				}
				override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
				override fun onPageScrollStateChanged(arg0: Int) {}
			}

		viewPagerBanner_.addOnPageChangeListener(viewPagerPageChangeListener)

		onClick()
		forApiHit()
		observer()

	}

	private fun sliderviewPager(arrayList_: ArrayList<Data>) {

//		if (subCategory_id!!.isNotEmpty()) {
//			for (i in arrayList_.indices) {
//				if (subCategory_id.equals(arrayList_[i].inventory_subcat_id)) {
//					Log.d("subCategory_idPos", i.toString())
//
//					viewPagerBanner_.setCurrentItem(i, true)
//					circularTabAdapter?.customNotifyDataChanged(mainDataList!!, i)
//					tabAdapter?.customNotifyDataChanged(mainDataList!!, i)
//					circularHeaderRv?.smoothScrollToPosition(i)
//					tabHeaderRv?.smoothScrollToPosition(i)
//					break
//				}
//			}
//		}

		sliderAdapterProduct = SliderAdapterProduct(
			this,
			arrayList_,
			this,
			object : SliderAdapterProduct.OnScrollMeasurement {
				override fun nestedScrollMeasurement(
					scrollX: Int,
					scrollY: Int,
					oldScrollX: Int,
					oldScrollY: Int
				) {

//					if (scrollY > oldScrollY) {
//						if (scrollY > 170) {
//							headerTabll.visibility = View.VISIBLE
//							circularHeaderRv.visibility = View.GONE
//							Log.d("storeVisibilityNested11", "$scrollY--$oldScrollY")
//						}
//					} else if (scrollX == scrollY) {
//						if (scrollY < 110) {
//							headerTabll.visibility = View.GONE
//							circularHeaderRv.visibility = View.VISIBLE
//						}
//
//						Log.d("storeVisibilityNested1", "$scrollY--$oldScrollY")
//					} else {
//						Log.d("storeVisibilityNested2", "$scrollY--$oldScrollY")
//					}

				}
			},
			width2
		)
		viewPagerBanner_.adapter = sliderAdapterProduct

	}

	private fun rvHorizontalTab(arrayList_: ArrayList<Data>) {
		tabAdapter = TabAdapter(this, arrayList_, object : TabAdapter.ItemClickShop {
			override fun onItemClick(pos: Int, model: Data) {
				selectedTab = pos
				viewPagerBanner_.setCurrentItem(selectedTab, true)
				circularTabAdapter?.customNotifyDataChanged(arrayList_, selectedTab)
				tabAdapter?.customNotifyDataChanged(arrayList_, selectedTab)
				circularHeaderRv?.smoothScrollToPosition(selectedTab)
				tabHeaderRv?.smoothScrollToPosition(selectedTab)
			}
		}, width, selectedTab)

		tabHeaderRv?.adapter = tabAdapter

	}

	private fun rvCircularTab(arrayList_: ArrayList<Data>) {
		circularTabAdapter =
			CircularTabAdapter(this, arrayList_, object : CircularTabAdapter.ItemClickShop {
				override fun onItemClick(pos: Int, model: Data) {
					selectedTab = pos
					viewPagerBanner_.setCurrentItem(selectedTab, true)
					circularTabAdapter?.customNotifyDataChanged(arrayList_, selectedTab)
					tabAdapter?.customNotifyDataChanged(arrayList_, selectedTab)
					circularHeaderRv?.smoothScrollToPosition(selectedTab)
					tabHeaderRv?.smoothScrollToPosition(selectedTab)
				}
			}, width, selectedTab)
		circularHeaderRv?.adapter = circularTabAdapter
	}

	private fun forApiHit() {
		//	showIOSProgress()
		fidooLoaderShow()

		if (SessionTwiclo(this).isLoggedIn) {
			viewmodel?.ViewAllProductApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken, category_idStr,
				SessionTwiclo(this).userLat,
				SessionTwiclo(this).userLng
			)
			viewmodel?.getCartCountApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken
			)
		}else{
			viewmodel?.ViewAllProductApi(
				"",
				"", category_idStr,
				SessionTwiclo(this).userLat,
				SessionTwiclo(this).userLng
			)
		}

	}

	private fun observer() {
		viewmodel?.viewAllproductsProductDrivenRes!!.observe(this, {
			Log.d("viewAllproductsProductDriven_", Gson().toJson(it))
			fidooLoaderCancel()
			if (it.error_code == 200) {
				dismissIOSProgress()
				mainDataList = it.data as ArrayList
				try {
					if (mainDataList!![0].products.isNotEmpty()) {
						rvCircularTab(mainDataList!!)
						rvHorizontalTab(mainDataList!!)
						sliderviewPager(mainDataList!!)
						no_item_foundll.visibility = View.GONE
						no_itemsFound_res.visibility = View.GONE

					} else {
						no_item_foundll.visibility = View.VISIBLE
						no_itemsFound_res.visibility = View.VISIBLE

					}
				} catch (e: Exception) {
					e.printStackTrace()
					no_item_foundll.visibility = View.VISIBLE
					no_itemsFound_res.visibility = View.VISIBLE
				}
			}
		})

		viewmodel?.failureResponse?.observe(this, {
			dismissIOSProgress()
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
			check = 1
			if (isNetworkConnected) {
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
					itemQuan_text_.text = count
					totalPrice_Text_.text = "₹ " + price
					cartCountFm_.visibility = View.VISIBLE
					if (total_cart_count == 0) {
						total_cart_count = 1
						slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
						cartCountFm_?.startAnimation(slide_)
					}
				} else {
					SessionTwiclo(this).storeId = ""
					SessionTwiclo(this).serviceId = ""
					cartCountFm_.visibility = View.GONE
					total_cart_count = 0
					slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
					cartCountFm_?.startAnimation(slide_)
				}
			}

		})
	}

	private fun onClick() {
		backIcon_cat.setOnClickListener {
			if (check == 0) {
				AppUtils.finishActivityLeftToRight(this);
			} else {
				check = 0
				val returnIntent = Intent()
				setResult(RESULT_OK, returnIntent)
				finish()

			}
		}

		search_onDailyNeed_cat.setOnClickListener {
			AppUtils.startActivityForResultRightToLeft(
				this, Intent(this, NewSearchActivity::class.java)
					.putExtra("storeId", "").putExtra("service_id", service_id), SEARCH_RESULT_CODE
			)
		}

		cartCountFm_.setOnClickListener {
			if (SessionTwiclo(this).isLoggedIn) {
				AppUtils.startActivityForResultRightToLeft(
					this, Intent(this, CartActivity::class.java)
						.putExtra("storeId", SessionTwiclo(this).storeId), VIEWALL_RESULT_CODE
				)
//                startActivity(
//                    Intent(this, CartActivity::class.java).putExtra(
//                        "store_id", SessionTwiclo(this).storeId
//                    )
//                )
			} else {
				showLoginDialog("Please login to proceed")
			}
		}

		appBar_.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
			val dy: Int = barOffset - verticalOffset
			barOffset = verticalOffset
			if (dy > 0 && fabVisible) {
				// scrolling up
				headerTabll.visibility = View.VISIBLE
				fabVisible = false
				Log.e("fabVisible", "up")
			} else if (dy < 0 && !fabVisible) {
				// scrolling down
				headerTabll.visibility = View.GONE
				fabVisible = true
				Log.e("fabVisible", "down")

			}
		})
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
		productModel: Product,
		pos: Int,
		store_id: String,
		item_count: String,
		type: String
	) {
		product_Id=productModel.product_id
		product_count=item_count
		if (type.equals("Replace")) {
			clearCart()
			SessionTwiclo(this).storeId = store_id
			SessionTwiclo(this).serviceId = productModel.service_id
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
		mainDataList?.get(main_position)!!.products[pos].cart_quantity = item_count
		Log.d("item_count", item_count)
	}

	override fun plusItemCart(
		main_position: Int,
		productModel: Product,
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
		mainDataList?.get(main_position)!!.products[pos].cart_quantity = item_count
	}

	override fun minusItemCart(
		main_position: Int,
		productModel: Product,
		pos: Int,
		store_id: String,
		item_count: String,
		type: String
	) {
		Log.d("item_count", item_count)
		mainDataList?.get(main_position)!!.products[pos].cart_quantity = item_count
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

	override fun onClickViewAll(main_position: Int, model: Data) {}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		Log.v("search_result_coe", "$requestCode - $SEARCH_RESULT_CODE - $resultCode")
		if (requestCode == SEARCH_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				product_Update = 1
				showIOSProgress()
				forApiHit()
			}
		}
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

}