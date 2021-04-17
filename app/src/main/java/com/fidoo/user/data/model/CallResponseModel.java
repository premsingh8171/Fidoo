package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallResponseModel {


    @SerializedName("error")
    @Expose
    public Boolean error;

    @SerializedName("error_code")
    @Expose
    public Integer error_code;

    @SerializedName("accountId")
    @Expose
    public String accountId;

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("message")
    @Expose
    public String message;

}

