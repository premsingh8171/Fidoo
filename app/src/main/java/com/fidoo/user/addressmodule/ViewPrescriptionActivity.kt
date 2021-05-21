package com.fidoo.user.addressmodule

import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_view_prescription.*


class ViewPrescriptionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_prescription)
        Log.e("iii", intent.getStringExtra("image").toString())
        Glide.with(this)
            .load(intent.getStringExtra("image"))
            .fitCenter()
            .into(myZoomageView)
        // myZoomageView?.setImageURI(Uri.parse(intent.getStringExtra("image"))!!)
        backIcon.setOnClickListener {
            finish()
        }
    }

}