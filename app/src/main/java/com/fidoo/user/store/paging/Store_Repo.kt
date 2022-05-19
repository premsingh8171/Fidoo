package com.fidoo.user.store.paging

import com.fidoo.user.api_request_retrofit.BackEndApi
import com.fidoo.user.api_request_retrofit.WebServiceClient

class Store_Repo(var accountId: String,
                 var accessToken: String,
                 var service_id: String,
                 var latitude: String,
                 var longitude: String,
                 var distance_start: String?,
                 var distance_end: String?,
                 var sort_by: String?,
                 var cuisine_to_search: String?,var pagecount: String?) {

    fun getrepodata(){
        WebServiceClient.client.create(BackEndApi::class.java).getStoresApi(accountId, accessToken, service_id,
        latitude, longitude, distance_start, distance_end, sort_by, cuisine_to_search, pagecount)
    }
}