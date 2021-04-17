package com.fidoo.user.api_request_retrofit;

import com.fidoo.user.data.model.LoginModel;
import com.fidoo.user.data.model.ResendModel;
import com.fidoo.user.data.model.VerificationModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    int ALL_INTERESTS_SUCCESS = 1;
    int ALL_INTERESTS_FAILURE = 2;

    int REGISTER_SUCCESS = 3;
    int REGISTER_FAILURE = 4;

    int LOGIN_SUCCESS = 5;
    int LOGIN_FAILURE = 6;

    int RESEND_SUCCESS = 532;
    int RESEND_FAILURE = 643;

    int OTP_SUCCESS = 55;
    int OTP_FAILURE = 66;

    int LOGOUT_SUCCESS = 7;
    int LOGOUT_FAILURE = 8;

    int FORGOT_PASS_SUCCESS = 9;
    int FORGOT_PASS_FAILURE = 10;

    int ACTIVATE_USER_SUCCESS = 11;

    int FETCH_MATCHES_SUCCESS = 12;
    int FETCH_MATCHES_FAILURE = 13;

    int MATCH_USER_SUCCESS = 14;
    int MATCH_USER_FAILURE = 15;

    int GET_PROFILE_SUCCESS = 16;
    int GET_PROFILE_FAILURE = 17;

    int UPLOAD_GALLARY_IMAGE_SUCCESS = 18;


    int FETCH_COUNTRY_SUCCESS = 19;
    int FETCH_COUNTRY_FAILURE = 20;


    int GET_FILTERS_SUCCESS = 22;
    int GET_FILTERS_FAILURE = 24;


    int UPDATE_FILTER_SUCCESS = 25;
    int UPDATE_FILTER_FAILURE = 26;

    int REFERRALS_SUCCESS = 27;
    int REFERRALS_FAILURE = 28;

    int PROFILE_SUCCESS = 29;
    int PROFILE_FAILURE = 30;

    int PRICE_PACKAGE_SUCCESS = 31;
    int PRICE_PACKAGE_FAILURE = 32;

    int GET_NOTIFICATIONS_SUCCESS = 33;
    int GET_NOTIFICATIONS_FAILURE = 34;

    int CHAT_SUCCESS = 35;
    int CHAT_FAILURE = 36;

    int SEND_MSG_SUCCESS = 37;
    int SEND_MSG_FAILURE = 38;

    int RECEIVE_MSG_SUCCESS = 39;

    int SEARCH_USERS_ON_FILTER_SUCCESS = 40;
    int SEARCH_USERS_ON_FILTER_FAILURE = 41;

    int MY_PACKAGE_SUCCESS = 42;
    int BUY_PACKAGE_SUCCESS = 43;

    int FETCH_RATING_USER_SUCCESS = 44;
    int FETCH_RATING_USER_FAILURE = 45;

    int RATE_USER_SUCCESS = 46;
    int _FAILURE = 47;

    int COIN_PACKAGE_SUCCESS = 48;
    int REWARDED_VIDEO_SUCCESS = 49;

    int OUR_ROOM_SUCCESS = 50;
    int OUR_ROOM_FAILURE = 51;

    int AFTER_CALL_CUT_SUCCESS = 52;
    int CALL_BUSY_SUCCESS = 54;
    int REPORT_USER = 55;
    int FIND_QR_FAILURE = 53;


    @FormUrlEncoded
    @POST("auth/register")
    Call<ModelCommon> registerUser(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone_no,
            @Field("is_term_accept") String is_term_accept,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("gender") String gender,
            @Field("referral_code") String referral_code);


    @FormUrlEncoded
    @POST("auth/password/email")
    Call<ModelCommon> forgotPassword(
            @Field("email") String email);


    @FormUrlEncoded
    @POST("login.inc.php")
    Call<LoginModel> login(
            @Field("username") String username,
            @Field("country_code") String country_code,
            @Field("device_id") String device_id,
            @Field("device_type") String device_type);

    @FormUrlEncoded
    @POST("otpResend.inc.php")
    Call<ResendModel> resendOtp(
            @Field("accountId") String accountId,
            @Field("accessToken") String accessToken);

    @FormUrlEncoded
    @POST("otpVerify.inc.php")
    Call<VerificationModel> verification(
            @Field("accountId") String accountId,
            @Field("accessToken") String accessToken,
            @Field("otp") String otp
           );



}
