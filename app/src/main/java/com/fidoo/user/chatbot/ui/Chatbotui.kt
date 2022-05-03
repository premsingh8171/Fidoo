package com.fidoo.user.chatbot.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.adapter.botlogocount
import com.example.myapplication.adapter.orderStatusMsgAdapter.botlogoCountforreply
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.chatbot.adapter.BtnYesNoAdapter.BtnYesNoAdapter
import com.fidoo.user.chatbot.adapter.FeedbackAdapter.BotFeedbackAdapter
import com.fidoo.user.chatbot.adapter.MainAdapter
import com.fidoo.user.chatbot.adapter.cancelOrderWithoutRefundAdapter.CancelWithoutRefundAdapter
import com.fidoo.user.chatbot.adapter.orderStatusMsgAdapter.orderStatusAdapter
import com.fidoo.user.chatbot.viewmodel.*
import com.fidoo.user.constants.useconstants
import com.fidoo.user.data.session.SessionNotNull
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.ui.OrdersFragment
import com.fidoo.user.ordermodule.ui.ReviewOrderActivity
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.google.gson.Gson
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_chatbotui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class Chatbotui :AppCompatActivity() {
    var viewModel: ChatbotViewModel? = null
    var botmsgViewModel: BotmsgViewModel? = null
    var oderStatusViewModel: OrderStatusViewModel? = null
    var cancelWithoutRefundmsgViewModel: CancelWithoutRefundmsgViewModel? = null
    var cancelWithoutRefundmsgwithKeyViewModel: CancelWithoutRefundmsgwithKeyViewModel? = null
    var feedbackViewModel: FeedbackViewModel? = null
    var viewmodel: TrackViewModel? = null
    var call_Diolog: Dialog? = null
    lateinit var dateTime: String
    lateinit var dateTime1: String
    lateinit var dateTime2: String
    lateinit var dateTime3: String
    lateinit var calendar: Calendar
    lateinit var calendar2: Calendar
    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var simpleDateFormat1: SimpleDateFormat
    lateinit var simpleDateFormat2: SimpleDateFormat
    lateinit var mainAdapter: MainAdapter
    lateinit var orderStatusAdapter: orderStatusAdapter
    lateinit var cancelWithoutRefundAdapter: CancelWithoutRefundAdapter
    lateinit var btnYesNoAdapter: BtnYesNoAdapter
    lateinit var botFeedbackAdapter: BotFeedbackAdapter
    var firstMsgListData = ArrayList<String>()
    var secondMsgListData = ArrayList<String>()
    var allStatus: String = ""
    var userPaymentMode: Int? = null
    var dBoyName: String = ""
    var merchantName: String = ""
    var cancelWithoutRefundData = ArrayList<String>()
    var cancelWithoutRefundDatawithkeyData = ArrayList<String>()
    var feedbackdata = ArrayList<String>()
    var resvalue: Int? = null
    var resFeedbackvalue: Int? = null
    var driverMobileNo: String? = ""
    val sessionInstance: SessionTwiclo
        get() = SessionTwiclo(this)
    val sessionInstanceNotNull: SessionNotNull
        get() = SessionNotNull(this)
    var store_phone: String? = ""

    //last timing//
    lateinit var calendar3: Calendar
    lateinit var simpleDateFormat3: SimpleDateFormat

    ////---------//

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbotui)
        calendar = Calendar.getInstance()

        simpleDateFormat = SimpleDateFormat("EE - dd LLL")
        ////dd.LLL.yyyy HH:mm:ss aaa z
        //EEEE.LLLL.yyyy KK:mm:ss aaa z
        dateTime = simpleDateFormat.format(calendar.time).toString()
        DateandTime.text = dateTime

        simpleDateFormat1 = SimpleDateFormat("hh:mm aa")
        dateTime1 = simpleDateFormat1.format(calendar.time).toString()


        //  time1.text = dateTime
