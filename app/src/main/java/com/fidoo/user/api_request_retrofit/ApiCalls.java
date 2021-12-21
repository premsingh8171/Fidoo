package com.fidoo.user.api_request_retrofit;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fidoo.user.data.model.LoginModel;
import com.fidoo.user.data.model.ResendModel;
import com.fidoo.user.data.model.VerificationModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ApiCalls {

    private final ApiInterface apiInterface;

    private final Activity mContext;

    public ApiCalls(Activity mContext) {
        this.mContext = mContext;
        apiInterface = ApiUtils.getApiServices(mContext);
    }

    // ##############
    //  Api's call
    // ##############
    public void registerUser(String fname, String lName, String mEmail, String mPwd, String mCellphone, String mGender, String mReferalCode, final Handler mHandler) {

        final Message msg = new Message();
        Call<ModelCommon> call = apiInterface.registerUser(fname, lName, mEmail,
                mPwd, mCellphone, "1", "", "", mGender,mReferalCode);
        call.enqueue(new Callback<ModelCommon>() {
            @Override
            public void onResponse(Call<ModelCommon> call, Response<ModelCommon> response) {

                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        Log.wtf("+++++++", "+++ ApiCalls registerOperator success responsebody +++" + response.body().getStatus());
                        Log.wtf("+++++++", "+++ ApiCalls registerOperator success responsebody +++" + response.body());
                        Log.wtf("+++++++", "+++ ApiCalls registerOperator success responsemessage +++" + response.body().getMessage());
                        msg.what = ApiInterface.REGISTER_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    } else {
                        Log.wtf("+++++++", "+++ ApiCalls registerOperator failure responsebody +++" + response.body().getStatus());
                        Log.wtf("+++++++", "+++ ApiCalls registerOperator failure responsemessage +++" + response.body().getMessage());
                        msg.what = ApiInterface.REGISTER_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelCommon> call, Throwable t) {
                Log.d("+++++++", "+++ ApiCalls registerOperator onFailure responsebody +++" + t.getMessage());
                msg.what = ApiInterface.REGISTER_FAILURE;
                msg.obj = t.getMessage();
                mHandler.sendMessage(msg);
            }
        });
    }

    public void forgotPassword(String mEmail, final Handler mHandler) {
        final Message msg = new Message();
        Call<ModelCommon> call = apiInterface.forgotPassword(mEmail);
        call.enqueue(new Callback<ModelCommon>() {
            @Override
            public void onResponse(Call<ModelCommon> call, Response<ModelCommon> response) {

                if (response.body() != null) {
                    if (response.body().getStatus()) {
                        Log.wtf("+++++++", "+++ ApiCalls forgotPassword success responsebody +++" + response.body().getStatus());
                        Log.wtf("+++++++", "+++ ApiCalls forgotPassword success responsebody +++" + response.body());
                        Log.wtf("+++++++", "+++ ApiCalls forgotPassword success responsemessage +++" + response.body().getMessage());
                        msg.what = ApiInterface.FORGOT_PASS_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    } else {
                        Log.wtf("+++++++", "+++ ApiCalls forgotPassword failure responsebody +++" + response.body().getStatus());
                        Log.wtf("+++++++", "+++ ApiCalls forgotPassword failure responsemessage +++" + response.body().getMessage());
                        msg.what = ApiInterface.FORGOT_PASS_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelCommon> call, Throwable t) {
                Log.d("+++++++", "+++ ApiCalls forgotPassword onFailure responsebody +++" + t.getMessage());
                msg.what = ApiInterface.FORGOT_PASS_FAILURE;
                msg.obj = t.getMessage();
                mHandler.sendMessage(msg);
            }
        });
    }


    public void loginUser(String country_code, String mobile_no, final Handler mHandler) {
        final Message msg = new Message();
        Log.e("yes","yes");
        Call<LoginModel> call = apiInterface.login(mobile_no, country_code, "fsdfsdf", "android");
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                Log.e("+++++++", "+++ ApiCalls loginUser  +++" + response.body());

                if (response.body() != null) {
                    if (!response.body().error) {
                        Log.wtf("+++++++", "+++ ApiCalls loginUser success responsebody +++" + response.body().accessToken);
                        Log.wtf("+++++++", "+++ ApiCalls loginUser success responsebody +++" + response.body());
                   //     Log.wtf("+++++++", "+++ ApiCalls loginUser success responsemessage +++" + response.body().getMessage());
                        msg.what = ApiInterface.LOGIN_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    } else {


                  //      Log.wtf("+++++++", "+++ ApiCalls loginUser failure responsebody +++" + response.body().getStatus());
                       // Log.wtf("+++++++", "+++ ApiCalls loginUser failure responsemessage +++" + response.body().errorDescription);
                        msg.what = ApiInterface.LOGIN_FAILURE;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Log.d("+++++++", "+++ ApiCalls loginUser onFailure responsebody +++" + t.getMessage());
                msg.what = ApiInterface.LOGIN_FAILURE;
                msg.obj = t.getMessage();
                mHandler.sendMessage(msg);
            }
        });
    }

    public void resendOtp(String accountId, String accessToken, final Handler mHandler) {
        final Message msg = new Message();
        Log.e("yes","yes");
        Call<ResendModel> call = apiInterface.resendOtp(accountId, accessToken);
        call.enqueue(new Callback<ResendModel>() {
            @Override
            public void onResponse(Call<ResendModel> call, Response<ResendModel> response) {
                Log.e("+++++++", "+++ ApiCalls loginUser  +++" + response.body());

                if (response.body() != null) {
                    if (!response.body().error) {
                        Log.wtf("+++++++", "+++ ApiCalls loginUser success responsebody +++" + response.body().accessToken);
                        Log.wtf("+++++++", "+++ ApiCalls loginUser success responsebody +++" + response.body());
                   //     Log.wtf("+++++++", "+++ ApiCalls loginUser success responsemessage +++" + response.body().getMessage());
                        msg.what = ApiInterface.RESEND_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    } else {
                  //      Log.wtf("+++++++", "+++ ApiCalls loginUser failure responsebody +++" + response.body().getStatus());
                        Log.wtf("+++++++", "+++ ApiCalls loginUser failure responsemessage +++" + response.body().message);
                        msg.what = ApiInterface.RESEND_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResendModel> call, Throwable t) {
                Log.d("+++++++", "+++ ApiCalls loginUser onFailure responsebody +++" + t.getMessage());
                msg.what = ApiInterface.LOGIN_FAILURE;
                msg.obj = t.getMessage();
                mHandler.sendMessage(msg);
            }
        });
    }

    public void verificationUser(String accessToken, String id,String otp, final Handler mHandler) {
        final Message msg = new Message();
        Log.e("otp___",accessToken+"---"+id+"--"+otp);
        Call<VerificationModel> call = apiInterface.verification(id,accessToken , otp
        );
        call.enqueue(new Callback<VerificationModel>() {
            @Override
            public void onResponse(Call<VerificationModel> call, Response<VerificationModel> response) {
                Log.e("+++++++", "+++ ApiCalls loginUser  +++" + response.body());

                if (response.body() != null) {
                    if (!response.body().error) {
                        Log.wtf("+++++++", "+++ ApiCalls loginUser success responsebody +++" + response.body().accessToken);
                        Log.wtf("+++++++", "+++ ApiCalls loginUser success responsebody +++" + response.body());
                   //     Log.wtf("+++++++", "+++ ApiCalls loginUser success responsemessage +++" + response.body().getMessage());
                        msg.what = ApiInterface.OTP_SUCCESS;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    } else {
                  //      Log.wtf("+++++++", "+++ ApiCalls loginUser failure responsebody +++" + response.body().getStatus());
                        Log.wtf("+++++++", "+++ ApiCalls loginUser failure responsemessage +++" + response.body().errorMessage);
                        msg.what = ApiInterface.OTP_FAILURE;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<VerificationModel> call, Throwable t) {
                Log.d("+++++++", "+++ ApiCalls loginUser onFailure responsebody +++" + t.getMessage());
                msg.what = ApiInterface.LOGIN_FAILURE;
                msg.obj = t.getMessage();
                mHandler.sendMessage(msg);
            }
        });
    }

}