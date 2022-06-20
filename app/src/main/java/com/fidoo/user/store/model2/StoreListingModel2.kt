package com.fidoo.user.store.model2

data class StoreListingModel2(
    val accessToken: String,
    val accountId: String,
    val curations: List<Curation>,
    val distance_end: String,
    val distance_start: String,
    val error: Boolean,
    val error_code: Int,
    val more_value: Boolean,
    val page_count: String,
    val store_list: List<Store>,
    val total_count: Int
) {
    data class Curation(
        val cuisine_id: String,
        val cusine_name: String,
        val image: String
    )

    data class Store(
        val address: String,
        val appearnce_score: String,
        val category_id: String,
        val category_name: String,
        val city: String,
        val couponsAvailable: List<CouponsAvailable>,
        val cuisineidArr: List<String>,
        val cuisines: List<String>,
        val delivery_distance: Int,
        val delivery_time: String,
        val fssai: String,
        val has_product_categories: String,
        val id: String,
        val image: String,
        val landmark: String,
        val locality: String,
        val name: String,
        val open_close_status: String,
        val pin: String,
        val rating: Int,
        val shop_no: String,
        val state: String,
        val status: String,
        val store_closing_time: String,
        val store_latitude: String,
        val store_longitude: String,
        val store_opening_time: String
    ) {
        data class CouponsAvailable(
            val coupon_desc: String,
            val coupon_name: String,
            val discount: String,
            val discount_type: String,
            val expiry_date: String,
            val mininum_amt: String,
            val upto: String
        )
    }
}