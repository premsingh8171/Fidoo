package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel


import com.google.gson.annotations.SerializedName

data class sendPackageModel(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("callToRider")
    val callToRider: Boolean,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("gif")
    val gif: String,
    @SerializedName("customer_phone")
    val customer_phone: String,
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("orderId")
    val orderId: String,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("rider_bottom_msg")
    val riderBottomMsg: List<String>,
    @SerializedName("rider_bottom_msg_ADR")
    val riderBottomMsgADR: List<String>,
    @SerializedName("rider_btm_icon")
    val riderBtmIcon: String,
    @SerializedName("rider_details")
    val riderDetails: RiderDetails
)