package com.fidoo.user.FidooPay

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import com.fidoo.user.R
import com.fidoo.user.utils.BaseFragment

class FidoPayDashboardFragment : BaseFragment()  {
    lateinit var mView: View
    override fun provideYourFragmentView(
    inflater: LayoutInflater?,
    parent: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {
    val window: Window = requireActivity().getWindow()
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.colorPrimary)
    mView = inflater!!.inflate(R.layout.fidoopay_dashboard_fragment, parent, false)
    return mView
   }
}