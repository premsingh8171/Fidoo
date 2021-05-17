package com.fidoo.user.grocery.model.getGroceryProducts

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase

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
    val subcategory_name: String,
    val sub_cat_id: String
)

@Entity(tableName = ProductsDatabase.TABLE_NAME_TODO)
data class Product(
    @ColumnInfo(name = "cart_quantity") var cart_quantity: Int=0,
    @ColumnInfo(name = "company_name") var company_name: String="",
   // var customize_item: List<Any>,
    @ColumnInfo(name = "image")  var image: String="",
    @ColumnInfo(name = "in_out_of_stock_status")var in_out_of_stock_status: String="",
    @ColumnInfo(name = "is_customize")   var is_customize: String="",
    @ColumnInfo(name = "is_customize_quantity") var is_customize_quantity: Int=0,
    @ColumnInfo(name = "is_nonveg")  var is_nonveg: String="",
    @ColumnInfo(name = "is_prescription") var is_prescription: String="",
    @ColumnInfo(name = "offer_price") var offer_price: String="",
    @ColumnInfo(name = "price") var price: String="",
    @ColumnInfo(name = "product_id") var product_id: String="",
    @ColumnInfo(name = "product_name") var product_name: String="",
    @ColumnInfo(name = "unit")  var unit: String="",
    @ColumnInfo(name = "weight") var weight: String="",
    @ColumnInfo(name = "cart_id") var cart_id: String="",
    @ColumnInfo(name = "product_sub_category_id") var product_sub_category_id: String="",
    @ColumnInfo(name = "product_category_id")  var product_category_id: String=""

)

