package com.fidoo.user.activity

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.dashboard_fiddopay.*

class TestClass : BaseActivity(){
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.dashboard_fiddopay)
        behavior = BottomSheetBehavior.from(bottomSheetBtn)
    }
}