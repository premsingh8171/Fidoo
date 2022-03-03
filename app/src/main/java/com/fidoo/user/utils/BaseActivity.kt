package com.fidoo.user.utils

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.*
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fidoo.user.R
import com.fidoo.user.api_request_retrofit.ApiCalls
import com.fidoo.user.data.CheckConnectivity
import com.fidoo.user.data.session.SessionNotNull
import com.fidoo.user.data.session.SessionTwiclo
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

abstract class BaseActivity : AppCompatActivity(), Handler.Callback {
    var _context: Activity? = null

    //  public Handler _handler = null;
    var uiHandlerMethod: UiHandleMethods? = null
        private set
    private var _progressDlg: ProgressDialog? = null
    private var _vibrator: Vibrator? = null
    var fidooLoader: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiHandlerMethod = UiHandleMethods(this)
        _context = this
        _vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        //  _handler = new Handler(this);
    }

    val isNetworkConnected: Boolean
        get() = CheckConnectivity(this).isNetworkAvailable
    val restfullInstance: ApiCalls
        get() = ApiCalls(this)

    override fun onDestroy() {
        closeProgress()
        /*  try {
            if (_vibrator != null)
                _vibrator.cancel();
        } catch (Exception e) {
        }*/_vibrator = null
        super.onDestroy()
    }

    fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    fun goForVerificationScreen(
        mClass: Class<*>?,
        accessToken: String?,
        id: String?,
        mobile: String?,
        otp: String?
    ) {
        val i = Intent(this, mClass)
        i.putExtra("accessToken", accessToken)
        i.putExtra("id", id)
        i.putExtra("mobile", mobile)
        i.putExtra("otp", otp)
        startActivity(i)
        finish()
        //    overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    fun dismissIOSProgress() {
        if (_progressDlg == null) {
            return
        }
        _progressDlg!!.dismiss()
        _progressDlg = null
    }

    val sessionInstance: SessionTwiclo
        get() = SessionTwiclo(this)
    val sessionInstanceNotNull: SessionNotNull
        get() = SessionNotNull(this)

    fun showIOSProgress() {
        closeProgress()
        _progressDlg = ProgressDialog(this, R.style.TransparentProgressDialog)
        _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        _progressDlg!!.setCancelable(false)
        _progressDlg!!.show()
    }

    fun getGeoAddressFromLatLong(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (addressList != null && addressList.size > 0) {
                val address = addressList[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    Companion.address += address.getAddressLine(i) + ","
                }
                try {
                    if (Companion.address != null) {
                        Companion.address =
                            Companion.address!!.substring(0, Companion.address!!.length - 1)
                    } else {
                        Companion.address = ""
                    }
                    if (address.locality != null) {
                        city = address.locality
                    } else {
                        city = ""
                    }
                    if (address.adminArea != null) {
                        state = address.adminArea
                    } else {
                        state = ""
                    }
                    if (address.countryName != null) {
                        country = address.countryName
                    } else {
                        country = ""
                    }
                    if (address.locality != null) {
                        locality = address.locality
                    } else {
                        locality = ""
                    }
                    if (address.postalCode != null) {
                        pincode = address.postalCode
                    } else {
                        pincode = ""
                    }
                    if (address.adminArea != null) {
                        mAdminArea = address.adminArea
                    } else {
                        mAdminArea = ""
                    }
                    if (address.subAdminArea != null) {
                        mSubAdminArea = address.subAdminArea
                    } else {
                        mSubAdminArea = ""
                    }
                    if (address.subLocality != null) {
                        mSubLocality = address.subLocality
                    } else {
                        mSubLocality = ""
                    }
                    if (address.thoroughfare != null) {
                        mThoroughfare = address.postalCode
                    } else {
                        mThoroughfare = ""
                    }
                    if (address.subThoroughfare != null) {
                        mSubThoroughfare = address.subThoroughfare
                    } else {
                        mSubThoroughfare = ""
                    }
                    if (address.phone != null) {
                        mPhone = address.phone
                    } else {
                        mPhone = ""
                    }
                    if (address.url != null) {
                        mUrl = address.url
                    } else {
                        mUrl = ""
                    }
                    if (address.featureName != null) {
                        mFeatureName = address.featureName
                    } else {
                        mFeatureName = ""
                    }
                } catch (ex: Exception) {
                    if (addressList[0].getAddressLine(0) != null) {
                        Companion.address =
                            addressList[0].getAddressLine(0) //// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    } else {
                        Companion.address = ""
                    }
                    if (Companion.address != null) {
                        Companion.address =
                            Companion.address!!.substring(0, Companion.address!!.length - 1)
                    } else {
                        Companion.address = ""
                    }
                    if (address.locality != null) {
                        city = address.locality
                    } else {
                        city = ""
                    }
                    if (address.adminArea != null) {
                        state = address.adminArea
                    } else {
                        state = ""
                    }
                    if (address.countryName != null) {
                        country = address.countryName
                    } else {
                        country = ""
                    }
                    if (address.locality != null) {
                        locality = address.locality
                    } else {
                        locality = ""
                    }
                    if (address.postalCode != null) {
                        pincode = address.postalCode
                    } else {
                        pincode = ""
                    }
                    if (address.adminArea != null) {
                        mAdminArea = address.adminArea
                    } else {
                        mAdminArea = ""
                    }
                    if (address.subAdminArea != null) {
                        mSubAdminArea = address.subAdminArea
                    } else {
                        mSubAdminArea = ""
                    }
                    if (address.subLocality != null) {
                        mSubLocality = address.subLocality
                    } else {
                        mSubLocality = ""
                    }
                    if (address.thoroughfare != null) {
                        mThoroughfare = address.postalCode
                    } else {
                        mThoroughfare = ""
                    }
                    if (address.subThoroughfare != null) {
                        mSubThoroughfare = address.subThoroughfare
                    } else {
                        mSubThoroughfare = ""
                    }
                    if (address.phone != null) {
                        mPhone = address.phone
                    } else {
                        mPhone = ""
                    }
                    if (address.url != null) {
                        mUrl = address.url
                    } else {
                        mUrl = ""
                    }
                    if (address.featureName != null) {
                        mFeatureName = address.featureName
                    } else {
                        mFeatureName = ""
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (ex: Exception) {
        }
        return address
    }

    fun getGeoAddressFromLatLong2(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (addressList != null && addressList.size > 0) {
                val address = addressList[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    Companion.address += address.getAddressLine(i) + ","
                }
                try {
                    if (Companion.address != null) {
                        Companion.address =
                            Companion.address!!.substring(0, Companion.address!!.length - 1)
                    } else {
                        Companion.address = ""
                    }
                    if (address.locality != null) {
                        city = address.locality
                    } else {
                        city = ""
                    }
                    if (address.adminArea != null) {
                        state = address.adminArea
                    } else {
                        state = ""
                    }
                    if (address.countryName != null) {
                        country = address.countryName
                    } else {
                        country = ""
                    }
                    if (address.locality != null) {
                        locality = address.locality
                    } else {
                        locality = ""
                    }
                    if (address.postalCode != null) {
                        pincode = address.postalCode
                    } else {
                        pincode = ""
                    }
                    if (address.adminArea != null) {
                        mAdminArea = address.adminArea
                    } else {
                        mAdminArea = ""
                    }
                    if (address.subAdminArea != null) {
                        mSubAdminArea = address.subAdminArea
                    } else {
                        mSubAdminArea = ""
                    }
                    if (address.subLocality != null) {
                        mSubLocality = address.subLocality
                    } else {
                        mSubLocality = ""
                    }
                    if (address.thoroughfare != null) {
                        mThoroughfare = address.postalCode
                    } else {
                        mThoroughfare = ""
                    }
                    if (address.subThoroughfare != null) {
                        mSubThoroughfare = address.subThoroughfare
                    } else {
                        mSubThoroughfare = ""
                    }
                    if (address.phone != null) {
                        mPhone = address.phone
                    } else {
                        mPhone = ""
                    }
                    if (address.url != null) {
                        mUrl = address.url
                    } else {
                        mUrl = ""
                    }
                    if (address.featureName != null) {
                        mFeatureName = address.featureName
                    } else {
                        mFeatureName = ""
                    }
                } catch (ex: Exception) {
                    if (addressList[0].getAddressLine(0) != null) {
                        Companion.address =
                            addressList[0].getAddressLine(0) //// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    } else {
                        Companion.address = ""
                    }
                    if (Companion.address != null) {
                        Companion.address =
                            Companion.address!!.substring(0, Companion.address!!.length - 1)
                    } else {
                        Companion.address = ""
                    }
                    if (address.locality != null) {
                        city = address.locality
                    } else {
                        city = ""
                    }
                    if (address.adminArea != null) {
                        state = address.adminArea
                    } else {
                        state = ""
                    }
                    if (address.countryName != null) {
                        country = address.countryName
                    } else {
                        country = ""
                    }
                    if (address.locality != null) {
                        locality = address.locality
                    } else {
                        locality = ""
                    }
                    if (address.postalCode != null) {
                        pincode = address.postalCode
                    } else {
                        pincode = ""
                    }
                    if (address.adminArea != null) {
                        mAdminArea = address.adminArea
                    } else {
                        mAdminArea = ""
                    }
                    if (address.subAdminArea != null) {
                        mSubAdminArea = address.subAdminArea
                    } else {
                        mSubAdminArea = ""
                    }
                    if (address.subLocality != null) {
                        mSubLocality = address.subLocality
                    } else {
                        mSubLocality = ""
                    }
                    if (address.thoroughfare != null) {
                        mThoroughfare = address.postalCode
                    } else {
                        mThoroughfare = ""
                    }
                    if (address.subThoroughfare != null) {
                        mSubThoroughfare = address.subThoroughfare
                    } else {
                        mSubThoroughfare = ""
                    }
                    if (address.phone != null) {
                        mPhone = address.phone
                    } else {
                        mPhone = ""
                    }
                    if (address.url != null) {
                        mUrl = address.url
                    } else {
                        mUrl = ""
                    }
                    if (address.featureName != null) {
                        mFeatureName = address.featureName
                    } else {
                        mFeatureName = ""
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (ex: Exception) {
        }
        return null
    }

    fun closeProgress() {
        if (_progressDlg == null) {
            return
        }
        _progressDlg!!.dismiss()
        _progressDlg = null
    }

    fun showAlertDialog(msg: String?) {
        val customBuilder = AlertDialog.Builder(
            _context!!
        )
        customBuilder.setTitle(getString(R.string.app_name))
        customBuilder.setMessage(msg)
        customBuilder.setNegativeButton(resources.getString(R.string.ok)) { dialog, which ->
            // MyActivity.this.finish();
        }
        customBuilder.setIcon(R.drawable.fidoo_green)
        customBuilder.setCancelable(false)
        val dialog = customBuilder.create()
        dialog.show()
        val b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        b?.setBackgroundColor(resources.getColor(R.color.white))
        dialog.show()
    }

    /**
     * show toast
     *
     * @param toast_string
     */
    fun showToast(toast_string: String?) {
        Toast.makeText(_context, toast_string, Toast.LENGTH_SHORT).show()
    }

    fun showInternetToast() {
        Toast.makeText(_context, "No Internet Connection!", Toast.LENGTH_LONG).show()
    }

    fun showToastLong(toast_string: String?) {
        Toast.makeText(_context, toast_string, Toast.LENGTH_LONG).show()
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            else -> {
            }
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun hideKeyboard(view: View?) {
        try {
            if (view != null) {
                val imm = _context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (Ex: Exception) {
        }
    }

    fun showKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun dynamicText(view: TextView, value: String, key: String) {
        view.text = ""
        val str = SpannableString(key)
        str.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            str.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(StyleSpan(Typeface.BOLD), 0, str.length, 0)
        view.append(value + "")
        view.append(str)
    }

    fun dynamicTextForCenter(
        view: TextView,
        start_str: String?,
        change_color_str: String,
        end_str: String?
    ) {
        view.text = ""
        val str = SpannableString(change_color_str)
       str.setSpan(
            ForegroundColorSpan(Color.GREEN),
            0,
            str.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(StyleSpan(Typeface.NORMAL), 0, str.length, 0)
        view.append(start_str)
        view.append(str)
        view.append(end_str)
    }

    companion object {
        var address: String? = ""
        var city = ""
        var state = ""
        var country = ""
        var locality = ""
        var pincode = ""
        private var mAdminArea = ""
        private var mSubAdminArea = ""
        private var mSubLocality = ""
        private var mThoroughfare = ""
        private var mSubThoroughfare = ""
        private const val mCountryCode = ""
        private var mPhone = ""
        private var mUrl = ""
        private var mFeatureName = ""
    }


    fun fidooLoaderShow() {
        fidooLoader = _context?.let { Dialog(it) }
        fidooLoader?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fidooLoader?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        fidooLoader?.setContentView(R.layout.fidoo_loader)
        fidooLoader?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        //   fidooLoader?.window?.attributes?.windowAnimations = R.style.cusProgressDialog
        fidooLoader?.setCanceledOnTouchOutside(false)
        fidooLoader?.show()

        val loader = fidooLoader?.findViewById<ImageView>(R.id.loader)

        Glide.with(this).load(R.drawable.fidooloader)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .placeholder(R.drawable.fidooloader)
            .error(R.drawable.fidooloader)
            .into(loader!!)
    }

    fun fidooLoaderCancel() {
        Handler(Looper.getMainLooper()).postDelayed({
            fidooLoader!!.dismiss()
        }, 4000)
    }

}