package com.fidoo.user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.android.synthetic.main.activity_auth.*
import kotlin.math.roundToInt
import com.google.android.gms.tasks.OnCompleteListener


class AuthActivity : AppCompatActivity() {
    var height = 0
    var width = 0

    private var mMixpanel: MixpanelAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        setContentView(R.layout.activity_auth)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        val metrics: DisplayMetrics = this.resources.displayMetrics
        width = metrics.widthPixels
        height = (width * 0.5).roundToInt()

        height= ((metrics.heightPixels * 40) / 100).toDouble().roundToInt()
        Log.d("height___", height.toString())

        fidooTxtLayout_auth.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )

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
    }

    fun onTokenRefresh() {
        // Get updated InstanceID token.
        var refreshedToken = FirebaseInstanceId.getInstance().token
        SessionTwiclo(this).deviceToken = refreshedToken
        Log.d("Refreshed_token", refreshedToken.toString())

    }


}