package com.fidoo.user.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fidoo.user.AboutUsActivity
import com.fidoo.user.R
import com.fidoo.user.data.SendResponse
import com.fidoo.user.databinding.FragmentSignInBinding
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.utils.CommonUtils.Companion.hideKeyboard
import com.fidoo.user.utils.CustomProgressDialog
import com.fidoo.user.viewmodels.LoginViewModel
import com.vanillaplacepicker.utils.ToastUtils.showToast


class SignInFragment : Fragment() {

    lateinit var layout: ConstraintLayout
    lateinit var pref: com.fidoo.user.data.session.SessionTwiclo
    private var _progressDlg: ProgressDialog? = null

    // assign the _binding variable initially to null and
    // also when the view is destroyed again it has to be set to null
    private var _binding: FragmentSignInBinding? = null

    // with the backing property of the kotlin we extract
    // the non null value of the _binding
    private val binding get() = _binding!!

    var viewmodel: LoginViewModel? = null

    var customeProgressDialog: CustomProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // inflate the layout and bind to the _binding
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        pref = getSessionInstance()


        //   mView = inflater.inflate(R.layout.fragment_sign_in, container, false)

        binding.btnSignIn.setOnClickListener {

            if (!isNetworkConnected()) {
                showToast(requireContext(), resources.getString(R.string.provide_internet))
            } else {

                //   startActivity(Intent(this,VerificationActivity::class.java))
                if (binding.phone.text.toString().equals("")) {
                    showToast(requireContext(), "Please enter mobile number")
                } else
                    if (binding.phone.text.toString().length != 10) {
                        showToast(requireContext(), "Please enter valid number")
                    } else
                        if (binding.phone.text.toString().startsWith("0")) {
                            showToast(requireContext(), "Number can't be starts with zero")
                        } else {
                            showIOSProgress()
                            viewmodel!!.login(
                                "+91",
                                com.fidoo.user.data.session.SessionTwiclo(
                                    requireContext()
                                ).deviceToken
                            )
                        }
            }

        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }


        binding.tvSkip.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            requireContext().startActivity(intent)
            requireActivity().finish()
        }

        binding.tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(requireContext(), AboutUsActivity::class.java).putExtra("privacy_policy", "privacy_policy")
            requireContext().startActivity(intent)
            requireActivity().finish()
        }


        viewmodel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        binding.viewmodel = viewmodel

        binding.mainLayout.setOnClickListener {

            hideKeyboard()
        }
        customeProgressDialog = CustomProgressDialog(requireContext())


        viewmodel?.progressDialog?.observe(this, Observer {
            if (it!!) customeProgressDialog?.show() else customeProgressDialog?.dismiss()
        })


        viewmodel?.userLogin?.observe(requireActivity(), { user ->
            dismissIOSProgress()
            val mModelData: com.fidoo.user.data.model.EditProfileModel = user
            pref.storeProfileDetail(mModelData)

            val sendData= SendResponse(mModelData.accessToken,mModelData.account.id,binding.phone.text.toString().trim(),"+91")
            val action = SignInFragmentDirections.actionSignInFragmentToOtpFragment(sendData)
            closeProgress()
            findNavController().navigate(action)

        })

        return binding.root
    }


    @SuppressLint("handlerLeak")
    private val mApiHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                com.fidoo.user.api_request_retrofit.ApiInterface.LOGIN_SUCCESS -> {
                    dismissIOSProgress()

                    val mModelData: com.fidoo.user.data.model.LoginModel = msg.obj as com.fidoo.user.data.model.LoginModel

                    val sendData= SendResponse(mModelData.accessToken,mModelData.account.id,binding.phone.text.toString().trim(),"+91")

                    val action = SignInFragmentDirections.actionSignInFragmentToOtpFragment(sendData)

                    findNavController().navigate(action)

                    /*goForVerificationScreen(
                        VerificationActivity::class.java,
                        mModelData.accessToken,
                        mModelData.account.id,
                        mobileNoEdt.text.toString(),
                        "+"+ccp.selectedCountryCode
                   )*/


                }
                com.fidoo.user.api_request_retrofit.ApiInterface.GET_PROFILE_FAILURE -> {
                    //dismissIOSProgress()
                    showToast(requireContext(), "failure: " + msg.obj)
                    Log.wtf("error: ", msg.obj.toString())
                }
                com.fidoo.user.api_request_retrofit.ApiInterface.LOGIN_FAILURE -> {
                    //dismissIOSProgress()
                    showToast(requireContext(), "failure: " + msg.obj)
                }
                com.fidoo.user.api_request_retrofit.ApiInterface.REPORT_USER -> {
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

        val activity = context as Activity
        layout = activity.findViewById<ConstraintLayout>(R.id.fidooTxtLayout)
    }


    override fun onResume() {
        super.onResume()
        layout.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSessionInstance(): com.fidoo.user.data.session.SessionTwiclo {
        return com.fidoo.user.data.session.SessionTwiclo(requireContext())
    }

    private fun isNetworkConnected(): Boolean {
        return com.fidoo.user.data.CheckConnectivity(requireContext()).isNetworkAvailable()
    }

    private fun closeProgress() {
        if (_progressDlg == null) {
            return
        }
        _progressDlg!!.dismiss()
        _progressDlg = null
    }


}