package com.fidoo.user.store.model.newmodel

data class Store(
    val address: String,
    val appearnce_score: Int,
    val category_id: String,
    val category_name: String,
    val city: String,
    val couponsAvailable: List<CouponsAvailable>,
    val cuisineidArr: List<String>,
    val cuisines: List<String>,
    val delivery_distance: Int,
    val delivery_time: Int,
    val has_product_categories: String,
    val id: String,
    val image: String,
    val landmark: String,
    val locality: String,
    val name: String,
    val open_close_status: String,
    val pin: String,
    val rating: Double,
    val shop_no: String,
    val state: String,
    val status: String,
    val store_closing_time: String,
    val store_latitude: String,
    val store_longitude: String,
    val store_opening_time: String
)