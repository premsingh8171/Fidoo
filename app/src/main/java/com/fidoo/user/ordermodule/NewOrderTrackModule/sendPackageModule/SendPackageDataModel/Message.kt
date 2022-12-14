package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel


import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("ADR_desc")
    val aDRDesc: String,
    @SerializedName("ADR_message")
    val aDRMessage: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("icon_img")
    val iconImg: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)