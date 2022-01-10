package com.fidoo.user.newRestaurants.model

data class Product(
    val cart_id: String,
    val cart_quantity: Int,
    val company_name: String,
    val contains_egg: String,
    val coupon_applied: String,
    val customize_item: List<CustomizeItem>,
    val discount: Int,
    val image: String,
    val in_out_of_stock_status: String,
    val is_customize: String,
    val is_customize_quantity: Int,
    val is_nonveg: String,
    val is_prescription: String,
    val offer_price: String,
    val price: String,
    val product_desc: String,
    val product_id: String,
    val product_name: String,
    val product_sub_category_id: String,
    val unit: String,
    val weight: String
)