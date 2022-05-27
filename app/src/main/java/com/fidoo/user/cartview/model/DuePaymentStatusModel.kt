package com.fidoo.user.cartview.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DuePaymentStatusModel(

    @SerializedName("accessToken")
    @Expose
    val accessToken: String,

    @SerializedName("accountId")
    @Expose
    val accountId: String,

    @SerializedName("error")
    @Expose
    val error: Boolean,

    @SerializedName("error_code")
    @Expose
    val error_code: Int,

    @SerializedName("payment_status")
    @Expose
    val payment_status: String,

    @SerializedName("message")
    @Expose
    val message: String,
)