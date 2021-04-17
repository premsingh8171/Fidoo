package com.fidoo.user.data.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionNotNull {

    private static final String PREF_NAME = "twiclo_device_token";
    private final SharedPreferences.Editor editor;
    private final SharedPreferences pref;
    private final int PRIVATE_MODE;
    private final Context _context;
    private final int securityQuestionLength = 0;

    public SessionNotNull(Context context) {

        PRIVATE_MODE = 0;
        _context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }


    public Boolean isRememberMe() {
        return pref.getBoolean("is_remember_me", false);
    }

    public void setRememberMe(boolean mFlag) {
        editor.putBoolean("is_remember_me", mFlag);
        editor.commit();
    }

    public String getEmailRememberMe() {
        return pref.getString("EmailRememberMe", "");
    }

    public void setEmailRememberMe(String mEmail) {
        editor.putString("EmailRememberMe", mEmail);
        editor.commit();

    }


    public String getPwdRememberMe() {
        return pref.getString("PwdRememberMe", "");
    }

    public void setPwdRememberMe(String mPwd) {
        editor.putString("PwdRememberMe", mPwd);
        editor.commit();
    }


    public String getAfterSignupToken() {
        return pref.getString("after_signup_token", "");
    }

    public void setAfterSignupToken(String mToken) {
        editor.putString("after_signup_token", mToken);
        editor.commit();
    }


    public Boolean isAfterSignup() {
        return pref.getBoolean("is_after_signup", false);
    }

    public void setAfterSignup(boolean mFlag) {
        editor.putBoolean("is_after_signup", mFlag);
        editor.commit();

    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public String getDeviceFCMToken() {
        return pref.getString("device_fcm_token", "dummy");
    }


    public void setDeviceFCMToken(String token) {
        editor.putString("device_fcm_token", token);
        editor.commit();
    }
}
