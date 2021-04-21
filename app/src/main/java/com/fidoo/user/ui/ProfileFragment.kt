package com.fidoo.user.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fidoo.user.AboutUsActivity
import com.fidoo.user.LoginActivity
import com.fidoo.user.R
import com.fidoo.user.SplashActivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.view.address.SavedAddressesActivity
import com.fidoo.user.viewmodels.LoginViewModel
import com.fidoo.user.viewmodels.LogoutViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    lateinit var mView : View
    var viewmodel: LogoutViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false)

        viewmodel = ViewModelProvider(requireActivity()).get(LogoutViewModel::class.java)

        mView.tv_yourAddresses.setOnClickListener {
            if (SessionTwiclo(requireContext()).isLoggedIn){
                startActivity(Intent(context, SavedAddressesActivity::class.java))
            }else{
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG).show()
            }

        }

        mView.tv_aboutUs.setOnClickListener {
            startActivity(Intent(context, AboutUsActivity::class.java).putExtra("about_us", "about_us"))
        }

        if (SessionTwiclo(requireContext()).isLoggedIn){
            mView.action_logout.visibility = View.VISIBLE
            mView.textView6.visibility = View.VISIBLE
            mView.tv_name.visibility = View.VISIBLE
            mView.tv_hi.visibility = View.VISIBLE
        }else{
            mView.action_logout.visibility = View.GONE
            mView.textView6.visibility = View.GONE
            mView.tv_name.visibility = View.GONE
            mView.tv_hi.visibility = View.GONE
        }

        mView.action_logout.setOnClickListener {

            if (SessionTwiclo(requireContext()).isLoggedIn){
                viewmodel?.logoutapi(
                    SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
                    SessionTwiclo(requireContext()).loggedInUserDetail.accessToken
                )

                startActivity(Intent(context, SplashActivity::class.java))
            }else{
                Toast.makeText(requireContext(), "Please login to proceed", Toast.LENGTH_LONG).show()
            }


        }

        viewmodel?.getlogoutResponse?.observe(requireActivity(), Observer { user ->
            dismissIOSProgress()

            Log.e("logout response", Gson().toJson(user))
            dismissIOSProgress()
            SessionTwiclo(requireContext()).clearSession()

            startActivity(Intent(requireContext(),SplashActivity::class.java))
            finishAffinity(requireActivity())

            //   Toast.makeText(this, "welcocsd", Toast.LENGTH_LONG).show()
        })

        return mView
    }


}