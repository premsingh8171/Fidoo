package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreDetailsModel {

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
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("category_id")
    @Expose
    public String categoryId;
    @SerializedName("service_id")
    @Expose
    public String service_id;
    @SerializedName("service_name")
    @Expose
    public String serviceName;
    @SerializedName("store_name")
    @Expose
    public String storeName;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("fssai")
    @Expose
    public String fssai;
    @SerializedName("subcategory")
    @Expose
    public List<Category> category = null;
    @SerializedName("rating")
    @Expose
    public String rating;
    @SerializedName("delivery_time")
    @Expose
    public String deliveryTime;

    @SerializedName("store_latitude")
    @Expose
    public String storeLatitude;

    @SerializedName("store_longitude")
    @Expose
    public String storeLongitude;

    @SerializedName("offers")
    @Expose
    public List<Offer> offers = null;


    public class Offer {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("coupon_name")
        @Expose
        public String couponName;
        @SerializedName("coupon_desc")
        @Expose
        public String couponDesc;
        @SerializedName("discount")
        @Expose
        public String discount;

    }

    public class Category {

        @SerializedName("product_sub_category_id")
        @Expose
        public String catId;
        @SerializedName("subcategory_name")
        @Expose
        public String catName;
        @SerializedName("product")
        @Expose
        public List<Product> product = null;

    }

    public class Product {
        @SerializedName("product_id")
        @Expose
        public String productId;
        @SerializedName("product_name")
        @Expose
        public String productName;
        @SerializedName("company_name")
        @Expose
        public String companyName;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("price")
        @Expose
        public String price;
        @SerializedName("offer_price")
        @Expose
        public String offerPrice;
        @SerializedName("weight")
        @Expose
        public String weight;
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
        @SerializedName("is_customize_quantity")
        @Expose
        public Integer is_customize_quantity;

        @SerializedName("cart_quantity")
        @Expose
        public Integer cartQuantity;

        @SerializedName("cart_id")
        @Expose
        public String cartId;

        @SerializedName("in_out_of_stock_status")
        @Expose
        public String in_out_of_stock_status;

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
