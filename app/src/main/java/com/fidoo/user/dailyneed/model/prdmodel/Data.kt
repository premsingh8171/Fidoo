package com.fidoo.user.dailyneed.model.prdmodel

data class Data(
    val image: String,
    val products: List<Product>,
    val subcategory_id: String,
    val inventory_subcat_id: String,
    val subcategory_name: String
)