package com.fidoo.user.restaurants.roomdatabase.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RestaurantProducts_table")
class StoreItemProductsEntity(
    var cartQuantity: Int? = 0,
    var companyName: String? = "",
    var image: String? = "",
    var in_out_of_stock_status: String? = "",
    var isCustomize: String? = "",
    var is_customize_quantity: Int? = 0,
    var isNonveg: String? = "",
    var isPrescription: String? = "",
    var offerPrice: String? = "",
    var price: String? = "",
    var productId: String? ="",
    var productName: String? = "",
    var weight: String? = "",
    var unit: String? = "",
    var cartId: String? = "",
    var customizeItem: String? = ""
) {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

