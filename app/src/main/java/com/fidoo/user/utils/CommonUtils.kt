package com.fidoo.user.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

class CommonUtils {


    companion object {

        private var _progressDlg: ProgressDialog? = null

        fun Context.showToast(
            context: Context = applicationContext,
            message: String,
            duration: Int = Toast.LENGTH_SHORT
        ) {
            Toast.makeText(context, message, duration).show()
        }

        fun dismissIOSProgress() {
            /*    try {
                if (mKProgressHUD != null) {
                    if (mKProgressHUD.isShowing()) {
                        mKProgressHUD.dismiss();
                    }
                }
            } catch (Exception ex) {
                Log.wtf("IOS_error_dismiss", ex.getCause());
            }
    */
            if (_progressDlg == null) {
                return
            }
            _progressDlg?.dismiss()
            _progressDlg = null
        }

        fun Activity.hideKeyboard() {
            hideKeyboard(currentFocus ?: View(this))
        }


        fun Context.hideKeyboard(view: View) {
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }


        fun Fragment.hideKeyboard() {
            view?.let { activity?.hideKeyboard(it) }
        }




        fun closeProgress() {
            if (_progressDlg == null) {
                return
            }
            _progressDlg!!.dismiss()
            _progressDlg = null
        }



    }

}