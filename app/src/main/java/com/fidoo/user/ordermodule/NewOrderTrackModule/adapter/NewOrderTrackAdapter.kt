package com.fidoo.user.ordermodule.ui.NewOrderTrackModule.adapter

import android.R.attr.data
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.Message
import com.fidoo.user.ordermodule.NewOrderTrackModule.adapter.NewOrderTrackViewHolder
import com.fidoo.user.ordermodule.NewOrderTrackModule.adapter.viewmodel
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.ui.NewTrackOrderActivity.Companion.call_Diolog
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.ui.NewTrackOrderActivity.Companion.store_phone
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.newordertrackitem.view.*


class NewOrderTrackAdapter(val context: Context, var msgdatalist:List<Message>) : RecyclerView.Adapter<NewOrderTrackViewHolder>() {
    var driverMobileNo: String? = ""
    //  lateinit var baseActivity : BaseActivity

    val sessionInstance: SessionTwiclo

    get() = SessionTwiclo(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrderTrackViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.newordertrackitem,parent,false)
        return NewOrderTrackViewHolder(view , context)
    }

    override fun onBindViewHolder(holder: NewOrderTrackViewHolder, position: Int) {
        val model=msgdatalist[position]
        holder.setdata(model)

        Log.d("fsdbdsdsjf", store_phone+"--"+"--"+SessionTwiclo(context).loggedInUserDetail.accountId)
       holder.itemView.TvTrackItem2.setOnClickListener {

           if (!store_phone.equals("")) {
                onCallPopUp(0)
                if (sessionInstance.profileDetail != null) {
                    viewmodel?.customerCallMerchantApi(
                        SessionTwiclo(context).loggedInUserDetail.accountId,
                        SessionTwiclo(context).loggedInUserDetail.accessToken,
                        sessionInstance.profileDetail.account.userName,
                        store_phone!!
                    )
                    store_phone?.let { it1 -> Log.d("calljgjgjgjg" , it1) }
                } else {
                    viewmodel?.customerCallMerchantApi(
                        SessionTwiclo(context).loggedInUserDetail.accountId,
                        SessionTwiclo(context).loggedInUserDetail.accessToken,
                        sessionInstance.loginDetail.phoneNumber,
                        store_phone!!

                    )
                    Log.d("sddffsddsds", Gson().toJson(store_phone!!))
                }
            }


           holder.itemView.TvTrackItem2.setOnClickListener {

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

    override fun getItemCount(): Int {
        return  msgdatalist.size
    }

    fun updateData(updateList: Any) {
        msgdatalist = java.util.ArrayList<Message>()

        notifyDataSetChanged()
    }

}