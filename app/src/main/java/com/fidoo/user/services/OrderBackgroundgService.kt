package com.fidoo.user.services

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.orderProcess
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel

class OrderBackgroundgService  : Service() {
    private var timer: CountDownTimer? = null
    var viewmodel: TrackViewModel? = null
    var session: SessionTwiclo? = null
    var hit: Int? = 1

    companion object{
        var  bgServicOrderId:String?=""
        var timer_count: Long?=30000
    }

//    override fun onCreate() {
//        super.onCreate()
//        timer_count=30000
//    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onTaskRemoved(intent)
        session=SessionTwiclo(applicationContext)

        timer = object : CountDownTimer(timer_count!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer_count = millisUntilFinished
                orderProcess = (millisUntilFinished / 1000).toInt()

                if ((millisUntilFinished / 1000) < 2) {
                    MainActivity.orderProcess = 0
                }

                Log.e("Location_Timer", "seconds remaining: " + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                if (hit==1) {
//                    proceedToOrder(
//                        session!!.loggedInUserDetail.accountId,
//                        session!!.loggedInUserDetail.accessToken,
//                        bgServicOrderId!!
//                    )

                   // timer_count=30000
                    hit=0
               }

               // Log.e("proceedToOrder_", "proceedToOrder--$bgServicOrderId--$hit")
                stopService(Intent(applicationContext, OrderBackgroundgService::class.java))

                timer!!.cancel()
            }

        }.start()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        try {
            val restartServiceIntent = Intent(applicationContext, this.javaClass)
            restartServiceIntent.setPackage(packageName)
            startService(restartServiceIntent)
            super.onTaskRemoved(rootIntent)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

//    fun proceedToOrder(accountId: String, accessToken: String, orderId: String){
//        WebServiceClient.client.create(BackEndApi::class.java).proceedToOrder(
//            accountId = accountId,
//            accessToken = accessToken,
//            orderId = orderId
//        ).enqueue(object : Callback<ProceedToOrder> {
//
//            override fun onResponse(call: Call<ProceedToOrder>, response: Response<ProceedToOrder>) {
//                stopService(Intent(applicationContext, OrderBackgroundgService::class.java))
//            }
//
//            override fun onFailure(call: Call<ProceedToOrder>, t: Throwable) {
//                Log.e("Error in making call","--"+t.toString())
//            }
//        })
//    }

    override fun onDestroy() {
        timer!!.cancel()
    }

}