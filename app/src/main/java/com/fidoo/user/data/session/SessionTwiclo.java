package com.fidoo.user.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.fidoo.user.data.SendResponse;
import com.fidoo.user.data.model.LoginModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.fidoo.user.profile.model.EditProfileModel;
import com.fidoo.user.data.model.VerificationModel;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class SessionTwiclo {

    private static final String PREF_NAME = "twiclo";
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private final int PRIVATE_MODE;
    private final Context _context;
    private final int securityQuestionLength = 0;

    public SessionTwiclo(Context context) {

         PRIVATE_MODE = 0;
        _context = context;

        if (_context != null) {
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();

        }

    }

    public Boolean isLoggedIn() {
        return pref.getBoolean("is_login", false);
    }

    public void setLoggedIn(boolean mFlag) {
        editor.putBoolean("is_login", mFlag);
        editor.commit();

    }

    public String getUserAddress() {
        return pref.getString("user_address", "");
    }

    public void setUserAddress(String mFlag) {
        editor.putString("user_address", mFlag);
        editor.commit();

    }

    public String getUserAddressId() {
        return pref.getString("user_address_id", "");
    }

    public void setUserAddressId(String mFlag) {
        editor.putString("user_address_id", mFlag);
        editor.commit();

    }

    public String getUserLat() {
        return pref.getString("user_lat", "");
    }

    public void setUserLat(String lat) {
        editor.putString("user_lat", lat);
        editor.commit();

    }

    public String getUserLng() {
        return pref.getString("user_lng", "");
    }

    public void setUserLng(String lng) {
        editor.putString("user_lng", lng);
        editor.commit();

    }

    public String getDeviceToken() {
        return pref.getString("device_token", "");
    }

    public void setDeviceToken(String token) {
        editor.putString("device_token", token);
        editor.commit();

    }


    public String getStoreId() {
        return pref.getString("store_id", "");
    }

    public void setStoreId(String store_id) {
        editor.putString("store_id", store_id);
        editor.commit();

    }

    public String getServiceId() {
        return pref.getString("service_id", "");
    }

    public void setServiceId(String service_id) {
        editor.putString("service_id", service_id);
        editor.commit();

    }

    /******
     * Store offline data so that we dont need to hit the api
     * *****/

    //   Store sub category and retreive sub category
    public void setMobileStatus(String mMobStatus) {
        VerificationModel mModel = getLoggedInUserDetail();
        //  mModel.getData().getUser().setMobile_status(mMobStatus);

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(mModel);
        editor.putString("store_logged_in_operator_details", jsonFavorites);
        editor.commit();
    }

    public void storeLoginDetail(LoginModel mModelCategory) {
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(mModelCategory);
        editor.putString("store_login_details", jsonFavorites);
        editor.commit();
    }

    public void storeProfileDetail(EditProfileModel mModelCategory) {
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(mModelCategory);
        editor.putString("store_profile_details", jsonFavorites);
        editor.commit();
    }

    public void storeLoggedInUserDetail(VerificationModel mModelCategory) {
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(mModelCategory);
        editor.putString("store_logged_in_operator_details", jsonFavorites);
        editor.commit();
    }

    //   To retrieve sub category details
    public VerificationModel getLoggedInUserDetail() {
        VerificationModel favorites;

        if (pref.contains("store_logged_in_operator_details")) {

            String jsonFavorites = pref.getString("store_logged_in_operator_details", null);
            Gson gson = new Gson();

            VerificationModel favoriteItems = gson.fromJson(jsonFavorites, VerificationModel.class);
            favorites = favoriteItems;

        } else {
            return null;
        }
        return favorites;
    }


    //   To retrieve sub category details
    public EditProfileModel getProfileDetail() {
        EditProfileModel favorites;

        if (pref.contains("store_profile_details")) {

            String jsonFavorites = pref.getString("store_profile_details", null);
            Gson gson = new Gson();

            EditProfileModel favoriteItems = gson.fromJson(jsonFavorites, EditProfileModel.class);
            favorites = favoriteItems;

        } else {
            return null;
        }
        return favorites;
    }


    public LoginModel getLoginDetail() {
        LoginModel favorites;

        if (pref.contains("store_login_details")) {

            String jsonFavorites = pref.getString("store_login_details", null);
            Gson gson = new Gson();

            LoginModel favoriteItems = gson.fromJson(jsonFavorites, LoginModel.class);
            favorites = favoriteItems;

        } else {
            return null;
        }
        return favorites;
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }


    public String getGuestLogin() {
        return pref.getString("guest_login", "");
    }

    public void setGuestLogin(String guest_login) {
        editor.putString("guest_login", guest_login);
        editor.commit();

    }

    public String getMobileno() {
        return pref.getString("mobile", "");
    }

    public void setMobileno(String mobile) {
        editor.putString("mobile", mobile);
        editor.commit();

    }


    public String getReferralId() {
        return pref.getString("referral_Id", "");
    }

    public void setReferralId(String referral_Id) {
        editor.putString("referral_Id", referral_Id);
        editor.commit();

    }

    //String
    public void saveRecentSearchArrayList(ArrayList<String> list, String key) {
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getRecentSearchArrayList(String key) {
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public String getStoreName() {
        return pref.getString("store_name", "");
    }

    public void setStoreName(String store_name) {
        editor.putString("store_name", store_name);
        editor.commit();
    }

    public String getStoreImg() {
        return pref.getString("store_img", "");
    }

    public void setStoreImg(String store_img) {
        editor.putString("store_img", store_img);
        editor.commit();
    }

    public String getOrderId() {
        return pref.getString("order_Id", "");
    }

    public void setOrderId(String order_Id) {
        editor.putString("order_Id", order_Id);
        editor.commit();
    }


    //String
    public void saveSendResponseList(ArrayList<SendResponse> list, String key) {
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<String> getSendResponseList(String key) {
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<ArrayList<SendResponse>>() {
        }.getType();
        return gson.fromJson(json, type);
    }




    //for rider polyline
    public void saveRiderLatLongArrayList(ArrayList<LatLng> list, String key) {
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<LatLng> getRiderLatLongArrayList(String key) {
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<ArrayList<LatLng>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

}
