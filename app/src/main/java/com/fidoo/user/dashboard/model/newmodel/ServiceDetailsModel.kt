package com.fidoo.user.dashboard.model.newmodel

data class ServiceDetailsModel(
    val accessToken: String,
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val homeData: List<HomeData>
)