package com.fidoo.user.ordermodule.NewOrderTrackModule.RestaurantsCase.adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.constants.useconstants
import com.fidoo.user.ordermodule.NewOrderTrackModule.RestaurantsCase.NewTrackModel.Message
import com.fidoo.user.ordermodule.viewmodel.OrderDetailsViewModel
import com.fidoo.user.ordermodule.viewmodel.TrackViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.android.synthetic.main.newordertrackitem.view.*
import kotlinx.android.synthetic.main.newordertrackitem.view.cardbotlayout
import java.lang.Exception



var viewmodel: TrackViewModel? = null
var orderViewModel: OrderDetailsViewModel? = null
var viewmodelusertrack: UserTrackerViewModel? = null






class NewOrderTrackViewHolder (val view : View, var context: Context) : RecyclerView.ViewHolder(view) {


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

                                TvTrackItem2.visibility = View.VISIBLE


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
                            TvTrackItem1.text = message.aDRDesc
                            TvTrackItem1.visibility = View.VISIBLE
                            TvTrackItem2.visibility = View.VISIBLE
                            TvTrackItem2.setTextColor(Color.parseColor("#FFFFFF"))
                            TvTrackItem1.setTextColor(Color.parseColor("#FFFFFF"))
                            TvTrackItem1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(message.aDRDesc, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                Html.fromHtml(message.aDRDesc)
                            }

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
                        TvTrackItem1.text = message.aDRDesc
                        TvTrackItem1.visibility = View.VISIBLE
                        TvTrackItem2.visibility = View.VISIBLE
                        TvTrackItem2.setTextColor(Color.parseColor("#FFFFFF"))
                        TvTrackItem1.setTextColor(Color.parseColor("#FFFFFF"))
                        TvTrackItem1.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(message.aDRDesc, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            Html.fromHtml(message.aDRDesc)
                        }

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
                            if(useconstants.orderStatusMain.equals("10")||useconstants.orderStatusMain.equals("16") || useconstants.orderStatusMain.equals("15")
                                ||useconstants.orderStatusMain.equals("9") ){
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


                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }


}