//        time2.text = dateTime
//        time3.text = dateTime
//        time4.text = dateTime
        oderStatusViewModel = ViewModelProvider(this).get(OrderStatusViewModel::class.java)
        oderStatusViewModel?.cancelOrderApi(
            SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId")
        )
        oderStatusViewModel?.orderStatusViewModel?.observe(this) {
            // Log.d("sddffsddsds", Gson().toJson(it))
            firstMsgListData = it.messages as ArrayList<String>
            allStatus = it.orderStatus
            //for payment mode
            userPaymentMode = it.userPaymentMode
            Log.d("sddffsddskgjgjds", Gson().toJson(it))
            setadapter()
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                DateandTime2.text = dateTime1
            }


//            if(it.orderStatus == "7" || it.orderStatus =="11"){
//                cancel.visibility =View.GONE
//            }else{
//                cancel.visibility =View.VISIBLE
//            }

            if (it.orderStatus.equals("1")) {
                orderstatus.text = "Order not confirm yet" // order status bot button
                orderconfirmStatus.text = "Order not confirm yet" // user reply
//                CoroutineScope(Dispatchers.Main).launch {
//                    delay(1500)
//                    cancel.visibility = View.VISIBLE
//
//                }
            } else {
                orderstatus.text = "Where is my order?"
                orderconfirmStatus.text = "Where is my order?"
//                CoroutineScope(Dispatchers.Main).launch {
//                    delay(1500)
//                    //cancel.visibility = View.GONE
//
//                }


            }
            if (it.userPaymentMode.equals("1")) {

            }

        }




//feedback Layout
        LayoutFeedback.visibility = View.INVISIBLE
        BtnYesNoReplayout.visibility = View.GONE
        tvlast.visibility = View.GONE

        cancel.text = "Cancel my order"



        CoroutineScope(Dispatchers.Main).launch {

            delay(2500)
            if (allStatus.equals("10") || allStatus.equals("3") || (userPaymentMode == 1 && !allStatus.equals(
                    "1"
                ))
            ) {
                cancel.visibility = View.GONE
                //
                BtnYes1.text = dBoyName
            } else {
                cancel.visibility = View.VISIBLE
                BtnYes1.text = merchantName
            }
            orderstatus.visibility = View.VISIBLE

        }

        orderstatus.setOnClickListener {
            //orderconfirmStatus.text = "Order not confirm yet"
            botlogolaysout.visibility = View.VISIBLE
            botlogo2.visibility = View.VISIBLE
            BtnYes.text = "Yes, Thank you \uD83D\uDE00"
            BtnNo.text = "No \uD83D\uDC4E"
            cancel.visibility = View.GONE
            orderstatus.visibility = View.GONE
            l2.visibility = View.VISIBLE
            DateandTime3.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                delay(4000)
                DateandTime4.visibility = View.VISIBLE
                delay(2000)
                if (allStatus == "1") {
                    LayoutFeedback.visibility = View.VISIBLE

                } else {
                    LayoutFeedback.visibility = View.GONE
                    tvlast.visibility = View.VISIBLE
                }
            }
//                tvlast.visibility = View.VISIBLE


            calendar2 = Calendar.getInstance()
            simpleDateFormat2 = SimpleDateFormat("hh:mm aa")
            dateTime2 = simpleDateFormat2.format(calendar2.time).toString()
            // DateandTime2.text = dateTime
            DateandTime3.text = dateTime2
            DateandTime4.text = dateTime2
            //LayoutFeedback.visibility  =View.VISIBLE
            botmsgViewModel = ViewModelProvider(this).get(BotmsgViewModel::class.java)
            botmsgViewModel?.botmsgapi(
                SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId")
            )
            botmsgViewModel?.botMsgViewModel?.observe(this) {
                // Log.d("sddffsddsds", Gson().toJson(it))
                secondMsgListData = it.messages as ArrayList<String>
//                dBoyName = it.dBoyName
//                merchantName = it.merchantName
                if (allStatus.equals("10")) {
                    BtnYes1.text = "Call " + it.dBoyName + " "
                } else {
                    BtnYes1.text = "Call " + it.merchantName + " "

                }
                Log.d("skhdfsff", it.merchantName + " ")

                setRecyclerViewBotmsg()

            }



            BtnNo.setOnClickListener {
                botlogo3.visibility = View.VISIBLE
                botlogolaysout3.visibility = View.VISIBLE
                BtnYesNoReplayout.visibility = View.VISIBLE
                BtnYesReply.text = "No \uD83D\uDC4E"
                LayoutFeedback.visibility = View.GONE
                tvlast.visibility = View.VISIBLE


                // --------------------
                DateandTime5.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)

                    tvlast.visibility = View.VISIBLE
                    /**
                     * calling button
                     */
