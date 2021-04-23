package com.fidoo.user

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.ActivitySplashBinding
import com.fidoo.user.utils.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : BaseActivity() {


    private  lateinit var splashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        /*FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            SessionTwiclo(this).deviceToken=token
            // Log and toast
            Log.d("Token", token.toString())
        })*/
    }

}