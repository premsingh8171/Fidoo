package com.fidoo.user.chatbot.model


import com.google.gson.annotations.SerializedName

data class cancelWithoutRefundEmpty(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("isShowCancel")
    val isShowCancel: Boolean,
    @SerializedName("messages")
    val messages: List<String>,
    @SerializedName("orderId")
    val orderId: String
)