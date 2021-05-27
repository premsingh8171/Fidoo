package com.fidoo.user.restaurants.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomizeProductResponseModel {
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

        @SerializedName("is_mandatory")
        @Expose
        public String isMandatory;

        @SerializedName("is_multiple")
        @Expose
        public String isMultiple;

        @SerializedName("max_selection_allowed")
        @Expose
        public String maxSelectionCount;

        @SerializedName("sub_cat")
        @Expose
        public List<SubCat> subCat = null;

    }

    public class SubCat {

        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("sub_cat_name")
        @Expose
        public String subCatName;

        @SerializedName("price")
        @Expose
        public String price;

    }
}
