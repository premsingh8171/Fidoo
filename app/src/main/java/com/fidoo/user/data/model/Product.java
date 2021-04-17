package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {
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

    public Product(String productId,List<String> customizeSubCatId, String isCustomize, String message, String quantity) {
        this.productId = productId;
        this.customizeSubCatId = customizeSubCatId;
        this.isCustomize = isCustomize;
        this.message = message;
        this.quantity = quantity;
    }

    public Product() {

    }
}
