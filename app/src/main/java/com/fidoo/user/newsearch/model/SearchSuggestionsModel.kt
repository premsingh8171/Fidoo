package com.fidoo.user.newsearch.model

data class SearchSuggestionsModel(
    val accessToken: String,
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val message: String,
    val suggestions: List<Suggestion>
)