package com.fidoo.user.newsearch.model

data class KeywordBasedSearchResultsModel(
    val accessToken: String,
    val accountId: String,
    val page_count: String,
    val more_value: Boolean,
    val error: Boolean,
    val error_code: Int,
    val total_count: Int,
    val stores: List<Store>
)