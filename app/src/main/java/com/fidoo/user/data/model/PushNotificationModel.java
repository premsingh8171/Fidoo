package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushNotificationModel {
    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("is_background")
    @Expose
    public Boolean isBackground;

    @SerializedName("payload")
    @Expose
    public Object payload;

    @SerializedName("orderid")
    @Expose
    public String orderid;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("orderacceptreject")
    @Expose
    public String orderacceptreject;

    @SerializedName("timestamp")
    @Expose
    public String timestamp;

    @SerializedName("screen")
    @Expose
    public String screen;

    @SerializedName("service_id")
    @Expose
    public String service_id;
}
