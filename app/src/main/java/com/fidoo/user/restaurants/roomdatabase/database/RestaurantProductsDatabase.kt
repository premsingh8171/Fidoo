package com.fidoo.user.restaurants.roomdatabase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fidoo.user.restaurants.roomdatabase.dao.RestaurantProductsDao
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity

@Database(entities = arrayOf(StoreItemProductsEntity::class), version = 1)
abstract class RestaurantProductsDatabase: RoomDatabase() {
    companion object{
       const val DB_NAME = "RestaurantProductList"
        const val TABLE_NAME = "RestaurantProducts_table"
    }
    abstract fun resProductsDaoAccess(): RestaurantProductsDao?
}