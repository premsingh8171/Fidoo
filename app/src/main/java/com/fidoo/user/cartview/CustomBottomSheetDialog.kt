package com.fidoo.user.cartview

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import com.fidoo.user.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomBottomSheetDialog(private val context: Activity, private val theme: Int) : Dialog(context, theme) {
    override fun onBackPressed() {
        super.onBackPressed()
        context.finish()
    }
}