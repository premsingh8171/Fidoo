package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendPackageOrderDetailModel {

    @SerializedName("error")
    @Expose
    public Boolean error;

    @SerializedName("error_code")
    @Expose
    public Integer errorCode;

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("accountId")
    @Expose
    public String accountId;

    @SerializedName("order_id")
    @Expose
    public String orderId;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("razorPayOrderId")
    @Expose
    public String razorPayOrderId;

}
