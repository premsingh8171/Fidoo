package com.fidoo.user.newRestaurants.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.newRestaurants.listener.NewAdapterCartAddRemoveClick
import com.fidoo.user.newRestaurants.model.Product
import com.fidoo.user.newRestaurants.model.Subcategory
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
	var adapterCartAddRemoveClick: NewAdapterCartAddRemoveClick,
	var next_page_: Int,
	private val storeID: String,
	private val updateSubcategory: UpdateSubcategory
) : RecyclerView.Adapter<NewStoreDetailsItemAdapter.ViewHolder>() {
	var newStoreItemsAdapter: NewStoreItemsAdapter? = null

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.new_store_details_item, parent, false)
	)

	override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

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
			adapterAddRemoveClick,
			adapterCartAddRemoveClick,
			next_page_,
			storeID,
			object : NewStoreItemsAdapter.UpdatePrd {
				override fun onClickUpdate(
					listData_: Product,
					pos: Int,
					prdQuantity: Int,
					type: String
				) {
					if (type.equals("Customized")) {
						//subcategory[position].product[pos].cart_quantity = prdQuantity
						newStoreItemsAdapter?.notifyDataSetChanged()
						updateSubcategory.onClickUpdate(
							subcategory[position],
							position,
							pos,
							prdQuantity,
							"Customized"
						)

					} else {
						subcategory[position].product[pos].cart_quantity = prdQuantity
						newStoreItemsAdapter?.notifyDataSetChanged()
						updateSubcategory.onClickUpdate(
							subcategory[position],
							position,
							pos,
							prdQuantity,
							"normal"
						)


					}

				}
			}
		)

		holder.itemView.recyclerviewPrdList.adapter = newStoreItemsAdapter

		if (next_page_ == 0) {
			holder.itemView.progressRlPrd.visibility = View.GONE
			holder.itemView.store_bottom_ll.visibility = View.GONE

			if (subcategory.size - 1 == position) {
				holder.itemView.progressRlPrd.visibility = View.VISIBLE
			} else {
				holder.itemView.progressRlPrd.visibility = View.GONE
			}
		} else {
			holder.itemView.progressRlPrd.visibility = View.GONE
			if (subcategory.size - 1 == position) {
				holder.itemView.fssaitxt.text = fssai
				holder.itemView.restaurant_nametxt.text = restaurantName
				holder.itemView.restaurant_addtxt.text = restaurantAddress
				holder.itemView.store_bottom_ll.visibility = View.VISIBLE
			} else {
				holder.itemView.store_bottom_ll.visibility = View.GONE
			}
		}
	}

	override fun getItemCount() = subcategory.size

	interface UpdateSubcategory {
		fun onClickUpdate(
			subcategory: Subcategory,
			mainPosition: Int,
			prdPosition: Int,
			prdQuantity: Int,
			type: String
		)
	}

	fun updateData(listData_: ArrayList<Subcategory>, next_page: Int) {
		subcategory = java.util.ArrayList<Subcategory>()
		subcategory.addAll(listData_)
		next_page_ = next_page
		notifyDataSetChanged()
	}

}