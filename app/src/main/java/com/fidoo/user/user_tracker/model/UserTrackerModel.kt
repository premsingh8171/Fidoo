package com.fidoo.user.user_tracker.model

 class UserTrackerModel(
    val accountId: String,
    val error: Boolean,
    val error_code: Int,
    val message: String,
    val user_name: String
)