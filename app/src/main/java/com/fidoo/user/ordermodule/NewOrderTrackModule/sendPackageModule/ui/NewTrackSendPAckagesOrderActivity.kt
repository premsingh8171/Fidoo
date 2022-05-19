package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.Adapter.sendpackageOrderAdapter
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.Message
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.viewModel.ViewModelSendPackage
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.NewTrackViewModel.NewOrderViewModel
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.adapter.NewOrderTrackAdapter
import kotlinx.android.synthetic.main.activity_new_track_send_packages_order.*
import kotlinx.android.synthetic.main.activity_track_order_new.*
import java.util.ArrayList

class NewTrackSendPAckagesOrderActivity : AppCompatActivity() {

    //for new tracking Screen//
    var newOrderViewModel: ViewModelSendPackage? = null
    lateinit var mainAdapter: sendpackageOrderAdapter
    var firstMsgListData = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_track_send_packages_order)

        newOrderViewModel = ViewModelProvider(this).get(ViewModelSendPackage::class.java)
        newOrderViewModel?.sendpackageNewTrackScreenApiCall(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!
        )
        newOrderViewModel?.newTrackViewModel?.observe(this) {
            // Log.d("sddffsddsds", Gson().toJson(it)
            firstMsgListData = it.messages as ArrayList<Message>
            SetRecyclerview()
        }
    }

    private fun SetRecyclerview() {
//        ordrePlaceGif.playAnimation()
//        ordrePlaceGif.loop(true)
//        ordrePlaceGif.repeatCount = LottieDrawable.INFINITE;
       mainAdapter = sendpackageOrderAdapter(this, firstMsgListData)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerViewSendPackage.layoutManager = linearLayoutManager
        recyclerViewSendPackage.adapter = mainAdapter


    }
}