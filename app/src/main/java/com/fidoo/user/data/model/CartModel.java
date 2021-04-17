package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartModel {
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
    @SerializedName("tax")
    @Expose
    public float tax;
    @SerializedName("delivery_charge")
    @Expose
    public int deliveryCharge;
    @SerializedName("discount_amount")
    @Expose
    public String discount_amount;
    //Cart Coupon
    @SerializedName("coupon_name")
    @Expose
    public String coupon_name;
    @SerializedName("coupon_id")
    @Expose
    public String coupon_id;
    @SerializedName("coupon_desc")
    @Expose
    public String coupon_desc;

    @SerializedName("delivery_discount")
    @Expose
    public int deliveryDiscount;
    // delivery coupon
    @SerializedName("delivery_coupon_name")
    @Expose
    public String delivery_coupon_name;

    @SerializedName("cart")
    @Expose
    public List<Cart> cart = null;

    public class Cart {

        @SerializedName("cart_id")
        @Expose
        public String cart_id;

        @SerializedName("product_id")
        @Expose
        public String productId;

        @SerializedName("quantity")
        @Expose
        public String quantity;

        @SerializedName("store_id")
        @Expose
        public String storeId;

        @SerializedName("store_name")
        @Expose
        public String storeName;

        @SerializedName("company_name")
        @Expose
        public String companyName;

        @SerializedName("product_name")
        @Expose
        public String productName;

        @SerializedName("product_image")
        @Expose
        public String productImage;

        @SerializedName("price")
        @Expose
        public String price;

        @SerializedName("offer_price")
        @Expose
        public String offerPrice;

        @SerializedName("message")
        @Expose
        public String message;

        @SerializedName("is_prescription")
        @Expose
        public String isPrescription;

        @SerializedName("cod")
        @Expose
        public String cod;

        @SerializedName("online")
        @Expose
        public String online;

        @SerializedName("is_customize")
        @Expose
        public String is_customize;

        @SerializedName("customize_item")
        @Expose
        public List<CustomizeItem> customizeItem = null;

    }
    public class CustomizeItem {

        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("sub_cat_name")
        @Expose
        public String subCatName;

        @SerializedName("price")
        @Expose
        public String price;

        @SerializedName("product_customize_id")
        @Expose
        public String productCustomizeId;

    }
}
