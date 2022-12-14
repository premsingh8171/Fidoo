//package com.fidoo.user.cartview.activity
//
//import android.app.Activity
//import android.app.Dialog
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.graphics.drawable.Drawable
//import android.net.Uri
//import android.os.Bundle
//import android.os.Handler
//import android.text.TextUtils
//import android.util.Log
//import android.view.*
//import android.widget.*
//import androidx.appcompat.app.AlertDialog
//import androidx.core.content.ContextCompat
//import androidx.databinding.DataBindingUtil
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.lifecycle.observe
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.room.Room
//import com.android.volley.Request
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.DataSource
//import com.bumptech.glide.load.engine.GlideException
//import com.bumptech.glide.request.RequestListener
//import com.bumptech.glide.request.target.Target
//import com.fidoo.user.BuildConfig
//import com.fidoo.user.R
//import com.fidoo.user.activity.MainActivity.Companion.addCartTempList
//import com.fidoo.user.activity.SplashActivity
//import com.fidoo.user.addressmodule.activity.SavedAddressesActivity
//import com.fidoo.user.addressmodule.viewmodel.AddressViewModel
//import com.fidoo.user.cartview.adapter.CartItemsAdapter
//import com.fidoo.user.cartview.adapter.PrescriptionAdapter
//import com.fidoo.user.cartview.model.CartModel
//import com.fidoo.user.cartview.roomdb.database.PrescriptionDatabase
//import com.fidoo.user.cartview.roomdb.entity.PrescriptionViewEntity
//import com.fidoo.user.cartview.viewmodel.CartViewModel
//import com.fidoo.user.data.model.*
//import com.fidoo.user.data.session.SessionTwiclo
//import com.fidoo.user.grocery.activity.GroceryItemsActivity.Companion.onresumeHandle
//import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase
//import com.fidoo.user.grocerynewui.activity.GroceryNewUiActivity.Companion.product_valueUpdate
//import com.fidoo.user.interfaces.AdapterClick
//import com.fidoo.user.interfaces.AdapterCustomRadioClick
//import com.fidoo.user.ordermodule.ui.TrackOrderActivity
//import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
//import com.fidoo.user.profile.ui.EditProfileActivity
//import com.fidoo.user.restaurants.activity.StoreItemsActivity.Companion.handleresponce
//import com.fidoo.user.restaurants.adapter.StoreCustomItemsAdapter
//import com.fidoo.user.restaurants.listener.CustomCartPlusMinusClick
//import com.fidoo.user.restaurants.model.CustomCheckBoxModel
//import com.fidoo.user.restaurants.model.CustomListModel
//import com.fidoo.user.restaurants.model.CustomizeProductResponseModel
//import com.fidoo.user.restaurants.model.StoreDetailsModel
//import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
//import com.fidoo.user.restaurants.viewmodel.StoreDetailsViewModel
//import com.fidoo.user.store.activity.StoreListActivity
//import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
//import com.fidoo.user.utils.BaseActivity
//import com.fidoo.user.utils.BaseFragment
//import com.fidoo.user.utils.FORADDRESS_REQUEST_CODE
//import com.fidoo.user.utils.showAlertDialog
//import com.github.dhaval2404.imagepicker.ImagePicker
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.google.gson.Gson
//import com.mixpanel.android.mpmetrics.MixpanelAPI
//import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
//import com.razorpay.Checkout
//import com.razorpay.PaymentResultListener
//import kotlinx.android.synthetic.main.fragment_cart.*
//import kotlinx.android.synthetic.main.no_internet_connection.*
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import org.json.JSONObject
//import java.io.File
//
//
//@Suppress("DEPRECATION")
//class CartFragment : BaseFragment(),
//	CartItemsAdapter.AdapterCartAddRemoveClick,
//	AdapterClick,
//	CustomCartPlusMinusClick,
//	AdapterCustomRadioClick, PaymentResultListener {
//
//	var viewmodel: CartViewModel? = null
//	var totalAmount: Double = 0.0
//	var storeViewModel: StoreDetailsViewModel? = null
//	var addressViewModel: AddressViewModel? = null
//	var finalPrice: Double = 0.0
//	lateinit var behavior: BottomSheetBehavior<LinearLayout>
//	private var categoryy: ArrayList<CustomListModel>? = null
//	private var mModelDataTemp: CustomizeProductResponseModel? = null
//	var customIdsList: ArrayList<String>? = null
//	var customIdsListTemp: ArrayList<CustomCheckBoxModel>? = null
//	var arraylist: ArrayList<PrescriptionViewEntity>? = null
//	var totalAmountWithouttax: Double = 0.0
//	var noOfItems: Int = 0
//	var tempProductId: String? = ""
//	var product_id: String? = ""
//	var storeId: String? = ""
//	var isLessContactClicked: Boolean = true
//	var tempOfferPrice: String? = ""
//	var filePathTemp: String = ""
//	var fileUri: Uri? = null
//
//	var count: Int = 1
//	var isPrescriptionRequire: String = ""
//	var distanceViewModel: TrackViewModel? = null
//	private val co = Checkout()
//	var storelocation: ArrayList<StoreDetailsModel>? = null
//	var storeCustomerDistance = ""
//	var isSelected: String = ""
//	var address_id: String = ""
//	var merchant_instructions: String = ""
//	var islocationValid: Int = 0
//	var checkItemUpdate: Int = 0
//	private lateinit var productsDatabase: ProductsDatabase
//	private lateinit var restaurantProductsDatabase: RestaurantProductsDatabase
//	private lateinit var prescriptionDatabase: PrescriptionDatabase
//	var viewmodelusertrack: UserTrackerViewModel? = null
//	var payment_failed_Diolog: Dialog? = null
//	var payment_suc_Diolog: Dialog? = null
//
//	private var start_Lat: Double? = 0.0
//	private var start_Lng: Double? = 0.0
//	private var end_Lat: Double? = 0.0
//	private var end_Lng: Double? = 0.0
//	var start_point: String = ""
//	var end_point: String = ""
//	var lastCustomized_str: String = ""
//	var check: Int = 0
//	var prescription_id: Int = 0
//	var checkStore: Int = 0
//	var prescriptionAdapter: PrescriptionAdapter? = null
//	private var mMixpanel: MixpanelAPI? = null
//
//
//
//
//	companion object {
//		var store_imgStr: String = ""
//		var store_nameStr: String = ""
//
//		var selectedAddressId: String = ""
//		var selectedAddressName: String = ""
//		var selectedAddressTitle: String = ""
//		var selectedCouponId: String = ""
//		var selectedCouponName: String = ""
//		var userLat: String = ""
//		var userLong: String = ""
//		var storeLat: String = ""
//		var storeLong: String = ""
//
//		//var storeCustomerDistance: String = ""
//		var delivery_Lat: Double? = 0.0
//		var delivery_Lng: Double? = 0.0
//		var tempOrderId: String = ""
//		var proceedClick: Int = 0
//		var finalOrderId: String = ""
//
//		var accessToken: String = ""
//		var accountId: String = ""
//	}
//	lateinit var mView: View
//
//	override fun onCreateView(
//		inflater: LayoutInflater, container: ViewGroup?,
//		savedInstanceState: Bundle?
//	): View? {
//		mView =
//			inflate(inflater, R.layout.fragment_home_newui, container, false)
//
//
//		val window: Window = this.window
//		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
//		storeId = intent.getStringExtra("storeId").toString()
//		//  tv_delivery_discount
//		viewmodel = ViewModelProviders.of(this).get(CartViewModel::class.java)
//		storeViewModel = ViewModelProviders.of(this).get(StoreDetailsViewModel::class.java)
//		addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
//		viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)
//		deleteAllPrecription()
//		behavior = BottomSheetBehavior.from(bottom_sheet)
//		selectedAddressId = ""
//		selectedAddressName = SessionTwiclo(this).userAddress
//		tv_delivery_address_title.text = selectedAddressTitle
//		tv_delivery_address.text = selectedAddressName
//		address_id = SessionTwiclo(this).userAddressId
//		Log.d("address_idaddress_id", address_id)
//		selectedCouponId = ""
//		storeCustomerDistance = ""
//		userLat = SessionTwiclo(this).userLat
//		userLong = SessionTwiclo(this).userLng
//		storeLat = ""
//		storeLong = ""
//		var deliveryOption = "contact"
//		customIdsList = ArrayList<String>()
//		customIdsListTemp = ArrayList<CustomCheckBoxModel>()
//		addCartTempList = ArrayList<AddCartInputModel>()
//
//		mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
//
//		if (SessionTwiclo(this).profileDetail != null && SessionTwiclo(this).profileDetail.account != null) {
//			if (SessionTwiclo(this).profileDetail.account.name.equals("")) {
//				startActivity(Intent(this, EditProfileActivity::class.java))
//			}
//		}
//
//		if (SessionTwiclo(this).loggedInUserDetail != null) {
//			accountId = SessionTwiclo(this).loggedInUserDetail.accountId
//			accessToken = SessionTwiclo(this).loggedInUserDetail.accessToken
//		} else {
//			accountId = SessionTwiclo(this).loginDetail.accountId.toString()
//			accessToken = SessionTwiclo(this).loginDetail.accessToken
//		}
//
//		//For Faster checkout of RazorPay
//		// co.setKeyID("rzp_live_iceNLz5pb15jtP")
//		var paykey = BuildConfig.pay_key.toString()
//		co.setKeyID(paykey)
//
//		if (isNetworkConnected) {
//			viewmodel?.getCartDetails(
//				accountId,
//				accessToken,
//				userLat,
//				userLong
//			)
//			addressViewModel?.getAddressesApi(
//				accountId,
//				accessToken,
//				"",
//				""
//			)
//			linear_progress_indicator.visibility = View.VISIBLE
//
//			no_internet_cart.visibility = View.GONE
//			no_internet_Ll.visibility = View.GONE
//
//			getStoreDetails()
//
//		} else {
//			linear_progress_indicator.visibility = View.GONE
//			main_lay.visibility = View.GONE
//			no_internet_cart.visibility = View.VISIBLE
//			no_internet_Ll.visibility = View.VISIBLE
//			showInternetToast()
//		}
//
//		retry_onRefresh.setOnClickListener {
//			if (isNetworkConnected) {
//				getStoreDetails()
//				viewmodel?.getCartDetails(
//					accountId,
//					accessToken,
//					userLat,
//					userLong
//				)
//				addressViewModel?.getAddressesApi(
//					accountId,
//					accessToken,
//					"",
//					""
//				)
//
//				linear_progress_indicator.visibility = View.VISIBLE
//				main_lay.visibility = View.VISIBLE
//				no_internet_cart.visibility = View.GONE
//				no_internet_Ll.visibility = View.GONE
//			} else {
//				main_lay.visibility = View.GONE
//				linear_progress_indicator.visibility = View.GONE
//				no_internet_cart.visibility = View.VISIBLE
//				no_internet_Ll.visibility = View.VISIBLE
//				showInternetToast()
//			}
//		}
//
//		backIcon.setOnClickListener {
//			if (checkItemUpdate == 0) {
//				AppUtils.finishActivityLeftToRight(this@CartFragment)
//			} else {
//				val returnIntent = Intent()
//				setResult(RESULT_OK, returnIntent)
//				finish()
//			}
//		}
//
//		prescriptionViewEnable.setOnClickListener {
//			prescriptionViewEnable.visibility = View.GONE
//			prescriptionLay2.visibility = View.VISIBLE
//		}
//
//		tv_delivery_charges_label.setOnClickListener {
//			chargesFm.visibility = View.VISIBLE
//			chargesFmBg.visibility = View.VISIBLE
//			chargesFmBgbottom.visibility = View.VISIBLE
//		}
//
//		chargesFmBg.setOnClickListener {
//			chargesFmBgbottom.visibility = View.GONE
//			chargesFm.visibility = View.GONE
//			chargesFmBg.visibility = View.GONE
//			chargesFmBg.visibility = View.GONE
//			tax_and_charges_lay.visibility = View.GONE
//		}
//
//		chargesFmBgbottom.setOnClickListener {
//			chargesFmBgbottom.visibility = View.GONE
//			chargesFm.visibility = View.GONE
//			chargesFmBg.visibility = View.GONE
//		}
//
//		tv_tax_charges_label.setOnClickListener {
//
//			chargesFmBg.visibility = View.VISIBLE
//			tax_and_charges_lay.visibility = View.VISIBLE
//
//		}
//
//		cb_no_contact_delivery.setOnCheckedChangeListener { _, b ->
//			deliveryOption = if (b) {
//				"contactless"
//			} else {
//				"contact"
//			}
//		}
//
//		prescriptionImg.setOnClickListener {
//			ImagePicker.with(this)
//				.crop()                    //Crop image(Optional), Check Customization for more option
//				//   .compress(1024)			//Final image size will be less than 1 MB(Optional)
//				//  .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//				.start()
//		}
//
//		customAddBtn.setOnClickListener {
//			if (isNetworkConnected) {
//				var mCartId: String? = null
//
//				for (i in 0 until categoryy!!.size) {
//					customIdsList!!.add(categoryy!!.get(i).id.toString())
//				}
//
//				viewmodel?.getCartDetailsResponse?.observe(this, Observer { user ->
//					val mCartModelData: CartModel = user
//
//					try {
//						if (user.cart.size != 0) {
//							for (i in 0 until user.cart.size) {
//								mCartId = mCartModelData.cart[i].cart_id
//							}
//						}
//					} catch (e: NullPointerException) {
//						e.toString()
//					}
//
//				})
//
//				Log.e("customIdsList", customIdsList.toString())
//				showIOSProgress()
//				SessionTwiclo(this).storeId = intent.getStringExtra("storeId")
//
//				addCartTempList!!.clear()
//				val addCartInputModel = AddCartInputModel()
//				addCartInputModel.productId = tempProductId
//				addCartInputModel.quantity = countValuee.text.toString()
//				addCartInputModel.message = "add product"
//				addCartInputModel.customizeSubCatId = customIdsList!!
//				addCartInputModel.isCustomize = "1"
//				addCartTempList!!.add(addCartInputModel)
//				viewmodel!!.addToCartApi(
//					accountId,
//					accessToken,
//					addCartTempList!!,
//					mCartId!!
//
//				)
//			} else {
//				showInternetToast()
//			}
//		}
//
//		delivery_address_lay.setOnClickListener {
//			if (!isNetworkConnected) {
//				showToast(resources.getString(R.string.provide_internet))
//
//			} else {
//				startActivityForResult(
//					Intent(this, SavedAddressesActivity::class.java)
//						.putExtra("type", "order"), FORADDRESS_REQUEST_CODE
//				)
//			}
//		}
//
//		cash_lay.setOnClickListener {
//			isSelected = "cash"
//			cash_lay.setBackgroundResource(R.drawable.black_rounded_solid)
//			online_lay.setBackgroundResource(R.drawable.payment_nonactiveleft)
//
//			img_cash.setColorFilter(resources.getColor(R.color.primary_color))
//			tv_cash.setTextColor(resources.getColor(R.color.primary_color))
//
////            online_lay.background = ResourcesCompat.getDrawable(
////                resources,
////                R.drawable.black_rounded_solid, null
////            )
//			tv_online.setTextColor(resources.getColor(R.color.grey))
//			img_online.setImageResource(R.drawable.online_pay_grey)
//			img_online.setColorFilter(resources.getColor(R.color.grey))
//
//		}
//
//		online_lay.setOnClickListener {
//			isSelected = "online"
//			online_lay.setBackgroundResource(R.drawable.black_rounded_solid)
//			cash_lay.setBackgroundResource(R.drawable.payment_nonactiveleft)
//
//			img_online.setColorFilter(resources.getColor(R.color.primary_color))
//			tv_online.setTextColor(resources.getColor(R.color.primary_color))
//
////            cash_lay.background = ResourcesCompat.getDrawable(
////                resources,
////                R.drawable.black_rounded_solid, null
////            )
//			tv_cash.setTextColor(resources.getColor(R.color.grey))
//			img_cash.setImageResource(R.drawable.cash_icon_grey)
//			img_cash.setColorFilter(resources.getColor(R.color.grey))
//
//		}
//
//		delivery_address_lay.setOnClickListener {
//			if (!isNetworkConnected) {
//				showToast(resources.getString(R.string.provide_internet))
//
//			} else {
//				startActivity(
//					Intent(this, SavedAddressesActivity::class.java).putExtra(
//						"type",
//						"order"
//					)
//				)
//			}
//		}
//
//		cart_payment_lay.setOnClickListener {
//			if (!isNetworkConnected) {
//				showToast(resources.getString(R.string.provide_internet))
//			} else {
//				Log.d("userAddressId_", SessionTwiclo(this).userAddressId)
//				merchant_instructions = instruction_restaurantEt.getText().toString().trim()
//				if (address_id.equals("") && tv_delivery_address.text == "") {
//					tv_delivery_address.text = "Select address"
//					tv_delivery_address_title.text = ""
//					showToast("Please select your address")
//				} else if (isPrescriptionRequire == "1") {
//					if (filePathTemp.equals("")) {
//						showToast("Please upload prescription")
//					} else {
//
//						if (isNetworkConnected) {
//							showIOSProgress()
//							viewmodel?.orderPlaceApi(
//								accountId,
//								accessToken,
//								finalPrice.toFloat().toString(),
//								deliveryOption,
//								address_id,
//								"",
//								ed_delivery_instructions.text.toString(),
//								isSelected, merchant_instructions
//							)
//
//						} else {
//							showInternetToast()
//						}
//					}
//				} else {
//					showIOSProgress()
//					viewmodel?.orderPlaceApi(
//						accountId,
//						accessToken,
//						finalPrice.toFloat().toString(),
//						deliveryOption,
//						address_id,
//						"",
//						ed_delivery_instructions.text.toString(),
//						isSelected, merchant_instructions
//					)
//				}
//			}
//		}
//
//		viewmodel?.addToCartResponse?.observe(this) { user ->
//
//			linear_progress_indicator.visibility = View.GONE
//			dismissIOSProgress()
//			Log.e("addToCartRes_cart", Gson().toJson(user))
//			if (user.errorCode == 200) {
//				val mModelData: AddToCartModel = user
//				if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//					behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//				} else {
//					behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//				}
//				try {
//					checkItemUpdate = 1
//					handleresponce = 1
//					//  if (user.product_customize_id!="") {
//					Thread {
//						if (user.cart_quantity!!.toInt() == 0) {
//						} else {
//							updateByCartIdProductCustomized(
//								user.cart_quantity!!.toInt(),
//								user.product_id!!,
//								user.is_customize_quantity!!.toInt(),
//								lastCustomized_str!!,
//								user.cart_id!!,
//								user.product_customize_id
//							)
//						}
//					}.start()
//					//   }
//				} catch (e: Exception) {
//				}
//
//				getStoreDetails()
//
//				viewmodel?.getCartDetails(
//					accountId,
//					accessToken,
//					userLat,
//					userLong
//				)
//				showToast(mModelData.message)
//			}
//		}
//
//		viewmodel?.customizeProductResponse?.observe(this) { user ->
//			dismissIOSProgress()
//
//			Log.e("stores response", Gson().toJson(user))
//			mModelDataTemp = user
//
//			categoryy = ArrayList()
//
//			for (i in 0..mModelDataTemp?.category?.size!! - 1) {
//				if (mModelDataTemp?.category?.get(i)!!.isMultiple.equals("0")) {
//					if (mModelDataTemp?.category?.get(i)!!.isMandatory.equals("0")) {
//
//					} else {
//						var customListModel: CustomListModel? = CustomListModel()
//						customListModel!!.category = mModelDataTemp?.category?.get(i)!!.catId
//						customListModel!!.id =
//							mModelDataTemp?.category?.get(i)!!.subCat[0].id.toInt()
//						customListModel!!.price = mModelDataTemp?.category?.get(i)!!.subCat[0].price
//						customListModel!!.subCatName =
//							mModelDataTemp?.category?.get(i)!!.subCat[0].subCatName
//						categoryy!!.add(customListModel)
//					}
//				}
//			}
//
//			var tempPrice: Double? = 0.0
//			for (i in 0..customIdsListTemp!!.size - 1) {
//				tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
//			}
//
//			for (i in 0..categoryy!!.size - 1) {
//				tempPrice = tempPrice!! + categoryy!!.get(i).price.toDouble()
//			}
//			tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
//
//			customAddBtn.text =
//				"Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()
//
//			val adapter = StoreCustomItemsAdapter(
//				this,
//				mModelDataTemp?.category!!,
//				this,
//				categoryy,
//				this
//			)
//			customItemsRecyclerview.layoutManager = LinearLayoutManager(this)
//			customItemsRecyclerview.setHasFixedSize(true)
//			customItemsRecyclerview.adapter = adapter
//			//   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
//		}
//
//		viewmodel?.addRemoveCartResponse?.observe(this) { user ->
//			dismissIOSProgress()
//			Log.e("addRemove_cart_response", Gson().toJson(user))
//			var product_customize_id: String = ""
//			var cart_id: String = ""
//			var is_customize_quantity: Int = 0
//			var product_id: String = ""
//			var cart_quantity: Int = 0
//			checkItemUpdate = 1
//			if (user.errorCode == 200) {
//				handleresponce = 1
//				try {
//					if (user.cart_quantity != null) {
//						cart_quantity = user.cart_quantity!!.toInt()
//					}
//					if (user.product_id != null) {
//						product_id = user.product_id!!
//					}
//					if (user.is_customize_quantity != null) {
//						is_customize_quantity = user.is_customize_quantity!!.toInt()
//					}
//					if (user.cart_id != null) {
//						cart_id = user.cart_id!!
//					}
//					if (user.product_customize_id != null) {
//						product_customize_id = user.product_customize_id!!
//					}
//				} catch (e: Exception) {
//
//				}
//				try {
//					Thread {
//						updateByCartIdProductCustomized(
//							cart_quantity, product_id, is_customize_quantity,
//							lastCustomized_str!!, cart_id, product_customize_id
//						)
//					}.start()
//				} catch (e: Exception) {
//
//				}
//				getStoreDetails()
//				viewmodel?.getCartDetails(
//					accountId,
//					accessToken,
//					userLat,
//					userLong
//				)
//			}
//
//		}
//
//		viewmodel?.deleteCartResponse?.observe(this) { user ->
//			dismissIOSProgress()
//
//			Log.e("deleteCartResponse_", Gson().toJson(user))
//			Toast.makeText(this, user.message, Toast.LENGTH_LONG).show()
//			viewmodel?.getCartDetails(
//				accountId,
//				accessToken,
//				userLat,
//				userLong
//			)
//			getStoreDetails()
//		}
//
//		viewmodel?.getCartDetailsResponse?.observe(this) { user ->
//			dismissIOSProgress()
//			main_lay.visibility = View.VISIBLE
//			linear_progress_indicator.visibility = View.GONE
//			Log.e("getCartDetailsResponse_", Gson().toJson(user))
//
//			isPrescriptionRequire = "0"
//			linear_progress_indicator.visibility = View.GONE
//			if (!user.error) {
//				if (user.cart != null) {
//					val cartIndex = user.cart
//
//					// store lat long
//					storeLat = user.store_lat
//					storeLong = user.store_long
//
//					Log.e("storeDataCartActivity__", "$storeLat---$storeLong")
//					calculateStoreCustomerDistance()
//
//
//					for (i in 0 until cartIndex.size) {
//						if (user.cart[i].isPrescription == "1") {
//							isPrescriptionRequire = user.cart[i].isPrescription
//						}
//					}
//					store_nameStr = cartIndex.get(0).storeName.toString()
//					loadImage(cartIndex.get(0).store_image.toString())
//					store_name_oncart.text = cartIndex.get(0).storeName.toString()
//					store_add_oncart.text = cartIndex.get(0).store_address.toString()
//
//					if (isPrescriptionRequire.equals("1")) {
//
//						prescriptionLay.visibility = View.VISIBLE
//					} else {
//
//						prescriptionLay.visibility = View.GONE
//					}
//
//
//					if (user.cart[0].cod.equals("1")) {
//						isSelected = "cash"
//
//						cash_lay.visibility = View.VISIBLE
//						//cashOnDeliveryRadioBtn.visibility = View.VISIBLE
//					} else {
//						cash_lay.visibility = View.GONE
//						//cashOnDeliveryRadioBtn.isChecked = false
//						//cashOnDeliveryRadioBtn.visibility = View.GONE
//					}
//
//					if (user.cart[0].online.equals("1")) {
//						online_lay.visibility = View.VISIBLE
//						isSelected = "online"
//						online_lay.setBackgroundResource(R.drawable.black_rounded_solid)
//						cash_lay.setBackgroundResource(R.drawable.payment_nonactiveleft)
//
//						img_online.setColorFilter(resources.getColor(R.color.primary_color))
//						tv_online.setTextColor(resources.getColor(R.color.primary_color))
//						tv_cash.setTextColor(resources.getColor(R.color.grey))
//						img_cash.setImageResource(R.drawable.cash_icon_grey)
//						img_cash.setColorFilter(resources.getColor(R.color.grey))
//					} else {
//						//onlineRadioBtn.isChecked = false
//						online_lay.visibility = View.GONE
//					}
//
//					if (user.cart[0].online.equals("1") && user.cart.get(0).cod.equals("1")) {
//						//cashOnDeliveryRadioBtn.isChecked = false
//						//onlineRadioBtn.isChecked = true
//					}
//
//					if (user.cart[0].online.equals("0") && user.cart.get(0).cod.equals("1")) {
//						//cashOnDeliveryRadioBtn.isChecked = true
//						//onlineRadioBtn.isChecked = false
//					}
//
//					if (user.cart[0].online.equals("1") && user.cart.get(0).cod.equals("0")) {
//						//cashOnDeliveryRadioBtn.isChecked = false
//						//onlineRadioBtn.isChecked = true
//					}
//
//					val mModelData: CartModel = user
//
//					Log.e("mModelData_", mModelData.cart.size.toString())
//					storeId=mModelData.cart[0].storeId.toString()
//
//					val adapter = CartItemsAdapter(this, mModelData.cart, this, this)
//					cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
//					cartItemsRecyclerView.setHasFixedSize(true)
//					cartItemsRecyclerView.adapter = adapter
//					var items: String? = ""
//					totalAmount = 0.0
//					totalAmountWithouttax = 0.0
//					noOfItems = 0
//					//nameLabel.text = user.cart.get(0).storeName
//					for (i in 0 until mModelData.cart.size) {
//						noOfItems += mModelData.cart[i].quantity.toString().toInt()
//					}
//					var tempp: Double? = 0.0
//					for (i in 0 until mModelData.cart.size) {
//						try {
//						//	Log.d("mModelData__", mModelData.cart[i].service_id)
//							if (mModelData.cart[0].service_id.toString()
//									.equals("5") || mModelData.cart[0].service_id.toString()
//									.equals("7")
//							) {
//								store_header_ll.visibility = View.VISIBLE
//							} else {
//								store_header_ll.visibility = View.GONE
//							}
//						} catch (e: java.lang.Exception) {
//							e.printStackTrace()
//						}
//
//
//						tempp = 0.0
//						for (j in 0 until mModelData.cart[i].customizeItem.size) {
//							tempp =
//								tempp!! + mModelData.cart[i].customizeItem.get(j).price.toDouble()
//						}
//						tempp = tempp!! + mModelData.cart[i].offerPrice.toDouble()
//
//						totalAmount += (mModelData.cart[i].quantity.toString().toDouble() * tempp)
//					}
//
//					val deliveryChargeWithTax = mModelData.deliveryCharge + mModelData.tax.toInt()
//
//					// Item Total
//					tv_subtotal.text = resources.getString(R.string.ruppee) + totalAmount
//
//
//					totalAmount = totalAmount + deliveryChargeWithTax
//					//showToast(totalAmount.toString())
//
//					if (mModelData.totalTaxAndCharges.toString() == "" || mModelData.totalTaxAndCharges == null){
//						totalAmount = (totalAmount - mModelData.deliveryDiscount)
//					}else{
//						totalAmount = (totalAmount - mModelData.deliveryDiscount) + mModelData.totalTaxAndCharges
//					}
//
//
//					//showToast(mModelData.totalTaxAndCharges.toString())
//					finalPrice = totalAmount
//
//
//					//numberOfItemsValue.text = noOfItems.toString() + " Items"
//
//					tv_place_order.text =
//						"Pay " + resources.getString(R.string.ruppee) + finalPrice.toFloat().toString()
//					tv_grand_total.text =
//						resources.getString(R.string.ruppee) + finalPrice.toFloat().toString()
//
//					Log.e("Bottom Price", tv_place_order.text.toString())
//					Log.e("Grand Total", tv_grand_total.text.toString())
//					//showToast(mModelData.deliveryDiscount)
//					//showToast(finalPrice.toString())
//					tv_delivery_charges.text = resources.getString(R.string.ruppee) + deliveryChargeWithTax
//
//
//					// Taxes and charges For UrbanPiper Products
//					tv_tax_charges.text = resources.getString(R.string.ruppee) + mModelData.totalTaxAndCharges
//					charges_value.text = resources.getString(R.string.ruppee) + mModelData.allItemChargeTotal
//					tax_value.text = resources.getString(R.string.ruppee) + mModelData.allItemTaxTotal
//
//					if (mModelData.deliveryDiscount == 0) {
//						//delivery_coupon_name.visibility = View.GONE
//						//delivery_coupon_value.visibility = View.GONE
//					}
//					Log.d("ddaifvzd", mModelData.deliveryDiscount.toFloat().toString())
//					if (mModelData.deliveryDiscount.toFloat().toString().equals("0.0")) {
//						tv_delivery_discount_label.visibility = View.GONE
//						tv_delivery_discount.visibility = View.GONE
//						tv_cart_afterdiscount_lable.visibility = View.VISIBLE
//						tv_cart_afterDiscount.visibility = View.VISIBLE
//
//					} else {
//						tv_cart_afterdiscount_lable.visibility = View.GONE
//						tv_cart_afterDiscount.visibility = View.GONE
//						tv_delivery_discount_label.visibility = View.VISIBLE
//						tv_delivery_discount.visibility = View.VISIBLE
//						tv_delivery_discount_label.text = "Delivery Discount ( " + mModelData.delivery_coupon_name + ")"
//						tv_delivery_discount.text = "- " + resources.getString(R.string.ruppee) + mModelData.deliveryDiscount.toFloat()
//						tv_cart_afterDiscount.text = resources.getString(R.string.ruppee) + mModelData.after_discount_value.toFloat().toString()
//
//					}
//					//discountValue.text = resources.getString(R.string.ruppee) + mModelData.discount_amount.toString()
//					//tv_delivery_discount.text = "Cart Discount (" + mModelData.coupon_name +")"
//
//					//totalAmountBottom.text = resources.getString(R.string.ruppee) + finalPrice.toString()
//
//					if (!mModelData.coupon_id.equals("")) {
//
//						tv_coupon.visibility = View.VISIBLE
//
//						tv_cart_discount.visibility = View.VISIBLE
//						tv_cart_discount_label.visibility = View.VISIBLE
//						tv_cart_discount_label.text =
//							"Cart Discount (" + mModelData.coupon_name + ")"
//						tv_coupon.text = "Cart Coupon Applied (" + mModelData.coupon_name + ")"
//						tv_cart_discount.text =
//							"- " + resources.getString(R.string.ruppee) + user.discount_amount.toFloat()
//								.toString()
//
//
//						if (mModelData.totalTaxAndCharges.toString() == "" || mModelData.totalTaxAndCharges == null){
//							totalAmount = (totalAmount - mModelData.discount_amount.toDouble())
//						}else{
//							totalAmount = (totalAmount - mModelData.discount_amount.toDouble()) + mModelData.totalTaxAndCharges
//						}
//
//
//						//update by prem
//						finalPrice=totalAmount
//						tv_place_order.text =
//							"Pay " + resources.getString(R.string.ruppee) + totalAmount.toFloat()
//								.toString()
//						tv_grand_total.text =
//							resources.getString(R.string.ruppee) + totalAmount.toString()
//						Log.e("after_discount_vffalue", mModelData.after_discount_value)
//						tv_cart_afterDiscount.text =
//							resources.getString(R.string.ruppee) + mModelData.after_discount_value.toFloat()
//								.toString()
//						tv_cart_afterDiscount.visibility = View.VISIBLE
//						tv_cart_afterdiscount_lable.visibility = View.VISIBLE
//
//						//showToast("Offer applied successfully")
//					} else {
//						tv_cart_discount.visibility = View.GONE
//						tv_cart_discount_label.visibility = View.GONE
//						tv_cart_afterDiscount.visibility = View.GONE
//						tv_cart_afterdiscount_lable.visibility = View.GONE
//						tv_coupon.visibility = View.GONE
//						coupon_lay.visibility = View.GONE
//						//discountValue.visibility = View.GONE
//					}
//
//					nested_top_lay.visibility = View.VISIBLE
//					//place_order_lay.visibility = View.VISIBLE
//					noItemsTxt.visibility = View.GONE
//					check = 1
//				} else {
//					nested_top_lay.visibility = View.GONE
//					place_order_lay.visibility = View.GONE
//					noItemsTxt.visibility = View.VISIBLE
//					check = 0
//
//					//showToast("No items")
//					//finish()
//				}
//			} else {
//				if (user.errorCode == 101) {
//					showAlertDialog(this)
//				}
//			}
//		}
//
//		viewmodel?.paymentResponse?.observe(this) { user ->
//			Log.e("cart___response", Gson().toJson(user))
//			dismissIOSProgress()
//			/// paySuccessPopUp()
//			SessionTwiclo(this).storeId = ""
//			proceedClick = 0
//			finalOrderId = user.orderId
//			viewmodel?.proceedToOrder(
//				accountId,
//				accessToken,
//				finalOrderId
//			)
//
//		}
//
//		viewmodel?.proceedToOrderResponse?.observe(this, { orderProceed ->
//			Log.e("proceedToOrderResponse", Gson().toJson(orderProceed))
//			dismissIOSProgress()
//		})
//
//		viewmodel?.uploadPrescriptionResponse?.observe(this) { user ->
//			dismissIOSProgress()
//			Log.e("uploadPrescriptio___", Gson().toJson(user))
//
//			prescriptiontUpdate(prescription_id, fileUri.toString(), filePathTemp, user.document_id)
//			prescription_id++
//			prescriptiontInsert(prescription_id, "null", "null", "null")
//			getPresciption()
//
//			cart_payment_lay.isEnabled = true
//			cart_payment_lay.alpha = 1.0f
//		}
//
//		viewmodel?.deletePrescriptionResponse?.observe(this, { prescription ->
//			Log.e("prescription__", Gson().toJson(prescription))
//			dismissIOSProgress()
//		})
//
//		viewmodel?.appplyPromoResponse?.observe(this, { user ->
//			dismissIOSProgress()
//
//			Log.e("cart response", Gson().toJson(user))
//			viewmodel?.getCartDetails(
//				accountId,
//				accessToken,
//				userLat,
//				userLong
//			)
//			if (user.couponApply.equals("2")) {
//
//
//					/*appliedpromoValue.visibility = View.VISIBLE
//					appliedpromoDesc.visibility = View.VISIBLE
//					removeBtn.visibility = View.VISIBLE
//					view333.visibility = View.VISIBLE
//					promoValue.visibility = View.GONE
//					applyBtn.visibility = View.GONE
//					getOfferTxt.visibility = View.GONE
//					offerIcon.visibility = View.GONE*/
//					//tv_coupon.text = promoValue.text.toString()
//					tv_cart_discount.text =
//						resources.getString(R.string.ruppee) + user.discountAmount
//					totalAmount = totalAmount - user.discountAmount.toDouble()
//					tv_place_order.text = "Pay " + resources.getString(R.string.ruppee) + totalAmount.toFloat().toString()
//					tv_grand_total.text = resources.getString(R.string.ruppee) + totalAmount.toFloat().toString()
//					Log.e("Grand Total after promo", tv_grand_total.text.toString())
//					Log.e("Final Price after promo", tv_place_order.text.toString())
//					//discountLabel.visibility = View.VISIBLE
//					//discountValue.visibility = View.VISIBLE
//					//showToast("Offer applied successfully")
//				} else {
//
//					/*appliedpromoValue.visibility = View.GONE
//					appliedpromoDesc.visibility = View.GONE
//					removeBtn.visibility = View.GONE
//					view333.visibility = View.GONE
//					promoValue.visibility = View.VISIBLE
//					// applyBtn.visibility = View.VISIBLE
//					getOfferTxt.visibility = View.VISIBLE
//					offerIcon.visibility = View.VISIBLE*/
//				showToast(user.message)
//			}
//
//			//   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
//		})
//
//		viewmodel?.orderPlaceResponse?.observe(this, { user ->
//			dismissIOSProgress()
//			Log.e("orderPlaceResponse____", Gson().toJson(user))
//			// Log.d("orderPlaceResponse____", user.errorCode.toString())
//			if (user.errorCode == 200) {
//				tempOrderId = user.orderId
//
//				if (isSelected == "online") {
//					if (user.error.equals(true)) {
//						if (user.storeOffline == 1) {
//							showToast(user.message)
//							}
//							if (user.productOutOfStock == 1) {
//								showToast(user.message)
//							}
//							if (user.storeOffline == 1 && user.productOutOfStock == 1) {
//								showToast("This Store/Item is not available at this moment")
//							}
//						} else {
//							startPayment(user.razorPayOrderId)
//						}
//						//launchPayUMoneyFlow()
//
//					} else {
//						if (isNetworkConnected) {
//							paySuccessPopUp()
//							viewmodel?.paymentApi(
//								accountId,
//								accessToken,
//								user.orderId,
//								"",
//								"",
//								"cash"
//							)
//
//						} else {
//							showInternetToast()
//						}
//
//				}
//			} else if (user.errorCode == 400) {
//				Toast.makeText(this, "" + user.message, Toast.LENGTH_LONG).show()
//
//			}
//		})
//
//		viewmodel?.cartCountResponse?.observe(this, { user ->
//			Log.d("cartCountResponse", Gson().toJson(user))
//			dismissIOSProgress()
//			if (user != null) {
//				if (!user.error) {
//					if (user.count != null) {
//						if (user.count.toInt() > 0) {
//							nested_top_lay.visibility = View.VISIBLE
//							//place_order_lay.visibility = View.VISIBLE
//							noItemsTxt.visibility = View.GONE
//							check = 1
//						} else {
//								nested_top_lay.visibility = View.GONE
//							place_order_lay.visibility = View.GONE
//							noItemsTxt.visibility = View.VISIBLE
//							check = 0
//						}
//					}
//				}
//			}
//
//		})
//
//		viewmodel?.failureResponse?.observe(this) { user ->
//			dismissIOSProgress()
//			Log.e("cart_response", Gson().toJson(user))
//			//  showToast(user)
//
//			linear_progress_indicator.visibility = View.GONE
//			isPrescriptionRequire = "0"
//			linear_progress_indicator.visibility = View.GONE
//			viewmodel?.getCartCountApi(
//				accountId,
//				accessToken
//			)
//		}
//
//		prescriptiontInsert(prescription_id!!, "null", "null", "null")
//
//		getPresciption()
//
//	}
//
//	private fun getStoreDetails() {
//		storeViewModel?.getStoreDetails(
//			accountId,
//			accessToken,
//			storeId,
//			"",
//			"",
//			""
//		)
//		//storeDetailsResponce()
//	}
//
//	override fun onResume() {
//		super.onResume()
//		storeId = intent.getStringExtra("storeId").toString()
//		address_id = SessionTwiclo(this).userAddressId
//		Log.d("address_id_____", "" + address_id)
//		tv_delivery_address_title.text = selectedAddressTitle
//		tv_delivery_address.text = selectedAddressName
//
//		if (!isNetworkConnected) {
//			showInternetToast()
//		} else {
//			showIOSProgress()
//			viewmodel?.getCartCountApi(
//				accountId,
//				accessToken
//			)
//			storeDetailsResponce()
//
//			viewmodelusertrack?.customerActivityLog(
//				accountId,
//				SessionTwiclo(this).mobileno,
//				"CartView Screen",
//				SplashActivity.appversion,
//				StoreListActivity.serive_id_,
//				SessionTwiclo(this).deviceToken
//			)
//
//		}
//		if (SessionTwiclo(this).userAddressId != "") {
//			tv_delivery_address_title.text = selectedAddressTitle
//		}
//
//
//		if (selectedCouponName != "") {
//			tv_coupon.text = selectedCouponName
//			viewmodel?.applyPromoApi(
//				accountId,
//				accessToken,
//				selectedCouponName
//			)
//		}
//
//	}
//
//	private fun storeDetailsResponce() {
//		storeViewModel?.getStoreDetailsApi?.observe(this, { storeData ->
//			Log.e("storeDataCartActivity", Gson().toJson(storeData))
//			dismissIOSProgress()
//			try {
//				storeLat = storeData.storeLatitude
//				storeLong = storeData.storeLongitude
//				Log.e("storeDataCartActivity__", "$storeLat---$storeLong")
//				//calculateStoreCustomerDistance()
//
//				if (storeData.category.size != 0) {
//					for (i in 0 until storeData.category.size) {
//						for (j in 0 until storeData.category[i].product.size) {
//							val productData = storeData.category[i].product[j]
//							if (product_id.equals(productData.productId)) {
//								var customNamesList_: ArrayList<String>? = ArrayList()
//
//								if (productData.customizeItem!!.size != 0) {
//									for (k in 0 until storeData.category[i].product[j].customizeItem.size) {
//										try {
//											var lastCustomized: String = ""
//											val productData =
//												storeData.category[i].product[j].customizeItem[k]
//											lastCustomized = productData.subCatName
//											customNamesList_!!.add(lastCustomized)
//											val s: Set<String> =
//												LinkedHashSet<String>(customNamesList_)
//											customNamesList_.clear()
//											customNamesList_.addAll(s)
//										} catch (e: Exception) {
//											e.printStackTrace()
//										}
//									}
//									var lastCustomized: String = ""
//									lastCustomized = customNamesList_.toString()
//									val regex = "\\[|\\]"
//									lastCustomized_str = lastCustomized.replace(regex.toRegex(), "")
//									Log.e("lastCustomized_str___", lastCustomized_str)
//								}
//								updateProductCustomized(
//									productData.cartQuantity,
//									productData.productId,
//									productData.is_customize_quantity,
//									lastCustomized_str
//								)
//							}
//						}
//					}
//
//				}
//
//				viewmodel?.getCartDetails(
//					accountId,
//					accessToken,
//					userLat,
//					userLong
//				)
//			} catch (e: java.lang.Exception) {
//				e.printStackTrace()
//			}
//
//			//calculateStoreCustomerDistance(it.storeLatitude+","+it.storeLongitude, SessionTwiclo(this).userLat+","+SessionTwiclo(this).userLng)
//
//		})
//
//	}
//
//	private fun startPayment(razorpayOrderId: String?) {
//		/* Instantiate Checkout
//		 */
//		val activity: Activity = this
//		val co = Checkout()
//		//var razorpayId = "qwerty"
//
//		//  val razorpayOrderId = OrderPlaceModel().razorPayOrderId
//		if (razorpayOrderId != null) {
//			Log.e("razorpayOrderId", razorpayOrderId)
//		}
//
//		try {
//			val options = JSONObject()
//			options.put("name", "FIDOO")
//			options.put("description", "Charges")
//			//You can omit the image option to fetch the image from dashboard
//			options.put("image", "https://fidoo.in/include/assets/fidoo-logo.jpg")
//			options.put("theme.color", "#339347");
//			options.put("currency", "INR")
//			options.put("order_id", razorpayOrderId)
//			//Log.e("RAZORPAY", "")
//			var amount = 0.0f
//			try {
//				Log.e("totalAmount", totalAmount.toString())
//				amount = totalAmount.toFloat()
//				//showToast(""+amount)
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//			Log.e("_", (amount * 100).toString())
//
//			options.put("amount", amount * 100)
//
//			val prefill = JSONObject()
//			if (SessionTwiclo(this).profileDetail != null) {
//				prefill.put("email", SessionTwiclo(this).profileDetail.account.emailid)
//				prefill.put(
//					"contact",
//					SessionTwiclo(this).profileDetail.account.country_code + SessionTwiclo(this).profileDetail.account.userName
//				)
//				options.put("prefill", prefill)
//			} else {
//				prefill.put("email", SessionTwiclo(this).loginDetail.account.emailid)
//				prefill.put(
//					"contact",
//					"+91" + SessionTwiclo(this).loginDetail.account.userName
//				)
//				options.put("prefill", prefill)
//			}
//
//			co.open(activity, options)
//		} catch (e: java.lang.Exception) {
//			Toast.makeText(activity, "Error in payment, please try again", Toast.LENGTH_LONG).show()
//			e.printStackTrace()
//		}
//
//	}
//
//	override fun onPaymentError(errorCode: Int, response: String?) {
//		Log.d("onPaymentError", "$errorCode--$response")
//		try {
//			viewmodelusertrack?.customerActivityLog(
//				accountId,
//				SessionTwiclo(this).mobileno,
//				"CartView Screen On Payment fialed",
//				SplashActivity.appversion,
//				StoreListActivity.serive_id_,
//				SessionTwiclo(this).deviceToken
//			)
//			//Toast.makeText(this, "Payment failed, Please try again", Toast.LENGTH_LONG).show()
//			payment_failed_Diolog()
//		} catch (e: java.lang.Exception) {
//			Log.e("onError", "Exception in onPaymentSuccess", e)
//		}
//	}
//
//	override fun onPaymentSuccess(razorpayPaymentId: String?) {
//		try {
//			proceedClick = 0
//			//  Log.d("tempOrderId___",tempOrderId)
//			viewmodel?.paymentApi(
//				accountId,
//				accessToken,
//				tempOrderId,
//				razorpayPaymentId!!,
//				"",
//				"online"
//			)
//
//			viewmodelusertrack?.customerActivityLog(
//				accountId,
//				SessionTwiclo(this).mobileno,
//				"CartView Screen On Payment Successfull",
//				SplashActivity.appversion,
//				StoreListActivity.serive_id_,
//				SessionTwiclo(this).deviceToken
//			)
//
//			paySuccessPopUp()
//		} catch (e: java.lang.Exception) {
//			Log.e("onSuccess", "Exception in onPaymentSuccess", e)
//		}
//	}
//
//	override fun onActivityResult(
//		requestCode: Int,
//		resultCode: Int,
//		data: Intent?
//	) {
//		super.onActivityResult(requestCode, resultCode, data)
//
//		// Result Code is -1 send from Payumoney activity
//		Log.d("cartActivity__", "request code $requestCode resultcode $resultCode")
//		if (requestCode == Checkout.RZP_REQUEST_CODE && resultCode == RESULT_OK && data != null)
//		else {
//			when (resultCode) {
//				Activity.RESULT_OK -> {
//					//Image Uri will not be null for RESULT_OK
//					Log.e("RESULTCODE", resultCode.toString())
//					Log.e("data", data.toString())
//					fileUri = data?.data
//					prescriptionImg.setImageURI(fileUri)
//					//You can get File object from intent
//					val file: File = ImagePicker.getFile(data)!!
//
//					//You can also get File Path from intent
//					val filePath: String = ImagePicker.getFilePath(data)!!
//					filePathTemp = filePath
//
//					Log.e("filePath_", filePath)
//
//					uplaodGallaryImage(filePath!!)
//				}
//				ImagePicker.RESULT_ERROR -> {
//					Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//				}
//				else -> {
//					Toast.makeText(this, "Please upload your prescription", Toast.LENGTH_SHORT)
//						.show()
//				}
//			}
//
//			if (resultCode == FORADDRESS_REQUEST_CODE) {
//				Log.v("userAddressId", SessionTwiclo(this).userAddressId)
//
//			}
//
//		}
//	}
//
//	override fun onItemClick(
//		productId: String?,
//		type: String?,
//		count: String?,
//		offerPrice: String?,
//		customize_count: Int?,
//		productType: String?,
//		cart_id: String?
//	) {
//
//		val builder = AlertDialog.Builder(this)
//		builder.setTitle("Remove")
//		builder.setMessage("Are you sure you want to remove this item?")
//		builder.setIcon(R.drawable.about_icon)
//		builder.setPositiveButton("Yes") { dialogInterface, which ->
//
//			if (isNetworkConnected) {
//				showIOSProgress()
//				if (cart_id != null) {
//					product_id = productId
//
//					updateProductS(0, productId!!)
//					updateProductRestaurant(0, productId!!)
//					viewmodel?.deleteCartDetails(
//						accountId,
//						accessToken, productId!!,
//						cart_id
//					)
//					onresumeHandle = 1
//				}
//			} else {
//				showInternetToast()
//			}
//		}
//		builder.setNegativeButton("No") { dialogInterface, which ->
//		}
//		val alertDialog: AlertDialog = builder.create()
//		alertDialog.setCancelable(false)
//		alertDialog.show()
//	}
//
//	//prescription database
//	private fun prescriptiontInsert(
//		pres_id: Int,
//		image: String,
//		file_path: String,
//		document_id: String
//	) {
//		Thread {
//			try {
//				prescriptionDatabase = Room.databaseBuilder(
//					applicationContext,
//					PrescriptionDatabase::class.java, PrescriptionDatabase.DB_NAME
//				)
//					.fallbackToDestructiveMigration()
//					.build()
//				prescriptionDatabase!!.prescriptionDao()!!
//					.insertPrescriptionView(
//						PrescriptionViewEntity(
//							pres_id.toString(),
//							image,
//							file_path,
//							document_id
//						)
//					)
//
//				getPresciption()
//
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//
//		}.start()
//	} //prescription database
//
//
//	private fun prescriptiontUpdate(
//		pres_id: Int,
//		image: String,
//		file_path: String,
//		document_id: String
//	) {
//		Thread {
//			try {
//				prescriptionDatabase = Room.databaseBuilder(
//					applicationContext,
//					PrescriptionDatabase::class.java, PrescriptionDatabase.DB_NAME
//				)
//					.fallbackToDestructiveMigration()
//					.build()
//				prescriptionDatabase!!.prescriptionDao()!!
//					.updatePrescriptionView(pres_id, image, file_path!!, document_id)
//
//				getPresciption()
//
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//
//		}.start()
//	}
//
//	//getprescription
//	private fun getPresciption() {
//		Handler().postDelayed(
//			{
//				try {
//					dismissIOSProgress()
//					prescriptionDatabase!!.prescriptionDao()!!.getPrescriptionView().observe(this, {
//						arraylist = it as ArrayList
//						Log.e("presasb_", arraylist!!.size.toString())
//
//						prescriptionAdapter = PrescriptionAdapter(
//							this,
//							arraylist!!,
//							object : PrescriptionAdapter.OnClickPrescription {
//								override fun clearImage(
//									position: Int,
//									model: PrescriptionViewEntity
//								) {
//									showIOSProgress()
//									deletePrecription(model.pres_id.toInt())
//									viewmodel?.deletePrescriptionApi(
//										accountId,
//										accessToken,
//										model.document_id
//									)
//								}
//
//								override fun uploadImage(
//									position: Int,
//									model: PrescriptionViewEntity
//								) {
//									prescription_id = model.pres_id.toInt()
//									ImagePicker.with(this@CartFragment)
//										.crop()                    //Crop image(Optional), Check Customization for more option
//										//   .compress(1024)			//Final image size will be less than 1 MB(Optional)
//										//  .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//										.start()
//								}
//							})
//						prescription_rv?.adapter = prescriptionAdapter
//
//					})
//
//
//				} catch (e: Exception) {
//					e.printStackTrace()
//				}
//
//			}, 100
//		)
//	}
//
//	//delete prescription
//	private fun deletePrecription(pres_id: Int) {
//		Thread {
//			try {
//				prescriptionDatabase = Room.databaseBuilder(
//					applicationContext,
//					PrescriptionDatabase::class.java, PrescriptionDatabase.DB_NAME
//				)
//					.fallbackToDestructiveMigration()
//					.build()
//
//				prescriptionDatabase!!.prescriptionDao()!!.deleteItem(pres_id)
//
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//		}.start()
//		getPresciption()
//
//	}
//
//	//delete all
//	private fun deleteAllPrecription() {
//		Thread {
//			try {
//				prescriptionDatabase = Room.databaseBuilder(
//					applicationContext,
//					PrescriptionDatabase::class.java, PrescriptionDatabase.DB_NAME
//				)
//					.fallbackToDestructiveMigration()
//					.build()
//
//				prescriptionDatabase!!.prescriptionDao()!!.deleteAllItem()
//
//				getPresciption()
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//
//		}.start()
//	}
//
//	//update database
//	private fun updateProductS(count: Int, productId: String) {
//		Thread {
//			try {
//				productsDatabase = Room.databaseBuilder(
//					applicationContext,
//					ProductsDatabase::class.java, ProductsDatabase.DB_NAME
//				)
//					.fallbackToDestructiveMigration()
//					.build()
//				productsDatabase!!.productsDaoAccess()!!.updateProducts(count.toInt(), productId!!)
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//
//		}.start()
//	}
//
//	//update database by restaurants
//	private fun updateProductRestaurant(count: Int, productId: String) {
//		Thread {
//			try {
//				restaurantProductsDatabase = Room.databaseBuilder(
//					applicationContext,
//					RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
//				)
//					.fallbackToDestructiveMigration()
//					.build()
//				restaurantProductsDatabase!!.resProductsDaoAccess()!!
//					.updateProducts(count.toInt(), productId!!)
//			} catch (e: Exception) {
//				e.printStackTrace()
//			}
//
//		}.start()
//	}
//
//	private fun updateProductCustomized(
//		count: Int,
//		productId: String,
//		customize_quantity: Int?,
//		customizeItemName: String?
//	) {
//		Thread {
//			restaurantProductsDatabase = Room.databaseBuilder(
//				applicationContext,
//				RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
//			)
//				.fallbackToDestructiveMigration()
//				.build()
//			restaurantProductsDatabase!!.resProductsDaoAccess()!!.updateCustomizeProducts(
//				count,
//				productId!!,
//				customize_quantity!!,
//				customizeItemName
//			)
//
//		}.start()
//	}
//
//	private fun updateByCartIdProductCustomized(
//		count: Int,
//		productId: String,
//		customize_quantity: Int?,
//		customizeItemName: String?,
//		cart_id: String?,
//		product_customize_Id: String?
//	) {
//		Thread {
//			restaurantProductsDatabase = Room.databaseBuilder(
//				applicationContext,
//				RestaurantProductsDatabase::class.java, RestaurantProductsDatabase.DB_NAME
//			)
//				.fallbackToDestructiveMigration()
//				.build()
//			restaurantProductsDatabase!!.resProductsDaoAccess()!!.customizeProductUpdate(
//				count,
//				productId!!,
//				customize_quantity!!,
//				customizeItemName,
//				cart_id,
//				product_customize_Id
//			)
//
//		}.start()
//	}
//
//	fun uplaodGallaryImage(mImagePth: String?) {
//		Log.e("mImagePth", mImagePth.toString())
//		Log.e("accountId", SessionTwiclo(this).loggedInUserDetail.accountId.toString())
//		Log.e("accessToken", SessionTwiclo(this).loggedInUserDetail.accessToken.toString())
//
//		var mImageParts: MultipartBody.Part? = null
//		// creating image format for upload
//		mImageParts = if (!TextUtils.isEmpty(mImagePth)) {
//			val file = File(mImagePth)
//			val requestFile =
//				RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
//			MultipartBody.Part.createFormData("document", file.name, requestFile)
//		} else {
//			val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
//			MultipartBody.Part.createFormData("document", "", requestFile)
//		}
//
//		// Parameter request body
//		val accountId = RequestBody.create("text/plain".toMediaTypeOrNull(), accountId)
//		val accessToken = RequestBody.create("text/plain".toMediaTypeOrNull(), accessToken)
//
//		showIOSProgress()
//		cart_payment_lay.isEnabled = false
//		cart_payment_lay.alpha = 0.2f
//		viewmodel?.uploadPrescriptionImage(
//			accountId,
//			accessToken, mImageParts
//		)
//	}
//
//
//	override fun onIdSelected(
//		productId: String?,
//		type: String?,
//		price: String?,
//		sub_cat_name: String?,
//		tempSelectionCount: Int
//	) {
//		if (type.equals("select")) {
//
//			if (productId != null) {
//				customIdsList!!.add(productId)
//			}
//			val customCheckBoxModel =
//				CustomCheckBoxModel()
//			customCheckBoxModel.id = productId
//			customCheckBoxModel.price = price
//
//			customIdsListTemp!!.add(customCheckBoxModel)
//		} else {
//
//			for (i in 0..customIdsList!!.size - 1) {
//				if (customIdsList!!.get(i).equals(productId)) {
//					customIdsList!!.removeAt(i)
//					customIdsListTemp!!.removeAt(i)
//					break
//				}
//			}
//
//
//		}
//
//		var tempPrice: Double? = 0.0
//		for (i in 0..customIdsListTemp!!.size - 1) {
//			tempPrice = tempPrice!! + customIdsListTemp!!.get(i).price.toDouble()
//		}
//
//		for (i in 0..categoryy!!.size - 1) {
//			tempPrice = tempPrice!! + categoryy!!.get(i).price.toDouble()
//		}
//
//		tempPrice = tempOfferPrice!!.toDouble() + tempPrice!!
//		// customAddBtn.text = resources.getString(R.string.ruppee) + tempPrice.toString()
//		customAddBtn.text = "Add | " + resources.getString(R.string.ruppee) + tempPrice.toString()
//
//	}
//
//	override fun onCustomRadioClick(checkedId: String?, position: String?) {
//
//		var tempCat: String? = ""
//		var tempPrice: String? = ""
//		for (i in 0..mModelDataTemp?.category!!.size - 1) {
//
//			for (j in 0..mModelDataTemp?.category!!.get(i).subCat.size - 1) {
//
//				if (checkedId == mModelDataTemp?.category!!.get(i).subCat.get(j).id) {
//					tempCat = mModelDataTemp?.category!!.get(i).catId
//					tempPrice = mModelDataTemp?.category!!.get(i).subCat.get(j).price
//
//					break
//				}
//				if (!tempCat.equals("")) {
//					break
//				}
//
//			}
//
//		}
//		var tempAddEdit: String? = "add"
//		var tempAddEditId: String? = "add"
//		for (i in 0..categoryy!!.size - 1) {
//
//			if (categoryy!!.get(i).category.equals(tempCat)) {
//				/*  var customListModel: CustomListModel?= CustomListModel()
//				  customListModel!!.category= category.get(i).catId
//				  customListModel!!.id= category.get(i).subCat.get(0).id.toInt()*/
//				tempAddEdit = "edit"
//				tempAddEditId = i.toString()
//				categoryy!!.get(i).id = checkedId!!.toInt()
//				categoryy!!.get(i).price = tempPrice
//				break
//			}
//
//
//		}
//
//
//		if (tempAddEdit.equals("edit")) {
//
//			categoryy!!.get(tempAddEditId!!.toInt()).id = checkedId!!.toInt()
//			categoryy!!.get(tempAddEditId.toInt()).price = tempPrice
//
//		} else {
//
//			val customListModel: CustomListModel =
//				CustomListModel()
//			customListModel!!.category = tempCat
//			customListModel.id = checkedId!!.toInt()
//			customListModel.price = tempPrice
//			categoryy!!.add(customListModel)
//
//		}
//
//
//		var tempPricee: Double? = 0.0
//		for (i in 0..customIdsListTemp!!.size - 1) {
//			tempPricee = tempPricee!! + customIdsListTemp!!.get(i).price.toDouble()
//		}
//
//		for (i in 0..categoryy!!.size - 1) {
//			tempPricee = tempPricee!! + categoryy!!.get(i).price.toDouble()
//		}
//		tempPricee = tempOfferPrice!!.toDouble() + tempPricee!!
//
//		customAddBtn.text = "Add | " + resources.getString(R.string.ruppee) + tempPricee.toString()
//
//
//	}
//
//	override fun onAddItemClick(
//		productId: String?,
//		items: String?,
//		offerPrice: String?,
//		isCustomize: String?,
//		prodcustCustomizeId: String?,
//		cart_id: String?,
//		cart_quan: String?
//	) {
//		product_id = productId
//
//		if (!isNetworkConnected) {
//			showToast(resources.getString(R.string.provide_internet))
//
//		} else {
//
//			if (!items.equals("") || isCustomize.equals("1")) {
//				tempOfferPrice = offerPrice
//				tempProductId = productId
//				val builder = AlertDialog.Builder(this)
//				builder.setTitle("Your previous customization")
//				builder.setMessage(items)
//				// builder.setIcon(android.R.drawable.ic_dialog_alert)
//				builder.setPositiveButton("I'LL CHOOSE") { dialogInterface, which ->
//					if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//						behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//					} else {
//						behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//					}
//
//					//  tempProductId = productId
//					showIOSProgress()
//					customIdsList!!.clear()
//					if (productId != null) {
//
//
//						viewmodel?.customizeProductApi(
//							accountId,
//							accessToken,
//							productId
//						)
//					}
//				}
//				builder.setNegativeButton("REPEAT") { dialogInterface, which ->
//					showIOSProgress()
//					updateProductS(cart_quan!!.toInt(), productId!!)
//					updateProductRestaurant(cart_quan!!.toInt(), productId!!)
//
//					viewmodel?.addRemoveCartDetails(
//						accountId,
//						accessToken,
//						productId,
//						"add",
//						isCustomize!!,
//						prodcustCustomizeId!!,
//						cart_id!!,
//						customIdsList!!
//					)
//				}
//				val alertDialog: AlertDialog = builder.create()
//				alertDialog.setCancelable(true)
//				alertDialog.show()
//			} else {
//				showIOSProgress()
//				updateProductS(cart_quan!!.toInt(), productId!!)
//				updateProductRestaurant(cart_quan!!.toInt(), productId!!)
//				onresumeHandle = 1
//				product_valueUpdate = 1
//
//				viewmodel?.addRemoveCartDetails(
//					accountId,
//					accessToken,
//					productId,
//					"add",
//					isCustomize!!,
//					prodcustCustomizeId!!,
//					cart_id!!,
//					customIdsList!!
//				)
//			}
//
//
//		}
//	}
//
//	override fun onRemoveItemClick(
//		productId: String?,
//		quantity: String?,
//		isCustomize: String?,
//		prodcustCustomizeId: String?,
//		cart_id: String?,
//		cart_quan: String?
//	) {
//		product_id = productId
//		if (!isNetworkConnected) {
//			showToast(resources.getString(R.string.provide_internet))
//
//		} else {
//			Log.d("isCustomize__", isCustomize!!)
//			showIOSProgress()
//			updateProductS(cart_quan!!.toInt(), productId!!)
//			if (!isCustomize.equals("1")) {
//				updateProductRestaurant(cart_quan!!.toInt(), productId!!)
//			}
//			onresumeHandle = 1
//			product_valueUpdate = 1
//
//			viewmodel?.addRemoveCartDetails(
//				accountId,
//				accessToken,
//				productId,
//				"remove",
//				isCustomize!!,
//				prodcustCustomizeId!!,
//				cart_id!!,
//				customIdsList!!
//			)
//		}
//	}
//
//	fun calculateStoreCustomerDistance() {
//		val source = userLat + "," + userLong
//		val destination = storeLat + "," + storeLong
//		val urlDirections =
//			"https://maps.googleapis.com/maps/api/directions/json?origin=$source&destination=$destination&key=AIzaSyBB7qiqrzaHv09qpdJ9erY8oZXscyA7TEY"
//		Log.e("urlDirections", urlDirections)
//		var dist_: Float = 0f
//		val directionsRequest = object :
//			StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> { response ->
//				val jsonResponse = JSONObject(response)
//				dismissIOSProgress()
//				Log.e("res_routes", "routes- $jsonResponse")
//				var status = jsonResponse.optString("status")
//				if (!status.equals("ZERO_RESULTS")) {
//					val routes = jsonResponse.optJSONArray("routes")
//					if (routes.length() != 0) {
//						val legs = routes.optJSONObject(0).optJSONArray("legs")
//						val distance =
//							legs.optJSONObject(0).optJSONObject("distance").get("value").toString()
//						val distanceTex =
//							legs.optJSONObject(0).optJSONObject("distance").getInt("value")
//						Log.e("distance_", distanceTex.toString())
//
//						start_Lat = legs.optJSONObject(0).optJSONObject("start_location").opt("lat")
//							.toString().toDouble()
//						start_Lng = legs.optJSONObject(0).optJSONObject("start_location").opt("lng")
//							.toString().toDouble()
//						getGeoAddressFromLatLong2(start_Lat!!, start_Lng!!)
//						end_Lat = legs.optJSONObject(0).optJSONObject("end_location").opt("lat")
//							.toString().toDouble()
//						end_Lng = legs.optJSONObject(0).optJSONObject("end_location").opt("lng")
//							.toString().toDouble()
//
//						getGeoAddressFromLatLong2(end_Lat!!, end_Lng!!)
//						end_point = city.toLowerCase()
//						start_point = city.toLowerCase()
//
//						var start_add = legs.optJSONObject(0).getString("start_address")
//						var end_add = legs.optJSONObject(0).getString("end_address")
//
//						var newStartadd = start_add.contains("Gurugram")
//						var newEndadd = end_add.contains("Gurugram")
//
////						Log.d(
////							"end_point__",
////							start_point + "==" + end_point + "----" + newStartadd + "---" + newEndadd
////						)
//
//						if (newStartadd == true || newEndadd == true) {
//							end_point = "gurugram"
//							start_point = "gurugram"
//							if (distanceTex != null && distanceTex < 15000) {
//								islocationValid = 1
//								if (check == 1) {
//									place_order_lay.visibility = View.VISIBLE
//								}
//							} else {
//								place_order_lay.visibility = View.GONE
//								showToast("We are not servicable at your location!")
//							}
//						} else {
//							place_order_lay.visibility = View.GONE
//							showToastLong("Restaurant is currently unserviceable.")
//						}
//
////						if ((end_point.equals("gurugram") && start_point.equals("gurugram"))
////							|| (end_point.equals("gurgaon") && start_point.equals("gurgaon"))
////						) {
////
////						} else {
////							place_order_lay.visibility = View.GONE
////							showToastLong("Restaurant is currently unserviceable.")
////						}
//					}
//				} else {
//				//	showToast("No result found")
//					place_order_lay.visibility = View.GONE
//
//				}
//			}, Response.ErrorListener { }) {
//
//		}
//		val requestQueue = Volley.newRequestQueue(this)
//		requestQueue.add(directionsRequest)
//
//	}
//
//
//	private fun loadImage(url: String) {
//		store_imgStr = url
//		Glide.with(this).load(url)
//			.listener(object : RequestListener<Drawable?> {
//				override fun onLoadFailed(
//					e: GlideException?,
//					model: Any,
//					target: Target<Drawable?>,
//					isFirstResource: Boolean
//				): Boolean {
//					return false
//				}
//
//				override fun onResourceReady(
//					resource: Drawable?,
//					model: Any,
//					target: Target<Drawable?>,
//					dataSource: DataSource,
//					isFirstResource: Boolean
//				): Boolean {
//					return false
//				}
//			})
//			.placeholder(R.drawable.default_store)
//			.error(R.drawable.default_store).into(storeImg_oncart)
//	}
//
//	private fun payment_failed_Diolog() {
//		payment_failed_Diolog = Dialog(this)
//		payment_failed_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//		payment_failed_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//		payment_failed_Diolog?.setContentView(R.layout.payment_failed_popup)
//		payment_failed_Diolog?.window?.setLayout(
//			WindowManager.LayoutParams.MATCH_PARENT,
//			WindowManager.LayoutParams.MATCH_PARENT
//		)
//
//		payment_failed_Diolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
//		payment_failed_Diolog?.setCanceledOnTouchOutside(true)
//		payment_failed_Diolog?.show()
//		val dismiss_paymentFailed =
//			payment_failed_Diolog?.findViewById<ImageView>(R.id.dismiss_paymentFailed)
//		val gif_paymentImg = payment_failed_Diolog?.findViewById<ImageView>(R.id.gif_paymentImg)
//
//		Glide.with(this).load(R.drawable.payment_failed)
//			.listener(object : RequestListener<Drawable?> {
//				override fun onLoadFailed(
//					e: GlideException?,
//					model: Any,
//					target: Target<Drawable?>,
//					isFirstResource: Boolean
//				): Boolean {
//					return false
//				}
//
//				override fun onResourceReady(
//					resource: Drawable?,
//					model: Any,
//					target: Target<Drawable?>,
//					dataSource: DataSource,
//					isFirstResource: Boolean
//				): Boolean {
//					return false
//				}
//			})
//			.placeholder(R.drawable.payment_failed)
//			.error(R.drawable.payment_failed).into(gif_paymentImg!!)
//
//		dismiss_paymentFailed?.setOnClickListener {
//			payment_failed_Diolog?.dismiss()
//		}
//
//	}
//
//	override fun onBackPressed() {
//		if (checkItemUpdate == 0) {
//			AppUtils.finishActivityLeftToRight(this@CartFragment)
//		} else {
//			val returnIntent = Intent()
//			setResult(RESULT_OK, returnIntent)
//			finish()
//		}
//	}
//
//	fun paySuccessPopUp() {
//		payment_suc_Diolog = Dialog(this)
//		payment_suc_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//		payment_suc_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//		payment_suc_Diolog?.setContentView(R.layout.payment_success_popup)
//		payment_suc_Diolog?.window?.setLayout(
//			WindowManager.LayoutParams.MATCH_PARENT,
//			WindowManager.LayoutParams.MATCH_PARENT
//		)
//		// payment_suc_Diolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
//		payment_suc_Diolog?.setCanceledOnTouchOutside(true)
//		payment_suc_Diolog?.show()
//		val payment_successImg =
//			payment_suc_Diolog?.findViewById<ImageView>(R.id.payment_successImg)
//
//		Glide.with(this).load(R.drawable.pay_suc)
//			.listener(object : RequestListener<Drawable?> {
//				override fun onLoadFailed(
//					e: GlideException?,
//					model: Any,
//					target: Target<Drawable?>,
//					isFirstResource: Boolean
//				): Boolean {
//					return false
//				}
//
//				override fun onResourceReady(
//					resource: Drawable?,
//					model: Any,
//					target: Target<Drawable?>,
//					dataSource: DataSource,
//					isFirstResource: Boolean
//				): Boolean {
//					return false
//				}
//			})
//			.placeholder(R.drawable.pay_suc)
//			.error(R.drawable.pay_suc).into(payment_successImg!!)
//
//		Handler().postDelayed({
//
//			if (!finalOrderId.equals("")) {
//				SessionTwiclo(this).storeImg = store_imgStr
//				SessionTwiclo(this).orderId = finalOrderId
//				SessionTwiclo(this).storeName = store_nameStr
//
//				startActivity(
//					Intent(this, TrackOrderActivity::class.java).putExtra(
//						"orderId",
//						finalOrderId
//					).putExtra(
//						"delivery_boy_name",
//						""
//					).putExtra(
//						"delivery_boy_mobile",
//						""
//					).putExtra(
//						"type",
//						""
//					)
//				)
//				finishAffinity()
//			}
//		}, 6000)
//	}
//
//}
