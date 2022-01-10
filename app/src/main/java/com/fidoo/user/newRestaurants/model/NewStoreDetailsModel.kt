package com.fidoo.user.newRestaurants.model

data class NewStoreDetailsModel(
    val accessToken: String,
    val accountId: String,
    val address: String,
    val delivery_time: String,
    val error: Boolean,
    val error_code: Int,
    val fssai: String,
    val id: String,
    val image: String,
    val next_available: Int,
    val offers: List<Any>,
    val rating: String,
    val service_id: String,
    val service_name: String,
    val start_id: Int,
    val status: String,
    val store_closing_time: String,
    val store_latitude: String,
    val store_longitude: String,
    val store_name: String,
    val subcategory: List<Subcategory>
)