package com.example.myapplication.Models.orderStatus


import com.google.gson.annotations.SerializedName

data class OrderStatusResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("isRefundAble")
    val isRefundAble: Boolean,
    @SerializedName("isShowCancel")
    val isShowCancel: Boolean,
    @SerializedName("messages")
    val messages: List<String>,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("order_status")
    val orderStatus: String
)