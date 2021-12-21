package com.fidoo.user.store.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.data.model.Product
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase
import com.fidoo.user.grocerynewui.activity.GroceryNewUiActivity
import com.fidoo.user.newsearch.ui.NewSearchActivity
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.search.ui.SearchItemActivity
import com.fidoo.user.store.adapter.RestaurantCurationsAdapter
import com.fidoo.user.store.adapter.StoreAdapter
import com.fidoo.user.store.model.StoreListingModel
import com.fidoo.user.store.viewmodel.StoreListingViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.showAlertDialog
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_store_list.*
import kotlinx.android.synthetic.main.no_internet_connection.*

@Suppress("DEPRECATION")
class StoreListActivity : com.fidoo.user.utils.BaseActivity() {

	var storeListingViewModel: StoreListingViewModel? = null
	var viewmodelusertrack: UserTrackerViewModel? = null
	var storeList: ArrayList<StoreListingModel.StoreList>? = null
	var storeListUpdated: ArrayList<StoreListingModel.StoreList>? = null
	var curationList: ArrayList<StoreListingModel.Curation>? = null
	var filterVisibility: Int = 0
	var selectionDistance: Int = 0
	var selectionRating: Int = 0
	var selectedValue: String? = ""
	var distanceStr: String? = ""
	var ratingStr: String? = ""
	var cuisine_to_search: String? = ""
	private lateinit var productsDatabase: ProductsDatabase
	private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase
	var product: Product? = null
	lateinit var analytics: FirebaseAnalytics
	private var layoutManger: GridLayoutManager? = null
	var relevancePopUp: Dialog? = null
	var adapterStore: StoreAdapter? = null
	private var mMixpanel: MixpanelAPI? = null

	companion object {
		var serive_id_: String? = ""
		var check_: String? = "0"
		var onBackPressHandle: Int? = 0
	}

	var width = 0

	//for pagination
	private var barOffset = 0
	private var fabVisible = true
	private var manager: GridLayoutManager? = null
	private var currentItems = 0
	private var totalItems: Int = 0
	private var scrollOutItems: Int = 0
	private var isScrolling = false
	private var isMore = false
	private var hit = 0
	private var pagecount = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val window: Window = this.getWindow()
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
		setContentView(R.layout.activity_store_list)
		mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
		manager = GridLayoutManager(this, 1)
		storeList = ArrayList()
		storeListUpdated = ArrayList()

		Log.d("storemain_list", "storemain")
		val mainText =
			"<span style=\"color:#339347\">Choose </span> <span>Your <br/>Favorite</span> <span style=\"color:#339347\">Store</span></string>"

//		tv_choose_your_fav_store.text =
//			HtmlCompat.fromHtml(mainText, HtmlCompat.FROM_HTML_MODE_COMPACT)