//
                    delay(300)
                    BtnYes1.visibility = View.VISIBLE
                }
                calendar3 = Calendar.getInstance()
                simpleDateFormat3 = SimpleDateFormat(" hh:mm aa")
                dateTime3 = simpleDateFormat3.format(calendar3.time).toString()
                // DateandTime2.text = dateTime
                DateandTime5.text = dateTime3
                DateandTime6.text = dateTime3
                resFeedbackvalue = 2
                feedbackViewModel = ViewModelProvider(this).get(FeedbackViewModel::class.java)
                intent.getStringExtra("orderId")?.let { it1 ->
                    feedbackViewModel?.FeedbackViewModelMsg(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken, it1, resFeedbackvalue!!
                    )
                }
                feedbackViewModel?.FeedbackViewModelResponse?.observe(this) {
                    // Log.d("sddffsddsds", Gson().toJson(it))
                    feedbackdata = it.messages as ArrayList<String>
                    FeedbackRecylerView()
                    DateandTime6.visibility = View.VISIBLE


                }

            }


            BtnYes1.setOnClickListener {
                if (allStatus == "10") {
                    onCallPopUp(1)
                    Log.d("sumit1" , Gson().toJson(it))
                    if (sessionInstance.profileDetail != null) {
                        viewmodel?.callCustomerApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            sessionInstance.profileDetail.account.userName,
                            driverMobileNo!!
                        )
                    } else {
                        viewmodel?.callCustomerApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            sessionInstance.loginDetail.phoneNumber,
                            driverMobileNo!!
                        )
                    }
                }else if(allStatus=="1") {
                    Toast.makeText(applicationContext,"Waiting for merchant to accept your order",Toast.LENGTH_SHORT).show()

                }else{
                    onCallPopUp(0)
                    if (sessionInstance.profileDetail != null) {

                        viewmodel?.customerCallMerchantApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            sessionInstance.profileDetail.account.userName,
                            store_phone!!

                        )
                        //Toast.makeText(this,"$store_phone",Toast.LENGTH_SHORT).show();
                        Log.d("sumit" , store_phone!!)
                    } else {
                        viewmodel?.customerCallMerchantApi(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken,
                            sessionInstance.loginDetail.phoneNumber,
                            store_phone!!
                        )
                    }
                }
            }



