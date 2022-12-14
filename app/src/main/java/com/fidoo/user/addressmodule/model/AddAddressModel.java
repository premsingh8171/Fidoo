package com.fidoo.user.addressmodule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddAddressModel {
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
    @SerializedName("address_id")
    @Expose
    public String addressId;
    @SerializedName("message")
    @Expose
    public String message;
}
