package com.fidoo.user.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.data.SliderItem
import kotlinx.android.synthetic.main.item_home_banner_adapter.view.*


class HomeBannerAdapter(
    val con: Context,
    var list: ArrayList<SliderItem>,
    var width: Int
) :
    RecyclerView.Adapter<HomeBannerAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_banner_adapter, parent, false)

    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

//        holder.itemView.cardView.layoutParams = ConstraintLayout.LayoutParams(
//            width,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )

        Glide.with(con)
            .load(list[position].imageUrl)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.img_banner)


    }

}