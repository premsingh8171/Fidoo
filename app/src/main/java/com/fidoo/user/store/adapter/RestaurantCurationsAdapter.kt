package com.fidoo.user.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.store.model.StoreListingModel
import kotlinx.android.synthetic.main.item_reataurant_curations.view.*


class RestaurantCurationsAdapter(
	val con: Context,
	val curationList: ArrayList<StoreListingModel.Curation>,
	var itemClick: ItemClickService,
	var width: Int
) :
	RecyclerView.Adapter<RestaurantCurationsAdapter.UserViewHolder>() {
	var index = 0

	class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

	override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
		LayoutInflater.from(parent.context)
			.inflate(R.layout.item_reataurant_curations, parent, false)
	)

	override fun getItemCount() = curationList.size

	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
		val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
			width,
			width
		)
		// params.setMargins(5,0,5,0)
		holder.itemView.restaurant_curationImg.layoutParams = params

		var retStr = curationList.get(position).cusineName.toLowerCase().substring(0, 1)
			.toUpperCase() + curationList.get(position).cusineName.toLowerCase().substring(1)

		holder.itemView.restaurant_curationTxt.text = retStr

		Glide.with(con)
			.load(curationList[position].image)
			.fitCenter()
			.placeholder(R.drawable.default_item)
			.error(R.drawable.default_item)
			.into(holder.itemView.restaurant_curationImg)

		holder.itemView.ResCurationItemLl.setOnClickListener {
			itemClick.onItemClick(position, curationList.get(position))
		}

	}

	interface ItemClickService {
		fun onItemClick(pos: Int, model: StoreListingModel.Curation)
	}
}