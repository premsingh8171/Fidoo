package com.fidoo.user.data.model;

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
    @SerializedName("store_list")
    @Expose
    public List<StoreList> storeList = null;

    public class StoreList {

        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("category_name")
        @Expose
        public String categoryName;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("address")
        @Expose
        public String address;

        @SerializedName("rating")
        @Expose
        public String rating;

        @SerializedName("delivery_time")
        @Expose
        public String delivery_time;

        @SerializedName("status")
        @Expose
        public String status;

        @SerializedName("open_close_status")
        @Expose
        public String open_close_status;

        @SerializedName("store_closing_time")
        @Expose
        public String store_closing_time;

        @SerializedName("has_product_categories")
        @Expose
        public String has_product_categories;

        @SerializedName("cuisines")
        @Expose
        public List<String> cuisines = null;

    }

}
