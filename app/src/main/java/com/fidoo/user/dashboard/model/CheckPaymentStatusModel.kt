package com.fidoo.user.dashboard.model

data class CheckPaymentStatusModel(
    val accessToken: String,
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val message: String,
    val order_id: String,
    val transaction_id: String
)