package com.fidoo.user.dailyneed.model

data class Data(
    val banners: List<String>,
    val categories: List<Category>,
    val brand_heading: String,
    val category_heading: String,
    val brands: List<Brand>,
    val category_id: String,
    val category_name: String,
    val category_one: String,
    val category_two: String,
    val heading: String,
    val products: List<Product>,
    val subcategories: List<Subcategory>,
    val subcategory_id: String,
    val subcategory_three: String,
    val subcategory_two: String

)