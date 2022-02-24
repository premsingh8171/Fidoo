package com.fidoo.user.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fidoo.user.R
import com.fidoo.user.activity.AboutUsActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.data.SendResponse
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.profile.viewmodel.EditProfileViewModel
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.viewmodels.LoginViewModel
import com.google.gson.Gson
import com.vanillaplacepicker.utils.ToastUtils
import kotlinx.android.synthetic.main.fragment_signup_screen_fm.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class SignUpScreenFm : Fragment() {
    lateinit var mView: View
    lateinit var sessionTwiclo: SessionTwiclo
    private var _progressDlg: ProgressDialog? = null
    var userTrackViewModel: UserTrackerViewModel? = null
    var viewmodel: LoginViewModel? = null
    var viewmodel_signup: EditProfileViewModel? = null
    var list:ArrayList<SendResponse>?=null

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+")

    companion object {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_signup_screen_fm, container, false)
        viewmodel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        viewmodel_signup =
            ViewModelProvider(requireActivity()).get(EditProfileViewModel::class.java)
        sessionTwiclo = SessionTwiclo(requireContext())
        list= ArrayList()
        list=sessionTwiclo?.getSendResponseList("SendResponce") as ArrayList<SendResponse>
        Log.d("list__",list.toString())
        userTrackViewModel =
            ViewModelProvider(requireActivity()).get(UserTrackerViewModel::class.java)

        mView.tv_privacy_policy_signup.setOnClickListener {

        }

        mView.signUp_Btn.setOnClickListener {
            if (mView.enter_name_txt_new.text.toString()
                    .equals("") or mView.enter_name_txt_new.text.toString()
                    .equals(" ")
            ) {
                ToastUtils.showToast(requireContext(), "Please enter name number")
            } else if (!(isValidEmail(mView.enter_email.text.toString())) ) {
                ToastUtils.showToast(requireContext(), "Please enter valid email")
            } else {
                showIOSProgress()
                userTrackViewModel?.customerActivityLog(
                    "",
                    "", "Sign Up Screen",
                    SplashActivity.appversion, "", SessionTwiclo(requireContext()).deviceToken
                )

                var mImageParts: MultipartBody.Part? = null
                // creating image format for upload
                mImageParts = if (!TextUtils.isEmpty("")) {
                    // Pass it like this
                    val file = File("")
                    val requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData("photo", file.name, requestFile)
                } else {
                    val requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
                    MultipartBody.Part.createFormData("photo", "", requestFile)
                }
                // Parameter request body
                val accountId =
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        sessionTwiclo.loginDetail.accountId.toString()
                    )  // Parameter request body
                val accessToken =
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        sessionTwiclo.loginDetail.accessToken.toString()
                    )  // Parameter request body
                val name =
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        mView.enter_name_txt_new.text.toString()
                    )
                val email =
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        mView.enter_email.text.toString()
                    )

                viewmodel_signup?.addUpdateProfileApi(
                    accountId,
                    accessToken, name, email, mImageParts
                )
            }
        }

        mView.tv_privacy_policy_signup.setOnClickListener {
            val intent = Intent(requireContext(), AboutUsActivity::class.java).putExtra(
                "privacy_policy",
                "privacy_policy"
            )
            requireContext().startActivity(intent)
        }

        viewmodel_signup?.addUpdateResponse?.observe(requireActivity(), Observer { user ->
            Log.e("addUpdateResponse__", Gson().toJson(user))
            closeProgress()
            if (user.account == null) {
            } else {
                sessionTwiclo.isLoggedIn = true
                sessionTwiclo.storeProfileDetail(user)
//                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_LONG).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finishAffinity()
            }
        })

        return mView
    }

    private fun showIOSProgress() {
        closeProgress()
        _progressDlg = ProgressDialog(requireContext(), R.style.TransparentProgressDialog)
        _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        _progressDlg!!.setCancelable(false)
        _progressDlg!!.show()
    }

    private fun closeProgress() {
        if (_progressDlg == null) {
            return
        }
        _progressDlg!!.dismiss()
        _progressDlg = null
    }

    fun isValidEmail(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }


}