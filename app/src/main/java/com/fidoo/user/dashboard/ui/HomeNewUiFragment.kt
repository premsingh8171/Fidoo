package com.fidoo.user.dashboard.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.addEditAdd
import com.fidoo.user.activity.MainActivity.Companion.orderSuccess
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.addressmodule.activity.SavedAddressesActivity
import com.fidoo.user.addressmodule.adapter.AddressesAdapter
import com.fidoo.user.addressmodule.adapter.AddressesAdapterBottom
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.addressmodule.viewmodel.AddressViewModel
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
import com.fidoo.user.restaurants.activity.NewDBStoreItemsActivity
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
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class HomeNewUiFragment : BaseFragment(), ClickEventOfDashboard{
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
	var payment_suc_Diolog: Dialog? = null

	var addressViewModel: AddressViewModel? = null
	var address = ArrayList<String>()
	lateinit var addressAdapter: AddressesAdapter

	private var mMixpanel: MixpanelAPI? = null
	private val props = JSONObject()

	private  var dialog : Dialog? = null

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
		fragmentHomeBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_home_newui, container, false)

		analytics = FirebaseAnalytics.getInstance(requireContext())
		addressViewModel = ViewModelProviders.of(requireActivity()).get(AddressViewModel::class.java)
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


		fragmentHomeBinding?.viewPagerBannerNewDesh!!.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)

		fragmentHomeBinding?.viewPagerBannerNewDesh!!.clipToPadding = false

		val viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
			object : ViewPager.OnPageChangeListener {
				override fun onPageSelected(position: Int) {}
				override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
				override fun onPageScrollStateChanged(arg0: Int) {}
			}

		fragmentHomeBinding?.viewPagerBannerNewDesh!!.addOnPageChangeListener(viewPagerPageChangeListener)

		val bundle = Bundle()
		bundle.putString("oncreate", "oncreate")
		bundle.putString("home", "Home Screen")
		analytics.logEvent("Home_Screen", bundle)

		try {
			if ((activity as MainActivity).isNetworkConnected) {
				if (SessionTwiclo(context).isLoggedIn) {
					viewmodel?.checkPaymentStatusApi(
						SessionTwiclo(context).loggedInUserDetail.accountId,
						SessionTwiclo(context).loggedInUserDetail.accessToken,
					)
				}
			}
		}catch (e:Exception){}

		apiCall("1")
		getObserveResponse()
		onClickEvent()

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
					view.visibility = View.VISIBLE
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
		fragmentHomeBinding?.textNewDesh?.text= SessionTwiclo(context).addressType


		return fragmentHomeBinding?.root
	}


	/**
	 * Showing BottomSheetDialog to see saved address or to add new one
	 */
	private fun showDialog() {
		dialog = context?.let { Dialog(it) }!!
		dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
		dialog?.setContentView(R.layout.manage_address_bottomsheet_dialogue)
		val lvAddNewAdd = dialog?.findViewById<LinearLayout>(R.id.lv_add_new_address)
		val bottomSheetAddress = dialog?.findViewById<LinearLayout>(R.id.ll_bottomSheetAddress)
		val lvCheckLocation = dialog?.findViewById<LinearLayout>(R.id.manage_location_Off_or_On)
		val rvManageAddress = dialog?.findViewById<RecyclerView>(R.id.rvManageSavedAddress)
		val mBtnToTurnOnLocation = dialog?.findViewById<Button>(R.id.btnToTurnLocationOn)
//		val address = dialog.findViewById<TextView>(R.id.home_address_tv)
//		address.text = SessionTwiclo(context).userAddress
		mBtnToTurnOnLocation?.setOnClickListener {
			val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
			startActivity(intent)
		}
		lvAddNewAdd?.setOnClickListener {
			startActivityForResult(
				Intent(context, SavedAddressesActivity::class.java)
					.putExtra("type", "address")
					.putExtra("where", where
					), AUTOCOMPLETE_REQUEST_CODE
			)
			addEditAdd = "Dashboard"
		}
		addressViewModel?.getAddressesResponse?.observe(requireActivity(), androidx.lifecycle.Observer { user ->
			Log.e("addresses_response", Gson().toJson(user))
			if (!user.addressList.isNullOrEmpty()) {
				bottomSheetAddress?.visibility = VISIBLE
				val adapter = AddressesAdapterBottom(
					requireContext(), user.addressList,
					object : AddressesAdapterBottom.SetOnDeteleAddListener {
						override fun onDelete(
							add_id: String,
							addressList: GetAddressModel.AddressList
						) {}

						override fun onClick(addressList: GetAddressModel.AddressList) {
							when {
								addressList.addressType.equals("1") -> {
									SessionTwiclo(requireContext()).userAddress = addressList.flatNo + ", " + addressList.landmark + ", " + addressList.location
									SessionTwiclo(requireContext()).addressType= "Home"
								}

								addressList.addressType.equals("2") -> {
									SessionTwiclo(requireContext()).userAddress = addressList.flatNo + ", " + addressList.landmark + ", " + addressList.location
									SessionTwiclo(requireContext()).addressType = "Office"

								}

								else -> {
									SessionTwiclo(requireContext()).userAddress = addressList.flatNo + ", " + addressList.landmark + ", " + addressList.location
									SessionTwiclo(requireContext()).addressType = "Other"

								}
							}
//							 holder.itemView.setassDefaultTxt.visibility=View.VISIBLE
//							  holder.itemView.selectedadd_img.visibility=View.VISIBLE
//							 holder.itemView.selectedadd_img.setImageResource(R.drawable.filter_on)

							SessionTwiclo(requireContext()).userAddress = addressList.flatNo + ", " + addressList.landmark + ", " + addressList.location
							SessionTwiclo(requireContext()).userAddressId = addressList.id
							SessionTwiclo(requireContext()).userLat = addressList.latitude
							SessionTwiclo(requireContext()).userLng = addressList.longitude
							dialog?.dismiss()
							restHomePage()
						}
					},
					"bottomSheetAddress"
				)
				rvManageAddress?.layoutManager = GridLayoutManager(requireContext(), 1)
				rvManageAddress?.setHasFixedSize(true)
				rvManageAddress?.adapter = adapter
			}
		})
//		if(isLocationEnabled(requireContext())){
//			lvCheckLocation.visibility = VISIBLE
//		}
		val manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			//Toast.makeText(context, "GPS is disable!", Toast.LENGTH_LONG).show()
				lvCheckLocation?.visibility = View.GONE
		dialog?.show()
		dialog?.window!!.setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)
		dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		dialog?.window!!.setGravity(Gravity.BOTTOM)
	}

	/**
	 *
	 */

	private fun restHomePage() {
		deleteRoomDataBase()
		if ((activity as MainActivity).isNetworkConnected) {
			if (SessionTwiclo(context).isLoggedIn) {
				viewmodel?.getCartCountApi(
					SessionTwiclo(context).loggedInUserDetail.accountId,
					SessionTwiclo(context).loggedInUserDetail.accessToken
				)
				userAddress_newDesh?.text = SessionTwiclo(context).userAddress

				if (SessionTwiclo(context).addressType.equals("")) {
					text_newDesh.text= "Your Location"
				}else{

					text_newDesh.text = SessionTwiclo(context).addressType
				}

			} else {
				userAddress_newDesh?.text = SessionTwiclo(context).userAddress
				text_newDesh.text= SessionTwiclo(context).addressType

			}
			fragmentHomeBinding?.noInternetOnHomeLlNewDesh!!.visibility = View.GONE
		}
	}

	/**
	 * To check Location is enabled or not
	 */
	private fun isLocationEnabled(context: Context): Boolean {
		val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		return LocationManagerCompat.isLocationEnabled(locationManager)
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

		fragmentHomeBinding?.deshbordRefreshNewDesh!!.setOnRefreshListener {
			apiCall("0")
		}

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
				fragmentHomeBinding?.deshbordRefreshNewDesh!!.visibility = VISIBLE
			}
			else {
				fragmentHomeBinding?.deshbordRefreshNewDesh!!.visibility = View.GONE
				fragmentHomeBinding?.mainViewNestedSNewDesh!!.visibility = View.GONE
				fragmentHomeBinding?.noInternetOnHomeLlNewDesh!!.visibility = VISIBLE
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
		/**
		 * Button to go to Manage Address BottomSheetDialogue
		 */
		fragmentHomeBinding?.addressLayNewDesh?.setOnClickListener {
			showDialog()
			/**
			 * First Method to pop up BottomSheetDialogue
			 */
//			val view:View = layoutInflater.inflate(R.layout.manage_address_bottomsheet_dialogue,null)
//			val dialog = BottomSheetDialog(requireContext())
//			dialog.setContentView(view)
//			dialog.show()
		}
	}

	private fun getObserveResponse() {
		viewmodel?.cartCountResponse?.observe(requireActivity(), { user ->
			Log.d("mModelData___", Gson().toJson(user))

			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
				_progressDlg = null
			}

			if (user != null) {
				if (!user.error) {
					val mModelData: CartCountModel = user

					if (context != null) {
						Log.d("mModelData___", mModelData.store_id)
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
						} catch (e: Exception) {
						}

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

		viewmodel?.checkPaymentStatusRes?.observe(requireActivity()) { user ->
			Log.e("checkPaymentStatusRes_", Gson().toJson(user))
			dismissIOSProgress()
			if (user.error_code==200){
				if (orderSuccess==0) {
					orderSuccess==1
					paySuccessPopUp()
				}
			}
		}
	}

	private fun homeRecyclerview(arrayList: ArrayList<HomeData>) {
		serviceDetailsAdapter = ServiceDetailsAdapter(mmContext!!, arrayList, this, width)
		fragmentHomeBinding?.categorySmallRecyclerviewNewDesh!!.adapter = serviceDetailsAdapter
	}

	private fun apiCall(dialogStartOrNot: String) {
		if ((activity as MainActivity).isNetworkConnected) {
			if (SessionTwiclo(context).isLoggedIn) {
				mMixpanel?.identify(SessionTwiclo(context).loggedInUserDetail.accountId)
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
					"1" )

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

				if (SessionTwiclo(context).addressType.equals("")) {
					text_newDesh.text= "Your Location"
				}else{

					text_newDesh.text = SessionTwiclo(context).addressType
				}

			} else {
				userAddress_newDesh?.text = SessionTwiclo(context).userAddress
				text_newDesh.text= SessionTwiclo(context).addressType

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
				Intent(activity, AuthActivity::class.java)
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
		} else {
			AppUtils.startActivityRightToLeft(
				requireActivity(),
				Intent(requireActivity(), ServiceDailyNeedActivity::class.java).putExtra(
					"serviceId", model.id
				).putExtra("serviceName", model.service_name)
			)
		}
	}

	override fun onStop() {
		super.onStop()
		dialog?.dismiss()
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
			//	context as Activity?, Intent(context, StoreItemsActivity::class.java)
			//	context as Activity?, Intent(context, NewStoreItemsActivity::class.java)
			context as Activity?, Intent(context, NewDBStoreItemsActivity::class.java)
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
				restaurantProductsDatabase.resProductsDaoAccess()!!.deleteAll()

			}.start()
		} catch (e: java.lang.Exception) {
			e.printStackTrace()
		}
	}

	private fun paySuccessPopUp() {
		payment_suc_Diolog = Dialog(requireActivity())
		payment_suc_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
		payment_suc_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		payment_suc_Diolog?.setContentView(R.layout.payment_success_popup)
		payment_suc_Diolog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT
		)
		payment_suc_Diolog?.setCanceledOnTouchOutside(true)
		payment_suc_Diolog?.show()
		val payment_successImg =
			payment_suc_Diolog?.findViewById<ImageView>(R.id.payment_successImg)

		Glide.with(this).load(R.drawable.pay_suc)
			.listener(object : RequestListener<Drawable?> {
				override fun onLoadFailed(
					e: GlideException?,
					model: Any,
					target: Target<Drawable?>,
					isFirstResource: Boolean
				): Boolean {
					return false
				}

				override fun onResourceReady(
					resource: Drawable?,
					model: Any,
					target: Target<Drawable?>,
					dataSource: DataSource,
					isFirstResource: Boolean
				): Boolean {
					return false
				}
			})
			.placeholder(R.drawable.pay_suc)
			.error(R.drawable.pay_suc).into(payment_successImg!!)

		Handler().postDelayed({
			payment_suc_Diolog?.dismiss()
		}, 5000)
	}


//	private fun setAddressAdapter() {
//		addressAdapter = AddressesAdapter(requireContext(), address)
//		val linearLayoutManager = LinearLayoutManager(requireContext())
//		recyclerView.adapter = addressAdapter
//		recyclerView.layoutManager = linearLayoutManager
//	}
}