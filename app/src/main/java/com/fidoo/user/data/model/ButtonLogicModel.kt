package com.example.myapplication.Models.chatbotButtonLogic


import com.google.gson.annotations.SerializedName

data class ButtonLogicModel(
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
    @SerializedName("isNeedToFeedBackButtons")
    val isNeedToFeedBackButtons: Boolean,
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