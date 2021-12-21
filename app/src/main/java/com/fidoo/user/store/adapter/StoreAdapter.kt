package com.fidoo.user.store.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.grocerynewui.activity.GroceryNewUiActivity
import com.fidoo.user.restaurants.activity.StoreItemsActivity
import com.fidoo.user.store.model.StoreListingModel
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.fav_store_item.view.*
import java.text.ParseException
import java.util.*

class StoreAdapter(
	val context: Context,
	private var storeList: ArrayList<StoreListingModel.StoreList>
) : RecyclerView.Adapter<StoreAdapter.UserViewHolder>() {

	var check: Int = 0
	private var isMore = false

	class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

		var storeName: TextView? = view.storeName
		var storeImg = view.productImg
		var location = view.tv_location
		var tv_locality = view.tv_locality

		//var ratingTxt = view.ratingTxt
		var deliveryTimeTxt: TextView? = view.tv_deliveryTime
		var star_ratingtxt: TextView? = view.star_ratingtxt
		var mainLay: LinearLayout? = view.store_lay
		var storeSpace_bottomLl: LinearLayout? = view.storeSpace_bottomLl
		var progressRl: RelativeLayout? = view.progressRl
		var onOffText: TextView? = view.open_close_status

		//var comingSoon = view.coming_soon_text
		var closingTimeText = view.closing_time
		var cuisine: TextView? = view.cuisine_types
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(
		LayoutInflater
			.from(parent.context)
			.inflate(R.layout.fav_store_item, parent, false)
	)

	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
		if (!storeList[position].cuisines.isNullOrEmpty()) {
			holder.cuisine?.text = storeList[position].cuisines.joinToString(separator = ", ")
			holder.cuisine?.visibility = View.VISIBLE
		} else {
			holder.cuisine?.visibility = View.GONE
			holder.cuisine?.text = ""
		}
		var coupanStr: String? = ""

		try {
			if (storeList[position].couponsAvailable.size > 0) {
				for (i in storeList[position].couponsAvailable.indices) {

					holder.itemView.discount_offer_txt.text =
						storeList[position].couponsAvailable[i].discount.toString() + "% OFF"
					holder.itemView.offertxt_.text =
						storeList[position].couponsAvailable[i].couponDesc.toString()
					holder.itemView.v1.visibility = View.VISIBLE
					holder.itemView.coupan_offerll.visibility = View.VISIBLE
					holder.itemView.discount_offer_Frm.visibility = View.VISIBLE

				}
			} else {
				holder.itemView.discount_offer_txt.text = ""
				holder.itemView.discount_offer_Frm.visibility = View.GONE
				holder.itemView.coupan_offerll.visibility = View.GONE
				holder.itemView.v1.visibility = View.GONE

			}
		} catch (e: Exception) { }

		var date: Date? = null

		try {
			if (storeList[position].storeClosingTime.equals("")) {
				holder.closingTimeText.visibility = View.GONE
				holder.closingTimeText.text = " "
			} else {
				holder.closingTimeText.text = storeList[position].storeClosingTime
				holder.closingTimeText?.setTextColor(Color.rgb(240, 0, 0))
				holder.closingTimeText.visibility = View.VISIBLE
			}

			holder.itemView.opening_timetxt.text = storeList[position].storeOpeningTime

		} catch (e: ParseException) {
			e.printStackTrace()
			holder.closingTimeText.visibility = View.GONE
			holder.closingTimeText.text = " "
		}

		var retStr = storeList[position].name.toLowerCase().substring(0, 1).toUpperCase() + storeList[position].name.toLowerCase().substring(1)
		holder.storeName?.text = retStr

		if (storeList[position].rating.toString() == "") {
			//holder.ratingTxt.text = "--"
		} else {
			//holder.ratingTxt.text = storeList[position].rating
		}


		if (storeList[position].status.equals("1")) {
			if (storeList[position].open_close_status.equals("1")) {
				holder.onOffText?.text = "Open"
				holder.onOffText?.setTextColor(Color.rgb(51, 147, 71))
				holder.storeName?.setTextColor(Color.parseColor("#000000"))
				holder.itemView.storeName.alpha = 1f
				holder.itemView.productImg.alpha = 1f
				holder.itemView.timeStore.alpha = 1f
				holder.itemView.distance_storeImg.alpha = 1f
				holder.itemView.tv_deliveryTime.alpha = 1f
				holder.itemView.star_ratingtxt.alpha = 1f
				holder.itemView.star_ratingimg.alpha = 1f
				holder.itemView.offer_img.alpha = 1f
				holder.itemView.offertxt_.alpha = 1f
				holder.itemView.tv_location.alpha = 1f
				holder.itemView.tv_locality.alpha = 1f
				holder.itemView.closing_time.alpha = 1f
				holder.itemView.cuisine_types.alpha = 1f
				holder.itemView.discount_offer_Frm.alpha = 1f
				holder.itemView.timeStore.setColorFilter(R.color.black)
				holder.itemView.distance_storeImg.setColorFilter(R.color.black)
				// holder.itemView.store_lay.alpha=1f
				//  holder.itemView.store_lay.setBackgroundResource(R.color.white)
				holder.itemView.productFram.visibility = View.GONE
				holder.itemView.opening_timetxt.visibility = View.GONE
				holder.itemView.unreachableServiceTxt.visibility = View.GONE

			} else if (storeList[position].open_close_status.equals("0")) {
				holder.onOffText?.text = "Offline"
				holder.onOffText?.setTextColor(Color.rgb(240, 0, 0))
				holder.storeName?.setTextColor(Color.parseColor("#818181"))
				holder.itemView.productImg.alpha = 0.2f
				holder.itemView.storeName.alpha = 0.3f
				holder.itemView.timeStore.alpha = 0.3f
				holder.itemView.distance_storeImg.alpha = 0.3f
				holder.itemView.tv_deliveryTime.alpha = 0.3f
				holder.itemView.star_ratingtxt.alpha = 0.3f
				holder.itemView.star_ratingimg.alpha = 0.3f
				holder.itemView.offertxt_.alpha = 0.3f
				holder.itemView.offer_img.alpha = 0.3f
				holder.itemView.tv_location.alpha = 0.3f
				holder.itemView.tv_locality.alpha = 0.3f
				holder.itemView.closing_time.alpha = 0.3f
				holder.itemView.cuisine_types.alpha = 0.3f
				holder.itemView.discount_offer_Frm.alpha = 0.3f
				holder.itemView.timeStore.setColorFilter(R.color.background)
				holder.itemView.distance_storeImg.setColorFilter(R.color.background)
				holder.itemView.opening_timetxt.visibility = View.VISIBLE

				if (check == 0) {
					holder.itemView.unreachableServiceTxt.visibility = View.VISIBLE
					check = -1
				} else {
					holder.itemView.unreachableServiceTxt.visibility = View.GONE
				}
			}

		} else if (storeList[position].status.equals("2")) {
			holder.onOffText?.setTextColor(Color.rgb(245, 195, 48))
			holder.onOffText?.text = "Coming Soon"
			holder.storeName?.setTextColor(Color.parseColor("#818181"))
			holder.itemView.productImg.alpha = 0.2f
			holder.itemView.storeName.alpha = 0.3f
			holder.itemView.timeStore.alpha = 0.3f
			holder.itemView.distance_storeImg.alpha = 0.3f
			holder.itemView.tv_deliveryTime.alpha = 0.3f
			holder.itemView.star_ratingtxt.alpha = 0.3f
			holder.itemView.star_ratingimg.alpha = 0.3f
			holder.itemView.offer_img.alpha = 0.3f
			holder.itemView.offertxt_.alpha = 0.3f
			holder.itemView.tv_location.alpha = 0.3f
			holder.itemView.tv_locality.alpha = 0.3f
			holder.itemView.closing_time.alpha = 0.3f
			holder.itemView.cuisine_types.alpha = 0.3f
			holder.itemView.discount_offer_Frm.alpha = 0.3f
			holder.itemView.timeStore.setColorFilter(R.color.background)
			holder.itemView.distance_storeImg.setColorFilter(R.color.background)
			holder.itemView.opening_timetxt.visibility = View.VISIBLE

			if (check == 0) {
				holder.itemView.unreachableServiceTxt.visibility = View.VISIBLE
				check = -1
			} else {
				holder.itemView.unreachableServiceTxt.visibility = View.GONE
			}

		} else {
			holder.storeName?.setTextColor(Color.parseColor("#000000"))
		}

		holder.star_ratingtxt?.text = storeList[position].rating.toString()
		holder.deliveryTimeTxt?.text = storeList[position].delivery_time.toString() + " minutes"
		holder. location?.text = " " + storeList[position].distance.toString() + "km"
		holder.tv_locality?.text = storeList[position].locality.toString() + " |"


		Glide.with(context)
			.load(storeList[position].image)
			.fitCenter()
			.placeholder(R.drawable.default_store)
			.error(R.drawable.default_store)
			.into(holder.storeImg)


		holder.mainLay?.setOnClickListener {

			if (storeList[position].status.equals("1")) {
				if (storeList[position].open_close_status.equals("1")) {
					try {
						if (storeList[position].couponsAvailable.size > 0) {
							coupanStr =
								storeList[position].couponsAvailable[0].couponDesc.toString()
						} else {
							coupanStr = ""
						}
					} catch (e: Exception) {
					}

					if (storeList[position].has_product_categories.equals("1")) {
						// AppUtils.startActivityRightToLeft(context as Activity?, Intent(context, GroceryItemsActivity::class.java)
						AppUtils.startActivityRightToLeft(
							context as Activity?, Intent(context, GroceryNewUiActivity::class.java)
								.putExtra("storeId", storeList[position].id)
								.putExtra("store_name", storeList.get(position).name)
								.putExtra("store_location", storeList[position].locality)
								.putExtra(
									"delivery_time",
									storeList[position].delivery_time.toString()
								)
								.putExtra(
									"cuisine_types",
									storeList[position].cuisines.joinToString(separator = ", ")
								)
								.putExtra("coupon_desc", coupanStr)
								.putExtra("distance", storeList[position].distance.toString())
						)
					} else {
						AppUtils.startActivityRightToLeft(
							context as Activity?, Intent(context, StoreItemsActivity::class.java)
								.putExtra("storeId", storeList[position].id)
								.putExtra("storeName", storeList[position].name)
								.putExtra("store_location", storeList[position].locality)
								.putExtra(
									"delivery_time",
									storeList[position].delivery_time.toString()
								)
								.putExtra(
									"cuisine_types",
									storeList[position].cuisines.joinToString(separator = ", ")
								)
								.putExtra("coupon_desc", coupanStr)
								.putExtra("distance", storeList[position].distance.toString())
						)
					}
				} else if (storeList[position].open_close_status.equals("0")) {
					Toast.makeText(context, "Currently store is offline", Toast.LENGTH_SHORT).show()
				}
			} else {
				Toast.makeText(context, "This store is  coming soon", Toast.LENGTH_SHORT).show()
			}
		}

		if (storeList.size - 1 == position) {
			holder.storeSpace_bottomLl!!.visibility = View.VISIBLE
			if (isMore) {
				holder.progressRl!!.visibility = View.VISIBLE
			} else {
				holder.progressRl!!.visibility = View.GONE
			}
		} else {
			holder.storeSpace_bottomLl!!.visibility = View.GONE
			holder.progressRl!!.visibility = View.GONE
		}

	}

	override fun getItemCount() = storeList.size

	fun setFilter(listData_: List<StoreListingModel.StoreList>) {
		storeList = java.util.ArrayList<StoreListingModel.StoreList>()
		storeList.addAll(listData_)
		notifyDataSetChanged()
	}

	fun updateData(listData_: List<StoreListingModel.StoreList>, isMore1: Boolean) {
		storeList = java.util.ArrayList<StoreListingModel.StoreList>()
		storeList.addAll(listData_)
		isMore = isMore1
		notifyDataSetChanged()
	}

}