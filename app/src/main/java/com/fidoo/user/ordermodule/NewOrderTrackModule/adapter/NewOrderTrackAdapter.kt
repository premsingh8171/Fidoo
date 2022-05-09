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
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterImageClick
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.Message
import com.fidoo.user.ordermodule.NewOrderTrackModule.adapter.NewOrderTrackViewHolder
import com.fidoo.user.ordermodule.NewOrderTrackModule.adapter.viewmodel
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.ui.NewTrackOrderActivity.Companion.call_Diolog
import com.fidoo.user.ordermodule.ui.NewOrderTrackModule.ui.NewTrackOrderActivity.Companion.store_phone
import com.fidoo.user.profile.model.EditProfileModel
import com.fidoo.user.utils.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.newordertrackitem.view.*


class NewOrderTrackAdapter(val context: Context, var msgdatalist:List<Message> , val adapterCallClick: AdapterImageClick) : RecyclerView.Adapter<NewOrderTrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrderTrackViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.newordertrackitem,parent,false)
        return NewOrderTrackViewHolder(view , context)
    }

    override fun onBindViewHolder(holder: NewOrderTrackViewHolder, position: Int) {
        val model=msgdatalist[position]
        holder.setdata(model)

        holder.itemView.TvTrackItem2.setOnClickListener {
            adapterCallClick.onSelectedImageClick(position)
            // con.startActivity(Intent(con,SingleProductActivity::class.java))
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