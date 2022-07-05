package com.fidoo.user.newsearch.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient
import com.fidoo.user.newsearch.model.KeywordBasedSearchResultsModel
import com.fidoo.user.newsearch.model.KeywordBasedSearchSuggestionsModel
import com.fidoo.user.newsearch.model.SearchSuggestionsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchNewViewModel(application: Application) : AndroidViewModel(application) {
    var searchSuggestionsRes: MutableLiveData<SearchSuggestionsModel>? = null
    var keywordBasedSearchSuggestionsRes: MutableLiveData<KeywordBasedSearchSuggestionsModel>? =
        null
    var keywordBasedSearchResultsRes: MutableLiveData<KeywordBasedSearchResultsModel>? = null
    var failureRes: MutableLiveData<String>? = null

    init {
        searchSuggestionsRes = MutableLiveData<SearchSuggestionsModel>()
        keywordBasedSearchSuggestionsRes = MutableLiveData<KeywordBasedSearchSuggestionsModel>()
        keywordBasedSearchResultsRes = MutableLiveData<KeywordBasedSearchResultsModel>()
        failureRes = MutableLiveData<String>()
    }


    fun searchSuggestionsApi(accountId: String, accessToken: String) {
        Log.d("searchSuggestionsApi", "$accountId--$accessToken")
        WebServiceClient.client.create(BackEndApi::class.java)
            .searchSuggestionsApi(accountId = accountId, accessToken = accessToken)
            .enqueue(object : Callback<SearchSuggestionsModel> {
                override fun onResponse(
                    call: Call<SearchSuggestionsModel>,
                    response: Response<SearchSuggestionsModel>
                ) {
                    searchSuggestionsRes?.value = response!!.body()
                }

                override fun onFailure(call: Call<SearchSuggestionsModel>, t: Throwable) {
                    failureRes?.value = t.toString()
                }

            })
    }

    fun keywordBasedSearchSuggestionsApi(
        accountId: String,
        accessToken: String,
        keyword: String,
        user_lat: String,
        user_long: String,
        page_count: String,
        service_id: String,
        search_type:String
    ) {
        Log.d(
            "searchSuggestionsApi",
            "$accountId--$accessToken$keyword--$user_lat$user_long--$page_count--$service_id--$search_type"
        )

        WebServiceClient.client.create(BackEndApi::class.java)
            .keywordBasedSearchSuggestionsApi(
                accountId = accountId,
                accessToken = accessToken,
                keyword = keyword,
                user_lat = user_lat,
                user_long = user_long,
                page_count = page_count,
                service_id = service_id,
                search_type = search_type
            )
            .enqueue(object : Callback<KeywordBasedSearchSuggestionsModel> {
                override fun onResponse(
                    call: Call<KeywordBasedSearchSuggestionsModel>,
                    response: Response<KeywordBasedSearchSuggestionsModel>
                ) {
                    keywordBasedSearchSuggestionsRes!!.value = response.body()
                }

                override fun onFailure(
                    call: Call<KeywordBasedSearchSuggestionsModel>,
                    t: Throwable
                ) {
                    failureRes!!.value = "Something wrong"
                }

            })
    }

    fun keywordBasedSearchResultsApi(
        accountId: String,
        accessToken: String,
        keyword: String,
        user_lat: String,
        user_long: String,
        page_count: String
    ) {
        Log.d("keywordBasedSearchResultsApi", "$accountId--$accessToken$keyword--$user_lat$user_long--$page_count")

        WebServiceClient.client.create(BackEndApi::class.java)
            .keywordBasedSearchResultsApi(
                accountId = accountId,
                accessToken = accessToken,
                keyword = keyword,
                user_lat = user_lat,
                user_long = user_long,
                page_count = page_count
            )
            .enqueue(object : Callback<KeywordBasedSearchResultsModel> {
                override fun onResponse(
                    call: Call<KeywordBasedSearchResultsModel>,
                    response: Response<KeywordBasedSearchResultsModel>
                ) {
                    keywordBasedSearchResultsRes!!.value = response!!.body()
                }

                override fun onFailure(
                    call: Call<KeywordBasedSearchResultsModel>,
                    t: Throwable
                ) {
                    failureRes!!.value = "Something wrong"
                }

            })
    }


}