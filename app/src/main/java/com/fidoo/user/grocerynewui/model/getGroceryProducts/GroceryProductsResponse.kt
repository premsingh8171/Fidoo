package com.fidoo.user.grocerynewui.model.getGroceryProducts

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val store_name: String,
    val has_subcategory: Int
)

class Category(
    val cat_id: String,
    val cat_name: String,
    val status: String,
    val subcategory: List<Subcategory>
)

@Entity(tableName = "Products_Sub_cat_table")
class Subcategory(
    val product: List<Product>,
    val subcategory_name: String,
    val sub_cat_id: String
){
    @PrimaryKey(autoGenerate = true)
    var scid: Int = 0
}


@Entity(tableName = "Products_table")
data class Product(
    var cart_quantity: Int = 0,
    var company_name: String = "",
    var image: String = "",
    var in_out_of_stock_status: String = "",
    var is_customize: String = "",
    var is_customize_quantity: Int = 0,
    var is_nonveg: String = "",
    var is_prescription: String = "",
    var offer_price: String = "",
    var price: String = "",
    var product_id: String = "",
    var product_name: String = "",
    var unit: String = "",
    var weight: String = "",
    var cart_id: String = "",
    var description: String = "",
    var product_sub_category_id: String = "",
    var product_category_id: String = ""
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}


