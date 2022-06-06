package com.fidoo.user.viewmodels

data class UpdateAppModel(
    val error: Boolean,
    val error_code: Int,
    val message: String,
    val latest_version: String,
    val is_under_maintenance: String
)