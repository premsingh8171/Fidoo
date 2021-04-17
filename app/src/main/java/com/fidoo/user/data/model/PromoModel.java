package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PromoModel {

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
    @SerializedName("offers")
    @Expose
    public List<Offer> offers = null;
    public class Offer {

        @SerializedName("coupon_name")
        @Expose
        public String couponName;
        @SerializedName("coupon_desc")
        @Expose
        public String couponDesc;
        @SerializedName("discount_amount")
        @Expose
        public String discountAmount;
        @SerializedName("mininum_amt")
        @Expose
        public String mininumAmt;
        @SerializedName("category_id")
        @Expose
        public String categoryId;
        @SerializedName("use_time")
        @Expose
        public String useTime;
        @SerializedName("expiry_date")
        @Expose
        public String expiryDate;

    }
}
