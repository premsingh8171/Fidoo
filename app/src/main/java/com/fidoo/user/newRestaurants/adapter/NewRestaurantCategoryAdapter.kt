package com.fidoo.user.newRestaurants.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.grocery.model.getGroceryProducts.Category
import com.fidoo.user.newRestaurants.model.Subcategory
import com.fidoo.user.restaurants.model.StoreDetailsModel
import kotlinx.android.synthetic.main.grocery_cat_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.*
import kotlinx.android.synthetic.main.grocery_sub_cat_item_layout.view.grocery_sub_tv

class NewRestaurantCategoryAdapter(
	var context: Context,
	var list: ArrayList<Subcategory>,
	var active_or_not: Int = -1,
	var subategoryItemClick: SubategoryItemClick
) : RecyclerView.Adapter<NewRestaurantCategoryAdapter.ViewHolder>() {
	var index: Int = -1

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(
			LayoutInflater.from(context).inflate(R.layout.grocery_cat_item_layout, parent, false)
		)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.itemView.cat_tv.text = list.get(position).subcategory_name
		holder.itemView.countTxt.text = "( "+list[position].product.size.toString()+" )";
		holder.itemView.category_constL.setOnClickListener {
			active_or_not = position
			subategoryItemClick.onItemClick(position, list.get(position))
			notifyDataSetChanged()
		}

		if (active_or_not == position) {
			holder.itemView.cat_tv.setTextColor(Color.parseColor("#339347"))
			holder.itemView.countTxt.setTextColor(Color.parseColor("#339347"))
		} else {
			holder.itemView.cat_tv.setTextColor(Color.parseColor("#000000"))
			holder.itemView.countTxt.setTextColor(Color.parseColor("#000000"))
		}
	}

	override fun getItemCount(): Int {
		return list.size
	}

	interface SubategoryItemClick {
		fun onItemClick(pos: Int, subcategory: Subcategory)
	}

}