package com.fidoo.user.sendpackages.model

data class SendPackagesUploadImageModel(
    val accessToken: String,
    val accountId: String,
    val document_id: String,
    val error: Boolean,
    val error_code: Int,
    val file: String,
    val message: String
)