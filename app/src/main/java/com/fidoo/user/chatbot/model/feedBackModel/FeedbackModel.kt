package com.fidoo.user.chatbot.model.feedBackModel


import com.google.gson.annotations.SerializedName

data class FeedbackModel(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("call_to_merchant")
    val callToMerchant: Boolean,
    @SerializedName("chatLogEntry")
    val chatLogEntry: Boolean,
    @SerializedName("delivery_mobile_number")
    val deliveryMobileNumber: Any,
    @SerializedName("end_of_chat")
    val endOfChat: Boolean,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("merchant_mobile_number")
    val merchantMobileNumber: String,
    @SerializedName("messages")
    val messages: List<String>,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("user_mobile_number")
    val userMobileNumber: String
)