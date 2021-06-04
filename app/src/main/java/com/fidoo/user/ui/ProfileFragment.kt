package com.fidoo.user.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fidoo.user.AboutUsActivity
import com.fidoo.user.R
import com.fidoo.user.SplashActivity
import com.fidoo.user.addressmodule.address.SavedAddressesActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.grocery.activity.GroceryItemsActivity
import com.fidoo.user.profile.EditProfileActivity
import com.fidoo.user.profile.EditProfileViewModel
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.viewmodels.LogoutViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_grocery_items.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.select_cat_popup.*


class ProfileFragment : Fragment() {

    lateinit var mView : View
    var viewmodel: LogoutViewModel? = null
    var profileViewModel: EditProfileViewModel? = null
    var filePathTemp: String? = ""
    var sessionTwiclo: SessionTwiclo?=null
    var logoutDiolog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false)
        sessionTwiclo = SessionTwiclo(requireContext())
        viewmodel = ViewModelProvider(requireActivity()).get(LogoutViewModel::class.java)
        profileViewModel = ViewModelProvider(requireActivity()).get(EditProfileViewModel::class.java)

        if (SessionTwiclo(context).profileDetail!= null) {
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

        }

        mView.edit_profile.setOnClickListener {
            if (SessionTwiclo(requireContext()).isLoggedIn){
                startActivity(Intent(context, EditProfileActivity::class.java))
            }else{
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG).show()
            }
        }

        mView.tv_yourAddresses.setOnClickListener {
            if (SessionTwiclo(requireContext()).isLoggedIn){
                startActivity(Intent(context, SavedAddressesActivity::class.java))
            }else{
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG).show()
            }

        }

        mView.tv_manage_your_addresses.setOnClickListener {
            if (SessionTwiclo(requireContext()).isLoggedIn){
                startActivity(Intent(context, SavedAddressesActivity::class.java))
            }else{
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG).show()
            }

        }

        mView.tv_terms.setOnClickListener {
            startActivity(Intent(context, AboutUsActivity::class.java).putExtra("about_us", "about_us"))
        }

        mView.tv_aboutUs.setOnClickListener {
            startActivity(Intent(context, AboutUsActivity::class.java).putExtra("about_us", "about_us"))
        }

        mView.tv_sharefriend.setOnClickListener {
            shareApp()
        }

        if (SessionTwiclo(requireContext()).isLoggedIn){
            mView.action_logout.visibility = View.VISIBLE
            //mView.tv_email.visibility = View.VISIBLE
            mView.tv_name.visibility = View.VISIBLE
            mView.tv_hi.visibility = View.VISIBLE
            mView.tv_name.text = SessionTwiclo(context).profileDetail.account.name
            //mView.tv_email.text = SessionTwiclo(context).profileDetail.account.emailid
        }else{
            mView.action_logout.text="LOGIN"
            mView.action_logout.visibility = View.VISIBLE
            //mView.tv_email.visibility = View.GONE
            mView.tv_name.visibility = View.GONE
            mView.tv_hi.visibility = View.GONE
        }

        mView.action_logout.setOnClickListener {
            if (sessionTwiclo!!.isLoggedIn){
                logoutPopUp()
            }else{
                startActivity(Intent(context, SplashActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }


        mView.tv_faq.setOnClickListener {
            startActivity(Intent(context, AboutUsActivity::class.java).putExtra("faq", "faq"))
        }

        mView.tv_helpSupport.setOnClickListener {
            startActivity(Intent(context, AboutUsActivity::class.java).putExtra("faq", "faq"))
        }

        viewmodel?.getlogoutResponse?.observe(requireActivity(), { user ->
            try {
                dismissIOSProgress()
                Log.e("logout__response", Gson().toJson(user))
                sessionTwiclo!!.clearSession()
                startActivity(Intent(requireActivity(), SplashActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

            }catch (e:Exception){
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

        cencelBtn?.setOnClickListener(View.OnClickListener {
            logoutDiolog?.dismiss()
        })
        rll_logout?.setOnClickListener(View.OnClickListener {
            logoutDiolog?.dismiss()
        })

        logoutBtn?.setOnClickListener(View.OnClickListener {
            viewmodel?.logoutapi(
                sessionTwiclo!!.loggedInUserDetail.accountId,
                sessionTwiclo!!.loggedInUserDetail.accessToken
            )
            logoutDiolog?.dismiss()

        })


    }

}