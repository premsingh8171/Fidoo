package com.fidoo.user.dailyneed.model

data class DailyNeedModel(
    val accessToken: String,
    val accountId: String,
    val data: List<Data>,
    val error: Boolean,
    val error_code: Int
)