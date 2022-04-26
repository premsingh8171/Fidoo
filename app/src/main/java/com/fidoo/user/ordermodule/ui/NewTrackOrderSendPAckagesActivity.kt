package com.fidoo.user.ordermodule.ui

import android.os.Bundle
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import java.util.ArrayList

class NewTrackOrderSendPAckagesActivity : BaseActivity(){
    var orderstatus: String = "1"
    var textStryArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_track_order_send_package)
    }
}