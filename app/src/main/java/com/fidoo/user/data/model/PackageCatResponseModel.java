package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PackageCatResponseModel {
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
    @SerializedName("categories_list")
    @Expose
    public List<CategoriesList> categoriesList = null;

    public class CategoriesList {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("cat_name")
        @Expose
        public String catName;

    }
}
