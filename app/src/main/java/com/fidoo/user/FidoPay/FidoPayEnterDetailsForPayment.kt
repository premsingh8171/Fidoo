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
    private var paymentModeDialogue: Dialog? = null
    private var paymentConfirmationDialogue: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.fidoopay_enter_detailsfor_payment)
        btnSelectModeOfPayment.setOnClickListener {
            showDialogueOfPaymentConfirmation()
        }
    }
    private fun showDialogueOfPaymentMode() {
        paymentModeDialogue = Dialog(this)
        paymentModeDialogue?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        paymentModeDialogue?.setContentView(R.layout.fidoopay_payment_confirmation_dialogue)

        paymentModeDialogue?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        paymentModeDialogue?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        paymentModeDialogue?.window!!.setGravity(Gravity.BOTTOM)
        paymentModeDialogue?.show()
    }
    private fun showDialogueOfPaymentConfirmation() {
        paymentConfirmationDialogue = Dialog(this)
        paymentConfirmationDialogue?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        paymentConfirmationDialogue?.setContentView(R.layout.fidoopay_payment_confirmation_dialogue)

        paymentConfirmationDialogue?.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        paymentConfirmationDialogue?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        paymentConfirmationDialogue?.window!!.setGravity(Gravity.CENTER)
        paymentConfirmationDialogue?.show()
    }
}