//if(allStatus.equals("10")) {
//    BtnYes1.setOnClickListener {
//
//    }
//}else{
//    BtnYes1.setOnClickListener {
//
//    }
//}
            ////////////////////////////////////calling //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


            BtnYes.setOnClickListener{
                botlogo3 .visibility = View.VISIBLE
                botlogolaysout3.visibility = View.VISIBLE
                BtnYesNoReplayout.visibility = View.VISIBLE
                BtnYesReply.text = "Yes, Thank you \uD83D\uDE00"
                LayoutFeedback.visibility = View.GONE
                DateandTime5.visibility = View.VISIBLE
                tvlast.visibility = View.VISIBLE

                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    DateandTime6.visibility = View.VISIBLE
                    tvlast.visibility = View.VISIBLE
                }
                calendar3 = Calendar.getInstance()
                simpleDateFormat3 = SimpleDateFormat(" hh:mm aa")
                dateTime3 = simpleDateFormat3.format(calendar3.time).toString()
                // DateandTime2.text = dateTime
                DateandTime5.text = dateTime3
                DateandTime6.text = dateTime3
                //for printing order
                resFeedbackvalue = 1
                feedbackViewModel = ViewModelProvider(this).get(FeedbackViewModel::class.java)
                intent.getStringExtra("orderId")?.let { it1 ->
                    feedbackViewModel?.FeedbackViewModelMsg(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken, it1, resFeedbackvalue!!
                    )
                }
                feedbackViewModel?.FeedbackViewModelResponse?.observe(this) {
                    // Log.d("sddffsddsds", Gson().toJson(it))
                    feedbackdata = it.messages as ArrayList<String>
                    Log.d("sddffsddsds", Gson().toJson(it))
                    FeedbackRecylerView()



                }
            }

            //////////////////////////////////////////cod order placed


        }
        backBtn.setOnClickListener {
            finish()

        }


        cancel.setOnClickListener {

            botlogo2.visibility = View.VISIBLE
            botlogolaysout.visibility = View.VISIBLE
            orderconfirmStatus.text = "Cancel my order"
            cancel.visibility = View.GONE
            orderstatus.visibility = View.GONE
            l2.visibility = View.VISIBLE
            DateandTime3.visibility = View.VISIBLE


            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                DateandTime4.visibility = View.VISIBLE


            }


            // LayoutFeedback.visibility  =View.VISIBLE
            calendar2 = Calendar.getInstance()
            simpleDateFormat2 = SimpleDateFormat("hh:mm aa")
            dateTime2 = simpleDateFormat2.format(calendar2.time).toString()
            // DateandTime2.text = dateTime
            DateandTime3.text = dateTime2
            DateandTime4.text = dateTime2

            //cancel order
            if (allStatus == "1") {
                BtnYes.text = "Yes, Thank you \uD83D\uDE00"
                BtnNo.text = "No \uD83D\uDC4E"


                CoroutineScope(Dispatchers.Main).launch {
                    delay(2500)
                    LayoutFeedback.visibility = View.VISIBLE
                }

                viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)
                intent.getStringExtra("orderId")?.let { it1 ->
                    viewModel?.cancelOrderApiChatBot(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken, it1
                    )
                }
                viewModel?.cancelOrderResponse?.observe(this) {


                }

                // if (it.orderstatus)
                botmsgViewModel = ViewModelProvider(this).get(BotmsgViewModel::class.java)
                botmsgViewModel?.botmsgapi(
                    SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken,
                    intent.getStringExtra("orderId")
                )
                botmsgViewModel?.botMsgViewModel?.observe(this) {
                    // Log.d("sddffsddsds", Gson().toJson(it))
                    secondMsgListData = it.messages as ArrayList<String>
                    Log.d("sddffsddsds", Gson().toJson(it))
                    setRecyclerViewBotmsg()

                }
                /////////// //Feedback model for cod and order placed situation//////////////////////////////////////////////////////////////////////////////////////

                BtnNo.setOnClickListener {
                    botlogo3 .visibility = View.VISIBLE
                    botlogolaysout3.visibility = View.VISIBLE
                    BtnYesNoReplayout.visibility = View.VISIBLE
                    BtnYesReply.text = "No \uD83D\uDC4E"
                    LayoutFeedback.visibility = View.GONE



                    DateandTime5.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        DateandTime6.visibility = View.VISIBLE
                        tvlast.visibility = View.VISIBLE

                        delay(500)
                        finish()
                        AppUtils.startActivityRightToLeft(
                            this@Chatbotui,Intent(this@Chatbotui, MainActivity::class.java)
                                .putExtra("orderId", intent.getStringExtra("orderId")!!))

                    }
                    simpleDateFormat1 = SimpleDateFormat(" hh:mm aa")
                    dateTime1 = simpleDateFormat1.format(calendar.time).toString()
                    // DateandTime2.text = dateTime
                    DateandTime5.text = dateTime1
                    DateandTime6.text = dateTime1
                    resFeedbackvalue = 2
                    feedbackViewModel = ViewModelProvider(this).get(FeedbackViewModel::class.java)
                    intent.getStringExtra("orderId")?.let { it1 ->
                        feedbackViewModel?.FeedbackViewModelMsg(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken, it1,
                            resFeedbackvalue!!
                        )
                    }
                    feedbackViewModel?.FeedbackViewModelResponse?.observe(this) {
                        // Log.d("sddffsddsds", Gson().toJson(it))
                        feedbackdata = it.messages as ArrayList<String>

                        Log.d("sddffsddsds", Gson().toJson(it))
                        FeedbackRecylerView()

                    }

                }
                BtnYes.setOnClickListener{
                    botlogo3 .visibility = View.VISIBLE
                    botlogolaysout3.visibility = View.VISIBLE
                    BtnYesNoReplayout.visibility = View.VISIBLE
                    BtnYesReply.text = "Yes, Thank you \uD83D\uDE00"
                    LayoutFeedback.visibility = View.GONE
                    DateandTime5.visibility = View.VISIBLE
// BtnYes.text = "Yes, Thank you \uD83D\uDE00"
//                BtnNo.text = "No \uD83D\uDC4E"
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        DateandTime6.visibility = View.VISIBLE
                        delay(1000)
                        tvlast.visibility = View.VISIBLE
                        delay(500)
                        finish()
                        AppUtils.startActivityRightToLeft(
                            this@Chatbotui,Intent(this@Chatbotui, MainActivity::class.java)
                                .putExtra("orderId", intent.getStringExtra("orderId")!!))
//                        delay(2000)
//                        finish()
//                        AppUtils.startActivityRightToLeft(
//                            this@Chatbotui,Intent(this@Chatbotui, MainActivity::class.java)
//                                .putExtra("orderId", intent.getStringExtra("orderId")!!))
                    }
                    simpleDateFormat1 = SimpleDateFormat(" hh:mm aa")
                    dateTime1 = simpleDateFormat1.format(calendar.time).toString()
                    // DateandTime2.text = dateTime
                    DateandTime5.text = dateTime1
                    DateandTime6.text = dateTime1

                    //for printing order
                    resFeedbackvalue = 1
                    feedbackViewModel = ViewModelProvider(this).get(FeedbackViewModel::class.java)
                    intent.getStringExtra("orderId")?.let { it1 ->
                        feedbackViewModel?.FeedbackViewModelMsg(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken, it1,
                            resFeedbackvalue!!
                        )
                    }
                    feedbackViewModel?.FeedbackViewModelResponse?.observe(this) {
                        // Log.d("sddffsddsds", Gson().toJson(it))
                        feedbackdata = it.messages as ArrayList<String>
                        Log.d("sddffsddsds", Gson().toJson(it))
                        FeedbackRecylerView()

                    }

                }

                //////////////////////////////////////////cod order placed
