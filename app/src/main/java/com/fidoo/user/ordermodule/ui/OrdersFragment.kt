package com.fidoo.user.ordermodule.ui

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.FragmentOrdersBinding
import com.fidoo.user.interfaces.AdapterReviewClick
import com.fidoo.user.ordermodule.adapter.OrdersAdapter
import com.fidoo.user.ordermodule.model.Feedback
import com.fidoo.user.ordermodule.model.MyOrdersModel
import com.fidoo.user.ordermodule.model.ReviewModel
import com.fidoo.user.ordermodule.model.UploadPresModel
import com.fidoo.user.ordermodule.viewmodel.MyOrdersFragmentViewModel
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.utils.showAlertDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@Suppress("DEPRECATION")
class OrdersFragment : Fragment(),
	AdapterReviewClick {

	var viewmodel: MyOrdersFragmentViewModel? = null
	var viewmodelusertrack: UserTrackerViewModel? = null
	private var _progressDlg: ProgressDialog? = null
	var orderIdTemp: String? = ""
	var mmContext: Context? = null
	var fragmentOrdersBinding: FragmentOrdersBinding? = null
	var checkStatusOfReview: Int = 0
	var ordersList: ArrayList<MyOrdersModel.Order>?=null

	companion object {
		var handleApiResponse: Int = 0
		var handleApiResponseForSendPackage: Int = 0
	}

	lateinit var analytics: FirebaseAnalytics
	var cancelOrderDialog: Dialog? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		fragmentOrdersBinding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false)

		viewmodel =
			ViewModelProviders.of(requireActivity()).get(MyOrdersFragmentViewModel::class.java)
		viewmodelusertrack =
			ViewModelProviders.of(requireActivity()).get(UserTrackerViewModel::class.java)
		analytics = FirebaseAnalytics.getInstance(requireContext())
		ordersList=ArrayList()

		val bundle = Bundle()
		bundle.putString("oncreate", "oncreate")
		bundle.putString("OrderList_Screen", "OrderList Screen")
		analytics.logEvent("Orderlist_Screen", bundle)

		try {
			_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
			_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

			_progressDlg!!.setCancelable(false)
			_progressDlg!!.show()
		} catch (ex: Exception) {
			Log.wtf("IOS_error_starting", ex.cause!!)
		}
		ApiCall()


		fragmentOrdersBinding?.noInternetLlInclude!!.retryOnRefresh.setOnClickListener {
			if ((activity as MainActivity).isNetworkConnected) {

				try {
					_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
					_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
					_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

					_progressDlg!!.setCancelable(false)
					_progressDlg!!.show()
				} catch (ex: Exception) {
					Log.wtf("IOS_error_starting", ex.cause!!)
				}
				if (SessionTwiclo(requireContext()).isLoggedIn) {
					viewmodel?.getMyOrders(
						SessionTwiclo(activity).loggedInUserDetail.accountId,
						SessionTwiclo(activity).loggedInUserDetail.accessToken
					)
				} else {
					_progressDlg!!.dismiss()
					fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
//                    Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG)
//                        .show()
				}


			} else {
				_progressDlg!!.dismiss()
				fragmentOrdersBinding?.ordersRecyclerView!!.visibility = View.GONE
				fragmentOrdersBinding?.noInternetLlInclude!!.noInternetLl.visibility = View.VISIBLE
				(activity as MainActivity).showInternetToast()
			}
		}

		fragmentOrdersBinding?.swipeRefreshLayOrd!!.setOnRefreshListener {
			ApiCall()
		}

		viewmodel?.failureResponse?.observe(requireActivity(), Observer { user ->
			//dismissIOSProgress()
			if (_progressDlg != null) {

				_progressDlg!!.dismiss()
				_progressDlg = null
			}
			Log.e("cart response", Gson().toJson(user))
			//showToast(user)
			Toast.makeText(mmContext, user, Toast.LENGTH_SHORT).show()


			//   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
		})

		viewmodel?.myOrdersResponse?.observe(requireActivity(), Observer { user ->
			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
				fragmentOrdersBinding?.ordersRecyclerView!!.visibility = View.VISIBLE
				fragmentOrdersBinding?.noInternetLlInclude!!.noInternetLl.visibility = View.GONE
				_progressDlg = null
			}
			Log.e("orderusersResponse", Gson().toJson(user))

			fragmentOrdersBinding?.swipeRefreshLayOrd!!.isRefreshing=false
			ordersList!!.clear()

			if(user.errorCode==200) {
				if (!user.error) {
					try {
						val mModelData: MyOrdersModel = user
						Log.e("ordersResponse", Gson().toJson(mModelData))
						ordersList = mModelData.orders as ArrayList
						if (mModelData.orders != null) {
							orderRv(ordersList!!)
							fragmentOrdersBinding?.noOrdersTxt?.visibility = View.GONE
						} else {
							fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
							// Toast.makeText(context,"No Orders", Toast.LENGTH_SHORT).show()
						}
						handleApiResponse = 0
					}catch (e:Exception){
						e.printStackTrace()
						if ((activity as MainActivity).isNetworkConnected) {
							fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
						}else {
						}
					}

				}
			}else {
				if (user.errorCode == 101) {
					showAlertDialog(requireContext())
				}
			}
		})

		viewmodel?.reviewResponse?.observe(requireActivity(), { user ->
			if (checkStatusOfReview == 1) {
				dismissIOSProgress()
				if (_progressDlg != null) {

					_progressDlg!!.dismiss()
					_progressDlg = null
				}


				val mModelData: ReviewModel = user
				Log.e("reviewResponse", Gson().toJson(mModelData))
				// Toast.makeText(requireContext(), user.message, Toast.LENGTH_SHORT).show()
				viewmodel?.getMyOrders(
					SessionTwiclo(activity).loggedInUserDetail.accountId,
					SessionTwiclo(activity).loggedInUserDetail.accessToken
				)
			}

		})

		viewmodel?.orderFeedback?.observe(requireActivity(), { feedback ->
			if (checkStatusOfReview == 1) {
				dismissIOSProgress()
				if (_progressDlg != null) {

					_progressDlg!!.dismiss()
					_progressDlg = null
				}


				val model: Feedback = feedback
				Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show()
				viewmodel?.getMyOrders(
					SessionTwiclo(activity).loggedInUserDetail.accountId,
					SessionTwiclo(activity).loggedInUserDetail.accessToken
				)
			}
		})

		viewmodel?.uploadPrescriptionResponse?.observe(requireActivity(), { user ->
			dismissIOSProgress()
			if (_progressDlg != null) {

				_progressDlg!!.dismiss()
				_progressDlg = null
			}
			val mModelData: UploadPresModel = user

			Log.e("uploadResponse", Gson().toJson(mModelData))

		})

