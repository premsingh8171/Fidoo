package com.fidoo.user.ordermodule.model

data class ProductChangeRequestDetailsModel(
    val accessToken: String,
    val accountId: String,
    val changes: List<Change>,
    val error: Boolean,
    val error_code: Int,
    val message: String,
    val order_id: String
)