package com.fidoo.user.addressmodule.model

data class RemoveAddressModel(
    val accessToken: String,
    val error: Boolean,
    val error_code: Int,
    val message: String
)