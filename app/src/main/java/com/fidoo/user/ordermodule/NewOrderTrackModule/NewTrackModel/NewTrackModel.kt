package com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel


import com.google.gson.annotations.SerializedName

data class NewTrackModel(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("callToMerchant")
    val callToMerchant: Boolean,
    @SerializedName("callToRider")
    val callToRider: Boolean,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("gif")
    val gif: String,
    @SerializedName("isAllowCancel")
    val isAllowCancel: Boolean,
    @SerializedName("merchant_details")
    val merchantDetails: MerchantDetails,
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
    @SerializedName("riderBtnBgColor")
    val riderBtnBgColor: String,
    @SerializedName("rider_details")
    val riderDetails: RiderDetails
)