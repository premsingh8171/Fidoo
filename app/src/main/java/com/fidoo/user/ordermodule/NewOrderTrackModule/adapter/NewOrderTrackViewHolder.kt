package com.fidoo.user.ordermodule.NewOrderTrackModule.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.constants.useconstants
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.Message
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.android.synthetic.main.newordertrackitem.view.*
import kotlinx.android.synthetic.main.newordertrackitem.view.cardbotlayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


var store_phone: String? = ""
var viewmodel: TrackViewModel? = null
var orderViewModel: OrderDetailsViewModel? = null
var viewmodelusertrack: UserTrackerViewModel? = null
var call_Diolog: Dialog? = null
var driverMobileNo: String? = ""

class NewOrderTrackViewHolder (val view : View, var context: Context) : RecyclerView.ViewHolder(view) {
    val sessionInstance: SessionTwiclo
        get() = SessionTwiclo(context)

    fun setdata(message: Message) {
        view.apply {

            cardbotlayout.visibility = View.GONE
            var url = message.iconImg
            GlideToVectorYou.init().with(context).load(Uri.parse(url), ImgTrack)
            var MainOrderStatus = useconstants.orderStatusMain
            var call = useconstants.callAllow
            TvTrackItem2.visibility = View.GONE

            try {
                if (!useconstants.isScroll) {

                    if (message.status == 1) {
                        cardbotlayout.visibility = View.VISIBLE
                        if( MainOrderStatus.equals("1")){
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(400)
                                TvTrackItem2.visibility = View.VISIBLE
                            }

                        }

                        if((MainOrderStatus.equals("11")) && (MainOrderStatus.equals("15")) ){
                            TvTrackItem2.visibility = View.GONE
                        }
                        if(((MainOrderStatus.equals("14")) && (MainOrderStatus.equals("9")) && (!MainOrderStatus.equals("15")))){
                            TvTrackItem2.visibility = View.GONE

                        }

                        if (MainOrderStatus == "13") {
//                            TvTrackItem2.visibility = View.VISIBLE
                            ImgTrack.visibility = View.VISIBLE
                            TvTrackItem.visibility = View.VISIBLE
                            TvTrackItem.text = message.aDRMessage
                            TvTrackItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRMessage, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRMessage)
                            }

                            TvTrackItem.setTextColor(Color.parseColor("#FFFFFF"))
                        } else {
                           
                            TvTrackItem.setBackgroundColor(Color.parseColor(message.bgColorDark))
                            ImgTrack.visibility = View.VISIBLE
                            TvTrackItem.visibility = View.VISIBLE
                            TvTrackItem.text = message.aDRMessage
                            TvTrackItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRMessage, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRMessage)
                            }

                            TvTrackItem1.visibility = View.VISIBLE
                            TvTrackItem1.setTextColor(Color.parseColor("#FFFFFF"))
                            TvTrackItem.setTextColor(Color.parseColor("#FFFFFF"))
                            TvTrackItem2.setTextColor(Color.parseColor("#FFFFFF"))
                            TvTrackItem1.text = message.aDRDesc
                            TvTrackItem1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRDesc, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRDesc)
                            }
                            //if(MainOrderStatus.equals("7") || MainOrderStatus.equals("11")) {
                           // TvTrackItem2.visibility = View.VISIBLE
                            //}



