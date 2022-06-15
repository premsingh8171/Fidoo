package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.constants.useconstants
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.Adapter.sendpackageOrderAdapter
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.Message
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.sendPackageModel
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.viewModel.ViewModelSendPackage
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.NewTrackViewModel.NewOrderViewModel
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.adapter.NewOrderTrackAdapter
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.utils.BaseActivity
import com.fidoo.user.utils.isNetworkConnected
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_new_track_send_packages_order.*
import kotlinx.android.synthetic.main.activity_track_order.*
import kotlinx.android.synthetic.main.activity_track_order_new.*
import java.util.ArrayList

class NewTrackSendPAckagesOrderActivity : BaseActivity() {

    //for new tracking Screen//
    var newOrderViewModel: ViewModelSendPackage? = null
    var newOrdertrackViewModel: TrackViewModel? = null
    lateinit var mainAdapter: sendpackageOrderAdapter
    var firstMsgListData = ArrayList<Message>()
    var rider_infoText= ArrayList<String>()
    var rider_infoText2= ArrayList<String>()
    private var rider_infoText2count=0
    private var rider_count = 0
    var viewmodel: TrackViewModel? = null

    lateinit var trackModel:sendPackageModel
    private var rider_mobNo: String= ""
    private var user_mobNo: String= ""
    var call_Diolog: Dialog? = null
    var timer: CountDownTimer?= null
    var timer2: CountDownTimer?= null

