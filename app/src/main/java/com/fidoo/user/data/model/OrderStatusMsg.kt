package com.fidoo.user.data.model


import com.google.gson.annotations.SerializedName

data class OrderStatusMsg(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("isNeedToFeedBackButtons")
    val isNeedToFeedBackButtons: Boolean,
    @SerializedName("isRefundAble")
    val isRefundAble: Boolean,
    @SerializedName("isShowCancel")
    val isShowCancel: Any,
    @SerializedName("messages")
    val messages: List<String>,
    @SerializedName("orderCancel")
    val orderCancel: Boolean,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("order_status")
    val orderStatus: String
)