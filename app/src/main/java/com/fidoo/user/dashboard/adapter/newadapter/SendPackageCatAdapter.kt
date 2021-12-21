package com.fidoo.user.dashboard.adapter.newadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dashboard.model.newmodel.PackageCategory
import kotlinx.android.synthetic.main.item_send_package_cat_new_ui.view.*


class SendPackageCatAdapter(
    val con: Context, val
    serviceList: MutableList<PackageCategory>,
    var itemClick: ItemClickService,
    var width: Int
) :
    RecyclerView.Adapter<SendPackageCatAdapter.UserViewHolder>() {
     var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_send_package_cat_new_ui, parent, false)
    )

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val params2: FrameLayout.LayoutParams= FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params2.setMargins(10,5,10,5)
        holder.itemView.cat_mainUi.layoutParams=params2

        val params: ConstraintLayout.LayoutParams= ConstraintLayout.LayoutParams(
            170,
            170
        )
        params.setMargins(20,0,5,0)
        holder.itemView.linear_layCat_newUi.layoutParams=params

            holder.itemView.cat_itemTxt.text=serviceList.get(position).name
            Glide.with(con)
                .load(serviceList[position].image)
                .fitCenter()
                .placeholder(R.drawable.default_item)
                .error(R.drawable.default_item)
                .into(holder.itemView.cat_itemImg)

    }

    interface ItemClickService {
        fun onItemClick(pos: Int, packageList: PackageCategory)
    }
}