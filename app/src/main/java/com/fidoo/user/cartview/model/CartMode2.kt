package com.fidoo.user.cartview.model

class CartMode2(
    val accessToken: String,
    val accountId: String,
    val after_discount_value: Int,
    val cart: List<Cart>,
    val coupon_desc: String,
    val coupon_id: String,
    val coupon_name: String,
    val delivery_charge: Int,
    val delivery_coupon_name: String,
    val delivery_discount: Int,
    val discount_amount: String,
    val distance: Int,
    val error: Boolean,
    val error_code: Int,
    val grand_total: Int,
    val store_open_close_status: String,
    val store_status: String,
    val tax: Int,
    val total_cart_value: Int
)
 class Cart(
    val cart_id: String,
    val cod: String,
    val company_name: String,
    val contains_egg: String,
    val customize_item: List<CustomizeItem>,
    val is_customize: String,
    val is_nonveg: String,
    val is_prescription: String,
    val message: String,
    val offer_price: String,
    val online: String,
    val price: String,
    val product_id: String,
    val product_image: String,
    val product_name: String,
    val quantity: String,
    val store_address: String,
    val store_id: String,
    val store_image: String,
    val store_name: String
)

 class CustomizeItem(
    val id: String,
    val price: String,
    val product_customize_id: String,
    val sub_cat_name: String
)