///--------------------------------------------------------------------------------------------------------------
            }else {

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    LayoutFeedback.visibility = View.VISIBLE
                }
                BtnYes.text = "Yes, Cancel it"
                BtnNo.text = "No, i want my order"

                cancelWithoutRefundmsgViewModel =
                    ViewModelProvider(this).get(CancelWithoutRefundmsgViewModel::class.java)

                intent.getStringExtra("orderId")?.let { it1 ->
                    cancelWithoutRefundmsgViewModel?.cancelWithoutRefundmsg(
                        SessionTwiclo(this).loggedInUserDetail.accountId,
                        SessionTwiclo(this).loggedInUserDetail.accessToken,
                        it1
                    )

                }
                cancelWithoutRefundmsgViewModel?.CancelWithoutRefundmsgResponse?.observe(this) {
                    // Log.d("sddffsddsds", Gson().toJson(it))
                    cancelWithoutRefundData = it.messages as ArrayList<String>
                    Log.d("sddffsddsdsdsfsfdsddsdddff", Gson().toJson(it.isShowCancel))
                    setRecyclerViewCancelWithoutRefund()

                }
                BtnNo.setOnClickListener {
                    botlogo3 .visibility = View.VISIBLE
                    botlogolaysout3.visibility = View.VISIBLE
                    BtnYesNoReplayout.visibility = View.VISIBLE
                    BtnYesReply.text = "No, i want my order"
                    LayoutFeedback.visibility = View.GONE

                    DateandTime5.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        DateandTime6.visibility = View.VISIBLE
                        tvlast.visibility = View.VISIBLE
                    }
                    simpleDateFormat1 = SimpleDateFormat(" hh:mm aa")
                    dateTime1 = simpleDateFormat1.format(calendar.time).toString()
                    // DateandTime2.text = dateTime
                    DateandTime5.text = dateTime1
                    DateandTime6.text = dateTime1
                    resvalue = 2
                    cancelWithoutRefundmsgwithKeyViewModel = ViewModelProvider(this).get(CancelWithoutRefundmsgwithKeyViewModel::class.java)
                    intent.getStringExtra("orderId")?.let { it1 ->
                        cancelWithoutRefundmsgwithKeyViewModel?.cancelWithoutRefundmsgWithKey(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken, it1,  resvalue
                        )
                    }
                    cancelWithoutRefundmsgwithKeyViewModel?.CancelWithoutRefundmsgResponseWithKey?.observe(this) {
                        // Log.d("sddffsddsds", Gson().toJson(it))
                        cancelWithoutRefundDatawithkeyData = it.messages as ArrayList<String>
                        Log.d("sddffsddsds", Gson().toJson(it))
                        setRecyclercancelWithoutRefundmsgwithKey()

                    }
