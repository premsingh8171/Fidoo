package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerModel {
    @SerializedName("error")
    @Expose
    public Boolean error;
    @SerializedName("accountId")
    @Expose
    public String accountId;
    @SerializedName("accessToken")
    @Expose
    public String accessToken;
    @SerializedName("banner")
    @Expose
    public List<String> banner = null;
}
