package com.fidoo.user.ordermodule.model

data class RepeatOrderModel(
    val accessToken: String,
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val message: String
)