		storeListingViewModel = ViewModelProviders.of(this).get(StoreListingViewModel::class.java)
		viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)
		analytics = FirebaseAnalytics.getInstance(this)
		// Display size
		val displayMetrics = DisplayMetrics()
		this.windowManager.defaultDisplay.getMetrics(displayMetrics)
		//  int height = displayMetrics.heightPixels;
		//  int height = displayMetrics.heightPixels;
		width = (displayMetrics.widthPixels - 100) / 4

		backIcon.setOnClickListener {
			finish()
			AppUtils.finishActivityLeftToRight(this)
		}

		try {
			val serive_id = intent.getStringExtra("serviceId")
			val serviceName = intent.getStringExtra("serviceName")
			Store_type_text.text = serviceName
			serive_id_ = serive_id
			Log.e("serive_id__", serive_id!!)
			MainActivity.service_idStr=serive_id_

			if (serive_id.equals("5")) {
				restaurant_curationll.visibility = View.VISIBLE
				sortRl.visibility = View.VISIBLE
			} else {
				restaurant_curationll.visibility = View.GONE
				sortRl.visibility = View.GONE
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}

		deleteRoomDataBase()
		apicall(serive_id_)

		sorListLlheader.setOnClickListener {
			relevancePopUp()
		}


//		storeVisibilityNested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//
//			if (serive_id_.equals("5")) {
//				when {
//					scrollY > oldScrollY -> {
//						if (930 < scrollY) {
//							//restaurant_curationll!!.visibility = View.GONE
//							sortRl!!.visibility = View.GONE
//							sortRlheader!!.visibility = View.VISIBLE
//						}
//						Log.d("storeVisibilityNested11", "$scrollY--$oldScrollY")
//
//					}
//					scrollX == scrollY -> {
//						if (oldScrollY < 200) {
//							//restaurant_curationll!!.visibility = View.VISIBLE
//							sortRl!!.visibility = View.VISIBLE
//							sortRlheader!!.visibility = View.GONE
//						}
//						Log.d("storeVisibilityNested1", "$scrollY--$oldScrollY")
//
//						//  restaurant_curationll!!.visibility = View.VISIBLE
//
//					}
//					else -> {
//						if (oldScrollY < 950) {
//							//restaurant_curationll!!.visibility = View.VISIBLE
//							sortRl!!.visibility = View.VISIBLE
//							sortRlheader!!.visibility = View.GONE
//						}
//						Log.d("storeVisibilityNested2", "$scrollY--$oldScrollY")
//
//					}
//				}
//			} else {
//				restaurant_curationll.visibility = View.GONE
//				sortRl.visibility = View.GONE
//			}
//
//		})
//
//		distance_txt.setOnClickListener {
//
//			if (selectionDistance == 0) {
//				distanceStr = "distance"
//				distance_txt.setBackgroundResource(R.drawable.black_rounded_solid)
//				selectionDistance = 1
//
//			} else {
//				distanceStr = ""
//				distance_txt.setBackgroundResource(R.drawable.round_outline)
//				selectionDistance = 0
//			}
//			selectedValue = distanceStr + "," + ratingStr
//			if (ratingStr!!.isEmpty()) {
//				selectedValue = distanceStr
//			}
//			apicall(serive_id_)
//		}
//
//		rating_txt.setOnClickListener {
//			if (selectionRating == 0) {
//				ratingStr = "rating"
//				rating_txt.setBackgroundResource(R.drawable.black_rounded_solid)
//				selectionRating = 1;
//			} else {
//				ratingStr = ""
//				rating_txt.setBackgroundResource(R.drawable.round_outline)
//				selectionRating = 0;
//			}
//			selectedValue = distanceStr + "," + ratingStr
//			if (distanceStr!!.isEmpty()) {
//				selectedValue = ratingStr
//			}
//			apicall(serive_id_)
//		}

		sorListLl.setOnClickListener {
			sortFm.visibility = View.VISIBLE
			sortFmBg.visibility = View.VISIBLE
		}

		sortFmBg.setOnClickListener {
			sortFmBg.visibility = View.GONE
			sortFm.visibility = View.GONE

		}

		relevanceTxt.setOnClickListener {
			adapterStore!!.setFilter(storeList as ArrayList)

			relevanceTxt.setTextColor(getResources().getColor(R.color.primary_color))
			ratingTxt.setTextColor(getResources().getColor(R.color.black))
			timingTxt.setTextColor(getResources().getColor(R.color.black))
			sortRl.visibility = View.VISIBLE
			restaurant_curationll.visibility = View.VISIBLE
			sortRlheader.visibility = View.GONE
			sortFmBg.visibility = View.GONE
			sortFm.visibility = View.GONE


			check_ = "0"
		}

		ratingTxt.setOnClickListener {

			relevanceTxt.setTextColor(getResources().getColor(R.color.black))
			ratingTxt.setTextColor(getResources().getColor(R.color.primary_color))
			timingTxt.setTextColor(getResources().getColor(R.color.black))

			sortRl.visibility = View.GONE
			sortFmBg.visibility = View.GONE
			sortFm.visibility = View.GONE
			restaurant_curationll.visibility = View.GONE
			sortRlheader.visibility = View.VISIBLE
			var sortedList =
				storeList!!.sortedWith(compareBy({ it.rating }, { it.status })).reversed()

			val intent = Intent(this@StoreListActivity, StoreFilterListActivity::class.java)
				.putExtra("selectedValue", "rating")
				.putExtra("serviceId", serive_id_)
				.putExtra("serviceName", intent.getStringExtra("serviceName"))
				.putExtra("cuisine_to_search", cuisine_to_search)
				.putExtra("cusineName", "")
			startActivity(intent)
			overridePendingTransition(R.anim.bottom_up, R.anim.nothing)

			// adapterStore!!.setFilter(sortedList!!)
			check_ = "1"
		}

		timingTxt.setOnClickListener {
			relevanceTxt.setTextColor(getResources().getColor(R.color.black))
			ratingTxt.setTextColor(getResources().getColor(R.color.black))
			timingTxt.setTextColor(getResources().getColor(R.color.primary_color))
			sortRl.visibility = View.GONE
			sortFmBg.visibility = View.GONE
			sortFm.visibility = View.GONE
			restaurant_curationll.visibility = View.GONE
			sortRlheader.visibility = View.VISIBLE

			var sortedList = storeList!!.sortedWith(compareBy({ it.delivery_time }))
			//  adapterStore!!.setFilter(sortedList!!)

			val intent = Intent(this@StoreListActivity, StoreFilterListActivity::class.java)
				.putExtra("selectedValue", "distance")
				.putExtra("serviceId", serive_id_)
				.putExtra("serviceName", intent.getStringExtra("serviceName"))
				.putExtra("cuisine_to_search", cuisine_to_search)
				.putExtra("cusineName", "")
			startActivity(intent)
			overridePendingTransition(R.anim.bottom_up, R.anim.nothing)

			check_ = "2"
		}

		search_stores_icon?.setOnClickListener {
			if (serive_id_!!.isNotEmpty()) {
				AppUtils.startActivityForResultRightToLeft(
					this, Intent(this, NewSearchActivity::class.java)
						.putExtra("storeId", "").putExtra("service_id", serive_id_),
					GroceryNewUiActivity.SEARCH_RESULT_CODE
				)
			}
		}

		//for collapsing
		app_barRes.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
			val dy: Int = barOffset - verticalOffset
			barOffset = verticalOffset
			Log.e("fabVisibleRes", "up --$dy")

			if (dy > 0 && fabVisible) {
				// scrolling up
				sortRlheader.visibility = View.VISIBLE
				fabVisible = false

			} else if (dy < 0 && !fabVisible) {
				// scrolling down
				sortRlheader.visibility = View.GONE

				fabVisible = true
				Log.e("fabVisibleRes", "down")

			}
		})
	}


	private fun apicall(serive_id: String?) {

		if (isNetworkConnected) {
			fidooLoaderShow()
			if (SessionTwiclo(this).isLoggedIn) {

				if (serive_id != null) {
					storeListingViewModel!!.getStores(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).loggedInUserDetail.accessToken,
						serive_id,
						SessionTwiclo(this).userLat,
						SessionTwiclo(this).userLng,
						"",
						"",
						selectedValue, cuisine_to_search, pagecount.toString()
					)
				}

				viewmodelusertrack?.customerActivityLog(
					SessionTwiclo(this).loggedInUserDetail.accountId,
					SessionTwiclo(this).mobileno, "StoreListing Screen",
					SplashActivity.appversion, serive_id_, SessionTwiclo(this).deviceToken
				)

			} else {
				if (serive_id != null) {
					storeListingViewModel!!.getStores(
						"",
						"",
						serive_id,
						SessionTwiclo(this).userLat,
						SessionTwiclo(this).userLng,
						"",
						"",
						selectedValue, cuisine_to_search, pagecount.toString()
					)
				}
			}

			no_internet_store.visibility = View.GONE
			no_internet_Ll.visibility = View.GONE
			//  store_list_ll.visibility = View.VISIBLE
			linear_progress_indicator.visibility = View.GONE

		} else {

			no_internet_store.visibility = View.VISIBLE
			no_internet_Ll.visibility = View.VISIBLE
			//   store_list_ll.visibility = View.GONE
			linear_progress_indicator.visibility = View.GONE
		}

		retry_onRefresh.setOnClickListener {
			if (isNetworkConnected) {
				if (SessionTwiclo(this).isLoggedIn) {
					if (serive_id != null) {
						storeListingViewModel!!.getStores(
							SessionTwiclo(this).loggedInUserDetail.accountId,
							SessionTwiclo(this).loggedInUserDetail.accessToken,
							serive_id,
							SessionTwiclo(this).userLat,
							SessionTwiclo(this).userLng,
							"",
							"",
							selectedValue, cuisine_to_search, pagecount.toString()
						)
					}
					viewmodelusertrack?.customerActivityLog(
						SessionTwiclo(this).loggedInUserDetail.accountId,
						SessionTwiclo(this).mobileno, "StoreListing Screen",
						SplashActivity.appversion, serive_id_, SessionTwiclo(this).deviceToken
					)
					val bundle = Bundle()
					bundle.putString("retry_onRefresh", "retry_onRefresh")
					bundle.putString("OrderList_Screen", "OrderList Screen")
					analytics.logEvent("Orderlist_Screen", bundle)
				} else {
					if (serive_id != null) {
						storeListingViewModel!!.getStores(
							"",
							"",
							serive_id,
							SessionTwiclo(this).userLat,
							SessionTwiclo(this).userLng,
							"",
							"",
							selectedValue, cuisine_to_search, pagecount.toString()
						)
					}
				}

				no_internet_store.visibility = View.GONE
				no_internet_Ll.visibility = View.GONE
				//  store_list_ll.visibility = View.VISIBLE
				linear_progress_indicator.visibility = View.GONE

			} else {
				showInternetToast()
				no_internet_store.visibility = View.VISIBLE
				no_internet_Ll.visibility = View.VISIBLE
				//  store_list_ll.visibility = View.GONE
				linear_progress_indicator.visibility = View.GONE
			}
		}

		storeListingViewModel?.getStoresApi?.observe(this, Observer { user ->
			linear_progress_indicator.visibility = View.GONE
			//	storeVisibilityNested.visibility = View.VISIBLE
			Log.e("stores_response", Gson().toJson(user))
			fidooLoaderCancel()

			if (user != null) {

				if (!user.error) {

					val mModelData: StoreListingModel = user

					storeListUpdated!!.clear()
					hit=0
					if (pagecount==0) {
						curationList = mModelData.curations as ArrayList
						if (curationList!!.isNotEmpty()) {

							var curationsAdapter = RestaurantCurationsAdapter(
								this,
								curationList!!,
								object : RestaurantCurationsAdapter.ItemClickService {
									override fun onItemClick(
										pos: Int,
										model: StoreListingModel.Curation
									) {
										cuisine_to_search = model.cuisineId
										val intent = Intent(
											this@StoreListActivity,
											StoreFilterListActivity::class.java
										)
											.putExtra("selectedValue", "rating")
											.putExtra("serviceId", serive_id_)
											.putExtra(
												"serviceName",
												intent.getStringExtra("serviceName")
											)
											.putExtra("cuisine_to_search", cuisine_to_search)
											.putExtra("cusineName", model.cusineName)

										startActivity(intent)
										overridePendingTransition(R.anim.bottom_up, R.anim.nothing)
									}
								},
								width
							)

							curation_RecyclerView.adapter = curationsAdapter
							layoutManger = GridLayoutManager(this, 2)
							layoutManger!!.orientation = LinearLayoutManager.HORIZONTAL
							curation_RecyclerView?.layoutManager = layoutManger

						} else {
							restaurant_curationll.visibility = View.GONE
							curation_RecyclerView.visibility = View.GONE
							sortRl.visibility = View.GONE
						}
					}

					isMore = user.more_value

					if (pagecount > 0) {
						storeListUpdated = mModelData.storeList as ArrayList
						storeList!!.addAll(storeListUpdated!!)
						Log.d("ordersList__", storeList!!.size.toString())
						adapterStore!!.updateData(storeList!!, isMore)
						adapterStore!!.notifyDataSetChanged()
					} else {
						storeList = mModelData.storeList as ArrayList
						storeListRv(storeList!!)

					}

					if (storeList!!.size != 0) {
						no_shop_ll.visibility = View.GONE
					} else {
						no_shop_ll.visibility = View.VISIBLE
					}

				} else {
					if (user.errorCode == 101) {
						showAlertDialog(this)
					}
				}

			}

		})

		storeListingViewModel?.failureResponse?.observe(this, {
			linear_progress_indicator.visibility = View.GONE
			Log.d("failureResponse___", it.toString())
		})

	}

	private fun storeListRv(storeList: ArrayList<StoreListingModel.StoreList>) {
		adapterStore = StoreAdapter(this, storeList!!)
		storesRecyclerView.layoutManager = LinearLayoutManager(this)
		storesRecyclerView.setHasFixedSize(true)
		storesRecyclerView.adapter = adapterStore
		storesRecyclerView.layoutManager = manager
		storesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

				Log.d("value_g_", "$dy-$currentItems---$totalItems---$scrollOutItems---$firstvisibleItem")

				if (dy > 1) {
					if (isScrolling && currentItems + scrollOutItems == totalItems) {
						if (isScrolling == true) {
							if (isMore) {
								if (hit == 0) {
									pagecount++
									//call api here
									if (SessionTwiclo(this@StoreListActivity).isLoggedIn) {
										Log.d("totalItem___", "aaya--+"+serive_id_)

										if (serive_id_ != null) {
											storeListingViewModel!!.getStores(
												SessionTwiclo(this@StoreListActivity).loggedInUserDetail.accountId,
												SessionTwiclo(this@StoreListActivity).loggedInUserDetail.accessToken,
												serive_id_!!,
												SessionTwiclo(this@StoreListActivity).userLat,
												SessionTwiclo(this@StoreListActivity).userLng,
												"",
												"",
												selectedValue,
												cuisine_to_search,
												pagecount.toString()
											)
										}
									}
									hit = 1
								}
								isScrolling = false
								isMore = false
							}

						}
					}
				}

			}

		})

	}

	//delete room data
	private fun deleteRoomDataBase() {
		Thread {
			productsDatabase = Room.databaseBuilder(
				applicationContext,
				ProductsDatabase::class.java, ProductsDatabase.DB_NAME
			)
				.fallbackToDestructiveMigration()
				.build()
			productsDatabase!!.productsDaoAccess()!!.deleteAll()

		}.start()

		Thread {
			restaurantProductsDatabase = Room.databaseBuilder(
				applicationContext,
				RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
			)
				.fallbackToDestructiveMigration()
				.build()
			restaurantProductsDatabase!!.resProductsDaoAccess()!!.deleteAll()

		}.start()

	}

	override fun startActivityForResult(intent: Intent?, requestCode: Int) {
		super.startActivityForResult(intent, requestCode)
		// Log.d("sfdhjfdds","aaya")
		deleteRoomDataBase()
	}

	override fun onBackPressed() {
		super.onBackPressed()
		AppUtils.finishActivityLeftToRight(this)
	}

	fun relevancePopUp() {
		relevancePopUp = Dialog(this)
		relevancePopUp?.requestWindowFeature(Window.FEATURE_NO_TITLE)
		relevancePopUp?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		relevancePopUp?.setContentView(R.layout.relevance_popup)

		relevancePopUp?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT
		)
		// relevancePopUp?.window?.attributes?.windowAnimations = R.style.diologIntertnet

		relevancePopUp?.setCanceledOnTouchOutside(true)
		relevancePopUp?.show()
		val dismisspopUp_ = relevancePopUp?.findViewById<ConstraintLayout>(R.id.dismisspopUp_)
		val relevance = relevancePopUp?.findViewById<TextView>(R.id.relevance)
		val RatingTxt_ = relevancePopUp?.findViewById<TextView>(R.id.RatingTxt_)
		val delivery_timeTxt = relevancePopUp?.findViewById<TextView>(R.id.delivery_timeTxt)
		dismisspopUp_!!.setOnClickListener {
			relevancePopUp!!.dismiss()
		}

		if (check_.equals("0")) {
			relevance!!.setTextColor(getResources().getColor(R.color.primary_color))
			RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
			delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))

		} else if (check_.equals("1")) {
			relevance!!.setTextColor(getResources().getColor(R.color.black))
			RatingTxt_!!.setTextColor(getResources().getColor(R.color.primary_color))
			delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))


		} else if (check_.equals("2")) {
			relevance!!.setTextColor(getResources().getColor(R.color.black))
			RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
			delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.primary_color))
		}

		relevance!!.setOnClickListener {
			relevancePopUp!!.dismiss()
//            adapterStore!!.setFilter(storeList as ArrayList)

			relevance.setTextColor(getResources().getColor(R.color.primary_color))
			RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
			delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))

			sortRl.visibility = View.VISIBLE
			restaurant_curationll.visibility = View.VISIBLE
			sortRlheader.visibility = View.GONE

			relevanceTxt.setTextColor(getResources().getColor(R.color.primary_color))
			ratingTxt.setTextColor(getResources().getColor(R.color.black))
			timingTxt.setTextColor(getResources().getColor(R.color.black))
			check_ = "0"
		}

		RatingTxt_!!.setOnClickListener {
			relevancePopUp!!.dismiss()
			var sortedList = storeList!!.sortedWith(compareBy({ it.rating })).reversed()
			// Log.d("sortedList",Gson().toJson(sortedList))
			//  adapterStore!!.setFilter(sortedList!!)

			sortRl.visibility = View.GONE
			restaurant_curationll.visibility = View.GONE

			relevance.setTextColor(getResources().getColor(R.color.black))
			RatingTxt_!!.setTextColor(getResources().getColor(R.color.primary_color))
			delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.black))

			relevanceTxt.setTextColor(getResources().getColor(R.color.black))
			ratingTxt.setTextColor(getResources().getColor(R.color.primary_color))
			timingTxt.setTextColor(getResources().getColor(R.color.black))
			sortRlheader.visibility = View.VISIBLE

			val intent = Intent(this@StoreListActivity, StoreFilterListActivity::class.java)
				.putExtra("selectedValue", "rating")
				.putExtra("serviceId", serive_id_)
				.putExtra("serviceName", intent.getStringExtra("serviceName"))
				.putExtra("cuisine_to_search", cuisine_to_search)
				.putExtra("cusineName", "")
			startActivity(intent)
			overridePendingTransition(R.anim.bottom_up, R.anim.nothing)

			check_ = "1"
		}

		delivery_timeTxt!!.setOnClickListener {
			relevancePopUp!!.dismiss()

			var sortedList = storeList!!.sortedWith(compareBy({ it.delivery_time }))
//           // Log.d("sortedList",Gson().toJson(sortedList))
//            adapterStore!!.setFilter(sortedList!!)

			sortRl.visibility = View.GONE
			restaurant_curationll.visibility = View.GONE
			sortRlheader.visibility = View.VISIBLE

			relevance.setTextColor(getResources().getColor(R.color.black))
			RatingTxt_!!.setTextColor(getResources().getColor(R.color.black))
			delivery_timeTxt!!.setTextColor(getResources().getColor(R.color.primary_color))

			relevanceTxt.setTextColor(getResources().getColor(R.color.black))
			ratingTxt.setTextColor(getResources().getColor(R.color.black))
			timingTxt.setTextColor(getResources().getColor(R.color.primary_color))

			val intent = Intent(this@StoreListActivity, StoreFilterListActivity::class.java)
				.putExtra("selectedValue", "distance")
				.putExtra("serviceId", serive_id_)
				.putExtra("serviceName", intent.getStringExtra("serviceName"))
				.putExtra("cuisine_to_search", cuisine_to_search)
				.putExtra("cusineName", "")
			startActivity(intent)
			overridePendingTransition(R.anim.bottom_up, R.anim.nothing)
			check_ = "2"
		}

	}

	override fun onResume() {
		super.onResume()
		if (onBackPressHandle == 1) {
			sortRl.visibility = View.VISIBLE
			restaurant_curationll.visibility = View.VISIBLE
			sortRlheader.visibility = View.GONE
			sortFmBg.visibility = View.GONE
			sortFm.visibility = View.GONE
			check_ = "0"
		}
	}


}