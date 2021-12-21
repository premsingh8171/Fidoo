package com.fidoo.user.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditProfileModel {
    @SerializedName("error")
    @Expose
    public Boolean error;
    @SerializedName("error_code")
    @Expose
    public Integer errorCode;
    @SerializedName("accountId")
    @Expose
    public String accountId;
    @SerializedName("accessToken")
    @Expose
    public String accessToken;
    @SerializedName("message")
    public String message;
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
        public String name="";
        @SerializedName("emailid")
        @Expose
        public String emailid="";
        @SerializedName("image")
        @Expose
        public String image="";
        @SerializedName("device_id")
        @Expose
        public String deviceId;
        @SerializedName("device_type")
        @Expose
        public String deviceType;
        @SerializedName("create_at")
        @Expose
        public String createAt;
        @SerializedName("support_number")
        @Expose
        public String support_number;
        @SerializedName("country_code")
        @Expose
        public String country_code="";

    }
}