//        viewmodel?.cancelOrderResponse?.observe(requireActivity(), { user ->
//
//        })

		viewmodel?.repeatOrderResponse?.observe(requireActivity(), { response ->
			Log.e("repeatOrderResponse", Gson().toJson(response))
			dismissProgressBar()
			if (response.error_code == 200) {
				AppUtils.startActivityRightToLeft(
					requireActivity(), Intent(requireContext(), CartActivity::class.java).putExtra(
						"storeId", SessionTwiclo(context).storeId
					)
				)
			}
		})

		return fragmentOrdersBinding?.root
	}

	private fun ApiCall() {
		if ((activity as MainActivity).isNetworkConnected) {
			if (SessionTwiclo(requireContext()).isLoggedIn) {
				viewmodel?.getMyOrders(
					SessionTwiclo(activity).loggedInUserDetail.accountId,
					SessionTwiclo(activity).loggedInUserDetail.accessToken
				)
				viewmodelusertrack?.customerActivityLog(
					SessionTwiclo(activity).loggedInUserDetail.accountId,
					SessionTwiclo(activity).mobileno,
					"OrderList Screen",
					SplashActivity.appversion,
					StoreListActivity.serive_id_,
					SessionTwiclo(activity).deviceToken
				)
			} else {
				_progressDlg!!.dismiss()
				fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
			}

		} else {
			_progressDlg!!.dismiss()
			fragmentOrdersBinding?.ordersRecyclerView!!.visibility = View.GONE
			fragmentOrdersBinding?.noInternetLlInclude!!.noInternetLl.visibility = View.VISIBLE
		}
	}

	private fun orderRv(orders: MutableList<MyOrdersModel.Order>) {
		val adapter =
			OrdersAdapter(mmContext, orders, this, object : OrdersAdapter.OnOrderItemClick {
				override fun onCancelOrder(orders: MyOrdersModel.Order, pos: Int) {
//                if (orders.orderId!=null){
//                    cancelOrderDialog(orders.orderId)
//                }


				}

				override fun onRepeatOrder(orders: MyOrdersModel.Order, pos: Int) {
					//repeat order
					showProgressBar()
					viewmodel?.repeatOrderApi(
						SessionTwiclo(activity).loggedInUserDetail.accountId,
						SessionTwiclo(activity).loggedInUserDetail.accessToken,
						orders.orderId
					)

				}
			})
		fragmentOrdersBinding?.ordersRecyclerView?.layoutManager = GridLayoutManager(context, 1)
		fragmentOrdersBinding?.ordersRecyclerView?.setHasFixedSize(true)
		fragmentOrdersBinding?.ordersRecyclerView?.adapter = adapter
	}


	override fun onReviewDoneClick(
		orderId: String?,
		storeRating: String?,
		reviewStore: String?,
		ratingDriver: String?,
		reviewDriver: String?
	) {
		try {
			_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
			_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

			_progressDlg!!.setCancelable(false)
			_progressDlg!!.show()
		} catch (ex: Exception) {
			Log.wtf("IOS_error_starting", ex.cause!!)
		}
		checkStatusOfReview = 1
		viewmodel?.reviewSubmitApi(
			SessionTwiclo(activity).loggedInUserDetail.accountId,
			SessionTwiclo(activity).loggedInUserDetail.accessToken,
			orderId,
			storeRating,
			reviewStore,
			ratingDriver,
			reviewDriver
		)

	}

	override fun onReviewSubmit(
		orderId: String?,
		star: String?,
		improvement: String?,
		message: String?,
		type: String?
	) {
		// Log.d("orderId____",orderId+"---"+star+"---"+improvement+"---"+message+"--"+type)

		try {
			_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
			_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

			_progressDlg!!.setCancelable(false)
			_progressDlg!!.show()
		} catch (ex: Exception) {
			Log.wtf("IOS_error_starting", ex.cause!!)
		}
		checkStatusOfReview = 1
		viewmodel?.addfeedbackApi(
			SessionTwiclo(activity).loggedInUserDetail.accountId,
			SessionTwiclo(activity).loggedInUserDetail.accessToken,
			orderId,
			star,
			improvement,
			message,
			type
		)

	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK) {
			//Image Uri will not be null for RESULT_OK
			val fileUri = data?.data
			// imgProfile.setImageURI(fileUri)

			val file: File = ImagePicker.getFile(data)!!

			//You can also get File Path from intent
			val filePath: String = ImagePicker.getFilePath(data)!!
			uplaodGallaryImage(orderIdTemp, data?.data!!.path!!)
		} else if (resultCode == ImagePicker.RESULT_ERROR) {
			Toast.makeText(mmContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
		} else {
			Toast.makeText(mmContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
		}
	}

	fun uplaodGallaryImage(orderId: String?, mImagePth: String?) {
		Log.e("orderId", orderId.toString())
		Log.e("mImagePth", mImagePth.toString())
		Log.e("accountId", SessionTwiclo(activity).loggedInUserDetail.accountId.toString())
		Log.e("accessToken", SessionTwiclo(activity).loggedInUserDetail.accessToken.toString())
		var mImageParts: MultipartBody.Part? = null
		// creating image format for upload
		mImageParts = if (!TextUtils.isEmpty(mImagePth)) {
			val file = File(mImagePth)
			val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
			MultipartBody.Part.createFormData("document", file.name, requestFile)
		} else {
			val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
			MultipartBody.Part.createFormData("document", "", requestFile)
		}
		// Parameter request body
		val accountId = RequestBody.create(
			"text/plain".toMediaTypeOrNull(),
			SessionTwiclo(activity).loggedInUserDetail.accountId
		)  // Parameter request body
		val accessToken = RequestBody.create(
			"text/plain".toMediaTypeOrNull(),
			SessionTwiclo(activity).loggedInUserDetail.accessToken
		)  // Parameter request body
		val orderId = RequestBody.create("text/plain".toMediaTypeOrNull(), orderId!!)
		try {
			_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
			_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

			_progressDlg!!.setCancelable(false)
			_progressDlg!!.show()
		} catch (ex: Exception) {
			Log.wtf("IOS_error_starting", ex.cause!!)
		}
		viewmodel?.uploadPrescriptionImage(accountId, accessToken, orderId, mImageParts)


	}

	private fun cancelOrderDialog(order_id: String) {
		cancelOrderDialog = Dialog(requireContext())
		cancelOrderDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
		cancelOrderDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		cancelOrderDialog?.setContentView(R.layout.logout_popup)
		cancelOrderDialog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT
		)
		cancelOrderDialog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
		cancelOrderDialog?.setCanceledOnTouchOutside(true)
		cancelOrderDialog?.show()
		val cencelBtn = cancelOrderDialog?.findViewById<CardView>(R.id.cencelBtn)
		val logoutBtn = cancelOrderDialog?.findViewById<CardView>(R.id.logoutBtn)
		val rll_logout = cancelOrderDialog?.findViewById<RelativeLayout>(R.id.rll_logout)
		val message_txt = cancelOrderDialog?.findViewById<TextView>(R.id.message_txt)
		val cancel_txt = cancelOrderDialog?.findViewById<TextView>(R.id.cancel_txt)
		val yes_txt = cancelOrderDialog?.findViewById<TextView>(R.id.yes_txt)

		message_txt!!.text = "Are you sure you want to cancel this order?"
		cancel_txt!!.text = "No"
		yes_txt!!.text = "Yes"

		cencelBtn?.setOnClickListener {
			cancelOrderDialog?.dismiss()
		}
		rll_logout?.setOnClickListener {
			//    cancelOrderDialog?.dismiss()
		}

		logoutBtn?.setOnClickListener {
			viewmodel!!.cancelOrderApi(
				SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
				SessionTwiclo(requireContext()).loggedInUserDetail.accessToken,
				order_id
			)
			cancelOrderDialog?.dismiss()

		}

	}


	override fun onAttach(context: Context) {
		super.onAttach(context)
		mmContext = context
	}

	override fun onResume() {
		super.onResume()

		if ((activity as MainActivity).isNetworkConnected) {
			if (SessionTwiclo(requireContext()).isLoggedIn) {
				if (handleApiResponse == 1 || handleApiResponseForSendPackage == 1) {
					viewmodel?.getMyOrders(
						SessionTwiclo(activity).loggedInUserDetail.accountId,
						SessionTwiclo(activity).loggedInUserDetail.accessToken
					)
				}
			} else {
				fragmentOrdersBinding?.noOrdersTxt?.visibility = View.VISIBLE
			}

			fragmentOrdersBinding?.ordersRecyclerView!!.visibility = View.VISIBLE
			fragmentOrdersBinding?.noInternetLlInclude!!.noInternetLl.visibility = View.GONE
		} else {
			_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
				(activity as MainActivity).showInternetToast()
			}
			fragmentOrdersBinding?.ordersRecyclerView!!.visibility = View.GONE
			fragmentOrdersBinding?.noInternetLlInclude!!.noInternetLl.visibility = View.VISIBLE
		}

	}

	fun showProgressBar() {
		try {
			_progressDlg = ProgressDialog(context, R.style.TransparentProgressDialog)
			_progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			_progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

			_progressDlg!!.setCancelable(false)
			_progressDlg!!.show()
		} catch (ex: Exception) {
			Log.wtf("IOS_error_starting", ex.cause!!)
		}
	}

	fun dismissProgressBar() {
		try {
			if (_progressDlg != null) {
				_progressDlg!!.dismiss()
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}

	}
}