    private var main_status: String= ""
    private var selection_msg = ""
    private var totalarray =0
    private var total_rate: Int = 0
    var feedbacl_dialog: Dialog? = null
    var rider_name: String= ""
    var order_id=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_track_send_packages_order)

        newOrderViewModel = ViewModelProvider(this).get(ViewModelSendPackage::class.java)
        viewmodel = ViewModelProvider(this).get(TrackViewModel::class.java)



        newOrderViewModel?.sendpackageNewTrackScreenApiCall(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken,
            intent.getStringExtra("orderId")!!
        )

        timer = object : CountDownTimer(8000, 8000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("_Timer", "seconds remaining: " + millisUntilFinished / 1000)
                if (!main_status.equals("3")) {

                    if (rider_infoText2.isEmpty()) {
                        tv_rider_language.text = rider_infoText[rider_count]
                        rider_count++
                        if (rider_count >= rider_infoText.size) {
                            rider_count = 0
                        }
                    }else{

                        tv_rider_language.text = rider_infoText2[rider_count]
                        rider_count++
                        if (rider_count >= rider_infoText2.size) {
                            rider_count = 0
                        }

                    }

                }
            }

            override fun onFinish() {


                if (!main_status.equals("3")) {
                    timer!!.start()
                }
            }
        }

        timer2 = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {


            }

            override fun onFinish() {
                Log.e("_Timersfadfdfdsf", timer.toString())

                newOrderViewModel?.sendpackageNewTrackScreenApiCall(
                    SessionTwiclo(this@NewTrackSendPAckagesOrderActivity).loggedInUserDetail.accountId,
                    SessionTwiclo(this@NewTrackSendPAckagesOrderActivity).loggedInUserDetail.accessToken,
                    intent.getStringExtra("orderId")!!
                )

                timer2!!.start()
            }


        }.start()

        imageView6.setOnClickListener {
            useconstants.track_sendpackage= true
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        imageView5.setOnClickListener {

            if (rider_mobNo.equals("")){

            }else {

                if (user_mobNo!!.isNotEmpty()) {
                    CallPopUp(1)

                    if (sessionInstance.profileDetail != null) {
                        viewmodel?.callCustomerApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            user_mobNo!!,
                            rider_mobNo!!
                        )
                    } else {
                        viewmodel?.callCustomerApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            user_mobNo!!,
                            rider_mobNo!!
                        )
                    }
                }
            }
        }

        home_btntv.setOnClickListener {
            useconstants.track_sendpackage= true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            deliveryCard.visibility= View.GONE
            feedback_popup()
        }





        newOrderViewModel?.newTrackViewModel?.observe(this) {
            // Log.d("sddffsddsds", Gson().toJson(it)

            if (isNetworkConnected) {

                if (it.errorCode == 200) {
                    trackModel = it
                    Log.d("observe", "---Hua")
                    order_id= it.orderId
                    firstMsgListData.clear()

                    main_status = it.orderStatus
                    firstMsgListData = it.messages as ArrayList<Message>
                    if (totalarray==0){
                        totalarray++
                        rider_infoText = it.riderBottomMsgADR as ArrayList<String>
                    }
                    if (!it.riderDetails.name.equals("") && rider_infoText2count==0 ){

                        rider_infoText2count++
                        rider_infoText2 = it.riderBottomMsgADR as ArrayList<String>
                    }

                    useconstants.package_trackRiderName = it.riderDetails.name
                    tv_track_order_id.text= "Order ID"+" "+order_id
                    SetRecyclerview()
                    timer!!.start()
                    rider_mobNo = it.riderDetails.number
                    user_mobNo = it.customer_phone

                    if (!it.rider_img.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(it.rider_img)
                            .into(iv_rider_image)
                    }


                    var img_url = trackModel.gif
                    Glide.with(this).load(it.gif).into(imageView2)
                    Glide.with(this).load(it.riderBtmIcon).into(imageView5)
                    if (!it.riderDetails.img.isNullOrEmpty()) {
                        Glide.with(this).load(it.riderDetails.img).into(iv_rider_image)
                    }


                    if (it.orderStatus.equals("3")) {

                        imageView2.visibility = View.GONE
                        delivered_ll.visibility = View.VISIBLE
                        constraintLayout2.visibility = View.GONE
                        constraintLayout.visibility = View.GONE
                    }
                }
            }




        }
    }
    private fun SetRecyclerview() {

        mainAdapter = sendpackageOrderAdapter(this, firstMsgListData)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerViewSendPackage.layoutManager = linearLayoutManager
        recyclerViewSendPackage.adapter = mainAdapter
    }

    private fun CallPopUp(type: Int) {
        call_Diolog = Dialog(this)
        call_Diolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        call_Diolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        call_Diolog?.setContentView(R.layout.call_popup)
        call_Diolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        call_Diolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        call_Diolog?.setCanceledOnTouchOutside(true)
        call_Diolog?.show()

        val callTypeTxt = call_Diolog?.findViewById<TextView>(R.id.callTypeTxt)
        val regImg = call_Diolog?.findViewById<ImageView>(R.id.regImg)
        val cancelDialogConstL =
            call_Diolog?.findViewById<ConstraintLayout>(R.id.cancelDialogConstL)

        if (regImg != null) {
            Glide.with(this)
                .load(R.drawable.call_wait)
                .fitCenter()
                .error(R.drawable.default_item)
                .into(regImg)
        }

        callTypeTxt!!.setText("Just a minute, connecting with the rider in a bit.")

        cancelDialogConstL?.setOnClickListener {
            call_Diolog?.dismiss()
        }
    }

    private fun feedback_popup(){
        feedbacl_dialog = Dialog(this)
        feedbacl_dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        feedbacl_dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        feedbacl_dialog?.setContentView(R.layout.feedback_layout_sp)
        feedbacl_dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        feedbacl_dialog?.window?.attributes?.windowAnimations = R.style.diologIntertnet1
        feedbacl_dialog?.setCanceledOnTouchOutside(true)
        feedbacl_dialog?.setCancelable(false)
        feedbacl_dialog?.show()

        val star1= feedbacl_dialog?.findViewById<ImageView>(R.id.fb_star1)
        val star2= feedbacl_dialog?.findViewById<ImageView>(R.id.fb_star2)
        val star3= feedbacl_dialog?.findViewById<ImageView>(R.id.fb_star3)
        val star4= feedbacl_dialog?.findViewById<ImageView>(R.id.fb_star4)
        val star5= feedbacl_dialog?.findViewById<ImageView>(R.id.fb_star5)

        val msg= feedbacl_dialog?.findViewById<EditText>(R.id.et_fb)
        val submit= feedbacl_dialog?.findViewById<TextView>(R.id.btn_submit)

        star1?.setOnClickListener {
            submit?.background?.setTint(Color.parseColor("#3711C0"))
            submit?.setTextColor(Color.WHITE)
            total_rate=1
            star1.setImageResource(R.drawable.ic_star_blue)
            star2?.setImageResource(R.drawable.ic_star_4)
            star3?.setImageResource(R.drawable.ic_star_4)
            star4?.setImageResource(R.drawable.ic_star_4)
            star5?.setImageResource(R.drawable.ic_star_4)

        }

        star2?.setOnClickListener {
            submit?.background?.setTint(Color.parseColor("#3711C0"))
            submit?.setTextColor(Color.WHITE)
            total_rate=2
            star1?.setImageResource(R.drawable.ic_star_blue)
            star2.setImageResource(R.drawable.ic_star_blue)
            star3?.setImageResource(R.drawable.ic_star_4)
            star4?.setImageResource(R.drawable.ic_star_4)
            star5?.setImageResource(R.drawable.ic_star_4)
        }

        star3?.setOnClickListener {
            submit?.background?.setTint(Color.parseColor("#3711C0"))
            submit?.setTextColor(Color.WHITE)
            total_rate=3
            star1?.setImageResource(R.drawable.ic_star_blue)
            star2?.setImageResource(R.drawable.ic_star_blue)
            star3.setImageResource(R.drawable.ic_star_blue)
            star4?.setImageResource(R.drawable.ic_star_4)
            star5?.setImageResource(R.drawable.ic_star_4)
        }

        star4?.setOnClickListener {
            submit?.background?.setTint(Color.parseColor("#3711C0"))
            submit?.setTextColor(Color.WHITE)
            total_rate=4
            star1?.setImageResource(R.drawable.ic_star_blue)
            star2?.setImageResource(R.drawable.ic_star_blue)
            star3?.setImageResource(R.drawable.ic_star_blue)
            star4.setImageResource(R.drawable.ic_star_blue)
            star5?.setImageResource(R.drawable.ic_star_4)
        }

        star5?.setOnClickListener {
            submit?.background?.setTint(Color.parseColor("#3711C0"))
            submit?.setTextColor(Color.WHITE)
            total_rate=5
            star1?.setImageResource(R.drawable.ic_star_blue)
            star2?.setImageResource(R.drawable.ic_star_blue)
            star3?.setImageResource(R.drawable.ic_star_blue)
            star4?.setImageResource(R.drawable.ic_star_blue)
            star5.setImageResource(R.drawable.ic_star_blue)
        }



            submit?.setOnClickListener {
                useconstants.movetoOrderFrag= true

                if (total_rate==0){


                }else {

                    Toast.makeText(this,"Thanks for Your valuable feedback", Toast.LENGTH_SHORT).show()


                    showIOSProgress()

                    deliveryCard.visibility= View.VISIBLE
                    selection_msg = msg?.text.toString()
                    viewmodel!!.addfeedbackApi(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        order_id,
                        total_rate.toString(),
                        "",
                        selection_msg,
                        "add"
                    )

                    // Toast.makeText(this, "$total_rate  $selection_msg", Toast.LENGTH_LONG).show()

                   // feedbacl_dialog!!.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }





    }

    override fun onBackPressed() {


            useconstants.track_sendpackage=true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

    }



}