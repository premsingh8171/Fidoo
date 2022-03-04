package com.example.myapplication.Models.cancelStatus


import com.google.gson.annotations.SerializedName

data class ChatCancelModel(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("order_status")
    val orderStatus: String
)