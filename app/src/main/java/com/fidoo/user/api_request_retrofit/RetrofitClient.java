package com.fidoo.user.api_request_retrofit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient extends Application {

    private static final int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl, final Activity mContext) {

        if (okHttpClient == null)
            initOkHttp(mContext);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                   // .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    private static void initOkHttp(final Context mContext) {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                String mToken = "";
/*
                if (new SessionKookaApp(mContext).getLoggedInUserDetail() != null) {
                    mToken = new SessionKookaApp(mContext).getLoggedInUserDetail().getToken();
                    Log.wtf("request_auth_token", mToken);
                }*/

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json");
                       // .header("Authorization", "Bearer " + mToken); // <-- this is the important line

                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        });

        okHttpClient = httpClient.build();
    }


}
