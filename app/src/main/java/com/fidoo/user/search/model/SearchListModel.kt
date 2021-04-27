package com.fidoo.user.search.model

 class SearchListModel(
    val accessToken: String,
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val product_list: List<Any>,
    val store: List<Store>
)

 class Store(
    val delivery_time: Any,
    val list: List<Product>,
    val store_id: String,
    val store_image: String,
    val store_name: String
)

 class Product(
    val cart_quantity: Int,
    val category_name: String,
    val company_name: String,
    val customize_item: List<Any>,
    val delivery_time: Any,
    val is_customize: String,
    val is_customize_quantity: Int,
    val is_nonveg: String,
    val is_prescription: String,
    val offer_price: String,
    val price: String,
    val product_desc: String,
    val product_id: String,
    val product_image: String,
    val product_name: String,
    val rating: String,
    val service_name: String,
    val store_address: String,
    val store_id: String,
    val store_image: String,
    val store_name: String,
    val unit: String,
    val weight: String
)