package com.fidoo.user.FidoPay

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import kotlinx.android.synthetic.main.fidoopay_enter_detailsfor_payment.*

class FidoPayEnterDetailsForPayment : BaseActivity() {
    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.fidoopay_enter_detailsfor_payment)
        btnSelectModeOfPayment.setOnClickListener {
            showDialogueOfPaymentMode()
        }
    }
    private fun showDialogueOfPaymentMode() {
        dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.fidoopay_select_modeofpayment_dialogue)

        dialog?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window!!.setGravity(Gravity.BOTTOM)
        dialog?.show()
    }
}