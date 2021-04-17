package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeServicesModel {
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
    @SerializedName("service_list")
    @Expose
    public List<ServiceList> serviceList = null;

    @SerializedName("default_address")
    @Expose
    public DefaultAddress defaultAddress;
    public class ServiceList {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("service_name")
        @Expose
        public String serviceName;
        @SerializedName("image")
        @Expose
        public String image;

    }
    public class DefaultAddress {

        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("phone_no")
        @Expose
        public String phoneNo;
        @SerializedName("flat_no")
        @Expose
        public String flatNo;
        @SerializedName("building")
        @Expose
        public String building;
        @SerializedName("address_id")
        @Expose
        public String address_id;
        @SerializedName("landmark")
        @Expose
        public String landmark;
        @SerializedName("address_type")
        @Expose
        public String addressType;
        @SerializedName("latitude")
        @Expose
        public String latitude;
        @SerializedName("longitude")
        @Expose
        public String longitude;

    }
}
