package com.fidoo.user.referral.model

data class ReferralModel(
    val accessToken: String,
    val accountId: String,
    val description: String,
    val error: Boolean,
    val error_code: Int,
    val image: String,
    val message: String,
    val short_description: String,
    val title: String
)