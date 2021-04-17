package com.fidoo.user.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import kotlinx.android.synthetic.main.service_adapter.view.*

class ServiceAdapter(val con: Context, private val serviceList: MutableList<com.fidoo.user.data.model.HomeServicesModel.ServiceList>) : RecyclerView.Adapter<ServiceAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.service_adapter, parent, false)
    )
    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: ServiceAdapter.UserViewHolder, position: Int) {
        holder.catName.text= serviceList[position].serviceName

        Glide.with(con)
            .load(serviceList[position].image)
            .fitCenter()
            .into(holder.imageLay)

        /* holder.itemLay.setOnClickListener {


                con.startActivity(
                    Intent(con,
                    StoreListingActivity::class.java).putExtra("serviceId",serviceList.get(position).id)
                )
        }*/

        if(serviceList[position].serviceName.equals("Send Packages")) {
            holder.catName.text="Send Packages"

        }

        //TODO
//        holder.categoryLay.setOnClickListener {
//
//            if(serviceList[position].serviceName.equals("Send Packages")) {
//
//                if (SessionTwiclo(con).isLoggedIn){
//                    con.startActivity(Intent(con, SendPackageActivity::class.java))
//                }else{
//                    showLoginDialog("Please login to proceed")
//                }
//
//            } else {
//               /* con.startActivity(
//                    Intent(con, StoreListingActivity::class.java).putExtra(
//                        "serviceId",
//                        serviceList[position].id
//                    ).putExtra("serviceName", serviceList[position].serviceName)
//                )*/
//            }
//        }
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var categoryLay=view.categoryLay
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
        builder.setPositiveButton("Login") { _, _ ->
            con.startActivity(Intent(con, LoginActivity::class.java))


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


}