package com.fidoo.user.grocery.model.getGroceryProducts

class GroceryProductsResponse(
    val accessToken: String,
    val accountId: String,
    val address: String,
    val category: List<Category>,
    val delivery_time: String,
    val error: Boolean,
    val error_code: Int,
    val fssai: String,
    val id: String,
    val image: String,
    val rating: String,
    val service_id: String,
    val service_name: String,
    val status: String,
    val store_name: String
)

 class Category(
    val cat_id: String,
    val cat_name: String,
    val status: String,
    val subcategory: List<Subcategory>
)

 class Subcategory(
    val product: List<Product>,
    val subcategory_name: String
)

class Product(
    val cart_quantity: Int,
    val company_name: String,
    val customize_item: List<Any>,
    val image: String,
    val in_out_of_stock_status: String,
    val is_customize: String,
    val is_customize_quantity: Int,
    val is_nonveg: String,
    val is_prescription: String,
    val offer_price: String,
    val price: String,
    val product_id: String,
    val product_name: String,
    val unit: String,
    val weight: String,
    val cart_id: String
)