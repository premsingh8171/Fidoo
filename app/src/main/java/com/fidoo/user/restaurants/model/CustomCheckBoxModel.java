package com.fidoo.user.restaurants.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomCheckBoxModel {
    @SerializedName("price")
    @Expose
    public String price;
    @SerializedName("id")
    @Expose
    public String id;

}
