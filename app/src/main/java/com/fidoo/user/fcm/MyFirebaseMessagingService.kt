/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fidoo.user.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.cartview.activity.CartActivity
import com.fidoo.user.data.model.PushNotificationModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.ui.TrackOrderActivity
import com.fidoo.user.store.activity.StoreListActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    // [START receive_message]
    var screen:String?=""
    var service_id:String?=""

    override fun onMessageReceived(p0: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${p0.from}")

        // Check if message contains a data payload.
        p0.data.let {
            Log.d(TAG, "Message data payload: " + p0.data)
        }

        val dataPush: PushNotificationModel?=
            Gson().fromJson(p0.data.get("data"),PushNotificationModel::class.java)
        Log.e("p_dataPushdata", Gson().toJson(dataPush))
        screen=dataPush?.screen!!
        service_id=dataPush?.service_id!!
        Log.e("screenscreen_", screen.toString())

        sendNotificationTemp(dataPush?.title!!, dataPush.message!!, dataPush.orderid!!)

        if(MainActivity.check == "yes") {
            TrackOrderActivity.notiInterface.notiStatus(dataPush.orderacceptreject)
        }

        // sendNotificationTemp(p0.data.get("title")!!, p0.data.get("message")!!, p0.data.get("orderid")!!)
         /*
        // Check if message contains a notification payload.
          p0?.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body!!)
        }*/
    }

    // [END receive_message]


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

    override fun onNewToken(p0: String) {
        Log.d(TAG, "Refreshed token: $p0")
        SessionTwiclo(this).deviceToken=p0
        Log.d(TAG, "Refreshed token:  "+ SessionTwiclo(this).deviceToken)
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(p0)
    }

    // [END on_new_token]
    /**
     * Persist token to third-party servers.
     *
     * @param token The new token.
     */

    private fun sendRegistrationToServer(token: String?) {}

    private   fun sendNotificationTemp(title: String,message: String,orderid: String): Unit {
        var intent =Intent()
        if (screen.equals("StoreListing Screen")){
          //  intent= Intent(this, OrderDetailsActivity::class.java).putExtra("orderId",orderid)
            intent= Intent(this, StoreListActivity::class.java)
                .putExtra("orderId",orderid)
                .putExtra("serviceId",service_id)

        }else if (screen.equals("CartView Screen")){
            intent= Intent(this, CartActivity::class.java).putExtra("orderId",orderid)

        }else{
            intent= Intent(this, MainActivity::class.java).putExtra("orderId",orderid)

        }

        val num = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(this, num    , intent, PendingIntent.FLAG_ONE_SHOT)
        val NOTIFICATION_ID = 1
        val NOTIFICATION_CHANNEL_ID = "my_notification_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val icon = BitmapFactory.decodeResource(
            getResources(),
            R.drawable.fidoo_green
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            // Configure the notification channel.
            notificationChannel.description = message
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setVibrate(longArrayOf(0, 100, 100, 100, 100, 100))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.fidoo_small_icon)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText(message)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private fun sendNotification(messageBody: String) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(messageBody, applicationContext)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

}