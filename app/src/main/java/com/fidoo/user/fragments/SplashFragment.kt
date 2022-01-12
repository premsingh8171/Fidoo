package com.fidoo.user.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.MediaController
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SliderScreenActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.CallBackVersionListener
import com.fidoo.user.utils.BaseFragment
import com.fidoo.user.viewmodels.UpdateAppModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.android.synthetic.main.fragment_splash.view.*
import kotlinx.android.synthetic.main.item_cat_new_ui.view.*
import kotlinx.android.synthetic.main.select_cat_popup.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class SplashFragment : BaseFragment() {

    lateinit var mView: View
    lateinit var mSessionTwiclo: SessionTwiclo
    var updateApp_dialog: Dialog? = null
    var mmContext: Context? = null
    private var mediaController: MediaController? = null
    var account_id: String? = ""


    override fun provideYourFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window = requireActivity().getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)

        mView = inflater!!.inflate(R.layout.fragment_splash, parent, false)
        mSessionTwiclo = SessionTwiclo(requireContext())

        if (isNetworkConnected) {
            // custAppVerCheck(BuildConfig.VERSION_NAME)
            SplashActivity.appversion?.let { custAppVerCheck(it) }
        } else {
            showInternetToast()
        }

        try {
            if (SessionTwiclo(mmContext).profileDetail != null) {
                account_id = SessionTwiclo(mmContext).profileDetail.accountId.toString()
            } else {
                account_id = SessionTwiclo(mmContext).loginDetail.accountId.toString()
            }
        } catch (e: java.lang.Exception) {
            account_id = ""
        }

        return mView
    }


    private fun updateAppDialog(newVersion_: String) {
        updateApp_dialog = Dialog(mmContext!!)
        updateApp_dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        updateApp_dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        updateApp_dialog?.setContentView(R.layout.update_app_dialog)
        updateApp_dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        updateApp_dialog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        updateApp_dialog?.show()
        val latest_version_txt = updateApp_dialog?.findViewById<TextView>(R.id.latest_version_txt)
        val updateversion_txt = updateApp_dialog?.findViewById<TextView>(R.id.updateversion_txt)
        latest_version_txt!!.text = "What's New in Version " + newVersion_

        updateversion_txt?.setOnClickListener{

            if (Build.VERSION_CODES.M >= 22) {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + mmContext?.getPackageName())
                        )
                    )
                } catch (e: java.lang.Exception) { e.printStackTrace()}
            } else {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id" + mmContext?.getPackageName())
                        )
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

        }

    }

    class AsyncTaskPlayStore(
        private var context: Context?,
        var callBackVersionListener: CallBackVersionListener
    ) : AsyncTask<String, String, String>() {
        var isAvailableInPlayStore = false
        var newVersion = ""
        var currentVersion = ""
        var isVersionAvailabel = false
        var mStringCheckUpdate = ""

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                isAvailableInPlayStore = true
                if (isNetworkAvailable(context!!)) {

                    mStringCheckUpdate =
                        Jsoup.connect("https://play.google.com/store/apps/details?id=" + context!!.getPackageName() + "&hl=en")
                            .timeout(30000)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("http://www.google.com")
                            .get()
                            .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                            .first()
                            .ownText()
                    return mStringCheckUpdate
                }
            } catch (e: java.lang.Exception) {
                isAvailableInPlayStore = false
                return mStringCheckUpdate
            } catch (e: Throwable) {
                isAvailableInPlayStore = false
                return mStringCheckUpdate
            }
            return mStringCheckUpdate
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (isAvailableInPlayStore == true) {
                newVersion = result!!
                // Log.d("new_Version", newVersion)
                currentVersion = SplashActivity.appversion.toString()
                isVersionAvailabel = !currentVersion.equals(newVersion, ignoreCase = true)
                callBackVersionListener.onGetResponse(
                    isVersionAvailabel,
                    newVersion,
                    currentVersion
                )
            }
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
                .isConnected
        }

    }

    fun custAppVerCheck(app_version: String) {
        Log.d("app_version", "$app_version")

        WebServiceClient.client.create(BackEndApi::class.java).updateApp(
            app_version = app_version,
            account_id,
            SessionTwiclo(mmContext).deviceToken
        ).enqueue(object : Callback<UpdateAppModel> {

            override fun onResponse(
                call: Call<UpdateAppModel>,
                response: Response<UpdateAppModel>
            ) {

                Log.d("splash_screen", Gson().toJson(response.body()))

                if (response.body()!!.error_code == 300) {
                    updateAppDialog(app_version)
                } else {

                    Glide.with(mmContext!!).load(R.drawable.splash_screen)
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
                        .placeholder(R.drawable.splash_screen)
                        .error(R.drawable.splash_screen).into(mView.fidooSplashLogo)

                    Handler().postDelayed({
                        if (mSessionTwiclo.isLoggedIn) {
                            goForVerificationScreen(
                                MainActivity::class.java,
                                mmContext!!,
                                "",
                                "",
                                "",
                                ""
                            )
                            return@postDelayed
                        } else {
                            try {
                                goForVerificationScreen(
                                    SliderScreenActivity::class.java,
                                    mmContext!!,
                                    "",
                                    "",
                                    "",
                                    ""
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }, 3000)

                }
            }

            override fun onFailure(call: Call<UpdateAppModel>, t: Throwable) {}

        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mmContext = context
    }

}