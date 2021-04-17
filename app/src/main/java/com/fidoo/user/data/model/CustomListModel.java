package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomListModel {
    @SerializedName("price")
    @Expose
    public String price;
    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("id")
    @Expose
    public Integer id;

}
