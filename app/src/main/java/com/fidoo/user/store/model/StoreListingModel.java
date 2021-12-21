package com.fidoo.user.store.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreListingModel {

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
    @SerializedName("distance_start")
    @Expose
    public String distance_start;
    @SerializedName("distance_end")
    @Expose
    public String distance_end;

    @SerializedName("page_count")
    @Expose
    public String page_count;

    @SerializedName("more_value")
    @Expose
    public Boolean more_value;

    @SerializedName("total_count")
    @Expose
    public Integer total_count;

    @SerializedName("curations")
    @Expose
    public List<Curation> curations = null;

    @SerializedName("store_list")
    @Expose
    public List<StoreList> storeList = null;


    public class Curation {
        @SerializedName("cuisine_id")
        @Expose
        public String cuisineId;
        @SerializedName("cusine_name")
        @Expose
        public String cusineName;
        @SerializedName("image")
        @Expose
        public String image;
    }


    public class StoreList {
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("category_id")
        @Expose
        public String categoryId;
        @SerializedName("category_name")
        @Expose
        public String categoryName;
        @SerializedName("has_product_categories")
        @Expose
        public String has_product_categories;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("rating")
        @Expose
        public Float rating;
        @SerializedName("delivery_time")
        @Expose
        public Integer delivery_time;
        @SerializedName("delivery_distance")
        @Expose
        public Integer distance;

        @SerializedName("shop_no")
        @Expose
        public String shopNo;

        @SerializedName("locality")
        @Expose
        public String locality;

        @SerializedName("landmark")
        @Expose
        public String landmark;
        @SerializedName("city")
        @Expose
        public String city;
        @SerializedName("state")
        @Expose
        public String state;
        @SerializedName("pin")
        @Expose
        public String pin;

        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("store_opening_time")
        @Expose
        public String storeOpeningTime;
        @SerializedName("store_closing_time")
        @Expose
        public String storeClosingTime;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("open_close_status")
        @Expose
        public String open_close_status;
        @SerializedName("store_latitude")
        @Expose
        public String storeLatitude;
        @SerializedName("store_longitude")
        @Expose
        public String storeLongitude;
        @SerializedName("cuisines")
        @Expose
        public List<String> cuisines = null;

//        @SerializedName("appearnce_score")
//        @Expose
//        public Integer appearxnceScore;

        @SerializedName("couponsAvailable")
        @Expose
        public List<CouponsAvailable> couponsAvailable = null;
    }


    public class CouponsAvailable {

        @SerializedName("coupon_name")
        @Expose
        public String couponName;
        @SerializedName("discount_type")
        @Expose
        public String discountType;
        @SerializedName("discount")
        @Expose
        public String discount;
        @SerializedName("upto")
        @Expose
        public String upto;
        @SerializedName("mininum_amt")
        @Expose
        public String mininumAmt;
        @SerializedName("expiry_date")
        @Expose
        public String expiryDate;
        @SerializedName("coupon_desc")
        @Expose
        public String couponDesc;

    }


}
