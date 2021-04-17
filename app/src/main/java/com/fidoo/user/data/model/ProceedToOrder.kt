package com.fidoo.user.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProceedToOrder {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("error_code")
    @Expose
    var errorCode: Int? = null

    @SerializedName("accessToken")
    @Expose
    var accessToken: String? = null

    @SerializedName("accountId")
    @Expose
    var accountId: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}
