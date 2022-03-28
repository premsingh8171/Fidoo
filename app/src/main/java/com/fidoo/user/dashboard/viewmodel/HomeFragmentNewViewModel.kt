package com.fidoo.user.dashboard.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fidoo.user.addressmodule.model.GetAddressModel
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragmentNewViewModel : ViewModel() {
    var addrListModel : MutableLiveData<GetAddressModel>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {
        addrListModel = MutableLiveData<GetAddressModel>()
        progressDialog = SingleLiveEvent<Boolean>()
        failureResponse = MutableLiveData<String>()
    }

    fun getAddressesApi(
        accountId: String,
        accessToken: String,
        storeLatitude: String,
        storeLongitude: String
    ) : MutableLiveData<GetAddressModel>
    {
        Log.d("getAddressesApi", "$accountId--$accessToken--$storeLatitude--$storeLongitude")
        return HomeNewUiFragmentRepo().getAddressList()
    }
}