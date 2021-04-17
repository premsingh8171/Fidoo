package com.fidoo.user.api_request_retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelCommon {


    @Expose
    @SerializedName("status")
    private boolean status;

    @Expose
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public boolean getStatus() {
        return status;
    }

}
