package com.fidoo.user.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.libraries.places.api.Places
import com.snap.appadskit.injection.SAAKApplication


class MyApplication : Application(), LifecycleObserver, Application.ActivityLifecycleCallbacks {

    // This init will run before the create
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Log.wtf("run", "onCreate")

        Places.initialize(applicationContext, "AIzaSyBvnYPa4tw9s5TSGwzePeWD4Kk7yulyy9c")
     /*   FirebaseApp.initializeApp(this)

        // initialise mobile ads
       FacebookSdk.fullyInitialize()*/


        // Check whether the app is in fore ground or background
      //  ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        // register the activity life cycle for getting the context
        registerActivityLifecycleCallbacks(this)

       // EmojiManager.install(GoogleEmojiProvider())

        //for snap kit
        SAAKApplication.init(applicationContext, listOf("782e6f17-ba38-4d79-9687-80180b65d28f"))
        SAAKApplication.getSdkVersion()

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        // App in foreground
        Log.wtf("App", "App is in Front")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        //App in background
        Log.wtf("App", "App is in Back")
    }

    init {
        instance = this
        // register the activity life cycle for getting the context
        registerActivityLifecycleCallbacks(this)

    }

    companion object {

        private var instance: MyApplication? = null
        private var mActivityContext: Activity? = null


        fun applicationContext(): MyApplication {
            return instance as MyApplication
        }

        fun activityContext(): Activity {
            return mActivityContext as Activity
        }
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {
        mActivityContext = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityResumed(activity: Activity) {

    }


}