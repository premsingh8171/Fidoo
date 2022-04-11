package com.fidoo.user.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.aaaaaaaaaaaaaaaaaaaaaaaaaa.*


@Suppress("DEPRECATION")
class TestClass : BaseActivity(){
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.aaaaaaaaaaaaaaaaaaaaaaaaaa)
        behavior = BottomSheetBehavior.from(bottomSheetBtn)
    }
}