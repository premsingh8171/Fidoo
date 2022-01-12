package com.fidoo.user.newRestaurants.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.MainActivity.Companion.tempProductList
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.interfaces.AdapterAddRemoveClick
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.newRestaurants.listener.NewAdapterCartAddRemoveClick
import com.fidoo.user.newRestaurants.model.Product
import com.fidoo.user.restaurants.model.StoreDetailsModel
import kotlinx.android.synthetic.main.store_product.view.*

class NewStoreItemsAdapter(
	val con: Context,
	private val adapterClick: AdapterClick,
	private var productList: ArrayList<com.fidoo.user.newRestaurants.model.Product>,
	var fssai: String,
	var restaurantName: String,
	var service_id: String,
	var restaurantAddress: String,
	var adapterAddRemoveClick: AdapterAddRemoveClick,
	private val adapterCartAddRemoveClick: NewAdapterCartAddRemoveClick,
	var total_item_count: Int,
	private val storeID: String,
	private val updatePrd: UpdatePrd,
) : RecyclerView.Adapter<NewStoreItemsAdapter.UserViewHolder>() {

	var arraylist: ArrayList<StoreDetailsModel.Product> = ArrayList()
	var customizeItemIdList: ArrayList<String> = ArrayList()
	var customizeItemName: ArrayList<String> = ArrayList()
	var count: Int = 0

	override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.store_product, parent, false)
	)

	override fun getItemCount() = productList.size

	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
		val index = productList[position]
		customizeItemIdList = ArrayList()
		customizeItemName = ArrayList()

		try {
			// Log.d("indexindex", index.price!! + "--" + index.offerPrice)

			if (!index.price!!.equals(index.offer_price!!)) {

				holder.offerPrice.paintFlags =
					holder.offerPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

				holder.offerPrice.text = con.resources.getString(R.string.ruppee) + "" + index.price

				holder.priceAfterDiscount.text =
					con.resources.getString(R.string.ruppee) + "" + index.offer_price

			} else {
				holder.priceAfterDiscount.text =
					con.resources.getString(R.string.ruppee) + "" + index.offer_price
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}

		if (index.offer_price.equals("0")) {
			holder.priceAfterDiscount.visibility = View.GONE
		} else {
			holder.priceAfterDiscount.visibility = View.VISIBLE
		}

//        try {
//            if (index.headerActiveornot.equals("1")) {
//                if (position == 0) {
//                    holder.devider_catLL.visibility = View.GONE
//                    holder.category_nameheader.visibility = View.GONE
//                    holder.category_nameheader.text = index.subcategory_name.toString()
//
//                } else {
//                    holder.devider_catLL.visibility = View.VISIBLE
//                    holder.category_nameheader.visibility = View.VISIBLE
//                    holder.category_nameheader.text = index.subcategory_name.toString()
//                }
//            }else{
//                if (position==0){
//
//                }else {
//                    holder.devider_catLL.visibility = View.GONE
//                    holder.category_nameheader.visibility = View.GONE
//                }
//            }
//            Log.d("subcategory_name____", index.subcategory_name.toString())
//
//        } catch (e: Exception) {
//        }

		holder.itemName.text = index.product_name

		holder.itemView.product_desc_txt.text = index.product_desc

		if (index.image!!.isNotEmpty()) {
			Glide.with(con)
				.load(index.image).thumbnail(0.05f)
				.fitCenter()
				.error(R.drawable.default_item)
				.into(holder.storeImg)
			holder.storeImg.visibility = View.VISIBLE
		} else {
			holder.storeImg.visibility = View.GONE
		}

	//	Log.d("indeximage", index.image.toString())

		if (productList[position].is_customize == "1") {
			holder.customizable.visibility = View.VISIBLE
		} else {
			holder.customizable.visibility = View.GONE
		}


		if (productList[position].is_nonveg!!.equals("0")) {
			holder.vegIcon.setImageResource(R.drawable.veg)
			//holder.vegIcon.setColorFilter(Color.rgb(53, 156, 71))
		} else if (productList[position].is_nonveg!!.equals("1")) {
			if (productList[position].contains_egg.equals("1")) {
				holder.vegIcon.setImageResource(R.drawable.egg_icon)
			} else {
				holder.vegIcon.setImageResource(R.drawable.non_veg)
			}
		}

		if (index.cart_quantity == 0) {
			holder.add_new_lay.visibility = View.VISIBLE
			holder.add_remove_lay.visibility = View.GONE

		} else {
			tempProductList!!.clear()
			holder.add_new_lay.visibility = View.GONE
			holder.add_remove_lay.visibility = View.VISIBLE

//            val tempProductListModel = TempProductListModel()
//            tempProductListModel.productId = index.productId
//            tempProductListModel.price = index.offerPrice
//            tempProductListModel.quantity = index.cartQuantity.toString()
//            tempProductList!!.add(tempProductListModel)
//            val customIdsList: ArrayList<String>?
//
//            customIdsList = ArrayList()
//            val addCartInputModel = AddCartInputModel()
//            addCartInputModel.productId = index.productId
//            addCartInputModel.quantity = index.cartQuantity.toString()
//            addCartInputModel.message = "add product"
//            addCartInputModel.customizeSubCatId = customIdsList
//            addCartInputModel.isCustomize = "0"
//            MainActivity.addCartTempList!!.add(addCartInputModel)
//            Log.e("llll", Gson().toJson(tempProductList))

			// count = index.cartQuantity!!

			holder.countValue.text = index.cart_quantity.toString()
		}

		if (index.in_out_of_stock_status == "1") {
			holder.stock_status.visibility = View.GONE

			holder.add_new_lay.setOnClickListener {

				if (checkForInternet(con)) {
					count = 0
					if (SessionTwiclo(con).isLoggedIn) {
						if (index.is_customize!!.equals("1")) {
							count++
							holder.countValue.text = count.toString()

							adapterClick.onItemClick(
								index.product_id,
								"custom",
								"1",
								index.offer_price,
								index.is_customize_quantity,
								"",
								productList[position].cart_id
							)

							//SessionTwiclo(con).storeId = storeID
							SessionTwiclo(con).serviceId = MainActivity.service_idStr

							updatePrd.onClickUpdate(productList[position],position,count,"Customized")


						} else {
							count++
							//Log.d("countcountcountcot",count.toString())
							holder.countValue.text = count.toString()
							holder.add_new_lay.visibility = View.GONE
							holder.add_remove_lay.visibility = View.VISIBLE

							if (SessionTwiclo(con).storeId.equals(storeID) || SessionTwiclo(con).storeId.equals(
									""
								)
							) {
								adapterAddRemoveClick.onItemAddRemoveClick(
									index.product_id, count.toString(), "add",
									index.offer_price, "",
									productList[position].cart_id, 0
								)
								SessionTwiclo(con).storeId = storeID
								SessionTwiclo(con).serviceId = MainActivity.service_idStr

								updatePrd.onClickUpdate(productList[position],position,count,"normal")

							} else {

								val builder = AlertDialog.Builder(con)
								builder.setTitle("Replace cart item!")
								builder.setMessage("Do you want to discard the previous selection?")
								builder.setIcon(android.R.drawable.ic_dialog_alert)
								builder.setPositiveButton("Yes") { _, _ ->
									adapterAddRemoveClick.clearCart()
									adapterAddRemoveClick.onItemAddRemoveClick(
										index.product_id,
										count.toString(),
										"add",
										index.offer_price,
										storeID,
										productList[position].cart_id, 0
									)

									SessionTwiclo(con).storeId = storeID
									SessionTwiclo(con).serviceId = MainActivity.service_idStr

									updatePrd.onClickUpdate(productList[position],position,count,"normal")

									//Toast.makeText(applicationContext,"clicked yes",Toast.LENGTH_LONG).show()
								}

								//performing negative action
								builder.setNegativeButton("No") { _, _ ->
									count = 0
									holder.countValue.text = count.toString()
									holder.add_new_lay.visibility = View.VISIBLE
									holder.add_remove_lay.visibility = View.GONE
								}
								// Create the AlertDialog
								val alertDialog: AlertDialog = builder.create()
								// Set other dialog properties
								alertDialog.setCancelable(false)
								alertDialog.show()
							}

						}

					} else {
						showLoginDialog("Please login to proceed")
					}

				}

			}

			holder.plusLay.setOnClickListener {

				if (checkForInternet(con)) {
					var count: Int = holder.countValue.text.toString().toInt()

					if (index.is_customize.equals("1")) {
						var items: String? = ""
						count++

//                        if (index.customizeItemName != "") {
//                            items = index.customizeItemName
//                        }

						var customizeItemId = ""
						customizeItemIdList.clear()

						try {
							for (element in productList[position].customize_item) {
								customizeItemIdList.add(element.id)
								customizeItemName.add(element.sub_cat_name)
							}
							customizeItemId =
								customizeItemIdList.toString().replace("[", "").replace("]", "")
							items = customizeItemName.toString().replace("[", "").replace("]", "")
						} catch (e: java.lang.Exception) {
							e.printStackTrace()
						}

						adapterCartAddRemoveClick.onAddItemClick(
							count.toString(),
							index.product_id,
							items,
							index.offer_price,
							index.is_customize,
							customizeItemId,
							productList[position].cart_id
						)

						updatePrd.onClickUpdate(productList[position],position,count,"Customized")

					} else {
						count++
						holder.countValue.text = count.toString()
						adapterAddRemoveClick.onItemAddRemoveClick(
							index.product_id,
							count.toString(),
							"add",
							index.offer_price, "",
							productList[position].cart_id, 0
						)
						updatePrd.onClickUpdate(productList[position],position,count,"normal")

					}
				}
			}

			holder.minusLay.setOnClickListener {
				if (checkForInternet(con)) {

					var count: Int = holder.countValue.text.toString().toInt()

					if (index.is_customize.equals("1")) {
						if (holder.countValue.text.toString().toInt() > 0) {
							count--
							holder.countValue.text = count.toString()
							var customizeItemId = ""
							customizeItemIdList.clear()
							try {
								for (element in productList[position].customize_item) {
									customizeItemIdList.add(element.id)
								}
								customizeItemId =
									customizeItemIdList.toString().replace("[", "").replace("]", "")
							} catch (e: java.lang.Exception) {
								e.printStackTrace()
							}

							adapterCartAddRemoveClick.onRemoveItemClick(
								index.product_id,
								count.toString(),
								index.is_customize,
								customizeItemId,
								productList[position].cart_id
							)
						}
					} else {
						if (count > 0) {
							count--
							holder.countValue.text = count.toString()

							if (count == 0) {
								holder.add_new_lay.visibility = View.VISIBLE
								holder.add_remove_lay.visibility = View.GONE
								adapterAddRemoveClick.onItemAddRemoveClick(
									index.product_id,
									count.toString(),
									"remove",
									index.offer_price,
									"",
									productList[position].cart_id,
									0
								)
							} else {
								adapterAddRemoveClick.onItemAddRemoveClick(
									index.product_id,
									count.toString(),
									"remove",
									index.offer_price,
									"",
									productList[position].cart_id, 0
								)
							}

						}
					}

					updatePrd.onClickUpdate(productList[position],position,count,"normal")

				}
			}

		} else if (index.in_out_of_stock_status.equals("0")) {
			holder.add_new_lay.visibility = View.GONE
			holder.add_remove_lay.visibility = View.GONE
			holder.stock_status.visibility = View.VISIBLE
		}


//		if (total_item_count - 1 == position) {
//			holder.fssaitxt.text = fssai
//			holder.restaurant_nametxt.text = restaurantName
//			holder.restaurant_addtxt.text = restaurantAddress
//			holder.store_bottom_ll.visibility = View.VISIBLE
//		} else {
//			holder.store_bottom_ll.visibility = View.GONE
//		}

	}

	private fun checkForInternet(context: Context): Boolean {
		val connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val network = connectivityManager.activeNetwork ?: return false
			val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
			return when {
				activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
				activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
				else -> false
			}
		} else {
			@Suppress("DEPRECATION") val networkInfo =
				connectivityManager.activeNetworkInfo ?: return false
			@Suppress("DEPRECATION")
			return networkInfo.isConnected
		}
	}

	private fun showLoginDialog(message: String) {
		val builder = androidx.appcompat.app.AlertDialog.Builder(con)
		//set title for alert dialog
		builder.setTitle("Alert")
		//set message for alert dialog
		builder.setMessage(message)
		// builder.setIcon(android.R.drawable.ic_dialog_alert)

		//performing positive action
		builder.setPositiveButton("Login") { _, which ->
			//   con.startActivity(Intent(con, LoginActivity::class.java))
			con.startActivity(
				Intent(
					con,
					SplashActivity::class.java
				).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
			)


		}

		//performing negative action
		builder.setNegativeButton("Cancel") { _, _ ->

		}
		// Create the AlertDialog
		val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
		// Set other dialog properties
		alertDialog.setCancelable(true)
		alertDialog.show()
	}

	class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		var offerPrice = view.productSellingPrice
		var priceAfterDiscount = view.productPrice
		var itemLay = view.store_item_lay

		//var qty = view.tv_quantity
		var itemName = view.productName
		var storeImg = view.productImage

		//var customText = view.customText
		var plusLay = view.btn_plus

		//var ratingTxtValue = view.ratingTxtValue
		var minusLay = view.btn_minus
		var countValue = view.tv_count
		var add_remove_lay = view.add_remove_lay
		var vegIcon = view.veg_icon
		var add_new_lay = view.add_item_lay
		var stock_status = view.tv_stock_status
		var customizable = view.tv_customizable
		var category_nameheader = view.category_nameheader
		var devider_catLL = view.devider_catLL
		var store_bottom_ll = view.store_bottom_ll
		var fssaitxt = view.fssaitxt
		var restaurant_nametxt = view.restaurant_nametxt
		var restaurant_addtxt = view.restaurant_addtxt
	}

	interface UpdatePrd {
		fun onClickUpdate(listData_: Product, pos: Int, prdQuantity: Int,type:String)
	}

	fun updateData(listData_: ArrayList<Product>, total_item: Int) {
		productList = java.util.ArrayList<Product>()
		productList.addAll(listData_)
		total_item_count = total_item
		notifyDataSetChanged()
	}

}