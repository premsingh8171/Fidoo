package com.fidoo.user.dailyneed.model.prdmodel

data class Product(
	val store_id: String,
	val distance: String,
	val category_id: String,
	val offer_price: String,
	val price: String,
	val product_id: String,
	val product_image: String,
	val product_name: String,
	val subcategory_id: String,
	val unit: String,
	val weight: String,
	val is_nonveg: String,
	val is_customize: String,
	val is_prescription: String,
	val cart_id: String,
	var cart_quantity: String,
	val is_customize_quantity: String,
	val service_id: String

)