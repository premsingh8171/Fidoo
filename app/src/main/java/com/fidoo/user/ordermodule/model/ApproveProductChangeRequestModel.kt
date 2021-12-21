package com.fidoo.user.ordermodule.model

data class ApproveProductChangeRequestModel(
    val accessToken: String,
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val message: String,
    val refund_amount: String
)