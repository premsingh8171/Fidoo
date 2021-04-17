package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationResponseModel {
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
    @SerializedName("order_id")
    @Expose
    public String orderId;
    @SerializedName("merchant_latitude")
    @Expose
    public String merchantLatitude;
    @SerializedName("merchant_longitude")
    @Expose
    public String merchantLongitude;
    @SerializedName("driver_latitude")
    @Expose
    public String driverLatitude;
    @SerializedName("driver_longitude")
    @Expose
    public String driverLongitude;
    @SerializedName("user_latitude")
    @Expose
    public String userLatitude;
    @SerializedName("user_longitude")
    @Expose
    public String userLongitude;
    @SerializedName("order_create_time")
    @Expose
    public String order_create_time;
    @SerializedName("driver_name")
    @Expose
    public String driver_name;
    @SerializedName("driver_mobile")
    @Expose
    public String driver_mobile;
}
