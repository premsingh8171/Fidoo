package com.fidoo.user.newsearch.model

data class Store(
    val couponsAvailable: List<CouponsAvailable>,
    val cuisineidArr: List<String>,
    val cuisines: List<String>,
    val delivery_time: Int,
    val locality: String,
    val products: List<Product>,
    val store_id: String,
    val store_image: String,
    val distance: String,
    val store_name: String
)