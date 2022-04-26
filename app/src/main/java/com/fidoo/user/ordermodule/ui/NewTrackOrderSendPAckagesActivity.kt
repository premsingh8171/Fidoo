package com.fidoo.user.ordermodule.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import java.util.ArrayList

class NewTrackOrderSendPAckagesActivity : BaseActivity(){
    var orderstatus: String = "1"
    var textStryArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_track_order_send_package)
        var orderplaceNextline_tv = findViewById<TextView>(R.id.orderplaceNextline_tv)
        var orderplace_tv = findViewById<TextView>(R.id.orderplace_tv)
        var bottom_LL = findViewById<LinearLayout>(R.id.bottom_LL)
        var status_riderTV = findViewById<TextView>(R.id.status_riderTV)

        textStryArray = ArrayList()

        textStryArray.add("finding nearest rider for your")
        textStryArray.add("waiting for rider assigning")
        textStryArray.add("hold on tight will assign rider soon")
        textStryArray.add("we will provide best service")
        textStryArray.add("wait a minute fiddo is working for you")


        if (orderstatus.equals("1")) {
            orderplaceNextline_tv.visibility = View.GONE
        }

        bottom_LL.setOnClickListener(View.OnClickListener {
            orderstatus = "2"
            orderplaceNextline_tv.visibility = View.VISIBLE
        })

        status_riderTV.post(object : Runnable {
            var i = 0
            override fun run() {
                status_riderTV.setText(textStryArray[i])
                i++
                if (i == 5) i = 0
                status_riderTV.postDelayed(this, 5000)
            }
        })
    }
}