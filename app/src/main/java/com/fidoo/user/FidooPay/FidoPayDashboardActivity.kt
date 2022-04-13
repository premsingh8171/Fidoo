package com.fidoo.user.FidooPay

import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior


class FidoPayDashboardActivity : BaseActivity(){
    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.fidoopay_dashboard_activity)
//        val fidoPayDashboardFragment = FidoPayDashboardFragment()
//        val fragmentManager: FragmentManager = supportFragmentManager
//        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout, fidoPayDashboardFragment).commit()
    }
}