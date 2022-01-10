package com.fidoo.user.newRestaurants.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.newRestaurants.model.Subcategory
import com.fidoo.user.restaurants.listener.AdapterCartAddRemoveClick
import kotlinx.android.synthetic.main.new_store_details_item.view.*

class NewStoreDetailsItemAdapter(
	var context: Context,
	var subcategory: ArrayList<Subcategory>,
	var adapterClick: AdapterClick,
	var fssai: String,
	var restaurantName: String,
	var service_id: String,
	var restaurantAddress: String,
	var adapterAddRemoveClick: AdapterAddRemoveClick,
	var adapterCartAddRemoveClick: AdapterCartAddRemoveClick,
	var total_item_count: Int,
	private val storeID: String
) : RecyclerView.Adapter<NewStoreDetailsItemAdapter.ViewHolder>() {
	var newStoreItemsAdapter: NewStoreItemsAdapter? = null

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.new_store_details_item, parent, false)
	)

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {

		if (position == 0) {
			holder.itemView.category_nameheaderTxt.visibility = View.GONE
		} else {
			holder.itemView.category_nameheaderTxt.visibility = View.VISIBLE
		}

		holder.itemView.category_nameheaderTxt.text = subcategory[position].subcategory_name

		newStoreItemsAdapter = NewStoreItemsAdapter(
			context,
			adapterClick,
			subcategory[position].product as ArrayList,
			fssai,
			restaurantName,
			service_id,
			restaurantAddress,
			adapterAddRemoveClick, adapterCartAddRemoveClick, total_item_count, storeID
		)
		holder.itemView.recyclerviewPrdList.adapter = newStoreItemsAdapter

	}

	override fun getItemCount() = subcategory.size

	fun updateData(listData_: ArrayList<Subcategory>, total_item: Int) {
		subcategory = java.util.ArrayList<Subcategory>()
		subcategory.addAll(listData_)
		total_item_count = total_item
		notifyDataSetChanged()
	}

}