package com.fidoo.user.sendpackages.model

 class SendPackagesCatModel(
    val accessToken: String,
    val accountId: String,
    val categories_list: List<Categories>,
    val error: Boolean,
    val error_code: Int
)

 class Categories(
    val cat_name: String,
    val id: String
)
