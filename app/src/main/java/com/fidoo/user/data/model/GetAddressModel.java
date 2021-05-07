package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAddressModel {

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
    @SerializedName("address_list")
    @Expose
    public List<AddressList> addressList = null;

    public class AddressList {

        @SerializedName("flat_no")
        @Expose
        public String flatNo;
        @SerializedName("building")
        @Expose
        public String building;
        @SerializedName("location")
        @Expose
        public String location;
        @SerializedName("phone_no")
        @Expose
        public String phone_no;
        @SerializedName("latitude")
        @Expose
        public String latitude;
        @SerializedName("longitude")
        @Expose
        public String longitude;
        @SerializedName("landmark")
        @Expose
        public String landmark;
        @SerializedName("address_type")
        @Expose
        public String addressType;
        @SerializedName("is_default")
        @Expose
        public String is_default;
        @SerializedName("id")
        @Expose
        public String id;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("email_id")
        @Expose
        public String email_id;

        @SerializedName("in_delivery_range")
        @Expose
        public String inDeliveryRange;

    }
}
