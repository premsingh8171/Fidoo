package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddCartInputModelFinal {

    @SerializedName("accountId")
    @Expose
    public String accountId;
    @SerializedName("accessToken")
    @Expose
    public String accessToken;
    @SerializedName("products")
    @Expose
    public List<Product> products = new ArrayList<Product>();
    @SerializedName("cart_id")
    @Expose
    public String cart_id;

}
