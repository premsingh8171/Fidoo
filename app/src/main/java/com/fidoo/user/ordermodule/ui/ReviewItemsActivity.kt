package com.fidoo.user.ordermodule.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.adapter.ReviewItemsAdapter
import com.fidoo.user.ordermodule.model.Change
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.android.synthetic.main.activity_review_items.*

class ReviewItemsActivity : BaseActivity() {
	var orderViewModel: OrderDetailsViewModel? = null
	var sessionTwiclo: SessionTwiclo? = null
	var reviewItemsAdapter: ReviewItemsAdapter? = null
	var viewmodel: TrackViewModel? = null
	var order_cancel_Diolog: Dialog? = null
	var payment_mode:String=""

	private var mMixpanel: MixpanelAPI? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
		window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
		setContentView(R.layout.activity_review_items)
		orderViewModel = ViewModelProvider(this).get(OrderDetailsViewModel::class.java)
		viewmodel = ViewModelProvider(this).get(TrackViewModel::class.java)
		sessionTwiclo= SessionTwiclo(this)
		payment_mode= intent.getStringExtra("payment_mode")!!
		//itemListRv
		onClick()
		apicall()
		responseObserver()
		mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")
	}

	private fun onClick() {
		accept_orderLll.setOnClickListener {
			showIOSProgress()
			orderViewModel!!.approveProductChangeRequestApi(
				sessionTwiclo!!.loggedInUserDetail.accountId,
				sessionTwiclo!!.loggedInUserDetail.accessToken,
				intent.getStringExtra("request_id"),
				intent.getStringExtra("orderId")
			)
		}

		cancel_orderLl.setOnClickListener {
			showIOSProgress()
			viewmodel!!.cancelOrderApi(
				SessionTwiclo(this).loggedInUserDetail.accountId,
				SessionTwiclo(this).loggedInUserDetail.accessToken,
				intent.getStringExtra("orderId")!!
			)
		}
	}

	private fun responseObserver() {
		orderViewModel?.productChangeRequestDetailsRes!!.observe(this, {
			Log.d("productChangeRequestDetailsRes", Gson().toJson(it))
			dismissIOSProgress()
			if (it.error_code == 200) {
				itemsList(it.changes as ArrayList)
			}
		})

		orderViewModel?.approveProductChangeRequestRes!!.observe(this, {
			Log.d("approveProductChangeRequestRes", Gson().toJson(it))
			dismissIOSProgress()
			if (it.error_code == 200) {
				finish()
			}
		})

		viewmodel?.cancelOrderResponse?.observe(this, { user ->
			dismissIOSProgress()
			orderCancelPopUp()
			Log.e("cancelOrderResponse", Gson().toJson(user))
		})

	}

	fun orderCancelPopUp() {
		order_cancel_Diolog = Dialog(this)
		order_cancel_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
		order_cancel_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		order_cancel_Diolog?.setContentView(R.layout.order_cancel_popup)
		order_cancel_Diolog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT
		)
		order_cancel_Diolog?.setCanceledOnTouchOutside(true)
		order_cancel_Diolog?.show()
		val order_cancelImg = order_cancel_Diolog?.findViewById<ImageView>(R.id.order_cancelImg)

		Glide.with(this).load(R.drawable.order_cancel)
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
			.placeholder(R.drawable.order_cancel)
			.error(R.drawable.order_cancel).into(order_cancelImg!!)

		Handler(Looper.getMainLooper()).postDelayed({
			order_cancel_Diolog?.dismiss()
			startActivity(Intent(this, MainActivity::class.java))
			finishAffinity()
		}, 3000)

	}

	private fun itemsList(arrayList: ArrayList<Change>) {
		reviewItemsAdapter = ReviewItemsAdapter(this,payment_mode, arrayList)
		itemListRv.adapter = reviewItemsAdapter
	}

	private fun apicall() {
		showIOSProgress()
		orderViewModel?.productChangeRequestDetailsApi(
			sessionTwiclo!!.loggedInUserDetail.accountId,
			sessionTwiclo!!.loggedInUserDetail.accessToken,
			intent.getStringExtra("request_id")
		)
	}

	override fun onBackPressed() {}
}