package com.fidoo.user.dashboard.adapter.newadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dashboard.model.newmodel.Curation
import kotlinx.android.synthetic.main.item_popular_curations.view.*


class PopularCurationsCatAdapter(
    val con: Context, val
    curationList: MutableList<Curation>,
    var itemClick: ItemClickService,
    var width: Int
) :
    RecyclerView.Adapter<PopularCurationsCatAdapter.UserViewHolder>() {
    var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_popular_curations, parent, false)
    )

    override fun getItemCount() = curationList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            width,
            width
        )
        //params.setMargins(5,0,5,0)
        holder.itemView.popular_curationImg.layoutParams = params

        try {
            var retStr = curationList.get(position).name.toLowerCase().substring(0, 1)
                .toUpperCase() + curationList.get(position).name.toLowerCase().substring(1)

            holder.itemView.popular_curationTxt.text = retStr
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Glide.with(con)
            .load(curationList[position].image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.popular_curationImg)

        holder.itemView.curationItemLl.setOnClickListener {
            itemClick.onItemClick(position, curationList.get(position))
        }

        if (curationList.size - 1 == position) {
            holder.itemView.itemCurationSpace_ll.visibility = View.VISIBLE
        } else {
            holder.itemView.itemCurationSpace_ll.visibility = View.GONE
        }
    }

    interface ItemClickService {
        fun onItemClick(pos: Int, model: Curation)
    }
}