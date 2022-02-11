package com.fidoo.user.ordermodule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fidoo.user.R
import com.fidoo.user.ordermodule.model.Change
import kotlinx.android.synthetic.main.review_items_list.view.*

class ReviewItemsAdapter(
	val context: Context,
	val payment_mode: String,
	private var changeItemList: ArrayList<Change>
) :
	RecyclerView.Adapter<ReviewItemsAdapter.UserViewHolder>() {

	var check: Int = 0

	class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.review_items_list, parent, false)
	)

	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

		holder.itemView.itemNameTxt.text =
			changeItemList[position].product_name + " " + changeItemList[position].weight + " " + changeItemList[position].unit
		holder.itemView.itemQuantityTxt.text =
			"Ordered : " + changeItemList[position].quantity_ordered + " | Available : " + changeItemList[position].quantity_avail
		holder.itemView.itemQuantityPriceTxt.text =
			context.resources.getString(R.string.ruppee) + " " + changeItemList[position].price

		holder.itemView.opening_timetxt.text =
			context.resources.getString(R.string.ruppee) + " " + changeItemList[position].refund_amount

		//here check payment mode functionality
		if (changeItemList[position].is_out_of_stock.equals("1")) {
			holder.itemView.availableItemImg.setImageResource(R.drawable.circle_vector_img)
		} else {
			holder.itemView.availableItemImg.setImageResource(R.drawable.darkgrey_circle)
		}

		if (payment_mode.equals("online")) {
			holder.itemView.refundRelativelay.visibility=View.VISIBLE
		} else {
			holder.itemView.refundRelativelay.visibility=View.GONE
		}

		if (changeItemList.size - 1 == position) {
			holder.itemView.Space_bottomLl.visibility = View.VISIBLE
		} else {
			holder.itemView.Space_bottomLl.visibility = View.GONE
		}
	}

	override fun getItemCount() = changeItemList.size
}