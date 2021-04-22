package com.fidoo.user.ui

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.fidoo.user.R
import com.fidoo.user.data.SendResponse
import kotlinx.android.synthetic.main.fragment_otp.view.*
import kotlinx.android.synthetic.main.fragment_otp.view.tv_resendOtp


class OtpFragment : com.fidoo.user.utils.BaseFragment() {


    lateinit var mView: View
    var otpTemp: String = ""
    val args: OtpFragmentArgs by navArgs()
    lateinit var mData: SendResponse


    override fun provideYourFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater!!.inflate(R.layout.fragment_otp, parent, false)

        mData = args.data
        mView.otp_view?.requestFocusOTP()

        mView.otp_view?.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                otpTemp = otp
            }
        }
        mView.tv_resendOtp.setOnClickListener {
            resendApi()
        }

        mView.tv_phone.text = mData.mobile


        mView.btn_continue.setOnClickListener(View.OnClickListener {
            if (mView.otp_view.otp.equals("")) {
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else
                if (mView.otp_view.otp?.length != 6) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter complete OTP",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    verificationApi(mView.otp_view.otp)
                }
        })

        return mView
    }


    @SuppressLint("handlerLeak")
    private val mApiHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                com.fidoo.user.api_request_retrofit.ApiInterface.RESEND_SUCCESS -> {
                    dismissIOSProgress()
                    val mModelData: com.fidoo.user.data.model.ResendModel = msg.obj as com.fidoo.user.data.model.ResendModel
                    Toast.makeText(requireContext(), mModelData.message, Toast.LENGTH_SHORT).show()

                }
                com.fidoo.user.api_request_retrofit.ApiInterface.OTP_SUCCESS -> {
                    dismissIOSProgress()

                    val mModelData: com.fidoo.user.data.model.VerificationModel = msg.obj as com.fidoo.user.data.model.VerificationModel
                    // store user details
                    sessionInstance.storeLoggedInUserDetail(mModelData)

                    // Send the control to next move
                    sessionInstance.isLoggedIn = true

                    goForVerificationScreen(
                        MainActivity::class.java,
                        mData.accesstoken,
                        mData.id,
                        mData.mobile,
                        mData.otp
                    )
                }
                com.fidoo.user.api_request_retrofit.ApiInterface.OTP_FAILURE -> {
                    dismissIOSProgress()
                    val mModelData: com.fidoo.user.data.model.VerificationModel = msg.obj as com.fidoo.user.data.model.VerificationModel
                    showToast(mModelData.errorMessage)
                }

            }
        }
    }


    fun verificationApi(otp: String?) {
        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))
            return
        }
        showIOSProgress()
        //TODO
        restfullInstance.verificationUser(
            mData.accesstoken,
            mData.id,
            otp,
            mApiHandler
        )
    }

    fun resendApi() {
        if (!isNetworkConnected) {
            showToast(resources.getString(R.string.provide_internet))
            return
        }
        showIOSProgress()
        //TODO
        restfullInstance.resendOtp(
            mData.id, mData.accesstoken,
            mApiHandler
        )
    }

}