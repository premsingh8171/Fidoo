package com.fidoo.user.ordermodule.NewOrderTrackModule.sendPackageModule.SendPackageDataModel


import com.google.gson.annotations.SerializedName

data class RiderDetails(
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("img")
    val img: String
)