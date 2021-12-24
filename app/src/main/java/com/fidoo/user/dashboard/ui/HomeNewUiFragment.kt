package com.fidoo.user.dashboard.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.addEditAdd
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.addressmodule.activity.SavedAddressesActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.dailyneed.ui.ServiceDailyNeedActivity
import com.fidoo.user.dashboard.adapter.SliderAdapterExample
import com.fidoo.user.dashboard.adapter.newadapter.ServiceDetailsAdapter
import com.fidoo.user.dashboard.listener.ClickEventOfDashboard
import com.fidoo.user.dashboard.model.newmodel.*
import com.fidoo.user.dashboard.viewmodel.HomeFragmentViewModel
import com.fidoo.user.data.SliderItem
import com.fidoo.user.data.model.BannerModel
import com.fidoo.user.data.model.CartCountModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.FragmentHomeNewuiBinding
import com.fidoo.user.profile.ui.ProfileFragment
import com.fidoo.user.restaurants.activity.StoreItemsActivity
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.sendpackages.activity.SendPackageActivity
import com.fidoo.user.store.activity.StoreFilterListActivity
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.AUTOCOMPLETE_REQUEST_CODE
import com.fidoo.user.utils.BaseFragment
import com.fidoo.user.utils.CardSliderLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.fragment_home_newui.*
import java.util.*
import kotlin.collections.ArrayList
import org.json.JSONObject




@Suppress("DEPRECATION")
class HomeNewUiFragment : BaseFragment(), ClickEventOfDashboard {
	lateinit var analytics: FirebaseAnalytics
	var serviceDetailsAdapter: ServiceDetailsAdapter? = null
	lateinit var mView: View
	var viewmodel: HomeFragmentViewModel? = null
	var viewmodelusertrack: UserTrackerViewModel? = null
	private var _progressDlg: ProgressDialog? = null
	var fragmentHomeBinding: FragmentHomeNewuiBinding? = null
	private var currentPosition = 0
	private var layoutManger: CardSliderLayoutManager? = null
	private var where: String? = ""
	lateinit var pref: SessionTwiclo
	var height = 0
	var width = 0
	var catIconWidth = 0
	var currentPage = 0
	var timer: Timer? = null
	val DELAY_MS: Long = 8000 //delay in milliseconds before task is to be executed
	val PERIOD_MS: Long = 8000 // time in milliseconds between successive task executions.


	private var mMixpanel: MixpanelAPI? = null
	private val props = JSONObject()

	companion object {
		var service_id: String? = ""
		var service_name: String? = ""
		var itemPosition: Int? = 0
	}

