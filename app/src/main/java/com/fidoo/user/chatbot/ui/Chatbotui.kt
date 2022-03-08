package com.fidoo.user.chatbot.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.chatbot.adapter.MainAdapter
import com.example.myapplication.adapter.botlogocount
import com.example.myapplication.adapter.orderStatusMsgAdapter.botlogoCountforreply
import com.fidoo.user.chatbot.adapter.orderStatusMsgAdapter.orderStatusAdapter

import com.fidoo.user.R
import com.fidoo.user.chatbot.viewmodel.BotmsgViewModel
import com.fidoo.user.chatbot.viewmodel.ChatbotViewModel
import com.fidoo.user.chatbot.viewmodel.OrderStatusViewModel
import com.fidoo.user.data.session.SessionTwiclo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chatbotui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class Chatbotui :AppCompatActivity() {
    var viewModel: ChatbotViewModel? = null
    var botmsgViewModel : BotmsgViewModel? =null
    var oderStatusViewModel: OrderStatusViewModel? = null
    lateinit var dateTime: String
    lateinit var dateTime1: String
    lateinit var calendar: Calendar
    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var simpleDateFormat1: SimpleDateFormat
    lateinit var mainAdapter: MainAdapter
    lateinit var orderStatusAdapter: orderStatusAdapter
    var firstMsgListData=ArrayList<String>()
    var secondMsgListData = ArrayList<String>()

    @SuppressLint("SetTextI18n")
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
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            DateandTime2.text = dateTime1
        }
        //  time1.text = dateTime
//        time2.text = dateTime
//        time3.text = dateTime
//        time4.text = dateTime
        oderStatusViewModel = ViewModelProvider(this).get(OrderStatusViewModel::class.java)
        oderStatusViewModel?.cancelOrderApi(SessionTwiclo(this).loggedInUserDetail.accountId,
            SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId"))
        oderStatusViewModel?.orderStatusViewModel?.observe(this) {
            // Log.d("sddffsddsds", Gson().toJson(it))
            firstMsgListData = it.messages as ArrayList<String>
            Log.d("sddffsddskgjgjds", Gson().toJson(it))
            setadapter()

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

            }else {
                orderstatus.text = "Where is my order?"
                orderconfirmStatus.text = "Where is my order?"
//                CoroutineScope(Dispatchers.Main).launch {
//                    delay(1500)
//                    //cancel.visibility = View.GONE
//
//                }

            }



        }

        cancel.text = "cancel my order"

        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
           // cancel.visibility = View.VISIBLE
            orderstatus.visibility = View.VISIBLE

        }

        orderstatus.setOnClickListener{
            //orderconfirmStatus.text = "Order not confirm yet"
            cancel.visibility = View.GONE
            orderstatus.visibility = View.GONE
            l2.visibility = View.VISIBLE
            DateandTime3.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                DateandTime4.visibility = View.VISIBLE
                delay(1000)
                tvlast.visibility = View.VISIBLE


            }

            simpleDateFormat1 = SimpleDateFormat(" hh:mm aa")
            dateTime1 = simpleDateFormat1.format(calendar.time).toString()
            // DateandTime2.text = dateTime
            DateandTime3.text = dateTime1
            DateandTime4.text = dateTime1
            //LayoutFeedback.visibility  =View.VISIBLE
            botmsgViewModel = ViewModelProvider(this).get(BotmsgViewModel::class.java)
            botmsgViewModel?.botmsgapi(SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId"))
            botmsgViewModel?.botMsgViewModel?.observe(this) {
                // Log.d("sddffsddsds", Gson().toJson(it))
                secondMsgListData =it as ArrayList<String>
                Log.d("sddffsddsds", Gson().toJson(it))
                setRecyclerViewBotmsg()

            }



        }
        backBtn.setOnClickListener{
            finish()

        }


        cancel.setOnClickListener{
            orderconfirmStatus.text = "Cancel my order"
            cancel.visibility = View.GONE
            orderstatus.visibility = View.GONE
            l2.visibility = View.VISIBLE
            DateandTime3.visibility = View.VISIBLE


            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                DateandTime4.visibility = View.VISIBLE
                delay(1000)
                tvlast.visibility = View.VISIBLE


            }
            // LayoutFeedback.visibility  =View.VISIBLE
            simpleDateFormat1 = SimpleDateFormat(" hh:mm aa")
            dateTime1 = simpleDateFormat1.format(calendar.time).toString()
            DateandTime3.text = dateTime1
            DateandTime4.text = dateTime1

            //
            viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)
            intent.getStringExtra("orderId")?.let { it1 ->
                viewModel?.cancelOrderApiChatBot( SessionTwiclo(this).loggedInUserDetail.accountId,
                    SessionTwiclo(this).loggedInUserDetail.accessToken, it1
                )
            }
            viewModel?.cancelOrderResponse?.observe(this) {
                // Log.d("sddffsddsds", Gson().toJson(it.orderId))
                // tv1.text = "Your order has been cancelled"
                // cancelmsgList= it as ArrayList<ChatCancelModel>
               // Log.d("sddffsddsds", Gson().toJson(it.orderStatus))
            }

            // if (it.orderstatus)
            botmsgViewModel = ViewModelProvider(this).get(BotmsgViewModel::class.java)
            botmsgViewModel?.botmsgapi(SessionTwiclo(this).loggedInUserDetail.accountId,
                SessionTwiclo(this).loggedInUserDetail.accessToken, intent.getStringExtra("orderId"))
            botmsgViewModel?.botMsgViewModel?.observe(this) {
                // Log.d("sddffsddsds", Gson().toJson(it))
                secondMsgListData =it as ArrayList<String>
                Log.d("sddffsddsds", Gson().toJson(it))
                setRecyclerViewBotmsg()

            }

        }

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

    override fun onResume() {
        super.onResume()
        // botlogoCountforreply.botcount1 = 0

    }

    override fun onStop() {
        super.onStop()

        botlogoCountforreply.botcount1 = 0
        botlogocount.botcount  = 0
    }

    override fun onPause() {
        super.onPause()

        botlogoCountforreply.botcount1 = 0
        botlogocount.botcount = 0
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

