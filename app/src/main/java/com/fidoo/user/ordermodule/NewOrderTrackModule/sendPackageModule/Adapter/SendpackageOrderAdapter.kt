package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.constants.useconstants
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.Message
import com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel.sendPackageModel
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.android.synthetic.main.orders_item_list.view.*
import kotlinx.android.synthetic.main.sendpackageitem.view.*

class sendpackageOrderAdapter (
    var con: Context,var arraylist: List<Message>) : RecyclerView.Adapter<sendpackageOrderAdapter.UserViewHolder>() {

        class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sendpackageitem, parent, false)
        )

        override fun getItemCount()  = arraylist.size

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val index = arraylist[position]

            var url_icon= index.iconImg
            GlideToVectorYou.init().with(con).load(Uri.parse(url_icon), holder.itemView.imageView4)

            try {

                if (useconstants.package_trackRiderName.equals("")){
                    holder.itemView.textView7.text = index.aDRMessage

                }else {
                    holder.itemView.textView7.text = replaceDriver_name(index.aDRMessage)
                }
                holder.itemView.textView8.text=index.aDRDesc

                if (index.status==0){
                    holder.itemView.textView8.visibility= View.GONE
                    holder.itemView.textView7.setTypeface(Typeface.DEFAULT_BOLD)
                    holder.itemView.textView7.setTextColor(Color.BLACK)
                }
                if (index.status==1){
                    holder.itemView.textView7.setTypeface(Typeface.DEFAULT_BOLD)
                    holder.itemView.textView8.setTextColor(Color.parseColor("#6E3F8D"))
                    holder.itemView.textView7.setTextColor(Color.parseColor("#6E3F8D"))
                    holder.itemView.textView8.visibility= View.VISIBLE
                }
                if (index.status==2){
                    holder.itemView.textView8.visibility= View.GONE
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    private fun replaceDriver_name(api_string:String):SpannableString{
        var newtext=useconstants.package_trackRiderName
        var size= newtext.length.toInt()
        var reqd= false

        var list: Array<String> = api_string.split(" ").toTypedArray()

            if (list[0].equals("Delivery")){
                reqd= true
                for (i in 2..list.size-1) {

                    newtext = newtext+" "+list[i]
                }
            }else{
                newtext= api_string
            }

        if (!reqd){
            size = 0
        }

        val spannableString = SpannableString(newtext)
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 0, size, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString

    }
    }