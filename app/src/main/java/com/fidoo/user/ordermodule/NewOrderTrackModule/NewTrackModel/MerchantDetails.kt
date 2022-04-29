package com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel


import com.google.gson.annotations.SerializedName

data class MerchantDetails(
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: String
)