	var sliderItem = SliderItem()
	var mmContext: Context? = null
	private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_newui, container, false)

		analytics = FirebaseAnalytics.getInstance(requireContext())

		viewmodel = ViewModelProviders.of(requireActivity()).get(HomeFragmentViewModel::class.java)
		viewmodelusertrack = ViewModelProviders.of(requireActivity()).get(UserTrackerViewModel::class.java)
		mMixpanel = MixpanelAPI.getInstance(requireContext(), "defeff96423cfb1e8c66f8ba83ab87fd")



		props.put("Dashboard initialized", true)
		mMixpanel?.track("DashBoard", props)

		pref = SessionTwiclo(requireContext())
		where = pref.guestLogin
		// Display size
		val displayMetrics = DisplayMetrics()
		requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
		width = displayMetrics.widthPixels
		height = Math.round(width * 0.49).toInt()
		catIconWidth = (width - 180) / 4



		fragmentHomeBinding?.viewPagerBannerNewDesh!!.layoutParams =
			LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)

		fragmentHomeBinding?.viewPagerBannerNewDesh!!.setClipToPadding(false)
		val viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
			object : ViewPager.OnPageChangeListener {
				override fun onPageSelected(position: Int) {}
				override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
				override fun onPageScrollStateChanged(arg0: Int) {}
			}

		fragmentHomeBinding?.viewPagerBannerNewDesh!!.addOnPageChangeListener(
			viewPagerPageChangeListener
		)

		val bundle = Bundle()
		bundle.putString("oncreate", "oncreate")
		bundle.putString("home", "Home Screen")
		analytics.logEvent("Home_Screen", bundle)

		apiCall("1")
		getObserveResponse()
		onClickEvent()
		// homeRecyclerview(arrayList!!)


		fragmentHomeBinding?.mainViewNestedSNewDesh!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
			if (scrollY > oldScrollY) {
				if (10 < scrollY) {
					fragmentHomeBinding?.topLayNewDesh!!.elevation = 20f
					fragmentHomeBinding?.topLayNewDesh!!.setBackgroundResource(R.color.white)
					val view =
						requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
					view.visibility = View.VISIBLE
				}
				//  Log.d("storeVisibilityNested11", "$scrollY--$oldScrollY")
			} else if (scrollX == scrollY) {
				if (scrollY < 5) {
					fragmentHomeBinding?.topLayNewDesh!!.setBackgroundResource(R.color.lightGray)
					fragmentHomeBinding?.topLayNewDesh!!.elevation = 0f
					val view =
						requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
					view.visibility = View.GONE
				}

				//  Log.d("storeVisibilityNested1", "$scrollY--$oldScrollY")
			} else {
				if (10 < scrollY) {
					fragmentHomeBinding?.topLayNewDesh!!.elevation = 20f
					fragmentHomeBinding?.topLayNewDesh!!.setBackgroundResource(R.color.white)
					val view =
						requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
					view.visibility = View.VISIBLE
				}
				//  Log.d("storeVisibilityNested2", "$scrollY--$oldScrollY")
			}

		})


		fragmentHomeBinding?.userAddressNewDesh?.text = SessionTwiclo(context).userAddress

		return fragmentHomeBinding?.root
	}

	private fun slideDown(view: View) {
		view.animate()
			.alpha(0.5f)
			.setDuration(500)
			.setListener(object : AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator?) {
					view.visibility = View.GONE
				}
			})
	}

	private fun slideUp(view: View) {
		view.animate()
			.alpha(1f)
			.setDuration(200)
			.setListener(object : AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator?) {
					view.visibility = VISIBLE
				}
			})
	}

	private fun onClickEvent() {
		fragmentHomeBinding?.deshbordRefreshNewDesh!!.setOnRefreshListener { apiCall("0") }

		fragmentHomeBinding?.retryOnHomeNewDesh!!.setOnClickListener {
			if ((activity as MainActivity).isNetworkConnected) {

				if (SessionTwiclo(context).isLoggedIn) {
					try {
						_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
						_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
						_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
						_progressDlg!!.setCancelable(false)
						_progressDlg!!.show()
					} catch (ex: Exception) {
						Log.wtf("IOS_error_starting", ex.cause!!)
					}

					viewmodel?.getHomeDataApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						SessionTwiclo(context).userLat,
						SessionTwiclo(context).userLng
					)

					viewmodel?.getBanners(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
						"1"
					)
					viewmodelusertrack?.customerActivityLog(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(requireContext()).mobileno, "Home Screen",
						SplashActivity.appversion, "", SessionTwiclo(requireContext()).deviceToken
					)

				} else {
					viewmodel?.getBanners(
						"",
						"",
						"1"
					)
					viewmodel?.getHomeDataApi(
						"",
						"",
						SessionTwiclo(context).userLat,
						SessionTwiclo(context).userLng
					)
				}
				fragmentHomeBinding?.noInternetOnHomeLlNewDesh!!.visibility = View.GONE
				fragmentHomeBinding?.deshbordRefreshNewDesh!!.visibility = View.VISIBLE

			} else {
				fragmentHomeBinding?.deshbordRefreshNewDesh!!.visibility = View.GONE
				fragmentHomeBinding?.mainViewNestedSNewDesh!!.visibility = View.GONE
				fragmentHomeBinding?.noInternetOnHomeLlNewDesh!!.visibility = View.VISIBLE
				(activity as MainActivity).showInternetToast()
			}

		}

		fragmentHomeBinding?.cartIconNewDesh?.setOnClickListener {
			if (SessionTwiclo(context).isLoggedIn) {
				AppUtils.startActivityRightToLeft(
					requireActivity(), Intent(context, CartActivity::class.java).putExtra(
						"storeId", SessionTwiclo(context).storeId
					)
				)
			} else {
				showLoginDialog("Please login to proceed")
			}

		}

		fragmentHomeBinding?.addressLayNewDesh?.setOnClickListener {
			startActivityForResult(
				Intent(context, SavedAddressesActivity::class.java)
					.putExtra("type", "address")
					.putExtra(
						"where", where
					), AUTOCOMPLETE_REQUEST_CODE
			)
			addEditAdd="Dashboard"
		}
	}

	private fun getObserveResponse() {

		viewmodel?.cartCountResponse?.observe(requireActivity(), { user ->
			Log.d("mModelData___",Gson().toJson(user))

			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
				_progressDlg = null
			}

			if (user != null) {
				if (!user.error) {
					val mModelData: CartCountModel = user

					if (context != null) {
						Log.d("mModelData___",mModelData.store_id)
						SessionTwiclo(context).storeId = mModelData.store_id
					}

					if (user.count != null) {
						if (user.count.toInt() > 0) {
							fragmentHomeBinding?.cartCountTxtNewDesh?.visibility = View.VISIBLE
							fragmentHomeBinding?.cartCountTxtNewDesh?.text = user.count.toString()
						} else {
							fragmentHomeBinding?.cartCountTxtNewDesh?.visibility = View.GONE
						}
					}

				} else {
					if (user.errorCode == 101) {
					}
				}
			}
		})

		viewmodel?.bannersResponse?.observe(requireActivity(), { user ->
			fragmentHomeBinding?.mainViewNestedSNewDesh?.visibility = View.VISIBLE
			fragmentHomeBinding?.deshbordRefreshNewDesh!!.isRefreshing = false
			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
				_progressDlg = null
			}
			if (user != null) {
				if (!user.error) {
					val mModelData: BannerModel = user
					Log.e("bannerResponse", Gson().toJson(mModelData))
					val sliderItemList: ArrayList<com.fidoo.user.data.SliderItem> = ArrayList()
					if (mModelData.banner != null) {
						for (i in 0 until mModelData.banner.size) {
							sliderItem = com.fidoo.user.data.SliderItem()
							sliderItem.imageUrl = mModelData.banner.get(i)
							sliderItemList.add(sliderItem)
						}
						val adapterr =
							SliderAdapterExample(
								activity
							) { }

						fragmentHomeBinding?.tabDotsHomeNewDesh?.setupWithViewPager(
							viewPagerBanner_newDesh,
							true
						)
						adapterr.renewItems(sliderItemList)
						fragmentHomeBinding?.viewPagerBannerNewDesh!!.adapter = adapterr

						val handler = Handler()
						val Update = Runnable {
							Log.d(
								"currenftPage_g_",
								currentPage.toString() + "----" + (sliderItemList.size - 1)
							)
							if (currentPage == sliderItemList.size) {
								//  fragmentHomeBinding?.viewPagerBannerNewDesh!!.setPadding(10,0,70,0)
								currentPage = 0
							} else {
								currentPage++
								// fragmentHomeBinding?.viewPagerBannerNewDesh!!.setPadding(70,0,10,0)

							}
							fragmentHomeBinding?.viewPagerBannerNewDesh!!.setCurrentItem(
								currentPage,
								true
							)
							Log.d("currenftPage__", currentPage.toString())

						}
						timer = Timer()
						timer!!.schedule(object : TimerTask() {
							override fun run() {
								handler.post(Update)
							}
						}, DELAY_MS, PERIOD_MS)

						try {
							if (user.show_slider.equals("1")) {
								fragmentHomeBinding?.bannerLLNewDesh!!.visibility = View.VISIBLE
							} else {
								fragmentHomeBinding?.bannerLLNewDesh!!.visibility = View.GONE
							}
						} catch (e: Exception) { }

					}
				}
			}
		})

		viewmodel?.homeDataResponse?.observe(requireActivity(), {
			Log.e("homeDataResponse__", Gson().toJson(it))
			fragmentHomeBinding?.mainViewNestedSNewDesh!!.visibility = View.VISIBLE
			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
				_progressDlg = null
			}
			//fidooLoaderCancel()

			if (it.error_code == 200) {
				if (it.homeData.isNotEmpty()) {

					homeRecyclerview(it.homeData as ArrayList)
				}
			}

		})

		viewmodel?.failureResponse?.observe(requireActivity(), { user ->
			//dismissIOSProgress()
			fragmentHomeBinding?.deshbordRefreshNewDesh!!.isRefreshing = false

			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
				_progressDlg = null
			}

			if (context != null) {
				Toast.makeText(context, user, Toast.LENGTH_SHORT).show()
			}

			Log.e("cart_response", Gson().toJson(user))
		})

	}

	private fun homeRecyclerview(arrayList: ArrayList<HomeData>) {
		serviceDetailsAdapter = ServiceDetailsAdapter(mmContext!!, arrayList, this, width)
		fragmentHomeBinding?.categorySmallRecyclerviewNewDesh!!.adapter = serviceDetailsAdapter
	}

	private fun apiCall(dialogStartOrNot: String) {
		if ((activity as MainActivity).isNetworkConnected) {
			if (SessionTwiclo(context).isLoggedIn) {
				// Ensure all future events sent from
				// the device will have the distinct_id 13793
				mMixpanel?.identify(SessionTwiclo(context).loggedInUserDetail.accountId)

				// Ensure all future user profile properties sent from
				// the device will have the distinct_id 13793
				mMixpanel?.people?.identify(SessionTwiclo(context).loggedInUserDetail.accountId)
				try {
					if (dialogStartOrNot == "1") {
						_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
						_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
						_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
						_progressDlg!!.setCancelable(false)
						_progressDlg!!.show()
					}
				} catch (ex: Exception) {
					Log.wtf("IOS_error_starting", ex.cause!!)
				}

				viewmodel?.getHomeDataApi(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken,
					SessionTwiclo(context).userLat,
					SessionTwiclo(context).userLng
				)

				viewmodel?.getBanners(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken,
					"1"
				)
				viewmodelusertrack?.customerActivityLog(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(requireContext()).mobileno, "Home Screen",
					SplashActivity.appversion, "", SessionTwiclo(requireContext()).deviceToken
				)

			} else {
				viewmodel?.getBanners(
					"",
					"",
					"1"
				)
				viewmodel?.getHomeDataApi(
					"",
					"",
					SessionTwiclo(context).userLat,
					SessionTwiclo(context).userLng
				)

			}
			fragmentHomeBinding?.noInternetOnHomeLlNewDesh!!.visibility = View.GONE
			fragmentHomeBinding?.deshbordRefreshNewDesh!!.visibility = View.VISIBLE

		} else {
			fragmentHomeBinding?.deshbordRefreshNewDesh!!.visibility = View.GONE
			fragmentHomeBinding?.mainViewNestedSNewDesh!!.visibility = View.GONE
			fragmentHomeBinding?.noInternetOnHomeLlNewDesh!!.visibility = View.VISIBLE
		}

	}


	override fun onActivityResult(
		requestCode: Int,
		resultCode: Int,
		data: Intent?
	) {
		super.onActivityResult(requestCode, resultCode, data)
		Log.d("MainActivity____", "request code $requestCode resultcode $resultCode")

		if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
			fragmentHomeBinding?.userAddressNewDesh?.text = SessionTwiclo(context).userAddress

		}
	}

	override fun onResume() {
		super.onResume()
		ProfileFragment.addManages = ""
		deleteRoomDataBase()
		if ((activity as MainActivity).isNetworkConnected) {
			if (SessionTwiclo(context).isLoggedIn) {
				viewmodel?.getCartCountApi(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken
				)
				userAddress_newDesh?.text = SessionTwiclo(context).userAddress
			} else {
				userAddress_newDesh?.text = SessionTwiclo(context).userAddress
			}
			fragmentHomeBinding?.noInternetOnHomeLlNewDesh!!.visibility = View.GONE

		}
	}

	override fun provideYourFragmentView(
		inflater: LayoutInflater?,
		parent: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		TODO("Not yet implemented")
	}

	private fun showLoginDialog(message: String) {
		val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
		builder.setTitle("Alert")
		builder.setMessage(message)
		builder.setPositiveButton("Login") { _, _ ->
			AppUtils.startActivityRightToLeft(
				requireActivity(),
				Intent(activity, SplashActivity::class.java)
			)
		}
		builder.setNegativeButton("Cancel") { _, _ ->

		}
		val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
		alertDialog.setCancelable(true)
		alertDialog.show()
	}

	override fun onExploreCatClick(outerPosition: Int?, innerPosition: Int?, model: Service) {
		Log.e("modelmodel", model.toString())
		if (model.id.equals("5") || model.id.equals("7")) {
			AppUtils.startActivityRightToLeft(
				requireActivity(),
				Intent(requireActivity(), StoreListActivity::class.java).putExtra(
					"serviceId", model.id
				).putExtra("serviceName", model.service_name)
			)

		} else if (model.id.equals("4")) {
			AppUtils.startActivityRightToLeft(
				requireActivity(), Intent(context, SendPackageActivity::class.java)
					.putExtra("where", where)
					.putExtra("cat_id", model.id)
					.putExtra("cat_name", model.service_name)
			)
		}else{
			AppUtils.startActivityRightToLeft(
				requireActivity(),
				Intent(requireActivity(), ServiceDailyNeedActivity::class.java).putExtra(
					"serviceId", model.id
				).putExtra("serviceName", model.service_name)
			)
		}
	}

	override fun onCurationCatClick(outerPosition: Int?, innerPosition: Int?, model: Curation) {
		Log.e("onCurationCatClick", model.toString())
		props.put("clicked on curations", model.name)
		val intent = Intent(mmContext, StoreFilterListActivity::class.java)
			.putExtra("selectedValue", "")
			.putExtra("serviceId", "5")
			.putExtra("serviceName", "Restaurants")
			.putExtra("cuisine_to_search", model.cuisine_id)
			.putExtra("cusineName", model.name)

		AppUtils.startActivityRightToLeft(requireActivity(), intent)
		// startActivity(intent)
	}

	override fun onPackageCatClick(
		outerPosition: Int?,
		innerPosition: Int?,
		model: PackageCategory
	) {
		if (innerPosition == 3) {
			AppUtils.startActivityRightToLeft(
				requireActivity(), Intent(context, SendPackageActivity::class.java)
					.putExtra("where", where)
					.putExtra("cat_id", "")
					.putExtra("cat_name", "")
			)
		} else {
			AppUtils.startActivityRightToLeft(
				requireActivity(), Intent(context, SendPackageActivity::class.java)
					.putExtra("where", where)
					.putExtra("cat_id", model.id)
					.putExtra("cat_name", model.name)
			)
		}
	}

	override fun onOfferCatClick(outerPosition: Int?, innerPosition: Int?, model: Offer) {
		props.put("clicked on offers", model.coupon_desc)
		Log.d("onOfferCatClick", model.delivery_distance.toString())
		AppUtils.startActivityRightToLeft(
			context as Activity?, Intent(context, StoreItemsActivity::class.java)
				.putExtra("storeId", model.store_id)
				.putExtra("storeName", model.store_name)
				.putExtra("store_location", model.address)
				.putExtra("delivery_time", model.delivery_time)
				.putExtra("cuisine_types", model.cuisines.joinToString(separator = ", "))
				.putExtra("coupon_desc", model.coupon_desc)
				.putExtra("distance", model.delivery_distance.toString())
		)
	}

	override fun onShopCatClick(outerPosition: Int?, innerPosition: Int?, model: ShopCategory) {
		if (model.id.equals("5") || model.id.equals("7")) {
			AppUtils.startActivityRightToLeft(
				requireActivity(),
				Intent(requireActivity(), StoreListActivity::class.java).putExtra(
					"serviceId", model.id
				).putExtra("serviceName", model.category_name)
			)
		} else {
			AppUtils.startActivityRightToLeft(
				requireActivity(),
				Intent(requireActivity(), ServiceDailyNeedActivity::class.java).putExtra(
					"serviceId", model.id
				).putExtra("serviceName", model.category_name)
			)
		}
	}

	override fun onUpcomingServicesClick(
		outerPosition: Int?,
		innerPosition: Int?,
		model: UpcomingServices
	) {
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mmContext = context
	}

	//delete room data
	private fun deleteRoomDataBase() {
		try {
			Thread {
				restaurantProductsDatabase = Room.databaseBuilder(
					requireContext().applicationContext,
					RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
				)
					.fallbackToDestructiveMigration()
					.build()
				restaurantProductsDatabase!!.resProductsDaoAccess()!!.deleteAll()

			}.start()
		} catch (e: java.lang.Exception) {
			e.printStackTrace()
		}

	}
}