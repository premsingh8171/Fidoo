package com.fidoo.user.ui

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.fidoo.user.R
import com.fidoo.user.data.SendResponse
import com.fidoo.user.data.model.*
import com.fidoo.user.utils.SmsBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.fragment_otp.view.*


class OtpFragment : com.fidoo.user.utils.BaseFragment() {


    lateinit var mView: View
    var otpTemp: String = ""
    val args: OtpFragmentArgs by navArgs()
    lateinit var mData: SendResponse
    lateinit var smsBroadcastReceiver: SmsBroadcastReceiver
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInmilliSeconds: Long = 60000
    companion object{
        const val TAG = "SMS_USER_CONSENT"

        const val REQ_USER_CONSENT = 100
    }


    override fun provideYourFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater!!.inflate(R.layout.fragment_otp, parent, false)

        mData = args.data
        mView.otp_view?.requestFocusOTP()

        startSmsUserConsent()

        mView.otp_view?.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                mView.btn_continue.visibility = View.VISIBLE

            }

            override fun onOTPComplete(otp: String) {
                otpTemp = otp
            }
        }
        startTimer()
        mView.tv_resendOtp.setOnClickListener {
            resendApi()

        }

        mView.tv_phone.text = mData.mobile


        mView.btn_continue.setOnClickListener {
            when {
                mView.otp_view.otp.equals("") -> {
                    Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
                }
                mView.otp_view.otp?.length != 6 -> {
                    Toast.makeText(
                        requireContext(),
                        "Please enter complete OTP",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                else -> {
                    verificationApi(mView.otp_view.otp)
                }
            }
        }

        return mView
    }


    @SuppressLint("handlerLeak")
    private val mApiHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                com.fidoo.user.api_request_retrofit.ApiInterface.RESEND_SUCCESS -> {
                    dismissIOSProgress()
                    val mModelData: ResendModel = msg.obj as ResendModel
                    Toast.makeText(requireContext(), mModelData.message, Toast.LENGTH_SHORT).show()

                }
                com.fidoo.user.api_request_retrofit.ApiInterface.OTP_SUCCESS -> {
                    dismissIOSProgress()

                    val mModelData: VerificationModel = msg.obj as VerificationModel
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
                    val mModelData: VerificationModel = msg.obj as VerificationModel
                    showToast(mModelData.errorMessage)
                }

            }
        }
    }

    private fun verificationApi(otp: String?) {
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
        startTimer()
    }

    private fun startSmsUserConsent() {
        SmsRetriever.getClient(requireActivity()).also {
            //We can add sender phone number or leave it blank
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    Log.d(TAG, "LISTENING_SUCCESS")
                }
                .addOnFailureListener {
                    Log.d(TAG, "LISTENING_FAILURE")
                }
        }
    }

    private fun registerToSmsBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver().also {broadcast ->
            broadcast.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    try {
                        intent?.let { context -> startActivityForResult(context, REQ_USER_CONSENT) }

                    }catch (e:Exception){}
                }

                override fun onFailure() {
                }
            }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        context?.registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInmilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInmilliSeconds = millisUntilFinished
                updateTimer1()
            }

            override fun onFinish() {
                mView?.timer_otp.setVisibility(View.GONE)
                mView?.resendOtp.setVisibility(View.VISIBLE)
                mView?.tv_resendOtp.setVisibility(View.VISIBLE)
                updateTimer1()
                timeLeftInmilliSeconds = 60000
            }
        }.start()
    }
    fun updateTimer1() {
        val minute = (timeLeftInmilliSeconds / 60000).toInt()
        val seconds = (timeLeftInmilliSeconds % 60000 / 1000).toInt()
        var timeLeftText: String
        timeLeftText = "" + minute
        timeLeftText += ":"
        if (seconds < 10) timeLeftText += "0"
        timeLeftText += seconds
        mView?.timer_otp.setText("$timeLeftText left")
        mView?.timer_otp.setVisibility(View.VISIBLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_USER_CONSENT -> {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    //That gives all message to us. We need to get the code from inside with regex
               try {
                   val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                   val code = message?.let { fetchVerificationCode(it) }

                   if (code != null) {
                       mView.otp_view.setOTP(code)
                       if (mView.otp_view.otp?.length != 6) {
                           Toast.makeText(
                               requireContext(),
                               "Please enter complete OTP",
                               Toast.LENGTH_SHORT
                           ).show()

                       }
                       else{
                           verificationApi(mView.otp_view.otp)
                       }

                   }
               }catch (e:Exception){e.printStackTrace()}

                }
            }
        }
    }

    private fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{6})").find(message)?.value ?: ""
    }

    override fun onStart() {
        super.onStart()
        registerToSmsBroadcastReceiver()
    }

}