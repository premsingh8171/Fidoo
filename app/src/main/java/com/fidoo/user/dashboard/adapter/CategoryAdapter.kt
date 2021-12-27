package com.fidoo.user.dashboard.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.sendpackages.activity.SendPackageActivity
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.dashboard.model.HomeServicesModel
import com.fidoo.user.data.session.SessionTwiclo
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.category_adapter.view.*


class CategoryAdapter(
    val con: Context, val
    serviceList: MutableList<HomeServicesModel.ServiceList>,
    var itemClick: ItemClick,
    var width: Int
) :
    RecyclerView.Adapter<CategoryAdapter.UserViewHolder>() {
     var valueselected = 0
     var index = 0


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.category_adapter, parent, false)
    )

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.catName.text=serviceList.get(position).serviceName

        val params:FrameLayout.LayoutParams= FrameLayout.LayoutParams(
            width,
            width
        )
        params.setMargins(5,5,5,5)
        holder.itemView.imageLay.layoutParams=params

        //  index = valueselected
        Glide.with(con)
            .load(serviceList[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.imageLay)

        if(serviceList[position].serviceName.equals("Send Packages")) {
            holder.catName.text="Send Packages"
        }

        holder.itemLay.setOnClickListener {

            if(serviceList[position].serviceName.equals("Send Packages")) {

                if (SessionTwiclo(con).isLoggedIn){
                    //con.sendBroadcast(Intent("start_send_package_fragment"))
                   // con.startActivity(Intent(con, SendPackageActivity::class.java))
                    AppUtils.startActivityRightToLeft(con as Activity?, Intent(con, SendPackageActivity::class.java));

                }else{
                    showLoginDialog("Please login to proceed")
                }

            } else {
                index=position
                notifyDataSetChanged()
                AppUtils.startActivityRightToLeft(con as Activity?,  Intent(con, StoreListActivity::class.java).putExtra(
                    "serviceId", serviceList[position].id
                ).putExtra("serviceName", serviceList[position].serviceName))

            }
        }

//        if (index == position) {
//            //holder.itemView.itemLay.setBackgroundResource(R.drawable.round_box)
//            holder.itemView.itemLay.setBackgroundResource(R.drawable.rectangle_shap)
//           // holder.itemView.imageLay.setColorFilter(con.getResources().getColor(R.color.white));
//            itemClick.onItemClick(position,serviceList[position])
//        } else {
//            holder.itemView.itemLay.setBackgroundResource(R.drawable.round_box)
//           // holder.itemView.imageLay.setColorFilter(con.getResources().getColor(R.color.primary_color));
//
//        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemLay=view.itemLay
        var imageLay=view.imageLay
        var catName=view.catName
    }

    private fun showLoginDialog(message: String){
        val builder = androidx.appcompat.app.AlertDialog.Builder(con)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(message)
        // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Login") { dialogInterface, which ->
            con.startActivity(Intent(con, AuthActivity::class.java))
        }

        //performing negative action
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        // Create the AlertDialog
        val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    fun updateReceiptsList(value: Int) {
        valueselected = value
        Log.d("valueselected__", java.lang.String.valueOf(valueselected))
        notifyDataSetChanged()
    }

    interface ItemClick {
        fun onItemClick(pos: Int, serviceList: HomeServicesModel.ServiceList)
    }
}