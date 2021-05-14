package com.fidoo.user.ordermodule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailsModel {
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
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("estimated_time")
    @Expose
    public String estimatedTime;
    @SerializedName("order_status")
    @Expose
    public String orderStatus;
    @SerializedName("store_name")
    @Expose
    public String storeName;

    @SerializedName("delivery_boy_name")
    @Expose
    public String deliveryBoyName;

    @SerializedName("store_address")
    @Expose
    public String storeAddress;

    @SerializedName("delivery_address")
    @Expose
    public String deliveryAddress;

    @SerializedName("payment_mode")
    @Expose
    public String paymentMode;

    @SerializedName("store_image")
    @Expose
    public String storeImage;

    @SerializedName("order_id")
    @Expose
    public String orderId;

    @SerializedName("total_price")
    @Expose
    public String totalPrice;

    @SerializedName("tax")
    @Expose
    public Float tax;

    @SerializedName("delivery_charge")
    @Expose
    public Integer deliveryCharge;

    @SerializedName("discount")
    @Expose
    public String discount;

    //Cart Coupon
    @SerializedName("cart_coupon")
    @Expose
    public String coupon_name;

    // delivery coupon
    @SerializedName("delivery_coupon")
    @Expose
    public String delivery_coupon_name;

    @SerializedName("delivery_discount")
    @Expose
    public String delivery_discount;

    @SerializedName("date_time")
    @Expose
    public String dateTime;
    @SerializedName("category_id")
    @Expose
    public String categoryId;
    @SerializedName("is_prescription")
    @Expose
    public String is_prescription;
    @SerializedName("category_name")
    @Expose
    public String categoryName;
    @SerializedName("prescription")
    @Expose
    public String prescription;

    @SerializedName("items")
    @Expose
    public List<Item> items = null;

    public class Item {

        @SerializedName("product_name")
        @Expose
        public String productName;
        @SerializedName("quantity")
        @Expose
        public String quantity;
        @SerializedName("price")
        @Expose
        public String price;
        @SerializedName("offer_price")
        @Expose
        public String offerPrice;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("customize_item")
        @Expose
        public List<CustomizeItem> customizeItem = null;

        @SerializedName("price_with_customization")
        @Expose
        public String price_with_customization;

    }

    public class CustomizeItem {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("sub_cat_name")
        @Expose
        public String subCatName;

    }
}
