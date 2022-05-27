package com.fidoo.user.cartview.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DuePaymentProcessModel(

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

    @SerializedName("message")
    @Expose
    val message: String,

    @SerializedName("payment_id")
    @Expose
    val payment_id: String,

    @SerializedName("razorPayOrderId")
    @Expose
    val razorPayOrderId: String
)