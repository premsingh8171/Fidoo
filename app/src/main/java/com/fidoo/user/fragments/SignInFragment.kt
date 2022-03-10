package com.fidoo.user.fragments


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.fidoo.user.R
import com.fidoo.user.activity.AboutUsActivity
import com.fidoo.user.activity.MainActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.api_request_retrofit.ApiInterface
import com.fidoo.user.data.SendResponse
import com.fidoo.user.data.model.LoginModel
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.databinding.FragmentSignInBinding
import com.fidoo.user.fragments.OtpFragment.Companion.backhanlde
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.utils.CommonUtils.Companion.hideKeyboard
import com.fidoo.user.utils.CustomProgressDialog
import com.fidoo.user.viewmodels.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.vanillaplacepicker.utils.ToastUtils.showToast
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class SignInFragment : Fragment() {
    var mmContext: Context? = null

    lateinit var layout: ConstraintLayout
    lateinit var pref: SessionTwiclo
    private var _progressDlg: ProgressDialog? = null

    // assign the _binding variable initially to null and
    // also when the view is destroyed again it has to be set to null
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    var viewmodel: LoginViewModel? = null
    var userTrackViewModel: UserTrackerViewModel? = null

    var customeProgressDialog: CustomProgressDialog? = null
    var where: String? = ""
    var sessionTwiclo: SessionTwiclo? = null
    var list: ArrayList<SendResponse>? = null

    private var mMixpanel: MixpanelAPI? = null

    private val props = JSONObject()
    lateinit var sendData: SendResponse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // inflate the layout and bind to the _binding
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        pref = getSessionInstance()
        sessionTwiclo = SessionTwiclo(requireContext())
        list = ArrayList()

        mMixpanel = MixpanelAPI.getInstance(requireContext(), "defeff96423cfb1e8c66f8ba83ab87fd")

        mMixpanel?.track("Login Screen Opened", null)


        //  sessionTwiclo!!.clearSession()
        // val activity = context as Activity
        //  layout = activity.findViewById<ConstraintLayout>(R.id.fidooTxtLayout_auth)

        //   mView = inflater.inflate(R.layout.fragment_sign_in, container, false)


        try {
            FirebaseApp.initializeApp(requireActivity())
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        "isnotSuccessful",
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                if (!token.equals("") && !token.equals("null")) {
                    SessionTwiclo(requireActivity()).deviceToken = token
                }
                // Log and toast
                // val msg = getString(R.string.msg_token_fmt, token)
                Log.d("ttoken_oken", token)
                //  Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })
        } catch (e: Exception) {
        }


        binding.btnSignIn.setOnClickListener {


            props.put("mobile number", binding.phone.text.toString())
            props.put("referral ID", sessionTwiclo!!.referralId)
            mMixpanel?.track("Login button clicked", null)

            if (!isNetworkConnected()) {
                showToast(requireContext(), resources.getString(R.string.provide_internet))
            } else {
                //   startActivity(Intent(this,VerificationActivity::class.java))
                if ((binding.phone.text.toString() == "") or (binding.phone.text.toString() == " ")
                ) {
                    showToast(requireContext(), "Please enter mobile number")
                } else if (binding.phone.text.toString().length != 10) {
                    showToast(requireContext(), "Please enter valid number")
                } else if (binding.phone.text.toString().startsWith("0")) {
                    showToast(requireContext(), "Number can't be starts with zero")
                } else {
                    showIOSProgress()

                    userTrackViewModel?.customerActivityLog(
                        "",
                        binding.phone.text.toString().trim(), "SignIn Screen",
                        SplashActivity.appversion, "", SessionTwiclo(requireContext()).deviceToken
                    )

                    viewmodel!!.login(
                        "+91",
                        sessionTwiclo!!.deviceToken,
                        sessionTwiclo!!.referralId,binding.phone.text.toString().trim()
                    )

                }


            }

        }

//        binding.tvSignUp.setOnClickListener {
//            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
//        }


        /*  binding.tvSkip.setOnClickListener {
              val intent = Intent(requireContext(), MainActivity::class.java)
              pref.guestLogin = "guest"
              requireContext().startActivity(intent)
              requireActivity().finish()
          }*/

        binding.tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(requireContext(), AboutUsActivity::class.java).putExtra(
                "privacy_policy",
                "privacy_policy"
            )
            requireContext().startActivity(intent)
            //requireActivity().finish()
        }

        viewmodel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        userTrackViewModel =
            ViewModelProvider(requireActivity()).get(UserTrackerViewModel::class.java)
        binding.viewmodel = viewmodel

        binding.mainLayout.setOnClickListener {

            hideKeyboard()
        }
        customeProgressDialog = CustomProgressDialog(requireContext())



        viewmodel?.progressDialog?.observe(this, {
            if (it!!) customeProgressDialog?.show() else customeProgressDialog?.dismiss()
        })


        viewmodel?.userLogin?.observe(requireActivity(), { user ->
            dismissIOSProgress()
            Log.e("loginres_", Gson().toJson(user))
            val mModelData: LoginModel = user
            pref.storeLoginDetail(mModelData)
            pref.guestLogin = "userlogin"

            sendData = SendResponse(
                mModelData.accessToken,
                mModelData.accountId.toString(),
                binding.phone.text.toString().trim(),
                "+91", mModelData.is_new_user
            )

            list?.add(sendData)
            sessionTwiclo!!.saveSendResponseList(list, "SendResponce")
            sessionTwiclo!!.setbackMobileno(binding.phone.text.toString().trim())


            val action = SignInFragmentDirections.actionSignInFragmentToOtpFragment(sendData)
            closeProgress()



            try {
                findNavController().navigate(action)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })


        viewmodel?.failureResponse?.observe(requireActivity(), {
            dismissIOSProgress()
        })

        return binding.root
    }

    @SuppressLint("handlerLeak")
    private val mApiHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                ApiInterface.LOGIN_SUCCESS -> {
                    dismissIOSProgress()

                    val mModelData: LoginModel = msg.obj as LoginModel
                    val sendData = SendResponse(
                        mModelData.accessToken,
                        mModelData.account.id,
                        binding.phone.text.toString().trim(),
                        "+91", mModelData.is_new_user
                    )

                    val action =
                        SignInFragmentDirections.actionSignInFragmentToOtpFragment(sendData)


                    findNavController().navigate(action)


                    /*goForVerificationScreen(
                        VerificationActivity::class.java,
                        mModelData.accessToken,
                        mModelData.account.id,
                        mobileNoEdt.text.toString(),
                        "+"+ccp.selectedCountryCode
                   )*/


                }
                ApiInterface.GET_PROFILE_FAILURE -> {
                    //dismissIOSProgress()
                    showToast(requireContext(), "failure: " + msg.obj)
                    Log.wtf("error: ", msg.obj.toString())
                }
                ApiInterface.LOGIN_FAILURE -> {
                    //dismissIOSProgress()
                    showToast(requireContext(), "failure: " + msg.obj)
                }
                ApiInterface.REPORT_USER -> {
                    //     dismissIOSProgress()
                    showToast(requireContext(), "Report successfully: " + msg.obj)
                    // Log.wtf("error: ", msg.obj.toString());
                }
            }
        }
    }

    private fun showIOSProgress() {
        closeProgress()
        _progressDlg = ProgressDialog(requireContext(), R.style.TransparentProgressDialog)
        _progressDlg!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        _progressDlg!!.setCancelable(false)
        _progressDlg!!.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mmContext = context

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSessionInstance(): SessionTwiclo {
        return SessionTwiclo(requireContext())
    }

    private fun isNetworkConnected(): Boolean {
        return com.fidoo.user.data.CheckConnectivity(requireContext()).isNetworkAvailable
    }

    private fun closeProgress() {
        if (_progressDlg == null) {
            return
        }
        _progressDlg!!.dismiss()
        _progressDlg = null
    }

    override fun onResume() {
        super.onResume()
        try {
            if (backhanlde == 1) {
                var mobileno = SessionTwiclo(context).getbackMobileno()

                binding.phone.setText(mobileno)
                Log.d("mobileno____", mobileno)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}