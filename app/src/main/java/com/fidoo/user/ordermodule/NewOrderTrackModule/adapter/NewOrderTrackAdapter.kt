package com.fidoo.user.ordermodule.ui.NewOrderTrackModule.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterImageClick
import com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel.Message
import com.fidoo.user.ordermodule.NewOrderTrackModule.adapter.NewOrderTrackViewHolder
import kotlinx.android.synthetic.main.activity_track_order_new.*
import kotlinx.android.synthetic.main.activity_track_order_new.view.*
import kotlinx.android.synthetic.main.newordertrackitem.view.*
import kotlin.math.roundToInt


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