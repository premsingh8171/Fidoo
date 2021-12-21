package com.fidoo.user.grocerynewui.roomdatabase.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Products_table")
class ProductsEntitiy (
    @ColumnInfo(name = "cart_quantity") var cart_quantity: Int=0,
    @ColumnInfo(name = "company_name") var company_name: String="",
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
    @ColumnInfo(name = "product_category_id") var product_category_id: String=""

){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}