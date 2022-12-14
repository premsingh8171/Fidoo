package com.fidoo.user.api_request_retrofit;

import com.fidoo.user.BuildConfig;
import android.app.Activity;

public class ApiUtils {
    private ApiUtils() {
    }

    //   Old Url = 12.196.5.118/myflow/public
    //   Live Url = http://80.241.218.224
    //   http://pint.com.co/
    //   URL :- 112.196.5.118/myhaven/api/   http://112.196.5.115/

    public static ApiInterface getApiServices(Activity mContext) {
        //return RetrofitClient.getClient("https://fidoo.in/api/method/",mContext).create(ApiInterface.class);
        return RetrofitClient.getClient(BuildConfig.BASE_URL,mContext).create(ApiInterface.class);

    }
}

