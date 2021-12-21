package com.fidoo.user.dashboard.model.newmodel

data class Offer(
    val delivery_time: String,
    val address: String,
    val city: String,
    val coupon_desc: String,
    val coupon_name: String,
    val couponsAvailable: List<Any>,
    val cuisineidArr: List<String>,
    val cuisines: List<String>,
    val delivery_distance: Int,
    val image: String,
    val landmark: String,
    val locality: String,
    val open_close_status: String,
    val pin: String,
    val shop_no: String,
    val state: String,
    val status: String,
    val store_closing_time: String,
    val store_id: String,
    val store_image: String,
    val store_latitude: String,
    val store_longitude: String,
    val store_name: String,
    val store_opening_time: String,
    val upto: String
)