//                    finish()
//                    AppUtils.startActivityRightToLeft(
//                        this@Chatbotui,Intent(this@Chatbotui, MainActivity::class.java)
//                            .putExtra("orderId", intent.getStringExtra("orderId")!!))
                }
                BtnYes.setOnClickListener{
                    botlogo3 .visibility = View.VISIBLE
                    botlogolaysout3.visibility = View.VISIBLE
                    BtnYesNoReplayout.visibility = View.VISIBLE
                    BtnYesReply.text = "Yes, Cancel it"
                    LayoutFeedback.visibility = View.GONE
                    DateandTime5.visibility = View.VISIBLE

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        DateandTime6.visibility = View.VISIBLE
                        tvlast.visibility = View.VISIBLE
                    }
                    simpleDateFormat1 = SimpleDateFormat(" hh:mm aa")
                    dateTime1 = simpleDateFormat1.format(calendar.time).toString()
                    // DateandTime2.text = dateTime
                    DateandTime5.text = dateTime1
                    DateandTime6.text = dateTime1
                    //for canceling order
                    viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)
                    intent.getStringExtra("orderId")?.let { it1 ->
                        viewModel?.cancelOrderApiChatBot(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken, it1
                        )
                    }
                    viewModel?.cancelOrderResponse?.observe(this) {


                    }

                    //for printing order
                    resvalue = 1
                    cancelWithoutRefundmsgwithKeyViewModel = ViewModelProvider(this).get(CancelWithoutRefundmsgwithKeyViewModel::class.java)
                    intent.getStringExtra("orderId")?.let { it1 ->
                        cancelWithoutRefundmsgwithKeyViewModel?.cancelWithoutRefundmsgWithKey(
                            SessionTwiclo(this).loggedInUserDetail.accountId,
                            SessionTwiclo(this).loggedInUserDetail.accessToken, it1,  resvalue
                        )
                    }
                    cancelWithoutRefundmsgwithKeyViewModel?.CancelWithoutRefundmsgResponseWithKey?.observe(this) {
                        // Log.d("sddffsddsds", Gson().toJson(it))
                        cancelWithoutRefundDatawithkeyData = it.messages as ArrayList<String>
                        Log.d("sddffsddsds", Gson().toJson(it))
                        setRecyclercancelWithoutRefundmsgwithKey()

                    }
                }

            }
