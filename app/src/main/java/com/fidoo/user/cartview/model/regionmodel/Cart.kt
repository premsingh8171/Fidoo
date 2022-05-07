package com.fidoo.user.cartview.model.regionmodel

data class Cart(
    val cart_id: String,
    val cod: String,
    val company_name: String,
    val contains_egg: String,
    val customize_item: List<Any>,
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
    val quantity: Int,
    val service_id: String,
    val store_address: String,
    val store_id: String,
    val store_image: String,
    val store_locality: String,
    val store_name: String
)