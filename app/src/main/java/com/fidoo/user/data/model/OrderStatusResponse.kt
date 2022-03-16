package com.fidoo.user.data.model


import com.google.gson.annotations.SerializedName

data class OrderStatusResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("chatLogEntry")
    val chatLogEntry: Boolean,
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
    val orderStatus: String,
    @SerializedName("userPaymentMode")
    val userPaymentMode: Int
)