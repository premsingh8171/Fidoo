package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductDetailsModel {
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
    public String productId;
    @SerializedName("company_name")
    @Expose
    public String companyName;
    @SerializedName("product_name")
    @Expose
    public String productName;
    @SerializedName("store_id")
    @Expose
    public String storeId;
    @SerializedName("store_name")
    @Expose
    public String storeName;
    @SerializedName("product_description")
    @Expose
    public String productDescription;
    @SerializedName("price")
    @Expose
    public String price;
    @SerializedName("weight")
    @Expose
    public String weight;
    @SerializedName("offer_price")
    @Expose
    public String offerPrice;
    @SerializedName("unit")
    @Expose
    public String unit;
    @SerializedName("is_nonveg")
    @Expose
    public String isNonveg;
    @SerializedName("is_customize")
    @Expose
    public String isCustomize;
    @SerializedName("is_prescription")
    @Expose
    public String isPrescription;
    @SerializedName("cart_quantity")
    @Expose
    public Integer cartQuantity;
    @SerializedName("images")
    @Expose
    public List<String> images = null;

    @SerializedName("specifications")
    @Expose
    public List<Specification> specifications = null;
}
