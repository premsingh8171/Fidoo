package com.fidoo.user.ordermodule.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.utils.BaseActivity
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_order_rejected.*

class OrderRejectedActivity : BaseActivity() {
    var sessionTwiclo: SessionTwiclo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_order_rejected)
        sessionTwiclo = SessionTwiclo(this)

        cancelBtnL.setOnClickListener {
            if (MainActivity.onBackpressHandle.equals("1")){
                startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            }else{
                finish()
            }
        }
    }


    override fun onBackPressed() {
		AppUtils.finishActivityLeftToRight(this)
    }
}