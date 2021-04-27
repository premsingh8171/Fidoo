package com.fidoo.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.fidoo.user.R
import com.fidoo.user.databinding.FragmentHomeBinding
import com.fidoo.user.databinding.FragmentSignUpBinding
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    private var fragmentSignupBinding: FragmentSignUpBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        fragmentSignupBinding = DataBindingUtil.inflate( inflater, R.layout.fragment_sign_up, container, false)

        fragmentSignupBinding?.tvSignIn?.setOnClickListener {
            findNavController().navigate(R.id.signInFragment)
        }



        // Inflate the layout for this fragment
        return fragmentSignupBinding?.root



    }
}