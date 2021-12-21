package com.fidoo.user.store.model.newmodel

data class StoreListingModelX(
    val accessToken: String,
    val accountId: String,
    val curations: List<Curation>,
    val distance_end: String,
    val distance_start: String,
    val error: Boolean,
    val error_code: Int,
    val store_list: List<Store>
)