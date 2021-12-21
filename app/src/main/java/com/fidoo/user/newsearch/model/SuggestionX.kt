package com.fidoo.user.newsearch.model

data class SuggestionX(
    val category_id: String,
    val couponAvailable: Any,
    val cuisineidArr: List<String>,
    val cuisines: List<String>,
    val delivery_time: String,
    val distance: String,
    val id: String,
    val store_id: String,
    val image: String,
    val locality: String,
    val name: String,
    val service_id: String,
    val subcategory_id: String,
    val category_name: String,
    val available: String,
    val opening_time: String,
    val type: String
)