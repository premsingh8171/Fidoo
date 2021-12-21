package com.fidoo.user.restaurants.roomdatabase.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RestaurantProducts_table")
class StoreItemProductsEntity(
    var headerActiveornot: String? = "",
    var product_sub_category_id: String? = "",
    var subcategory_name: String? = "",
    var cartQuantity: Int? = 0,
    var companyName: String? = "",
    var image: String? = "",
    var in_out_of_stock_status: String? = "",
    var isCustomize: String? = "",
    var is_customize_quantity: Int? = 0,
    var isNonveg: String? = "",
    var contains_egg: String? = "",
    var isPrescription: String? = "",
    var offerPrice: String? = "",
    var price: String? = "",
    var productId: String? ="",
    var productName: String? = "",
    var weight: String? = "",
    var unit: String? = "",
    var cartId: String? = "",
    var customizeItemName: String? = "",
    var customizeItemId: String? = "",
    var product_desc: String? = ""
) {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

