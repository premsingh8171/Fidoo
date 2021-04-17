package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TempProductListModel {
    @SerializedName("price")
    @Expose
    public String price;
    @SerializedName("productId")
    @Expose
    public String productId;
    @SerializedName("quantity")
    @Expose
    public String quantity;

}
