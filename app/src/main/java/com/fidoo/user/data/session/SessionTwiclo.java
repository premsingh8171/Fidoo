package com.fidoo.user.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.fidoo.user.profile.EditProfileModel;
import com.fidoo.user.data.model.VerificationModel;


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
        if(_context!=null) {
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


    // Crash on pressing on search icon twice

    public void setStoreId(String store_id) {
        editor.putString("store_id", store_id);
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


    public void clearSession() {
        editor.clear();
        editor.commit();
    }


}
