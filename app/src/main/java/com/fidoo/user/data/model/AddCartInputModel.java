package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddCartInputModel {

    @SerializedName("product_id")
    @Expose
    public String productId;

    @SerializedName("quantity")
    @Expose
    public String quantity;

    @SerializedName("is_customize")
    @Expose
    public String isCustomize;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("customize_sub_cat_id")
    @Expose
    public List<String> customizeSubCatId = null;


}
