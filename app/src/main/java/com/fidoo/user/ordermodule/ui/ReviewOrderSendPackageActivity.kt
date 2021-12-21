package com.fidoo.user.ordermodule.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fidoo.user.R
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.model.ReviewModel
import com.fidoo.user.ordermodule.ui.OrdersFragment.Companion.handleApiResponse
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.CommonUtils
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.android.synthetic.main.activity_review_order_sendpackages.*

@Suppress("DEPRECATION")
class ReviewOrderSendPackageActivity : BaseActivity() {
    var viewmodel: OrderDetailsViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var orderId: String? = ""
    var driver_rating: String? = ""
    var store_rating: String? = ""
    private var _progressDlg: ProgressDialog? = null
    private var mMixpanel: MixpanelAPI? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_review_order_sendpackages)
        viewmodel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        viewmodelusertrack = ViewModelProviders.of(this).get(UserTrackerViewModel::class.java)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        if (intent.getStringExtra("orderId")!=null){
            orderId=intent.getStringExtra("orderId")
        }

        onClick()

        if (isNetworkConnected) {
            viewmodel?.sendPackageOrderDetailsApi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId")
            )
            viewmodelusertrack?.customerActivityLog(SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).mobileno,"ReviewOrder Screen",
                SplashActivity.appversion, "",SessionTwiclo(this).deviceToken
            )
        } else { }

        backIcon_reviewOrd.setOnClickListener {
            finish()
            // AppUtils.finishActivityLeftToRight(this)
        }

        viewmodel?.sendPackageOrderDetailsRes?.observe(this, { user ->
            dismissIOSProgress()
            Log.e("orders_details_ffResponse", Gson().toJson(user))

            if (user!=null) {
                visible_View_ReviewOrdLl.visibility=View.VISIBLE

//                val adapter = ItemsAdapter(this, mModelData.items)
//                delivered_itemsRecyclerView?.layoutManager = GridLayoutManager(this, 1)
//                delivered_itemsRecyclerView?.setHasFixedSize(true)
//                delivered_itemsRecyclerView?.adapter = adapter

                try{
                    // var date_time = user.deleivered_at
                    //var time = date_time.split(" ")[1]
                    // var time_AmPm = date_time.split(" ")[2]
                    //  Log.e("time_______", time+time_AmPm)
                    // order_delivered_time.text = time + " " + time_AmPm
                    order_delivered_time.text =user.deleivered_at

                }catch (e:Exception){
                    e.printStackTrace()
                }

                package_itemsTxt.text = user.item_name


                if (user.delivery_boy_name == "") {
                } else {
                    driver_name_OnSendPackgeTxt.text = user.delivery_boy_name
                }
                if (user.from_address == "") {
                } else {
                    locText_PicupAdd_reviewOrd.text = user.from_address
                }
                if (user.to_address == "") {
                } else {
                    tv_toAddress_reviewOrd.text = user.to_address
                }


            }else{
                visible_View_ReviewOrdLl.visibility=View.GONE
            }
        })


        viewmodel?.reviewResponse?.observe(this, { user ->
            CommonUtils.dismissIOSProgress()

                if (_progressDlg != null) {
                    _progressDlg!!.dismiss()
                    _progressDlg = null
                }
                handleApiResponse=1

                val mModelData: ReviewModel = user
                Log.e("reviewResponse_", Gson().toJson(mModelData))
                Toast.makeText(this, user.message, Toast.LENGTH_SHORT).show()
                finish()

        })


        viewmodel?.failureResponse?.observe(this, Observer { user ->
            if (_progressDlg != null) {
                _progressDlg!!.dismiss()
                _progressDlg = null
            }
        })
    }

    @SuppressLint("ResourceType")
    private fun onClick() {
        driver_star1.setOnClickListener {
            driver_star1.setImageResource(R.drawable.start_on)
            driver_star2.setImageResource(R.drawable.start_off)
            driver_star3.setImageResource(R.drawable.start_off)
            driver_star4.setImageResource(R.drawable.start_off)
            driver_star5.setImageResource(R.drawable.start_off)
            driver_rating = "1"
            review_submitSendPackage.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_color));
        }

        driver_star2.setOnClickListener {
            driver_star1.setImageResource(R.drawable.start_on)
            driver_star2.setImageResource(R.drawable.start_on)
            driver_star3.setImageResource(R.drawable.start_off)
            driver_star4.setImageResource(R.drawable.start_off)
            driver_star5.setImageResource(R.drawable.start_off)
            driver_rating = "2"
            review_submitSendPackage.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_color));
        }

        driver_star3.setOnClickListener {
            driver_star1.setImageResource(R.drawable.start_on)
            driver_star2.setImageResource(R.drawable.start_on)
            driver_star3.setImageResource(R.drawable.start_on)
            driver_star4.setImageResource(R.drawable.start_off)
            driver_star5.setImageResource(R.drawable.start_off)
            driver_rating = "3"
            review_submitSendPackage.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_color));
        }

        driver_star4.setOnClickListener {
            driver_star1.setImageResource(R.drawable.start_on)
            driver_star2.setImageResource(R.drawable.start_on)
            driver_star3.setImageResource(R.drawable.start_on)
            driver_star4.setImageResource(R.drawable.start_on)
            driver_star5.setImageResource(R.drawable.start_off)
            driver_rating = "4"
            review_submitSendPackage.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_color));
        }

        driver_star5.setOnClickListener {
            driver_star1.setImageResource(R.drawable.start_on)
            driver_star2.setImageResource(R.drawable.start_on)
            driver_star3.setImageResource(R.drawable.start_on)
            driver_star4.setImageResource(R.drawable.start_on)
            driver_star5.setImageResource(R.drawable.start_on)
            driver_rating = "5"
            review_submitSendPackage.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_color));
        }

        review_submitSendPackage.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorHint));


        review_submitSendPackage.setOnClickListener {
            if (!driver_rating.equals("")) {
                onReviewDone()
            }
        }
    }

    private fun onReviewDone() {
        try {
            _progressDlg = ProgressDialog(this, R.style.TransparentProgressDialog)
            _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

            _progressDlg!!.setCancelable(false)
            _progressDlg!!.show()
        } catch (ex: Exception) {
            Log.wtf("IOS_error_starting", ex.cause!!)
        }
       // checkStatusOfReview=1
        viewmodel?.reviewSubmitApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            orderId,
            store_rating,
            "",
            driver_rating,
            ""
        )
    }


    override fun onBackPressed() {
        finish()
    }
}