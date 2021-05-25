package com.fidoo.user.restaurents.roomdatabase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fidoo.user.data.model.StoreDetailsModel
import com.fidoo.user.restaurents.roomdatabase.dao.RestaurantProductsDao

@Database(entities = [StoreDetailsModel.Product::class], version = 1)
abstract class RestaurantProductDB : RoomDatabase() {

    companion object{
        const val DB_NAME = "Restaurants_db"
        const val TABLE_NAME = "Restaurants_product_table"
    }
    abstract fun restaurantsProductsDao(): RestaurantProductsDao?
}