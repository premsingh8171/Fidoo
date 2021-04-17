package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("error")
    @Expose
    public Boolean error;
    @SerializedName("error_code")
    @Expose
    public Integer errorCode;
    @SerializedName("phone_number")
    @Expose
    public String phoneNumber;
    @SerializedName("accountId")
    @Expose
    public Integer accountId;
    @SerializedName("accessToken")
    @Expose
    public String accessToken;
    @SerializedName("error_description")
    @Expose
    public String errorDescription;
    @SerializedName("account")
    @Expose
    public Account account;

    public class Account {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("user_name")
        @Expose
        public String userName;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("emailid")
        @Expose
        public String emailid;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("device_id")
        @Expose
        public String deviceId;
        @SerializedName("device_type")
        @Expose
        public String deviceType;
        @SerializedName("create_at")
        @Expose
        public String createAt;

    }



}

