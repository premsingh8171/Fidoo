package com.fidoo.user.dashboard.adapter.newadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dashboard.model.newmodel.UpcomingServices
import kotlinx.android.synthetic.main.item_upcoming_service_ui.view.*


class UpComingServiceAdapter(
    val con: Context, val
    upcomingServices: MutableList<UpcomingServices>,
    var itemClick: ItemClickShop,
    var width: Int
) :
    RecyclerView.Adapter<UpComingServiceAdapter.UserViewHolder>() {
     var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_upcoming_service_ui, parent, false)
    )

    override fun getItemCount() = upcomingServices.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {


        Glide.with(con)
            .load(upcomingServices[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.upcoming_service_Img)

        holder.itemView.upcomingServiceCardL.setOnClickListener {
            itemClick.onItemClick(position,upcomingServices[position])
        }

        if (upcomingServices.size-1==position){
            holder.itemView.upcoming_service_Space_ll.visibility=View.VISIBLE
        }else{
            holder.itemView.upcoming_service_Space_ll.visibility=View.GONE
        }

    }

    interface ItemClickShop {
        fun onItemClick(pos: Int, model: UpcomingServices)
    }
}