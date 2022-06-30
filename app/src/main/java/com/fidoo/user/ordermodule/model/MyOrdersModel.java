package com.fidoo.user.ordermodule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyOrdersModel {

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

    @SerializedName("more_value")
    @Expose
    public Boolean more_value;

    @SerializedName("page_count")
    @Expose
    public String page_count;

    @SerializedName("orders")
    @Expose
    public List<Order> orders = null;

    public class Order {

        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("estimated_time")
        @Expose
        public String estimatedTime;
        @SerializedName("order_status")
        @Expose
        public String orderStatus;

        @SerializedName("is_rate_to_driver")
        @Expose
        public String is_rate_to_driver;

        @SerializedName("delivery_rating")
        @Expose
        public String delivery_rating;

        @SerializedName("is_rate_to_merchant")
        @Expose
        public String is_rate_to_merchant;

        @SerializedName("merchant_rating")
        @Expose
        public String merchant_rating;

        @SerializedName("store_name")
        @Expose
        public String storeName;
        @SerializedName("store_address")
        @Expose
        public String storeAddress;
        @SerializedName("store_image")
        @Expose
        public String storeImage;
        @SerializedName("order_id")
        @Expose
        public String orderId;
        @SerializedName("total_price")
        @Expose
        public String totalPrice;
        @SerializedName("service_type_id")
        @Expose
        public String serviceTypeId;
        @SerializedName("order_date")
        @Expose
        public String orderDate;
        @SerializedName("deliver_cat_name")
        @Expose
        public String deliverCatName;
        @SerializedName("price")
        @Expose
        public String price;
        @SerializedName("from_address")
        @Expose
        public String fromAddress;
        @SerializedName("from_note")
        @Expose
        public String fromNote;

        @SerializedName("from_latitude")
        @Expose
        public String fromLatitude;

        @SerializedName("from_longitude")
        @Expose
        public String fromLongitude;

        @SerializedName("to_address")
        @Expose
        public String toAddress;

        @SerializedName("to_note")
        @Expose
        public String toNote;

        @SerializedName("to_latitude")
        @Expose
        public String toLatitude;

        @SerializedName("to_longitude")
        @Expose
        public String toLongitude;

        @SerializedName("distance")
        @Expose
        public String distance;

        @SerializedName("delivery_time")
        @Expose
        public String deliveryTime;

        @SerializedName("category_id")
        @Expose
        public String category_id;

        @SerializedName("prescription")
        @Expose
        public String prescription;

        @SerializedName("delivery_boy_name")
        @Expose
        public String delivery_boy_name;

        @SerializedName("delivery_boy_mobile")
        @Expose
        public String delivery_boy_mobile;

        @SerializedName("package_item_name")
        @Expose
        public String package_item_name;

        @SerializedName("package_item_image")
        @Expose
        public String package_item_image;

        @SerializedName("items")
        @Expose
        public List<Item> items = null;
    }


    public class Item {
        @SerializedName("product_id")
        @Expose
        public String productId;
        @SerializedName("product_name")
        @Expose
        public String productName;
        @SerializedName("quantity")
        @Expose
        public String quantity;
        @SerializedName("product_desc")
        @Expose
        public String product_desc;
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
