package com.fidoo.user.newRestaurants.model

data class Subcategory(
    val product: List<Product>,
    val product_sub_category_id: String,
    val subcategory_name: String
)