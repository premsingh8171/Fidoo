package com.fidoo.user.restaurants.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase
import com.fidoo.user.restaurants.roomdatabase.database.RestaurantProductsDatabase
import com.fidoo.user.restaurants.roomdatabase.entity.StoreItemProductsEntity
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User




@Dao
interface RestaurantProductsDao {
    @Insert
    fun insertResProducts( vararg product: StoreItemProductsEntity)

    @Insert
    fun insertProducts(productList: ArrayList<StoreItemProductsEntity>)

    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME)
    fun getAllProducts(): LiveData<List<StoreItemProductsEntity>>

    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME+" WHERE productId =:product_Id")
    fun getSpecificValue(product_Id: String?): StoreItemProductsEntity?

    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME +" WHERE productName LIKE :query")
    fun searchQuery(query:String?): LiveData<List<StoreItemProductsEntity>>

    // @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME +" ORDER BY product_sub_category_id ASC LIMIT :limit")

    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME +" LIMIT :limit")
    fun getAllProducts2(limit:String?): LiveData<List<StoreItemProductsEntity>>


    @Query("SELECT * FROM " + RestaurantProductsDatabase.TABLE_NAME +" LIMIT :pageSize OFFSET :pageIndex")
    fun getAllProducts3(pageSize:String,pageIndex:String): LiveData<List<StoreItemProductsEntity>>

    @Query("SELECT COUNT(*) FROM "+  RestaurantProductsDatabase.TABLE_NAME)
    fun getTableCount(): LiveData<Integer>


    @Query("UPDATE "+ RestaurantProductsDatabase.TABLE_NAME +" SET cartQuantity=:quantity WHERE productId = :product_id")
    fun updateProducts(quantity: Int?, product_id: String)


    @Query("UPDATE "+ RestaurantProductsDatabase.TABLE_NAME +" SET cartQuantity=:quantity, is_customize_quantity=:is_customize_quantity, customizeItemName=:customizeItemName WHERE productId = :product_id")
    fun updateCustomizeProducts(quantity: Int?, product_id: String, is_customize_quantity: Int?,customizeItemName:String?)


    @Query("UPDATE "+ RestaurantProductsDatabase.TABLE_NAME +" SET cartQuantity=:cart_quantity, is_customize_quantity=:is_customize_quantity, customizeItemName=:customizeItemName, cartId=:cart_id, customizeItemId=:product_customize_id WHERE productId = :product_id")
    fun customizeProductUpdate(cart_quantity: Int?, product_id: String, is_customize_quantity: Int?,customizeItemName:String?,cart_id:String?,product_customize_id:String?)


    @Update
    fun updateProduct(products: StoreItemProductsEntity): Int

    @Delete
    fun deleteProduct(products: StoreItemProductsEntity): Int

    @Query("DELETE FROM RestaurantProducts_table")
    fun deleteAll()
}