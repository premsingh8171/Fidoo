package com.fidoo.user

import android.os.Bundle
import android.view.WindowManager


class SplashActivity : com.fidoo.user.utils.BaseActivity() {


    //  private  lateinit var splashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


//        FirebaseApp.initializeApp();
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("Token", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            SessionTwiclo(this).deviceToken=token
//            // Log and toast
//            Log.d("Token", token.toString())
//        })
    }

}