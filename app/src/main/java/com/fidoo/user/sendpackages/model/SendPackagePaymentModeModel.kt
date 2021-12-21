package com.fidoo.user.sendpackages.model

data class SendPackagePaymentModeModel(
    val accessToken: String,
    val accountId: String,
    val cash: Int,
    val error: Boolean,
    val error_code: Int,
    val free: Int,
    val online: Int
)