//            finish()
//            AppUtils.startActivityRightToLeft(
//                this@Chatbotui,Intent(this@Chatbotui, MainActivity::class.java)
//                    .putExtra("orderId", intent.getStringExtra("orderId")!!))

        }
        //----------------------------------



        DateandTime5.visibility = View.GONE
        DateandTime6.visibility = View.GONE


    }

    private fun FeedbackRecylerView() {

        //feedbackdata
        botFeedbackAdapter = BotFeedbackAdapter(this,feedbackdata)
        val linearLayoutManager= LinearLayoutManager(this)
        recyclerView3.adapter= botFeedbackAdapter
        recyclerView3.layoutManager=linearLayoutManager

    }

    private fun setRecyclercancelWithoutRefundmsgwithKey() {
        btnYesNoAdapter = BtnYesNoAdapter(this,cancelWithoutRefundDatawithkeyData)
        val linearLayoutManager= LinearLayoutManager(this)
        recyclerView3.adapter= btnYesNoAdapter
        recyclerView3.layoutManager=linearLayoutManager
    }


    private fun setadapter() {
        mainAdapter = MainAdapter(this,firstMsgListData)
        val linearLayoutManager= LinearLayoutManager(this)
        recyclerView.adapter= mainAdapter
        recyclerView.layoutManager=linearLayoutManager
    }


    private  fun setRecyclerViewBotmsg(){
        orderStatusAdapter = orderStatusAdapter(this,secondMsgListData)
        val linearLayoutManager= LinearLayoutManager(this)
        recyclerView2.adapter= orderStatusAdapter
        recyclerView2.layoutManager=linearLayoutManager

    }
    private  fun setRecyclerViewCancelWithoutRefund(){
        cancelWithoutRefundAdapter = CancelWithoutRefundAdapter(this,cancelWithoutRefundData)
        val linearLayoutManager= LinearLayoutManager(this)
        recyclerView2.adapter= cancelWithoutRefundAdapter
        recyclerView2.layoutManager=linearLayoutManager

    }

    override fun onResume() {
        super.onResume()
        // botlogoCountforreply.botcount1 = 0




    }

    override fun onStop() {
        super.onStop()

        botlogoCountforreply.botcount1 = 0
        botlogocount.botcount  = 0
        botlogocount.botrefundCount = 0
        botlogocount.botYesNoCount = 0
    }

    override fun onPause() {
        super.onPause()

        botlogoCountforreply.botcount1 = 0
        botlogocount.botcount = 0
        botlogocount.botrefundCount = 0
        botlogocount.botYesNoCount = 0
    }
    private fun onCallPopUp(type: Int) {
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

        if (type == 0) {
            callTypeTxt!!.setText("Just a minute, connecting with the merchant in a bit.")
        } else {
            callTypeTxt!!.setText("Just a minute, connecting with the rider in a bit.")
        }

        cancelDialogConstL?.setOnClickListener {
            call_Diolog?.dismiss()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        AppUtils.startActivityRightToLeft(
            this@Chatbotui,Intent(this@Chatbotui, MainActivity::class.java)
                .putExtra("orderId", intent.getStringExtra("orderId")!!))

    }
}


// val model = datalist[position]
//        var count = datalist.size
//        holder.itemLayoutBinding.apply {
//            cardbotlayout.visibility = View.GONE
//            CoroutineScope(Dispatchers.Main).launch {
//                delay(position * 1000L)
//                Glide.with(context).asGif().load(R.drawable.typing).into(loadingGifChat)
//                delay(position*600L)
//                tvTitle.text = model
//                loadingGifChat.visibility = View.GONE
//                cardbotlayout.visibility = View.VISIBLE
//            }
//        }
//    }