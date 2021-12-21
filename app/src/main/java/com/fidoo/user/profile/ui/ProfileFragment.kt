package com.fidoo.user.profile.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fidoo.user.R
import com.fidoo.user.activity.AboutUsActivity
import com.fidoo.user.activity.AuthActivity
import com.fidoo.user.activity.SplashActivity
import com.fidoo.user.addressmodule.activity.SavedAddressesActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.profile.viewmodel.EditProfileViewModel
import com.fidoo.user.referral.activity.ReferralActivity
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.viewmodels.LogoutViewModel
import com.google.gson.Gson
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    lateinit var mView: View
    var viewmodel: LogoutViewModel? = null
    var profileViewModel: EditProfileViewModel? = null
    var filePathTemp: String? = ""
    var sessionTwiclo: SessionTwiclo? = null
    var logoutDiolog: Dialog? = null

    private var mMixpanel: MixpanelAPI? = null

    companion object {
        var addManages: String? = ""
    }

    var height = 0
    var contsCardHeight = 0
    var width = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false)
        sessionTwiclo = SessionTwiclo(requireContext())
        viewmodel = ViewModelProvider(requireActivity()).get(LogoutViewModel::class.java)
        profileViewModel = ViewModelProvider(requireActivity()).get(EditProfileViewModel::class.java)

        mView.app_version_onpro_txt.text = "ver " + SplashActivity.appversion
        mMixpanel = MixpanelAPI.getInstance(requireContext(), "defeff96423cfb1e8c66f8ba83ab87fd")

        // Display size
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        //  int height = displayMetrics.heightPixels;
        //  int height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels
        //height = Math.round(width * 0.5).toInt()
        height = Math.round(((displayMetrics.heightPixels * 40) / 100).toDouble()).toInt()
        Log.d("height_rrrr__", height.toString())
        contsCardHeight = height - 200
        Log.d("height___", contsCardHeight?.toString())

        mView.constraintLayoutPro.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )

        val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.topToTop=0
        params.setMargins(50,contsCardHeight,50,0)
        mView.constCart.layoutParams = params


        if (SessionTwiclo(context).profileDetail != null) {
            Log.e("ddd", SessionTwiclo(context).profileDetail.account.image!!)
            /*nameEdt.setText(SessionTwiclo(context).profileDetail.account.name!!)
            nameEdt.setSelection(nameEdt.length())
            emailEdt.setText(SessionTwiclo(context).profileDetail.account.emailid!!)
            emailEdt.setSelection(emailEdt.length())*/

            Glide.with(this).asBitmap()
                .load(SessionTwiclo(context).profileDetail.account.image!!)
                // .fitCenter()
                // .override(100, 100)
                .placeholder(R.drawable.ic_user_single)
                .error(R.drawable.ic_user_single)
                .into(mView.img_user)

        } else {

        }

        mView.edit_profile.setOnClickListener {
            if (SessionTwiclo(requireContext()).isLoggedIn) {
                AppUtils.startActivityRightToLeft(
                    requireActivity(),
                    Intent(context, EditProfileActivity::class.java)
                )
            } else {
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG)
                    .show()
            }
        }

        mView.tv_yourAddresses.setOnClickListener {
            if (SessionTwiclo(requireContext()).isLoggedIn) {
                //  startActivity(Intent(context, SavedAddressesActivity::class.java).putExtra("type","address"))
                //change prem
                addManages = "add_manage"
                AppUtils.startActivityRightToLeft(
                    requireActivity(),
                    Intent(context, SavedAddressesActivity::class.java)
                )
            } else {
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG)
                    .show()
            }
        }

        mView.tv_manage_your_addresses.setOnClickListener {
            if (SessionTwiclo(requireContext()).isLoggedIn) {
                addManages = "add_manage"
                AppUtils.startActivityRightToLeft(
                    requireActivity(),
                    Intent(context, SavedAddressesActivity::class.java)
                )
            } else {
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG)
                    .show()
            }

        }

        mView.tv_terms.setOnClickListener {
            AppUtils.startActivityRightToLeft(
                requireActivity(),
                Intent(context, AboutUsActivity::class.java).putExtra("about_us", "about_us")
            )
        }

        mView.tv_aboutUs.setOnClickListener {
            AppUtils.startActivityRightToLeft(
                requireActivity(),
                Intent(context, AboutUsActivity::class.java).putExtra("about_us", "about_us")
            )
        }

        mView.tv_sharefriend.setOnClickListener {
            //shareApp()
            if (SessionTwiclo(requireContext()).isLoggedIn) {
                AppUtils.startActivityRightToLeft(
                    requireActivity(),
                    Intent(context, ReferralActivity::class.java)
                )
            } else {
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG)
                    .show()
            }
        }

        if (SessionTwiclo(requireContext()).isLoggedIn) {
            mView.action_logout.visibility = View.VISIBLE
            //mView.tv_email.visibility = View.VISIBLE
            mView.tv_name.visibility = View.VISIBLE
            mView.tv_mobile.visibility = View.VISIBLE
            try {

                if (SessionTwiclo(context).profileDetail != null) {
                    mView.tv_name.text = SessionTwiclo(context).profileDetail.account.name
                    mView.tv_mobile.text = SessionTwiclo(context).profileDetail.account.userName
                } else {
                    mView.tv_name.text = SessionTwiclo(context).loginDetail.account.name
                    mView.tv_mobile.text = SessionTwiclo(context).loginDetail.account.userName
                }

            } catch (e: Exception) {
            }
            //mView.tv_email.text = SessionTwiclo(context).profileDetail.account.emailid
        } else {
            mView.action_logout.text = "LOGIN"
            mView.action_logout.visibility = View.VISIBLE
            //mView.tv_email.visibility = View.GONE
            mView.tv_name.visibility = View.GONE
            mView.tv_mobile.visibility = View.GONE
        }

        mView.action_logout.setOnClickListener {
            if (sessionTwiclo!!.isLoggedIn) {
                logoutPopUp()
            } else {
                startActivity(
                    Intent(
                        context,
                        AuthActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }


        mView.tv_faq.setOnClickListener {
            AppUtils.startActivityRightToLeft(
                requireActivity(),
                Intent(context, AboutUsActivity::class.java).putExtra("faq", "faq")
            )
        }

        mView.tv_helpSupport.setOnClickListener {
            AppUtils.startActivityRightToLeft(
                requireActivity(),
                Intent(context, AboutUsActivity::class.java).putExtra("faq", "faq")
            )
        }

        viewmodel?.getlogoutResponse?.observe(requireActivity(), { user ->
            try {
                dismissIOSProgress()
                Log.e("logout__response", Gson().toJson(user))
                sessionTwiclo!!.clearSession()
                startActivity(
                    Intent(
                        requireActivity(),
                        AuthActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )

            } catch (e: Exception) {
                e.printStackTrace()
//                sessionTwiclo!!.clearSession()
//                startActivity(Intent(requireActivity(), SplashActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

            }
        })

        return mView
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage =
                """ Hey, I use FIDOO to order anything I want be it food, medicine delivery, my pet items, groceries, daily needs & more. Everything delivered right to your doorstep. You’ll love this,
            """.trimIndent()
            shareMessage = """${shareMessage}Click the link to download! 
            https://play.google.com/store/apps/details?id=com.fidoo.user
            
            """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
        }
    }

    private fun logoutPopUp() {
        logoutDiolog = Dialog(requireActivity())
        logoutDiolog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        logoutDiolog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        logoutDiolog?.setContentView(R.layout.logout_popup)
        logoutDiolog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        logoutDiolog?.window?.attributes?.windowAnimations = R.style.diologIntertnet
        logoutDiolog?.setCanceledOnTouchOutside(true)
        logoutDiolog?.show()
        val cencelBtn = logoutDiolog?.findViewById<CardView>(R.id.cencelBtn)
        val logoutBtn = logoutDiolog?.findViewById<CardView>(R.id.logoutBtn)
        val rll_logout = logoutDiolog?.findViewById<RelativeLayout>(R.id.rll_logout)

        cencelBtn?.setOnClickListener{
            logoutDiolog?.dismiss()
        }
        rll_logout?.setOnClickListener{
            logoutDiolog?.dismiss()
        }

        logoutBtn?.setOnClickListener{
            viewmodel?.logoutapi(
                sessionTwiclo!!.loggedInUserDetail.accountId,
                sessionTwiclo!!.loggedInUserDetail.accessToken
            )
            logoutDiolog?.dismiss()

        }


    }

    override fun onResume() {
        super.onResume()
        try {

            if (SessionTwiclo(context).profileDetail != null) {
                mView.tv_name.text = SessionTwiclo(context).profileDetail.account.name
                mView.tv_mobile.text =
                    "+91 " + SessionTwiclo(context).profileDetail.account.userName
                Glide.with(this).asBitmap()
                    .load(SessionTwiclo(context).profileDetail.account.image!!)
                    .placeholder(R.drawable.ic_user_single)
                    .error(R.drawable.ic_user_single)
                    .into(mView.img_user)
            } else {
                mView.tv_name.text = SessionTwiclo(context).loginDetail.account.name
                mView.tv_mobile.text = "+91 " + SessionTwiclo(context).loginDetail.account.userName
                Glide.with(this).asBitmap()
                    .load(SessionTwiclo(context).loginDetail.account.image!!)
                    .placeholder(R.drawable.ic_user_single)
                    .error(R.drawable.ic_user_single)
                    .into(mView.img_user)
            }

        } catch (e: Exception) {
        }
    }

    private fun setMarginsCustom(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }
}