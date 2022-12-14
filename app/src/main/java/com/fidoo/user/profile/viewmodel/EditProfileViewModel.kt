package com.fidoo.user.profile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.profile.model.EditProfileModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    var addUpdateResponse: MutableLiveData<EditProfileModel>? = null
    var failureResponse: MutableLiveData<String>? = null

    init {

        addUpdateResponse = MutableLiveData<EditProfileModel>()
        failureResponse = MutableLiveData<String>()
    }

    fun addUpdateProfileApi(
        accountId: RequestBody?,
        accessToken: RequestBody?,
        name: RequestBody?,
        email: RequestBody?,
        image: MultipartBody.Part?
    ) {
        Log.d("addupdateprofilr", "accid-${Gson().toJson(accountId)}   acctkn-${Gson().toJson(accessToken)}   name-${ Gson().toJson(name)}  email-${ Gson().toJson(email)}   image-${Gson().toJson(image)}")
//        GlobalScope.launch(Dispatchers.IO) {
//            var response = WebServiceClient.client.create(BackEndApi::class.java)
//                .addUpdateProfileApi(accountId, accessToken, name, email, image)
//            if (response.isSuccessful) {
//                addUpdateResponse?.postValue(response.body())
//            } else {
//                failureResponse?.postValue("Something went wrong")
//            }
//        }

        val call: Call<EditProfileModel> = WebServiceClient.client.create(BackEndApi::class.java)
            .addUpdateProfileApi(accountId, accessToken, name, email, image)
        call.enqueue(object : Callback<EditProfileModel> {
            override fun onResponse(
                call: Call<EditProfileModel>,
                response: Response<EditProfileModel>
            ) {
             //   println("response_is ${response.body()}")
                addUpdateResponse?.value = response.body()
            }

            override fun onFailure(
                call: Call<EditProfileModel>,
                t: Throwable
            ) {
                failureResponse?.value = "Something went wrong"
                Log.e("ddddd", t.message.toString())
            }
        })


    }


}