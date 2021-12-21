package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddRemoveCartModel {
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

    @SerializedName("product_id")
    @Expose
    public String product_id;

    @SerializedName("cart_id")
    @Expose
    public String cart_id;

    @SerializedName("is_customize_quantity")
    @Expose
    public String is_customize_quantity;

    @SerializedName("cart_quantity")
    @Expose
    public String cart_quantity;

    @SerializedName("product_customize_id")
    @Expose
    public String product_customize_id;

    @SerializedName("customize_sub_cat_id")
    @Expose
    public String customize_sub_cat_id;

    @SerializedName("message")
    @Expose
    public String message;
}

