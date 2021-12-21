package com.fidoo.user.dashboard.adapter.newadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dashboard.model.newmodel.Service
import kotlinx.android.synthetic.main.item_cat_new_ui.view.*
import kotlinx.android.synthetic.main.item_cat_new_ui.view.cat_itemImg
import kotlinx.android.synthetic.main.item_cat_new_ui.view.cat_itemTxt
import kotlinx.android.synthetic.main.item_cat_new_ui.view.linear_layCat_newUi


class ServiceCategoryAdapter(
    val con: Context, val
    serviceList: MutableList<Service>,
    var itemClick: ItemClickService,
    var width: Int
) :
    RecyclerView.Adapter<ServiceCategoryAdapter.UserViewHolder>() {
     var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_cat_new_ui, parent, false)
    )

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

       // Log.d("im___",serviceList[position].image)
        val params2: ConstraintLayout.LayoutParams= ConstraintLayout.LayoutParams(
            width,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params2.leftToLeft=0
        params2.rightToRight=0
        if (position<1) {
            params2.setMargins(25, 0, 25, 0)
            holder.itemView.cons_ll.layoutParams = params2
        }else{
            params2.setMargins(25, 0, 25, 0)
            holder.itemView.cons_ll.layoutParams = params2
        }

        val params: LinearLayout.LayoutParams= LinearLayout.LayoutParams(
            width,
            width
        )
       // params.setMargins(5,0,5,0)
        holder.itemView.cat_itemImg.layoutParams=params

            holder.itemView.cat_itemTxt.text=serviceList.get(position).service_name
            Glide.with(con)
                .load(serviceList[position].image)
                .fitCenter()
                .placeholder(R.drawable.default_item)
                .error(R.drawable.default_item)
                .into(holder.itemView.cat_itemImg)

        holder.itemView.linear_layCat_newUi.setOnClickListener {
            itemClick.onItemClick(2+position,serviceList.get(position))
        }

    }

    interface ItemClickService {
        fun onItemClick(pos: Int, serviceList: Service)
    }
}