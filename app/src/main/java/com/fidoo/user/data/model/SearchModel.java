package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchModel {
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
    @SerializedName("product_list")
    @Expose
    public List<ProductList> productList = null;
    public class ProductList {

        @SerializedName("product_id")
        @Expose
        public String productId;
        @SerializedName("product_name")
        @Expose
        public String productName;
        @SerializedName("product_desc")
        @Expose
        public String productDesc;
        @SerializedName("product_image")
        @Expose
        public String productImage;
        @SerializedName("price")
        @Expose
        public String price;
        @SerializedName("offer_price")
        @Expose
        public String offerPrice;
        @SerializedName("company_name")
        @Expose
        public String companyName;
        @SerializedName("weight")
        @Expose
        public String weight;
        @SerializedName("unit")
        @Expose
        public String unit;
        @SerializedName("category_name")
        @Expose
        public String categoryName;
        @SerializedName("store_id")
        @Expose
        public String storeId;
        @SerializedName("store_name")
        @Expose
        public String storeName;
        @SerializedName("store_address")
        @Expose
        public String storeAddress;
        @SerializedName("store_image")
        @Expose
        public String storeImage;
        @SerializedName("service_name")
        @Expose
        public String serviceName;
        @SerializedName("is_customize")
        @Expose
        public String isCustomize;
        @SerializedName("is_prescription")
        @Expose
        public String isPrescription;
        @SerializedName("is_nonveg")
        @Expose
        public String isNonveg;
        @SerializedName("cart_quantity")
        @Expose
        public Integer cartQuantity;
        @SerializedName("is_customize_quantity")
        @Expose
        public Integer isCustomizeQuantity;
        @SerializedName("rating")
        @Expose
        public String rating;
        @SerializedName("delivery_time")
        @Expose
        public String deliveryTime;

    }
}
