package com.fidoo.user.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.utils.BaseFragment

class AuthFragment : BaseFragment() {
    lateinit var mView: View

    override fun provideYourFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)

        mView = inflater!!.inflate(R.layout.fragment_auth, parent, false)
//        try {
//            findNavController()!!.navigate(R.id.action_splashFragment_to_authFragment)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return mView
    }
}