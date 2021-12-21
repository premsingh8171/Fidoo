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

    @SerializedName("show_slider")
    @Expose
    public String show_slider;
    @SerializedName("show_send_package")
    @Expose
    public String show_send_package;

    @SerializedName("banner")
    @Expose
    public List<String> banner = null;
}
