package com.fidoo.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.fidoo.user.adapter.ServiceAdapter
import com.fidoo.user.viewmodels.HomeFragmentViewModel
import kotlinx.android.synthetic.main.activity_services.*

class ServicesActivity : AppCompatActivity() {

    var serviceViewModel: HomeFragmentViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setContentView(R.layout.activity_services)

        serviceViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel::class.java)


        serviceViewModel?.getHomeServices(
            com.fidoo.user.data.session.SessionTwiclo(
                this
            ).loggedInUserDetail.accountId, com.fidoo.user.data.session.SessionTwiclo(
                this
            ).loggedInUserDetail.accessToken)



        serviceViewModel?.homeServicesResponse?.observe(this, Observer {
            val serviceAdapter = ServiceAdapter(this, it.serviceList)
            service_recyclerView.layoutManager = GridLayoutManager(this,3, GridLayoutManager.VERTICAL,false)
            service_recyclerView.setHasFixedSize(true)
            service_recyclerView.adapter = serviceAdapter
        })
    }


}