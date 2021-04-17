package com.fidoo.user.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerificationModel {
    @SerializedName("error")
    @Expose
    public Boolean error;
    @SerializedName("error_code")
    @Expose
    public Integer errorCode;
    @SerializedName("error_description")
    @Expose
    public String errorMessage;
    @SerializedName("accountId")
    @Expose
    public String accountId;
    @SerializedName("accessToken")
    @Expose
    public String accessToken;
}
