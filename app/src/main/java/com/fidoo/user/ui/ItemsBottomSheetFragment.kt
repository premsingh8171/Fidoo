package com.fidoo.user.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fidoo.user.R
import com.fidoo.user.adapter.PackageCategoriesAdapter
import com.fidoo.user.interfaces.AdapterClick
import com.fidoo.user.utils.CommonUtils.Companion.dismissIOSProgress
import com.fidoo.user.viewmodels.SendPackagesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_items_bottom_sheet.view.*


class ItemsBottomSheetFragment : BottomSheetDialogFragment(),
    AdapterClick {

    companion object {
        lateinit var mView: View
        var catId: MutableLiveData<String> ? = null
        var viewmodel: SendPackagesViewModel? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(SendPackagesViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_items_bottom_sheet, container, false)

        if (!isNetworkConnected()) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.provide_internet),
                Toast.LENGTH_SHORT
            ).show()

        } else {
            if (com.fidoo.user.data.session.SessionTwiclo(requireContext()).isLoggedIn) {
                viewmodel?.getPackageCatApi(
                    com.fidoo.user.data.session.SessionTwiclo(
                        requireContext()
                    ).loggedInUserDetail.accountId,
                    com.fidoo.user.data.session.SessionTwiclo(
                        requireContext()
                    ).loggedInUserDetail.accessToken
                )
            } else {
                viewmodel?.getPackageCatApi(
                    "",
                    ""
                )
            }
            viewmodel?.getPackageCatApi(
                com.fidoo.user.data.session.SessionTwiclo(requireContext()).loggedInUserDetail.accountId,
                com.fidoo.user.data.session.SessionTwiclo(requireContext()).loggedInUserDetail.accessToken
            )
        }


        viewmodel?.getcatResponse?.observe(this, Observer { user ->
            dismissIOSProgress()
            Log.e("cat response", Gson().toJson(user))
            val adapter = PackageCategoriesAdapter(requireContext(), user.categoriesList, this)

            mView.customItemsRecyclerview.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            mView.customItemsRecyclerview.setHasFixedSize(true)
            mView.customItemsRecyclerview.adapter = adapter
        })

        return mView
    }

    override fun onItemClick(
        productId: String?,
        type: String?,
        count: String?,
        offerPrice: String?,
        customize_count: Int?,
        productType: String?,
        cart_id: String?
    ) {
        catId?.value = productId.toString()
        dismiss()
    }


    fun isNetworkConnected(): Boolean {
        return com.fidoo.user.data.CheckConnectivity(requireContext()).isNetworkAvailable
    }


}