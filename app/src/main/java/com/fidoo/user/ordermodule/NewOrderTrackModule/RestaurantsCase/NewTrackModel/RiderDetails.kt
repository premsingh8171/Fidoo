package com.fidoo.user.ordermodule.NewOrderTrackModule.RestaurantsCase.NewTrackModel


import com.google.gson.annotations.SerializedName

data class RiderDetails(
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: String
)