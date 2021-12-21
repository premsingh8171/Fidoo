package com.fidoo.user.dailyneed.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.dailyneed.model.Product
import com.fidoo.user.dailyneed.onclicklistener.ItemOnClickListener
import com.fidoo.user.data.model.AddCartInputModel
import com.fidoo.user.data.model.TempProductListModel
import com.fidoo.user.data.session.SessionTwiclo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.grocery_new_item_layout.view.*
import kotlinx.android.synthetic.main.item_dairy_n_bakery.view.*
import kotlinx.android.synthetic.main.item_dairy_n_bakery.view.tv_unit


class BreakfastCerealsAdapter(
	val con: Context, val
	productList: MutableList<Product>,
	var itemClick: ItemOnClickListener,
	var width: Int
) :
	RecyclerView.Adapter<BreakfastCerealsAdapter.UserViewHolder>() {
	var index = 0
	var count = 0

	class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

	override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.item_dairy_n_bakery, parent, false)
	)

	override fun getItemCount() = productList.size

	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

//        val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
//            width,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        params.setMargins(25, 15, 25, 5)
//        holder.itemView.item_carDPrd.layoutParams = params

		holder.itemView.item_tv_breakFast.text = productList[position].product_name
		holder.itemView.tv_unit.text =
			productList[position].weight + " " + productList[position].unit.toString()


		if (productList[position].price.equals(productList[position].offer_price)){
			holder.itemView.tv_price_breakFast.text = "₹ " + productList[position].price
			holder.itemView.actual_price_breakFast.text = ""
		}else{
			holder.itemView.tv_price_breakFast.text = "₹ " + productList[position].price
			holder.itemView.actual_price_breakFast.text = "₹ " + productList[position].offer_price
		}
		holder.itemView.actual_price_breakFast.paintFlags =
			holder.itemView.actual_price_breakFast.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

		//  holder.itemView.qua_txt_breakFast.text = productList[position].category_name

		Glide.with(con)
			.load(productList[position].product_image)
			.fitCenter()
			.placeholder(R.drawable.default_item)
			.error(R.drawable.default_item)
			.into(holder.itemView.breakFast_item_img)

		if (productList[position].cart_quantity.equals("0")) {
			holder.itemView.add_itemll_breakFast.visibility = View.VISIBLE
			holder.itemView.minusplus_ll_breakFast.visibility = View.GONE
		} else {
			holder.itemView.add_itemll_breakFast.visibility = View.GONE
			holder.itemView.minusplus_ll_breakFast.visibility = View.VISIBLE

			val tempProductListModel = TempProductListModel()
			tempProductListModel.productId = productList[position].product_id
			tempProductListModel.price = productList[position].offer_price
			tempProductListModel.quantity = productList[position].cart_quantity.toString()
			MainActivity.tempProductList!!.add(tempProductListModel)
			val customIdsList: ArrayList<String>?

			customIdsList = ArrayList()
			val addCartInputModel = AddCartInputModel()
			addCartInputModel.productId = productList[position].product_id
			addCartInputModel.quantity = productList[position].cart_quantity
			addCartInputModel.message = "add product"
			addCartInputModel.customizeSubCatId = customIdsList
			addCartInputModel.isCustomize = "0"
			MainActivity.addCartTempList!!.add(addCartInputModel)
			Log.e("llll", Gson().toJson(MainActivity.tempProductList))

			count = productList[position].cart_quantity.toInt()
			holder.itemView.qua_txt_breakFast.text = count.toString()

		}

		//add to cart first time
		holder.itemView.add_itemll_breakFast.setOnClickListener {
			count = productList[position].cart_quantity.toInt()

			if (SessionTwiclo(con).isLoggedIn) {
				count++
				holder.itemView.add_itemll_breakFast.visibility = View.GONE
				holder.itemView.minusplus_ll_breakFast.visibility = View.VISIBLE
				holder.itemView.qua_txt_breakFast.text = count.toString()
				if (
					(SessionTwiclo(con).storeId.equals(productList[position].store_id)||SessionTwiclo(con).storeId.equals(""))

//					(SessionTwiclo(con).serviceId.equals(productList[position].service_id) || SessionTwiclo(
//						con
//					).serviceId.equals(""))
				) {

					itemClick.addtoCart(
						1,
						productList[position],
						position,
						productList[position].store_id,
						count.toString(),
						"add"
					)
					SessionTwiclo(con).storeId = productList[position].store_id
					SessionTwiclo(con).serviceId = productList[position].service_id

				} else {
					val builder = AlertDialog.Builder(con)
					builder.setTitle("Replace cart item!")
					builder.setMessage("Do you want to discard the previous selection?")
					builder.setIcon(android.R.drawable.ic_dialog_alert)
					builder.setPositiveButton("Yes") { _, _ ->
						SessionTwiclo(con).storeId = productList[position].store_id
						SessionTwiclo(con).serviceId = productList[position].service_id

						itemClick.addtoCart(
							1,
							productList[position],
							position,
							productList[position].store_id,
							count.toString(),
							"Replace"
						)
					}

					//performing negative action
					builder.setNegativeButton("No") { _, _ ->
						count = 0
						holder.itemView.add_itemll_breakFast.visibility = View.VISIBLE
						holder.itemView.minusplus_ll_breakFast.visibility = View.GONE
					}
					val alertDialog: AlertDialog = builder.create()
					alertDialog.setCancelable(false)
					alertDialog.show()
				}

			} else {
				showLoginDialog("Please login to proceed")
			}
		}

		//minus cart quantity
		holder.itemView.subt_img_breakFast.setOnClickListener {
			count = productList[position].cart_quantity.toInt()
			if (count > 0) {
				count--
				if (count == 0) {
					holder.itemView.add_itemll_breakFast.visibility = View.VISIBLE
					holder.itemView.minusplus_ll_breakFast.visibility = View.GONE
				}
				itemClick.minusItemCart(
					1,
					productList[position],
					position,
					productList[position].store_id,
					count.toString(),
					"remove"
				)
			} else {
				holder.itemView.add_itemll_breakFast.visibility = View.VISIBLE
				holder.itemView.minusplus_ll_breakFast.visibility = View.GONE
			}
			holder.itemView.qua_txt_breakFast.text = count.toString()

		}

		//add more cart quantity
		holder.itemView.add_img_breakFast.setOnClickListener {
			count = productList[position].cart_quantity.toInt()
			count++
			holder.itemView.qua_txt_breakFast.text = count.toString()
			itemClick.plusItemCart(
				1,
				productList[position],
				position,
				productList[position].store_id,
				count.toString(),
				"add"
			)

		}

		if (productList.size - 1 == position) {
			holder.itemView.spaceView_dairy.visibility = View.VISIBLE
		} else {
			holder.itemView.spaceView_dairy.visibility = View.GONE
		}

	}

	private fun showLoginDialog(message: String) {
		val builder = androidx.appcompat.app.AlertDialog.Builder(con)
		builder.setTitle("Alert")
		builder.setMessage(message)
		builder.setPositiveButton("Login") { _, _ ->
			con.startActivity(Intent(con, AuthActivity::class.java))
		}
		builder.setNegativeButton("Cancel") { _, _ -> }
		val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
		alertDialog.setCancelable(true)
		alertDialog.show()
	}
}