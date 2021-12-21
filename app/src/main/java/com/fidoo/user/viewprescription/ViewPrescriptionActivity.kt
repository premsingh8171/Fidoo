package com.fidoo.user.viewprescription

import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_view_prescription.*


class ViewPrescriptionActivity : BaseActivity() {
    private var mMixpanel: MixpanelAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_prescription)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        Log.e("iii", intent.getStringExtra("image").toString())
        Glide.with(this)
            .load(intent.getStringExtra("image"))
            .fitCenter()
            .into(myZoomageView)
        // myZoomageView?.setImageURI(Uri.parse(intent.getStringExtra("image"))!!)
        backIcon.setOnClickListener {
            finish()
            AppUtils.finishActivityLeftToRight(this)
        }
    }

    override fun onBackPressed() {
        AppUtils.finishActivityLeftToRight(this)
    }

}