package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreCategoriesModel {

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
    @SerializedName("service_name")
    @Expose
    public String serviceName;
    @SerializedName("store_name")
    @Expose
    public String storeName;
    @SerializedName("fssai")
    @Expose
    public String fssai;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("rating")
    @Expose
    public String rating;
    @SerializedName("delivery_time")
    @Expose
    public String deliveryTime;
    @SerializedName("offers")
    @Expose
    public List<Offer> offers = null;
    @SerializedName("category")
    @Expose
    public List<Category> category = null;

    public class Category {

        @SerializedName("cat_id")
        @Expose
        public String catId;
        @SerializedName("cat_name")
        @Expose
        public String catName;
        @SerializedName("cat_image")
        @Expose
        public String catImage;

    }

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
}
