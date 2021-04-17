package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApplyPromoModel {
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
    @SerializedName("discount_amount")
    @Expose
    public String discountAmount;
    @SerializedName("coupon_apply")
    @Expose
    public String couponApply;
    @SerializedName("message")
    @Expose
    public String message;
}
