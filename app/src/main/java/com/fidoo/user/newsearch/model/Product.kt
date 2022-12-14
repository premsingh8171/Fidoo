package com.fidoo.user.newsearch.model

data class Product(
    val contains_egg: String,
    val is_nonveg: String,
    val offer_price: String,
    val product_id: String,
    val product_image: String,
    val product_name: String,
    val is_customize : String,
    val in_stock : String,
    val active : String,
    var cart_id : String,
    var cart_quantity: String
)