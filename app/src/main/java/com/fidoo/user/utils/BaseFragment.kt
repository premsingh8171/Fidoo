package com.fidoo.user.utils

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.*
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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

abstract class BaseFragment : Fragment(), Handler.Callback {
    var _context: Context? = null
    var _handler: Handler? = null
    var uiHandlerMethod: UiHandleMethods? = null
    private var _progressDlg: ProgressDialog? = null
    private var _vibrator: Vibrator? = null
    var fidooLoader: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiHandlerMethod = UiHandleMethods(requireActivity())
        _context = requireContext()
        _vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        _handler = Handler(this)
    }

    val isNetworkConnected: Boolean
        get() = CheckConnectivity(requireContext()).isNetworkAvailable
    val restfullInstance: ApiCalls
        get() = ApiCalls(requireActivity())

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
        context_: Context?,
        accessToken: String?,
        id: String?,
        mobile: String?,
        otp: String?
    ) {
        val i = Intent(context_, mClass)
        i.putExtra("accessToken", accessToken)
        i.putExtra("id", id)
        i.putExtra("mobile", mobile)
        i.putExtra("otp", otp)
        startActivity(i)
        requireActivity().finish()
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
        get() = SessionTwiclo(requireContext())
    val sessionInstanceNotNull: SessionNotNull
        get() = SessionNotNull(requireContext())

    fun showIOSProgress() {
        closeProgress()
        _progressDlg = ProgressDialog(requireContext(), R.style.TransparentProgressDialog)
        _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        _progressDlg!!.setCancelable(false)
        _progressDlg!!.show()
    }

    fun getGeoAddressFromLatLong(latitude: Double, longitude: Double): String {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            //   String knownName = addresses.get(0).getFeatureName(); // Only if available else return
            address
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
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
        Toast.makeText(_context, "No Internet Connection!", Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(toast_string: String?) {
        Toast.makeText(_context, toast_string, Toast.LENGTH_LONG).show()
    }

    /*  public void vibrate() {

        if (_vibrator != null)
            _vibrator.vibrate(500);
    }*/
    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            else -> {
            }
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanseState: Bundle?
    ): View? {
        return provideYourFragmentView(inflater, parent, savedInstanseState)
    }

    abstract fun provideYourFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View


    fun fidooLoaderShow() {
        fidooLoader = Dialog(requireActivity())
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