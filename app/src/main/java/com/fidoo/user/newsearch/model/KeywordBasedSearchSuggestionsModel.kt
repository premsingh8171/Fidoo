package com.fidoo.user.newsearch.model

data class KeywordBasedSearchSuggestionsModel(
    val accessToken: String,
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val message: String,
    val more_value: Boolean,
    val suggestions: List<SuggestionX>,
    val total_count: Int
)