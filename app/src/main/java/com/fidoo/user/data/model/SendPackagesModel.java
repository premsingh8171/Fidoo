package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendPackagesModel {
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

    @SerializedName("payment_amt")
    @Expose
    public String paymentAmt;

    @SerializedName("order_id")
    @Expose
    public String orderId;

    @SerializedName("distance")
    @Expose
    public String distance;

    @SerializedName("time")
    @Expose
    public String time;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("razorPayOrderId")
    @Expose
    public String razorPayOrderId;

    @SerializedName("additional_distance")
    @Expose
    public String additional_distance;

    @SerializedName("base_price")
    @Expose
    public String base_price;

    @SerializedName("additional_charges")
    @Expose
    public String additional_charges;

    @SerializedName("tax")
    @Expose
    public String tax;

    @SerializedName("base_distance")
    @Expose
    public String base_distance;

}
