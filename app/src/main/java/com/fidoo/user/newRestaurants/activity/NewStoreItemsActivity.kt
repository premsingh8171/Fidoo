package com.fidoo.user.newRestaurants.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.addCartTempList
import com.fidoo.user.activity.MainActivity.Companion.tempProductList
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.cartview.viewmodel.CartViewModel
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.interfaces.AdapterCustomRadioClick
import com.fidoo.user.newRestaurants.adapter.NewRestaurantCategoryAdapter
import com.fidoo.user.newRestaurants.adapter.NewStoreDetailsItemAdapter
import com.fidoo.user.newRestaurants.listener.NewAdapterCartAddRemoveClick
import com.fidoo.user.newRestaurants.model.Subcategory
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.restaurants.adapter.StoreCustomItemsAdapter
import com.fidoo.user.restaurants.listener.CustomCartPlusMinusClick
import com.fidoo.user.restaurants.model.CustomCheckBoxModel
import com.fidoo.user.restaurants.model.CustomListModel
import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
import com.fidoo.user.restaurants.viewmodel.StoreDetailsViewModel
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.showAlertDialog
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_new_store_items.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import kotlinx.android.synthetic.main.no_item_found.*
import java.util.*
import kotlin.collections.ArrayList


class NewStoreItemsActivity :
	BaseActivity(),
	AdapterClick,
	CustomCartPlusMinusClick,
	AdapterCustomRadioClick,
	AdapterAddRemoveClick,
	NewAdapterCartAddRemoveClick {
	private var categoryy: ArrayList<CustomListModel>? = null
	private var mainlist: ArrayList<Subcategory>? = null
	var latestList: ArrayList<Subcategory>? = null
	private var mModelDataTemp: CustomizeProductResponseModel? = null
	lateinit var behavior: BottomSheetBehavior<LinearLayout>
	var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
	var cartViewModel: CartViewModel? = null //Un used
	var viewmodel: StoreDetailsViewModel? = null
	var distanceViewModel: TrackViewModel? = null//Un used
	var customIdsList: ArrayList<String>? = null
	var customNamesList: ArrayList<String>? = null
	var tempProductId: String? = ""
	var mCustomizeCount: Int? = 0
	var tempOfferPrice: String? = ""
	var tempPrice: Double? = 0.0
	var tempType: String? = ""
	var tempCount: String? = ""
	var count: Int = 1
	var countRes: Int = 0
	var veg: Int = 0
	var nonveg_str: String = ""
	var contains_egg: String = ""
	var cartId: String = ""
	lateinit var storeID: String
	var cat_listShow: Int = 0
	var cus_itemProductId: String = ""
	var custom_itemCount: Int = 0
	var total_cart_count: Int = 0
	var search_value: String? = ""
	var storeName: String = ""
	private var mMixpanel: MixpanelAPI? = null

	companion object {
		var lastCustomized_str: String = ""
		var product_customize_id: String = ""
		var customerLatitude: String = ""
		var customerLongitude: String = ""
		var handleresponce: Int = 0
		var storeIDCheckOnCart: String = ""
		var handleRes = 0

		//for bottom view of restaurant license
		var fssai: String? = ""
		var restaurantName: String? = ""
		var restaurantAddress: String? = ""
	}

	//for pagination
	private var manager: GridLayoutManager? = null
	private var currentItems = 0
	private var totalItems: Int = 0
	private var scrollOutItems: Int = 0
	private var isScrolling = false

	var veg_filter: Int = 0
	var egg_filter: Int = 0
	var cat_visible: Int = 0
	var cart_count: Int = 0
	var restaurantCategoryAdapter: NewRestaurantCategoryAdapter?=null
	var newStoreDetailsItemAdapter: NewStoreDetailsItemAdapter? = null
	var selectCategoryDiolog: Dialog? = null
	private var slide_: Animation? = null
	lateinit var catrecyclerView: RecyclerView
	lateinit var viewAll_txt: TextView
	var cat_id: String? = ""
	var active_or_not: Int = 0
	var clickevent: Int = 1
	var sessionTwiclo: SessionTwiclo? = null
	var viewmodelusertrack: UserTrackerViewModel? = null

	// Room db
	private var barOffset = 0
	private var fabVisible = true

	//for pagination
	var pagecount: Int = 0
	var next_available: Int = 0

	var typeStr: String? = ""
	var prdCount: Int? = 0
	var main_Position: Int? = 0
	var prd_Position: Int? = 0
	var backgroungHit: Int? = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val window: Window = this.getWindow()
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
		setContentView(R.layout.activity_new_store_items)

		mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
		distanceViewModel = ViewModelProvider(this).get(TrackViewModel::class.java)
		behavior = BottomSheetBehavior.from(bottom_sheet)

		tempProductList = ArrayList()
		addCartTempList = ArrayList()
		customIdsList = ArrayList()
		customNamesList = ArrayList()
		customIdsListTemp = ArrayList()
		mainlist = ArrayList()
		storeID = intent.getStringExtra("storeId")!!
		storeIDCheckOnCart = storeID
		Log.d("storeIDCheckOnCart___", storeIDCheckOnCart)
		manager = GridLayoutManager(this, 1)
		customerLatitude = ""
		customerLongitude = ""
		//  store_preference_Rlay  for meat ui Gone
		mainlist!!.clear()
		sessionTwiclo = SessionTwiclo(this)


		viewmodel = ViewModelProvider(this).get(StoreDetailsViewModel::class.java)
		cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
		viewmodelusertrack = ViewModelProvider(this).get(UserTrackerViewModel::class.java)
		tv_location.text = intent.getStringExtra("store_location").toString().replace(" ,", ", ")

		cartitemView_LLstore.setOnClickListener {

			if (SessionTwiclo(this).isLoggedIn) {
				startActivity(
					Intent(this, CartActivity::class.java).putExtra(
						"store_id",
						SessionTwiclo(this).storeId
					)
				)
			} else {
				showLoginDialog("Please login to proceed")
			}
		}

		app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
			val dy: Int = barOffset - verticalOffset
			barOffset = verticalOffset
			if (dy > 0 && fabVisible) {
				// scrolling up
				fabVisible = false
				Log.e("fabVisible", "up")
				store_details_lay.visibility = View.GONE
				tv_store_name.visibility = View.VISIBLE
			//	category_header_TXt.visibility = View.GONE
			} else if (dy < 0 && !fabVisible) {
				// scrolling down
				fabVisible = true
				Log.e("fabVisible", "down")
				store_details_lay.visibility = View.VISIBLE
				tv_store_name.visibility = View.INVISIBLE
				//category_header_TXt.visibility = View.GONE
			}
		})

		search_ClearTxt.setOnClickListener {
			searchEdt_ResPrd.getText().clear()
		}

		search_backImg.setOnClickListener {
			hideKeyboard(searchEdt_ResPrd)
			visibilityView()
		}

		main_restatuarant_const.setOnTouchListener(object : View.OnTouchListener {
			override fun onTouch(v: View?, event: MotionEvent?): Boolean {
				hideKeyboard(main_restatuarant_const)
				return true
			}

		})

		RestaurantPrdSearch.setOnClickListener {
			searchEdt_ResPrd.isCursorVisible = true
			showKeyboard(searchEdt_ResPrd)
			store_details_lay.visibility = View.VISIBLE
			res_header_constL.visibility = View.GONE
			search_visibility_card.visibility = View.VISIBLE
//            slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
//            cartitemView_LL?.startAnimation(slide_)
		}

		searchEdt_ResPrd?.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable) {}
			override fun beforeTextChanged(
				s: CharSequence, start: Int,
				count: Int, after: Int
			) {
			}

			override fun onTextChanged(
				s: CharSequence, start: Int,
				before: Int, count: Int
			) {
				search_value = s.toString()

				if (mainlist!!.isEmpty()) {
					return
				}

			}
		})

		backIcon.setOnClickListener {
			if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
				AppUtils.finishActivityLeftToRight(this)
			} else {
				behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
				cat_FloatBtn.visibility = View.VISIBLE
				if (cart_count == 0) {
					cartitemView_LLstore.visibility = View.GONE
				} else {
					cartitemView_LLstore.visibility = View.VISIBLE
				}
			}
		}

		cat_FloatBtn.setOnTouchListener(object : View.OnTouchListener {
			override fun onTouch(p0: View?, ev: MotionEvent?): Boolean {
				if (ev!!.action == MotionEvent.ACTION_DOWN) {
				} else if (ev!!.action == MotionEvent.ACTION_UP) {
					try {
						if (cat_visible == 1) {
							catPopUp()
						}
					} catch (e: Exception) {
						e.printStackTrace()
					}
				}
				return true
			}
		})

		customAddBtn.setOnClickListener {
			//Log.e("addCartTempList", Gson().toJson(addCartTempList))
			for (i in 0 until categoryy!!.size) {
				customIdsList!!.add(categoryy!![i].id.toString())
				customNamesList!!.add(categoryy!![i].subCatName.toString())
			}
			var lastCustomized: String = ""
			lastCustomized = customNamesList.toString()
			val regex = "\\[|\\]"
			lastCustomized_str = lastCustomized.replace(regex.toRegex(), "")
			product_customize_id = "1"
			//Log.e("customIdsList", customIdsList.toString() + "\n" + lastCustomized_str)

			addCartTempList!!.clear()
			val addCartInputModel = AddCartInputModel()
			addCartInputModel.productId = tempProductId
			addCartInputModel.quantity = countValue.text.toString()
			addCartInputModel.message = "add product"
			addCartInputModel.customizeSubCatId = customIdsList!!
			addCartInputModel.isCustomize = "1"
			addCartTempList!!.add(0, addCartInputModel)

			Log.d(
				"storeIdstoreId__",
				intent.getStringExtra("storeId").toString() + "\n" + SessionTwiclo(this).storeId
			)

			if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(
					this
				).storeId.equals("")
			) {

				showIOSProgress()
				SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
				SessionTwiclo(this).serviceId = MainActivity.service_idStr

				handleresponce = 1
				// product_customize_id

				viewmodel!!.addToCartApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken,
					addCartTempList!!,
					cartId
				)

				mainlist!![main_Position!!].product[prd_Position!!].cart_quantity = prdCount!!
				newStoreDetailsItemAdapter!!.notifyDataSetChanged()

				behavior.state = BottomSheetBehavior.STATE_COLLAPSED
			} else {
				clearCartPopup()
			}
		}

		veg_switch_img.setOnClickListener {
			showIOSProgress()

			pagecount = 0
			if (veg_filter == 0) {
				veg_switch_img.setImageResource(R.drawable.filter_on)
				// egg_switch_img.setImageResource(R.drawable.filter_off)
				//  egg_filter=0
				nonveg_str = "0"
				getStoreDetailsApiNewApiCall()
				veg_filter = 1

			} else {
				veg_switch_img.setImageResource(R.drawable.filter_off)
				nonveg_str = ""
				getStoreDetailsApiNewApiCall()
				veg_filter = 0

			}

			visibilityView()
			searchEdt_ResPrd.getText().clear()
		}

		egg_switch_img.setOnClickListener {
			showIOSProgress()

			pagecount = 0
			if (egg_filter == 0) {
				egg_switch_img.setImageResource(R.drawable.filter_on)
				// veg_switch_img.setImageResource(R.drawable.filter_off)
				contains_egg = "1"
				getStoreDetailsApiNewApiCall()
				egg_filter = 1

			} else {
				egg_switch_img.setImageResource(R.drawable.filter_off)
				contains_egg = ""
				getStoreDetailsApiNewApiCall()
				egg_filter = 0
			}
			visibilityView()
			searchEdt_ResPrd.getText().clear()
		}

		//Default behaviour of Bottom Sheet
		behavior.state = BottomSheetBehavior.STATE_COLLAPSED

		storeName = intent.getStringExtra("storeName").toString()

		tv_store_name.text =
			storeName.split(' ').joinToString(" ") { it.capitalize(Locale.getDefault()) }
		store_nameTxt.text =
			storeName.split(' ').joinToString(" ") { it.capitalize(Locale.getDefault()) }

		//storeID = intent.getStringExtra("storeId")!!

		transLay.setOnClickListener {
			if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
				//searchLay.visibility = View.GONE
				behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
			} else {
				//searchLay.visibility = View.VISIBLE
				behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
				if (cart_count == 0) {
					cartitemView_LLstore.visibility = View.GONE
				} else {
					cartitemView_LLstore.visibility = View.VISIBLE

				}
				cat_FloatBtn.visibility = View.VISIBLE
			}
		}

		if (isNetworkConnected) {
			if (sessionTwiclo!!.isLoggedIn) {
				getStoreDetailsApiNewApiCall()
				cat_FloatBtn.visibility = View.VISIBLE
				main_restatuarant_const.visibility = View.VISIBLE
				linear_progress_indicator.visibility = View.GONE
				no_internet_store.visibility = View.GONE
				no_internet_Ll.visibility = View.GONE
				if (SessionTwiclo(this).loggedInUserDetail.accountId != null) {
					viewmodelusertrack?.customerActivityLog(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).mobileno,
						"Restaurant Screen",
						SplashActivity.appversion,
						StoreListActivity.serive_id_,
						SessionTwiclo(this).deviceToken
					)
				}
			} else {
				getStoreDetailsApiNewApiCall()
			}
		} else {
			cartitemView_LLstore.visibility = View.GONE
			cat_FloatBtn.visibility = View.GONE
			main_restatuarant_const.visibility = View.GONE
			linear_progress_indicator.visibility = View.GONE
			no_internet_store.visibility = View.VISIBLE
			no_internet_Ll.visibility = View.VISIBLE
		}

		retry_onRefresh.setOnClickListener {

			if (isNetworkConnected) {
				pagecount = 0
				if (sessionTwiclo!!.isLoggedIn) {
					getStoreDetailsApiNewApiCall()
					cat_FloatBtn.visibility = View.VISIBLE
					main_restatuarant_const.visibility = View.VISIBLE
					linear_progress_indicator.visibility = View.VISIBLE
					no_internet_store.visibility = View.GONE
					no_internet_Ll.visibility = View.GONE
					viewmodelusertrack?.customerActivityLog(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).mobileno,
						"Restaurant Screen",
						SplashActivity.appversion,
						StoreListActivity.serive_id_,
						SessionTwiclo(this).deviceToken
					)
				} else {
					getStoreDetailsApiNewApiCall()
				}
			} else {
				cartitemView_LLstore.visibility = View.GONE
				cat_FloatBtn.visibility = View.GONE
				main_restatuarant_const.visibility = View.GONE
				linear_progress_indicator.visibility = View.GONE
				no_internet_store.visibility = View.VISIBLE
				no_internet_Ll.visibility = View.VISIBLE
				showInternetToast()
			}

		}

		//cart count response
		viewmodel?.cartCountResponse?.observe(this) { cartcount ->
			// dismissIOSProgress()
			addCartTempList!!.clear()
			tempProductList!!.clear()
			//Log.d("cartCountResponse___",cartcount.toString())
			var count = cartcount.count
			var price = cartcount.price
			SessionTwiclo(this).storeId = cartcount.store_id
			if (!cartcount.error) {
				if (!count.equals("0")) {
					cart_count = 1
					//  cartIcon.setImageResource(R.drawable.cart_icon)
					//  cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
					itemQuantity_textstore.text = count
					totalprice_txtstore.text = "₹ " + price
					cartitemView_LLstore.visibility = View.VISIBLE
					if (total_cart_count == 0) {
						total_cart_count = 1
						slide_ = AnimationUtils.loadAnimation(this, R.anim.rv_left_right_anim)
						cartitemView_LLstore?.startAnimation(slide_)
					}
				} else {
					cart_count = 0
					itemQuantity_textstore.text = "0"
					totalprice_txtstore.text = ""
					//   cartIcon.setImageResource(R.drawable.ic_cart)
					//  cartIcon.setColorFilter(Color.argb(255, 199, 199, 199))
					cartitemView_LLstore.visibility = View.GONE
					total_cart_count = 0
					slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
					cartitemView_LLstore?.startAnimation(slide_)
					SessionTwiclo(this).serviceId = ""

				}
				cat_FloatBtn.visibility = View.VISIBLE
			}

		}

		viewmodel?.newStoreDetailsRes?.observe(this, Observer { storeData ->
			dismissIOSProgress()
			linear_progress_indicator.visibility = View.GONE
			mainPrdListLl.visibility = View.VISIBLE
			Log.d("getStoreDetailsApiNewApi__", Gson().toJson(storeData))
			handleresponce = 0

			if(storeData.error_code == 200) {
				tempProductList!!.clear()
				addCartTempList!!.clear()
				next_available = storeData.next_available

//                if (cat_listShow == 0) {
//                     mainlist!!.clear()
//                  }

				try {

					if (storeData.service_id.equals("7")) {
						store_preference_Rlay.visibility = View.GONE
					} else {
						store_preference_Rlay.visibility = View.VISIBLE
					}

					restaurantAddress = storeData.address.toString()
					fssai = "License no. " + storeData.fssai.toString()
					restaurantName = storeData.store_name.toString()

				} catch (e: Exception) {
					e.printStackTrace()
				}

				if (storeData.subcategory.isNotEmpty()) {

//				executor().execute {
//					for (i in storeData.subcategory.indices) {
//						val categoryData = storeData.subcategory[i]
//						if (cat_listShow == 0) {
//							//catList.add(categoryData)
//						}
//					}

					if (pagecount > 0) {
						latestList = storeData.subcategory as ArrayList
						mainlist!!.addAll(latestList!!)
						val s: Set<Subcategory> = LinkedHashSet<Subcategory>(mainlist)
						mainlist!!.clear()
						mainlist!!.addAll(s)
						newStoreDetailsItemAdapter!!.updateData(mainlist!!, next_available)
						//newStoreDetailsItemAdapter!!.notifyDataSetChanged()
					} else {
						mainlist!!.clear()
						mainlist = storeData.subcategory as ArrayList
						val s: Set<Subcategory> = LinkedHashSet<Subcategory>(mainlist)
						mainlist!!.clear()
						mainlist!!.addAll(s)
						recyclerviewListData(mainlist!!)
					}

					pagecount = storeData.start_id

//					if (next_available==0) {
//						Handler(Looper.getMainLooper()).postDelayed({
//							backgroungHit=1
//							if (isNetworkConnected) {
//
//								if (SessionTwiclo(this@NewStoreItemsActivity).isLoggedIn) {
//
//									viewmodel?.getStoreDetailsApiNew(
//										SessionTwiclo(this@NewStoreItemsActivity).loggedInUserDetail.accountId,
//										SessionTwiclo(this@NewStoreItemsActivity).loggedInUserDetail.accessToken,
//										intent.getStringExtra("storeId"),
//										nonveg_str,
//										cat_id,
//										contains_egg, pagecount.toString()
//									)
//								} else {
//									viewmodel?.getStoreDetailsApiNew(
//										"",
//										"",
//										intent.getStringExtra("storeId"),
//										nonveg_str,
//										cat_id,
//										contains_egg, pagecount.toString()
//									)
//								}
//							}
//
//						}, 3000)
//					}

				}

				tv_deliveryTime.text = intent.getStringExtra("delivery_time") + " minutes"


				cat_visible = 1
				no_itemsFound_res.visibility = View.GONE
				no_item_foundll.visibility = View.GONE

			} else if (storeData.error_code == 101) {
				showAlertDialog(this)
			} else {
				cat_visible = 0
				no_itemsFound_res.visibility = View.VISIBLE
				no_item_foundll.visibility = View.VISIBLE
			}
		})

		viewmodel?.addRemoveCartResponse?.observe(this) { user ->
			dismissIOSProgress()
			Log.e("addRemoveCartRes____", Gson().toJson(user))
			if (user.errorCode == 200) {
				handleresponce = 1

				if (SessionTwiclo(this).isLoggedIn) {
					viewmodel?.getCartCountApi(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken
					)
				}
			} else if (user.errorCode == 101) {
				showAlertDialog(this)

			}
			//   getStoreDetailsApiNewApiCall()
		}

		viewmodel?.failureResponse?.observe(this) { user ->
			dismissIOSProgress()
			Log.e("cart response", Gson().toJson(user))
			// showToast(user)
			//  Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
		}

		viewmodel?.addToCartResponse?.observe(this, Observer { user ->
			dismissIOSProgress()
			if (user.errorCode == 200) {
				handleresponce = 1
				Log.e("stores_addResponse____", Gson().toJson(user))
				if (tempType.equals("custom")) {
					if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
						behavior.state = BottomSheetBehavior.STATE_COLLAPSED
						//searchLay.visibility = View.GONE
					} else {
						//searchLay.visibility = View.VISIBLE
						behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
					}

				} else {
					tempProductList!!.clear()
					addCartTempList!!.clear()
				}


				viewmodel?.getCartCountApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken
				)
			} else if (user.errorCode == 101) {
				showAlertDialog(this)

			}
			// getStoreDetailsApiNewApiCall()
		})

		viewmodel?.customizeProductResponse?.observe(this, Observer { user ->
			dismissIOSProgress()
			cartitemView_LLstore.visibility = View.GONE
			cat_FloatBtn.visibility = View.GONE
			if (user.errorCode == 200) {
				Log.e("stores___esponse", Gson().toJson(user))
				mModelDataTemp = user

				categoryy = ArrayList()

				for (i in 0 until mModelDataTemp?.category?.size!!) {
					if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
						try {
							if (mModelDataTemp?.category?.get(i)!!.isMandatory.equals("0")) {
							} else {
								var customListModel: CustomListModel? = CustomListModel()
								customListModel!!.category =
									mModelDataTemp?.category?.get(i)!!.catId
								try {
									customListModel!!.id =
										mModelDataTemp?.category?.get(i)!!.subCat[0].id.toInt()
									customListModel!!.price =
										mModelDataTemp?.category?.get(i)!!.subCat[0].price
									customListModel!!.subCatName =
										mModelDataTemp?.category?.get(i)!!.subCat[0].subCatName
								} catch (e: Exception) {
								}

								categoryy!!.add(customListModel)
							}
						} catch (e: java.lang.Exception) {
							e.printStackTrace()
						}
					}
				}

				tempPrice = 0.0

				//var tempPrice: Double? = 0.0
				for (i in 0 until customIdsListTemp!!.size) {
					tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
				}

				for (i in 0 until categoryy!!.size) {
					if (categoryy!!.get(i).price != null) {
						tempPrice = tempPrice!! + categoryy!![i].price.toDouble()
					}
				}
				Log.e("tempPrice", tempPrice.toString())

				try {
					tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
				} catch (e: NumberFormatException) {
					Log.e("Exception", e.toString())
				}


				//tempPrice = tempPrice!! + plusMinusPrice
				//showToast(tempPrice.toString())
				Log.e("tempPriceTotal", tempPrice.toString())

				// customAddBtn.text = "Item total "+resources.getString(R.string.ruppee) + tempPrice.toString()
				customAddBtn.text =
					"Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()

				//	Log.e("tempPriceTotal_", mModelDataTemp?.category!![0].subCat.size.toString())
				try {
					if (mModelDataTemp?.category!![0].subCat.size == 0) {
						customAddBtn.visibility = View.GONE
					} else {
						customAddBtn.visibility = View.VISIBLE

					}
				} catch (e: java.lang.Exception) {
					e.printStackTrace()
				}


				val adapter = StoreCustomItemsAdapter(
					this,
					mModelDataTemp?.category!!,
					this,
					categoryy,
					this
				)
				customItemsRecyclerview.layoutManager = LinearLayoutManager(this)
				customItemsRecyclerview.setHasFixedSize(true)
				customItemsRecyclerview.adapter = adapter
				// Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
			} else if (user.errorCode == 101) {
				showAlertDialog(this)

			}
		})

		viewmodel?.clearCartResponse?.observe(this, Observer { user ->
			dismissIOSProgress()
			if (user.errorCode == 200) {
				Log.e("stores_response", Gson().toJson(user))
				if (tempType.equals("custom")) {

					viewmodel!!.addToCartApi(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken,
						addCartTempList!!,
						""
					)
				} else {

					addCartTempList!!.clear()
					val addCartInputModel = AddCartInputModel()
					addCartInputModel.productId = cus_itemProductId
					addCartInputModel.quantity = "1"
					addCartInputModel.message = "add product"
					addCartInputModel.customizeSubCatId = customIdsList!!
					addCartInputModel.isCustomize = "0"
					addCartTempList!!.add(0, addCartInputModel)

					viewmodel!!.addToCartApi(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken,
						addCartTempList!!,
						""

					)
				}
				//   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
			} else if (user.errorCode == 101) {
				showAlertDialog(this)
			}
		})

	}

	private fun visibilityView() {
		store_details_lay.visibility = View.VISIBLE
		res_header_constL.visibility = View.VISIBLE
		search_visibility_card.visibility = View.GONE
		// slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
		//   cartitemView_LL?.startAnimation(slide_)
	}

	private fun catPopUp() {
		clickevent == 1
		selectCategoryDiolog = Dialog(this)
		selectCategoryDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
		selectCategoryDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		selectCategoryDiolog?.setContentView(R.layout.select_cat_restaurant_popup)
		selectCategoryDiolog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT
		)

