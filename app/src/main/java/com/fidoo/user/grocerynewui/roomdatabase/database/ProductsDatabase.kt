package com.fidoo.user.grocerynewui.roomdatabase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import com.fidoo.user.grocerynewui.roomdatabase.dao.ProductsDao

@Database(entities = [Product::class], version = 1,exportSchema = false)
abstract class ProductsDatabase: RoomDatabase() {
    companion object{
       const val DB_NAME = "ProductList"
        const val TABLE_NAME = "Products_table"
    }
    abstract fun productsDaoAccess(): ProductsDao?
}