package com.fidoo.user.restaurants.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity

@Dao
interface RestaurantProductsDao {
    @Insert
    fun insertResProducts( vararg product: StoreItemProductsEntity)

    @Insert
    fun insertProducts(productList: ArrayList<StoreItemProductsEntity>)

    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME)
    fun getAllProducts(): LiveData<List<StoreItemProductsEntity>>

    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME +" LIMIT :limit")
    fun getAllProducts2(limit:String?): LiveData<List<StoreItemProductsEntity>>


    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME +" LIMIT :pageSize OFFSET :pageIndex")
    fun getAllProducts3(pageSize:String,pageIndex:String): LiveData<List<StoreItemProductsEntity>>

    @Query("SELECT COUNT(*) FROM "+  RestaurantProductsDatabase.TABLE_NAME)
    fun getTableCount(): LiveData<Integer>


    @Query("UPDATE "+ RestaurantProductsDatabase.TABLE_NAME +" SET cartQuantity=:quantity WHERE productId = :product_id")
    fun updateProducts(quantity: Int?, product_id: String)


    @Update
    fun updateProduct(products: StoreItemProductsEntity): Int

    @Delete
    fun deleteProduct(products: StoreItemProductsEntity): Int

    @Query("DELETE FROM RestaurantProducts_table")
    fun deleteAll()
}