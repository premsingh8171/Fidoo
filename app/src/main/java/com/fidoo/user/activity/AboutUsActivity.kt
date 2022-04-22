package com.fidoo.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_about_us.*


class AboutUsActivity : BaseActivity() {

    private var mMixpanel: MixpanelAPI? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_about_us)


        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        mMixpanel?.track("About Us page opened", null)


        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        aboutUsWebView.webViewClient = WebViewClient()

        // this will load the url of the website
        if (intent.getStringExtra("about_us").equals("about_us")) {
            loadUrOnWebView("https://fidoo.in/our-story")
        } else if (intent.getStringExtra("privacy_policy").equals("privacy_policy")) {
            loadUrOnWebView("https://fidoo.in/privacy-policy")
        } else {
            // aboutUsWebView.loadUrl("https://fidoo.in/faq")
            loadUrOnWebView("https://fidoo.in/faq")
        }


        // this will enable the javascript settings
        aboutUsWebView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        aboutUsWebView.settings.setSupportZoom(true)



        aboutUsWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                if (url.startsWith("tel:")) {
//                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
//                    startActivity(intent)
//                    return true
//                }
//                return false
//            }
                if (url.startsWith("tel:")) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    startActivity(intent);
                    return true;
                } else if (url.contains("mailto:")) {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(intent);
                    return true;

                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        }
    }


    fun loadUrOnWebView(coverUrl: String?) {
        aboutUsWebView.getSettings().setJavaScriptEnabled(true)
        aboutUsWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)
        aboutUsWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
        aboutUsWebView.getSettings().setJavaScriptEnabled(true)
        aboutUsWebView.getSettings().setAppCacheEnabled(true)
        aboutUsWebView.getSettings().setDatabaseEnabled(true)
        aboutUsWebView.getSettings().setDomStorageEnabled(true)
        aboutUsWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)
        aboutUsWebView.getSettings().setBuiltInZoomControls(true)
        aboutUsWebView.getSettings().setJavaScriptEnabled(true)
        aboutUsWebView.clearView()
        aboutUsWebView.measure(100, 100)
        aboutUsWebView.getSettings().setUseWideViewPort(true)
        aboutUsWebView.getSettings().setLoadWithOverviewMode(true)
        aboutUsWebView.setWebViewClient(object : WebViewClient() {
            // For api level bellow 24
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d("url___t_u", url)
                if (url.startsWith("http")) {
                    return false
                } else if (url.startsWith("tell:")) {
                    handleTelLink(url)
                    return true
                } else if (url.startsWith("whatsapp:")) {
                    openWhatsApp(url)
                    return true

                } else if (url.startsWith("mailto:")) {
                    Log.d("sddssdsd",url.toString())
                    if (url.startsWith("mailto:")) {
                        val mt: MailTo = MailTo.parse(url)
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
                        intent.putExtra(Intent.EXTRA_TEXT, mt.getBody())
                        intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject())
                        intent.putExtra(Intent.EXTRA_CC, mt.getCc())
                        intent.type = "message/rfc822"
                        startActivity(intent)
                        view.reload()
                        return true
                    } else {
                        view.loadUrl(url)
                    }
                    return true

                }
                return false
            }

            // From api level 24
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                // Get the tel: url
                val url = request.url.toString()
                Log.d("url____u", url)
                if (url.startsWith("http")) {
                    return false
                } else if (url.startsWith("tell:")) {
                    handleTelLink(url)
                    return true
                } else if (url.startsWith("whatsapp:")) {
                    openWhatsApp(url)
                    return true

                } else if (url.startsWith("mailto:")) {
                    if (url.startsWith("mailto:")) {
                        val mt: MailTo = MailTo.parse(url)
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
                        intent.putExtra(Intent.EXTRA_TEXT, mt.getBody())
                        intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject())
                        intent.putExtra(Intent.EXTRA_CC, mt.getCc())
                        intent.type = "message/rfc822"
                        startActivity(intent)
                        view.reload()
                        return true
                    } else {
                        view.loadUrl(url)
                    }
                    return true

                }
                return false
            }
        })
        aboutUsWebView.loadUrl(coverUrl!!)
    }

    private fun openWhatsApp(url: String) {
        // val contact = "+91 8273836749" // use country code with your phone number
        // val url = "https://api.whatsapp.com/send?phone=$contact"
        // https://api.whatsapp.com/send/?phone=919871322057&text&app_absent=0
        var url1 = url.split("phone=")
        var url2 = url1[1].split("&")
        var phone = url2[0]

        try {
            val i = Intent(Intent.ACTION_SENDTO)
            i.data = Uri.parse("smsto:" + phone)
            i.setPackage("com.whatsapp")
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(
                this,
                "Whatsapp app not installed in your phone",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }


    // Custom method to handle html tel: link
    protected fun handleTelLink(url: String?) {
        // Initialize an intent to open dialer app with specified phone number
        // It open the dialer app and allow user to call the number manually
        try {
            Log.d("handleTelLink",url.toString())

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }catch (e:Exception){e.printStackTrace()}

    }

    override fun onBackPressed() {
        AppUtils.finishActivityLeftToRight(this)
    }
}