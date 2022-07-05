package com.fidoo.user.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fidoo.user.BuildConfig
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.utils.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.snap.appadskit.external.SAAKConversionEventType
import com.snap.appadskit.external.SAAKEventMetadata
import com.snap.appadskit.external.SAAKRegistrationConfiguration
import com.snap.appadskit.injection.SAAKApplication


@Suppress("DEPRECATION")
class SplashActivity : BaseActivity() {

    //  private  lateinit var splashBinding: ActivitySplashBinding
    lateinit var pref: SessionTwiclo

    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var appversion: String? = "1.0.53"
        var mobile_number: String? = ""
        var splashActivity: SplashActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = 0x00000000  // transparent
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            window.addFlags(flags)
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_splash)
        appversion = "1.0.53"
      //  appversion = BuildConfig.VERSION_NAME

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        splashActivity = this
        pref = SessionTwiclo(this)

        try {
            FirebaseApp.initializeApp(this)
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Token", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                SessionTwiclo(this).deviceToken = token
                Log.d("Token_", token.toString())
            })
        } catch (e: Exception) {e.printStackTrace()}

        onTokenRefresh()

        firebaseGetDynamiclink()

        try {
            val mSnapAppAdsKit = SAAKApplication.getSnapAppAdsKit()
            if (mSnapAppAdsKit != null) {
                mSnapAppAdsKit.trackEvent(
                    // You must select an event type to track
                    registrationConfiguration = SAAKRegistrationConfiguration(
                        SAAKConversionEvent = SAAKConversionEventType.CUSTOM_EVENT_1
                    ),
                    // Optional fields can be tracked in SAAKEventMetadata
                    eventMetadata = SAAKEventMetadata()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun firebaseGetDynamiclink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    try {
                        if (deepLink.toString().isNotEmpty()) {
                            Log.d("deepLink__", deepLink.toString())
                            var referral = deepLink.toString().split("user_id=")
                            var referralId = referral[1]
                            pref.referralId = referralId
                            Log.d("deepLink__", referral[1])
                            //   showToastLong("user_id-$referralId")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            .addOnFailureListener(this) { e ->
                Log.e("TAG__", "getDynamicLink:onFailure", e)
            }
    }

    fun onTokenRefresh() {
        // Get updated InstanceID token.
        var refreshedToken = FirebaseInstanceId.getInstance().token
        SessionTwiclo(this).deviceToken = refreshedToken
        Log.d("Refreshed_token", refreshedToken.toString())

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        SAAKApplication.destroy()
        super.onDestroy()
    }

}