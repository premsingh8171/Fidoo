package com.fidoo.user.dailyneed.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.dailyneed.model.Brand
import kotlinx.android.synthetic.main.item_brand_ui.view.*


class BrandAdapter(
    val con: Context, val
    brandsList: MutableList<Brand>,
    var itemClick: ItemClickBrand,
    var width: Int
) :
    RecyclerView.Adapter<BrandAdapter.UserViewHolder>() {
    var index = 0

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_brand_ui, parent, false)
    )

    override fun getItemCount() = brandsList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

//        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//            width,
//            width
//        )
//        params.setMargins(0, 15, 0, 25)
//        holder.itemView.hotspotItemClickll.layoutParams = params


        Glide.with(con)
            .load(brandsList[position].brand_image)
            .fitCenter()
            .placeholder(R.drawable.default_item)
            .error(R.drawable.default_item)
            .into(holder.itemView.brand_itemImg)

        holder.itemView.brand_itemImg.setOnClickListener {
            itemClick.onItemClick(position, brandsList[position])
        }

    }

    interface ItemClickBrand {
        fun onItemClick(pos: Int, model: Brand)
    }
}