//        slide_ = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
//        selectCategoryDiolog?.layout_catPopup?.startAnimation(slide_)
		selectCategoryDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
		selectCategoryDiolog?.setCanceledOnTouchOutside(true)
		selectCategoryDiolog?.show()
		val outsid_viewRl = selectCategoryDiolog?.findViewById<ConstraintLayout>(R.id.outsid_viewRl)
		val txtError = selectCategoryDiolog?.findViewById<TextView>(R.id.txtError)
		viewAll_txt = selectCategoryDiolog?.findViewById<TextView>(R.id.viewAll_txt_)!!
		val dismisspopUp = selectCategoryDiolog?.findViewById<ImageView>(R.id.dismisspopUp)
		catrecyclerView = selectCategoryDiolog?.findViewById(R.id.cat_resRecyclerview)!!

		// catRecyclerview
		txtError?.setOnClickListener(View.OnClickListener {
			selectCategoryDiolog?.dismiss()
		})

		outsid_viewRl?.setOnClickListener(View.OnClickListener {
			selectCategoryDiolog?.dismiss()
		})

		dismisspopUp?.setOnClickListener(View.OnClickListener {
			selectCategoryDiolog?.dismiss()
		})

		if (active_or_not == 0) {
			viewAll_txt.setTextColor(Color.parseColor("#a9a9a9"))
		} else {
			viewAll_txt.setTextColor(Color.parseColor("#000000"))

		}

		viewAll_txt?.setOnClickListener(View.OnClickListener {
			cat_id = ""
			active_or_not = 0
			viewAll_txt.setTextColor(Color.parseColor("#a9a9a9"))
			restaurantCategoryAdapter!!.notifyDataSetChanged()
			selectCategoryDiolog?.dismiss()

			//api call here
			getStoreDetailsApiNewApiCall()
			visibilityView()
			searchEdt_ResPrd.getText().clear()

		})

		cat_listShow = 1
		rvCategory(mainlist!!)

	}

	private fun rvCategory(subcategory: ArrayList<Subcategory>) {
		Log.d("subcategory___", subcategory.size.toString())
		clickevent = 1
		restaurantCategoryAdapter = NewRestaurantCategoryAdapter(
			this,
			subcategory,
			active_or_not,
			object : NewRestaurantCategoryAdapter.SubategoryItemClick {
				override fun onItemClick(pos: Int, subcategory: Subcategory) {
					Log.d("subcategory_id__", subcategory.product_sub_category_id)
					active_or_not = pos
					cat_id = subcategory.product_sub_category_id
					viewAll_txt.setTextColor(Color.parseColor("#000000"))
					selectCategoryDiolog?.dismiss()

					category_header_.text = subcategory.subcategory_name
				//	storeItemsRecyclerview?.smoothScrollToPosition(pos)
					storeItemsRecyclerview?.scrollToPosition(pos)


//                    try {
//                        for (i in mainlist!!.indices) {
//                            if (mainlist!![i].subcategory_name.equals(subcategory.subcategory_name.toString())) {
//                                if (clickevent == 1) {
//                                    Log.e("product_sub_category_id_", i.toString())
//                                    if (i == 0) {
//                                        storeItemsRecyclerview?.smoothScrollToPosition(i)
//                                    } else {
//                                        storeItemsRecyclerview?.smoothScrollToPosition(i!!)
//                                    }
//
//                                    clickevent = 0
//                                }
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }


				}
			})

		catrecyclerView?.adapter = restaurantCategoryAdapter

	}

	private fun recyclerviewListData(arrayList: ArrayList<Subcategory>) {
		storeItemsRecyclerview.layoutManager = LinearLayoutManager(this)
		storeItemsRecyclerview.setHasFixedSize(true)

		newStoreDetailsItemAdapter = NewStoreDetailsItemAdapter(
			this,
			arrayList,
			this,
			fssai!!,
			restaurantName!!,
			"3.5",
			restaurantAddress!!,
			this,
			this,
			0,
			storeID, object : NewStoreDetailsItemAdapter.UpdateSubcategory {
				override fun onClickUpdate(
					subcategory: Subcategory,
					mainPosition: Int,
					prdPosition: Int,
					prdQuantity: Int,
					type: String
				) {
					typeStr = type
					prd_Position = prdPosition
					main_Position = mainPosition
					prdCount = prdQuantity
					if (type.equals("Customized")) {
					} else {
						arrayList[mainPosition].product[prdPosition].cart_quantity = prdQuantity
						newStoreDetailsItemAdapter!!.notifyDataSetChanged()
					}

				}
			}
		)

		storeItemsRecyclerview.adapter = newStoreDetailsItemAdapter
		storeItemsRecyclerview.layoutManager = manager
		storeItemsRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				super.onScrollStateChanged(recyclerView, newState)
				if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					isScrolling = true
				}
			}

			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				currentItems = manager!!.childCount
				totalItems = manager!!.itemCount
				scrollOutItems = manager!!.findFirstVisibleItemPosition()
				var firstvisibleItem = manager!!.findFirstCompletelyVisibleItemPosition()

				Log.d("value_gg_", "$dy-$currentItems---$totalItems---$scrollOutItems---$firstvisibleItem")

				if (searchEdt_ResPrd.getText().toString()
						.equals("") || searchEdt_ResPrd.getText().toString().startsWith(" ")
				) {
					try {
						category_header_.visibility = View.VISIBLE
						category_header_.text =
							arrayList!!.get(scrollOutItems)!!.subcategory_name.toString()

						active_or_not = scrollOutItems
						restaurantCategoryAdapter!!.notifyDataSetChanged()


//						category_header_TXt.text =
//							mainlist!!.get(scrollOutItems)!!.subcategory_name.toString()

//						try {
//							for (i in catList.indices) {
//								if (catList[i].catName.equals(
//										category_header_.getText().toString()
//									)
//								) {
//									Log.d("totalItem__gg_", "$i--${catList.size}")
//									active_or_not = i
//									restaurantCategoryAdapter.notifyDataSetChanged()
//								}
//							}
//						} catch (e: java.lang.Exception) {
//							e.printStackTrace()
//						}

					} catch (e: Exception) {
						e.printStackTrace()
					}

					if (dy > 1) {

						if (isScrolling && (currentItems + scrollOutItems)/2 == totalItems/2) {

							if (next_available == 0) {
								if (handleresponce == 0) {
									if (isScrolling == true) {
										handleresponce = 1

										if (isNetworkConnected) {

											if (SessionTwiclo(this@NewStoreItemsActivity).isLoggedIn) {

												viewmodel?.getStoreDetailsApiNew(
													SessionTwiclo(this@NewStoreItemsActivity).loggedInUserDetail.accountId,
													SessionTwiclo(this@NewStoreItemsActivity).loggedInUserDetail.accessToken,
													intent.getStringExtra("storeId"),
													nonveg_str,
													cat_id,
													contains_egg, pagecount.toString()
												)
											} else {
												viewmodel?.getStoreDetailsApiNew(
													"",
													"",
													intent.getStringExtra("storeId"),
													nonveg_str,
													cat_id,
													contains_egg, pagecount.toString()
												)
											}
										}
										isScrolling = false
									}
								}
							}
						}
					}

				} else {

					try {
						category_header_.visibility = View.VISIBLE
						category_header_.text =
							arrayList!!.get(scrollOutItems)!!.subcategory_name.toString()
					} catch (e: Exception) {
						e.printStackTrace()
					}
				}
			}

		})
	}

	private fun getStoreDetailsApiNewApiCall() {

		if (isNetworkConnected) {
			if (backgroungHit==0){
				showIOSProgress()
			}
//			category_header_.visibility = View.GONE
//			category_header_.text = ""
			if (SessionTwiclo(this).isLoggedIn) {
				viewmodel?.getCartCountApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken
				)

				viewmodel?.getStoreDetailsApiNew(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken,
					intent.getStringExtra("storeId"),
					nonveg_str,
					cat_id,
					contains_egg, pagecount.toString()
				)
			} else {
				viewmodel?.getStoreDetailsApiNew(
					"",
					"",
					intent.getStringExtra("storeId"),
					nonveg_str,
					cat_id,
					contains_egg, pagecount.toString()
				)
			}
		} else {
			showInternetToast()
		}

	}

	override fun clearCart() {
		viewmodel?.clearCartApi(
			SessionTwiclo(this).loggedInUserDetail.accountId,
			SessionTwiclo(this).loggedInUserDetail.accessToken
		)
	}

	private fun clearCartPopup() {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Replace cart item!")
		builder.setMessage("Do you want to discard the previous selection?")
		builder.setIcon(android.R.drawable.ic_dialog_alert)
		builder.setPositiveButton("Yes") { _, _ ->
			viewmodel?.clearCartApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken
			)

		}
		builder.setNegativeButton("No") { dialogInterface, which -> }
		val alertDialog: AlertDialog = builder.create()
		alertDialog.setCancelable(false)
		alertDialog.show()
	}

	// This lisner call when customize item is going to add
	override fun onItemClick(
		productId: String?,
		type: String?,
		count: String?,
		offerPrice: String?,
		customize_count: Int?,
		productType: String?,
		cart_id: String?
	) {

		tempType = type
		tempCount = count
		this.count = count!!.toInt()
		tempProductId = productId
		mCustomizeCount = customize_count
		tempOfferPrice = offerPrice
		countValue.text = tempCount
		cus_itemProductId = productId!!
		custom_itemCount = count!!.toInt()

		if (type == "custom") {
			if (mCustomizeCount == 0) {
				customIdsListTemp?.clear()

				if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
					behavior.state = BottomSheetBehavior.STATE_EXPANDED
					//searchLay.visibility = View.GONE
				} else {
					//searchLay.visibility = View.VISIBLE
					behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

				}
				//  tempProductId = productId
				showIOSProgress()
				customIdsList!!.clear()
				customNamesList!!.clear()
				if (productId != null) {
					viewmodel?.customizeProductApi(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken, productId
					)
				}
			} else {
				val builder = AlertDialog.Builder(this)
				builder.setTitle("Your previous customization")
				builder.setMessage(tempCount)
				// builder.setIcon(android.R.drawable.ic_dialog_alert)
				builder.setPositiveButton("I'LL CHOOSE") { _, _ ->
					if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
						behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
						//searchLay.visibility = View.GONE
					} else {
						behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
						//searchLay.visibility = View.VISIBLE
					}

					//  tempProductId = productId
					showIOSProgress()
					//customIdsList!!.clear()
					if (productId != null) {
						viewmodel?.customizeProductApi(
							SessionTwiclo(this).loggedInUserDetail.accountId,
							SessionTwiclo(this).loggedInUserDetail.accessToken, productId
						)
					}
				}

				//performing negative action
				builder.setNegativeButton("REPEAT") { dialogInterface, which -> }
				val alertDialog: AlertDialog = builder.create()
				alertDialog.setCancelable(true)
				alertDialog.show()
			}

		} else {

			if (SessionTwiclo(this).storeId.equals(intent.getStringExtra("storeId")) || SessionTwiclo(
					this
				).storeId.equals("")
			) {
				Log.e("intent store id", intent.getStringExtra("storeId").toString())
				SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
				Log.e("  store id", SessionTwiclo(this).storeId.toString())
				showIOSProgress()

				viewmodel!!.addToCartApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken,
					addCartTempList!!,
					""
				)
				SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
				// product_customize_id

			} else {
				clearCartPopup()
			}
		}
	}

	override fun onIdSelected(
		productId: String?,
		type: String?,
		price: String?,
		sub_cat_name: String?,
		tempSelectionCount: Int
	) {
		//Log.d("")
		if (type == "select") {
			if (productId != null) {
				customIdsList!!.add(productId)
				customNamesList!!.add(sub_cat_name!!)
			}
			val customCheckBoxModel =
				CustomCheckBoxModel()
			customCheckBoxModel.id = productId
			customCheckBoxModel.price = price
			customIdsListTemp!!.add(customCheckBoxModel)
			//customAddBtn.text = tempOfferPrice

		} else if (type == "unselect") {
			for (i in 0 until customIdsList!!.size) {
				if (customIdsList!![i] == productId) {
					customIdsList!!.removeAt(i)
					customNamesList!!.removeAt(i)
					customIdsListTemp!!.removeAt(i)
					break
				}
			}
		}


		var tempIdPrice: Double? = 0.0
		//plusMinusPrice = 0.0
		tempPrice = 0.0


		for (i in 0 until customIdsListTemp!!.size) {
			tempIdPrice = tempIdPrice!! + customIdsListTemp!![i].price.toDouble()
		}

		for (i in 0 until categoryy!!.size) {
			tempIdPrice = tempIdPrice!! + categoryy!![i].price.toDouble()
		}

		tempPrice = tempOfferPrice!!.toDouble() + tempIdPrice!!
		Log.e("TempPrice on ID ", tempPrice.toString())
		//plusMinusPrice = tempIdPrice + tempPrice!!
		//tempPrice = tempPrice?.plus(plusMinusPrice)
		customAddBtn.text = "Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()

		/*if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED){
			customAddBtn.text = tempOfferPrice
		}*/
	}


	override fun onCustomRadioClick(checkedId: String?, position: String?) {
		var tempCat: String? = ""
		var tempPrice: String? = ""
		var tempPriceSubCat_Name: String? = ""

		for (i in 0 until mModelDataTemp?.category!!.size) {
			for (j in 0 until mModelDataTemp?.category!!.get(i).subCat.size) {
				if (checkedId == mModelDataTemp?.category!!.get(i).subCat.get(j).id) {
					tempCat = mModelDataTemp?.category!!.get(i).catId
					tempPrice = mModelDataTemp?.category!!.get(i).subCat.get(j).price
					tempPriceSubCat_Name =
						mModelDataTemp?.category!!.get(i).subCat.get(j).subCatName
					break
				}
				if (!tempCat.equals("")) {
					break
				}
			}
		}
		var tempAddEdit: String? = "add"
		var tempAddEditId: String? = "add"

		for (i in 0 until categoryy!!.size) {
			if (categoryy!!.get(i).category.equals(tempCat)) {
				/*  var customListModel: CustomListModel?= CustomListModel()
				  customListModel!!.category= category.get(i).catId
				  customListModel!!.id= category.get(i).subCat.get(0).id.toInt()*/
				tempAddEdit = "edit"
				tempAddEditId = i.toString()
				categoryy!!.get(i).id = checkedId!!.toInt()
				categoryy!!.get(i).price = tempPrice
				categoryy!!.get(i).subCatName = tempPriceSubCat_Name
				break
			}
		}

		if (tempAddEdit.equals("edit")) {
			categoryy!!.get(tempAddEditId!!.toInt()).id = checkedId!!.toInt()
			categoryy!!.get(tempAddEditId.toInt()).price = tempPrice
			categoryy!!.get(tempAddEditId.toInt()).subCatName = tempPriceSubCat_Name
		} else {
			var customListModel: CustomListModel? =
				CustomListModel()
			customListModel!!.category = tempCat
			customListModel.id = checkedId!!.toInt()
			customListModel.price = tempPrice
			customListModel.subCatName = tempPriceSubCat_Name
			categoryy!!.add(customListModel)
		}
		var tempPricee: Double? = 0.0
		for (i in 0 until customIdsListTemp!!.size) {
			tempPricee = tempPricee!! + customIdsListTemp!!.get(i).price.toDouble()
		}

		for (i in 0 until categoryy!!.size) {
			tempPricee = tempPricee!! + categoryy!!.get(i).price.toDouble()
		}

		tempPricee = tempOfferPrice!!.toDouble() + tempPricee!!
		customAddBtn.text = resources.getString(R.string.ruppee) + tempPricee.toString()
	}

	override fun onItemAddRemoveClick(
		productId: String?,
		count: String?,
		type: String?,
		price: String?,
		sid: String?,
		cartId: String?,
		position: Int
	) {
		cus_itemProductId = productId.toString()
		Log.d("count__add", count!! + "=" + cartId + "=" + type)
		Log.d("count__addID", productId!!)
		// showIOSProgress()

		if (type.equals("add")) {
			//  cartIcon.setImageResource(R.drawable.cart_icon)

			if (tempProductList!!.size == 0) {
				//customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()
//                    val tempProductListModel = TempProductListModel()
//                    tempProductListModel.productId = productId
//                    tempProductListModel.quantity = count
//                    tempProductListModel.price = price
//                    tempProductList!!.add(tempProductListModel)

				addCartTempList!!.clear()
				val addCartInputModel = AddCartInputModel()
				addCartInputModel.productId = productId
				addCartInputModel.quantity = count
				addCartInputModel.message = "add product"
				addCartInputModel.customizeSubCatId = customIdsList!!
				addCartInputModel.isCustomize = "0"
				addCartTempList!!.add(0, addCartInputModel)



				if (cartId != null) {
					viewmodel!!.addToCartApi(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken,
						addCartTempList!!,
						cartId
					)
				}
				tempProductList!!.clear()
			} else {
				var check: String = ""
				var tempPos: Int = 0

				for (i in 0 until tempProductList!!.size) {
					Log.e("check1", tempProductList!!.get(i).productId)
					Log.e("check2", productId)
					if (tempProductList!![i].productId.equals(productId)) {
						check = "edit"
						tempPos = i
						//tempProductList!!.get(i).quantity = count
						break
					}
				}
				if (check == "edit") {
					try {
						if (addCartTempList!!.size != 0) {
							tempProductList!![tempPos].quantity = count
							addCartTempList!![tempPos].quantity = count
						}
					} catch (e: Exception) {
						e.printStackTrace()
					}

				} else {

					val tempProductListModel = TempProductListModel()
					tempProductListModel.productId = productId
					tempProductListModel.quantity = count
					tempProductListModel.price = price
					tempProductList!!.add(tempProductListModel)
					addCartTempList!!.clear()
					val addCartInputModel = AddCartInputModel()
					addCartInputModel.productId = productId
					addCartInputModel.quantity = count
					addCartInputModel.message = "add product"
					addCartInputModel.customizeSubCatId = customIdsList!!
					addCartInputModel.isCustomize = "0"
					addCartTempList!!.add(0, addCartInputModel)

				}

				viewmodel!!.addToCartApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken,
					addCartTempList!!,
					cartId!!.toString()
				)
			}
		} else {
			//  cartIcon.setImageResource(R.drawable.ic_cart)
			var check = "edit"
			var checkPos = 0
			Log.e("check1", Gson().toJson(tempProductList!!))
			for (i in 0 until tempProductList!!.size) {
				if (tempProductList!![i].productId.equals(productId)) {
					if (count == "0") {
						check = "remove"
						checkPos = i
						break
					}
				}
			}
			Log.d("checkpos", checkPos.toString())

			if (check == "remove") {
				//   cartIcon.setImageResource(R.drawable.ic_cart)
				//ll_view_cart.visibility = View.GONE // to hide the bottom cart bar if non-customized item quantity becomes zero
				addCartTempList!!.removeAt(checkPos)
				tempProductList!!.removeAt(checkPos)
				customIdsList!!.clear()

				if (cartId != null) {
					viewmodel?.addRemoveCartDetails(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken,
						productId,
						"remove",
						"0",
						"",
						cartId,
						customIdsList!!
					)
				}

			} else {

				if (tempProductList!!.size != 0) {
					tempProductList!![checkPos].quantity = count
					addCartTempList!![checkPos].quantity = count
				}

				customIdsList!!.clear()

				if (cartId != null) {
					viewmodel?.addRemoveCartDetails(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken,
						productId,
						"remove",
						"0",
						"",
						cartId,
						customIdsList!!
					)
				}
			}
		}

		//plusMinusPrice = 0.0
		tempPrice = 0.0
		Log.d("check1", Gson().toJson(tempProductList!!))
		var bottomPrice: Double? = 0.0
		var bottomCount: Int? = 0
		for (i in 0 until tempProductList!!.size) {
			bottomPrice =
				bottomPrice!! + (tempProductList!!.get(i).price.toDouble() * tempProductList!!.get(
					i
				).quantity.toInt())
			bottomCount = bottomCount!! + tempProductList!!.get(i).quantity.toInt()
		}
		//txt_price_.text = resources.getString(R.string.ruppee) + bottomPrice.toString()
		//cartCountTxt.text = bottomCount.toString()
		//  cartIcon.setImageResource(R.drawable.cart_icon)
		//   cartIcon.setColorFilter(Color.argb(255, 53, 156, 71))
		if (bottomCount == 1) {
			//txt_items_.text = bottomCount.toString() + " item"
		} else {
			//txt_items_.text = bottomCount.toString() + " items"
		}

	}


	override fun onAddItemClick(
		quantity: String?,
		productId: String?,
		items: String?,
		offerPrice: String?,
		isCustomize: String?,
		prodcustCustomizeId: String?,
		cart_id: String?
	) {
		product_customize_id = prodcustCustomizeId!!
		tempOfferPrice = offerPrice
		//plusMinusPrice = 0.0
		tempPrice = 0.0
		tempProductId = productId
		cus_itemProductId = productId!!
		custom_itemCount = quantity!!.toInt()

		if (cart_id != null) {
			cartId = cart_id
		}

		Log.d("isCustomize__", isCustomize!! + "\n" + items + "---" + prodcustCustomizeId)

		val builder = AlertDialog.Builder(this)
		builder.setTitle("Your previous customization")
		builder.setMessage(items)
		builder.setPositiveButton("I'LL CHOOSE") { _, which ->

			if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
				behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
				//searchLay.visibility = View.GONE
			} else {
				behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
				//searchLay.visibility = View.VISIBLE
			}

			//tempProductId = productId
			showIOSProgress()
			customIdsList!!.clear()
			customNamesList!!.clear()
			viewmodel?.customizeProductApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken, productId!!
			)
		}

		//performing negative action
		builder.setNegativeButton("REPEAT") { _, which ->
			showIOSProgress()
			viewmodel?.addRemoveCartDetails(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken,
				productId!!,
				"add",
				isCustomize!!,
				prodcustCustomizeId!!,
				cart_id!!,
				customIdsList!!
			)
			mainlist!![main_Position!!].product[prd_Position!!].cart_quantity = prdCount!!
			newStoreDetailsItemAdapter!!.notifyDataSetChanged()
		}
		// Create the AlertDialog
		val alertDialog: AlertDialog = builder.create()
		// Set other dialog properties
		alertDialog.setCancelable(true)
		alertDialog.show()
	}

	override fun onRemoveItemClick(
		productId: String?,
		quantity: String?,
		isCustomize: String?,
		prodcustCustomizeId: String?,
		cart_id: String?
	) {
		product_customize_id = prodcustCustomizeId!!
		if (!isNetworkConnected) {
			showToast(resources.getString(R.string.provide_internet))
		} else {
			if (cart_id != null) {
				viewmodel?.addRemoveCartDetails(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken,
					productId!!,
					"remove",
					isCustomize!!,
					prodcustCustomizeId!!,
					cart_id,
					customIdsList!!
				)
			}
		}
	}

	private fun showLoginDialog(message: String) {
		val builder = AlertDialog.Builder(this)
		builder.setTitle("Alert")
		builder.setMessage(message)
		builder.setPositiveButton("Login") { _, which ->
			sessionTwiclo!!.clearSession()
			startActivity(
				Intent(
					this,
					AuthActivity::class.java
				).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
			)

//            startActivity(
//                Intent(this, LoginActivity::class.java)
//            )
		}
		builder.setNegativeButton("Cancel") { _, _ -> }
		val alertDialog: AlertDialog = builder.create()
		alertDialog.setCancelable(true)
		alertDialog.show()
	}

	override fun onResume() {
		super.onResume()
		storeID = intent.getStringExtra("storeId")!!
		storeIDCheckOnCart = storeID

		var str = intent.getStringExtra("cuisine_types")

		if (str!!.endsWith(",")) {
			var sb = StringBuffer(str)
			sb.deleteCharAt(sb.length - 1)
			tv_cuisnes.text = sb.toString()
		} else {
			tv_cuisnes.text = str
		}

		tv_distance.text = intent.getStringExtra("distance") + "km"
		if (!intent.getStringExtra("coupon_desc").equals("")) {
			tv_coupon.text = intent.getStringExtra("coupon_desc")
			coupan_view_ll.visibility = View.VISIBLE
		} else {
			coupan_view_ll.visibility = View.GONE
		}

		Log.d("OnRESUME", "RESUME")
		behavior.state = BottomSheetBehavior.STATE_COLLAPSED
		if (handleresponce == 1) {
			if (SessionTwiclo(this).isLoggedIn) {
				viewmodel?.getCartCountApi(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).loggedInUserDetail.accessToken
				)
			}
		}

		if (handleRes == 1) {
			pagecount = 0
			getStoreDetailsApiNewApiCall()
		}

	}

	override fun onBackPressed() {
		if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
			AppUtils.finishActivityLeftToRight(this)
		} else {
			behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
			if (cart_count == 0) {
				cartitemView_LLstore.visibility = View.GONE
			} else {
				cartitemView_LLstore.visibility = View.VISIBLE

			}
			cat_FloatBtn.visibility = View.VISIBLE
		}

	}

}