///-----------------------------------------------------------------------------------------------------------------------------
                            TvTrackItem2.setOnClickListener {

                                if (!store_phone.equals("")) {
                                    onCallPopUp(0)
                                    if (sessionInstance.profileDetail != null) {
                                        viewmodel?.customerCallMerchantApi(
                                            SessionTwiclo(context).loggedInUserDetail.accountId,
                                            SessionTwiclo(context).loggedInUserDetail.accessToken,
                                            sessionInstance.profileDetail.account.userName,
                                            store_phone!!
                                        )
                                    } else {
                                        viewmodel?.customerCallMerchantApi(
                                            SessionTwiclo(context).loggedInUserDetail.accountId,
                                            SessionTwiclo(context).loggedInUserDetail.accessToken,
                                            sessionInstance.loginDetail.phoneNumber,
                                            store_phone!!
                                        )
                                    }
                                }


                                TvTrackItem2.setOnClickListener {

                                    onCallPopUp(1)

                                    if (sessionInstance.profileDetail != null) {
                                        viewmodel?.callCustomerApi(
                                            SessionTwiclo(context).loggedInUserDetail.accountId,
                                            SessionTwiclo(context).loggedInUserDetail.accessToken,
                                            sessionInstance.profileDetail.account.userName,
                                            driverMobileNo!!
                                        )
                                    } else {
                                        viewmodel?.callCustomerApi(
                                            SessionTwiclo(context).loggedInUserDetail.accountId,
                                            SessionTwiclo(context).loggedInUserDetail.accessToken,
                                            sessionInstance.loginDetail.phoneNumber,
                                            driverMobileNo!!
                                        )
                                    }
                                }

                            }
                            //-------------------------------------------------------------------------------




                            if (MainOrderStatus == "14") {
                                TvTrackItem.text = message.aDRMessage
                                TvTrackItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(message.aDRMessage, Html.FROM_HTML_MODE_COMPACT)
                                } else {
                                    Html.fromHtml(message.aDRMessage)
                                }
   }

                            TvTrackItem1.text = message.aDRDesc
                            TvTrackItem1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRDesc, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRDesc)
                            }


                        }
                    }
                    if (message.status == 2) {
                        cardbotlayout.visibility = View.VISIBLE
                        if (useconstants.pos == 0) {
                            TvTrackItem.text = message.aDRMessage
                            TvTrackItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRMessage, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRMessage)
                            }
                            ImgTrack.visibility = View.VISIBLE
                            TvTrackItem.visibility = View.VISIBLE

                            TvTrackItem.setBackgroundColor(Color.parseColor(message.bgColorDark))
                            useconstants.pos++
                        }


                    }
                } else {

                    cardbotlayout.visibility = View.VISIBLE
                    if (MainOrderStatus == "13") {
                        ImgTrack.visibility = View.VISIBLE
                        TvTrackItem.visibility = View.VISIBLE
                        TvTrackItem.text = message.aDRMessage
                        TvTrackItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(message.aDRMessage, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            Html.fromHtml(message.aDRMessage)
                        }
                        TvTrackItem.setTextColor(Color.parseColor("#FFFFFF"))
                    } else {
                        TvTrackItem2.visibility = View.GONE
                        cardbotlayout.visibility = View.VISIBLE
                        TvTrackItem.text = message.aDRMessage
                        TvTrackItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(message.aDRMessage, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            Html.fromHtml(message.aDRMessage)
                        }
                        TvTrackItem.visibility = View.VISIBLE
                        TvTrackItem.setBackgroundColor(Color.parseColor(message.bgColorDark))
                        ImgTrack.visibility = View.VISIBLE

                        if (!message.desc.isNullOrEmpty()) {
                            TvTrackItem1.visibility = View.VISIBLE
                            TvTrackItem1.text = message.aDRDesc
                            TvTrackItem1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRDesc, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRDesc)
                            }
                            TvTrackItem1.setBackgroundColor(Color.parseColor(message.bgColorDark))

                        }
                        var url = message.iconImg
                        GlideToVectorYou.init().with(context).load(Uri.parse(url), ImgTrack)
                        if (message.status == 1) {
                            TvTrackItem1.visibility = View.VISIBLE
                            TvTrackItem1.setTextColor(Color.parseColor("#FFFFFF"))
                            TvTrackItem.setTextColor(Color.parseColor("#FFFFFF"))
                            TvTrackItem2.setTextColor(Color.parseColor("#FFFFFF"))
//                    TvTrackItem1.text = message.desc
                            //  if(MainOrderStatus.equals("7") || MainOrderStatus.equals("11")) {
                            if(useconstants.orderStatusMain.equals("10")||useconstants.orderStatusMain.equals("16") || useconstants.orderStatusMain.equals("16") ){
                                TvTrackItem2.visibility = View.GONE
                            }else{
                                TvTrackItem2.visibility = View.VISIBLE
                            }
                            //}

                            TvTrackItem1.text = message.aDRDesc

                            TvTrackItem1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRDesc, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRDesc)
                            }
                            if (MainOrderStatus.equals("14")) {
                                TvTrackItem.text = message.aDRMessage
                                TvTrackItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(message.aDRMessage, Html.FROM_HTML_MODE_COMPACT)
                                } else {
                                    Html.fromHtml(message.aDRMessage)
                                }

                            }
                        }


                    }

                    TvTrackItem2.setOnClickListener {

                        onCallPopUp(1)

                        if (sessionInstance.profileDetail != null) {
                            viewmodel?.callCustomerApi(
                                SessionTwiclo(context).loggedInUserDetail.accountId,
                                SessionTwiclo(context).loggedInUserDetail.accessToken,
                                sessionInstance.profileDetail.account.userName,
                                driverMobileNo!!
                            )
                        } else {
                            viewmodel?.callCustomerApi(
                                SessionTwiclo(context).loggedInUserDetail.accountId,
                                SessionTwiclo(context).loggedInUserDetail.accessToken,
                                sessionInstance.loginDetail.phoneNumber,
                                driverMobileNo!!
                            )
                        }
                    }


                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }

    private fun onCallPopUp(type: Int) {
        call_Diolog = Dialog(context)
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
            Glide.with(context)
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
}






