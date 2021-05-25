package com.fidoo.user.restaurents.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fidoo.user.data.model.Product
import com.fidoo.user.data.model.StoreDetailsModel
import com.fidoo.user.restaurents.roomdatabase.database.RestaurantProductDB

@Dao
interface RestaurantProductsDao {
    @Insert
    fun insertProducts( vararg product: StoreDetailsModel.Product)

    @Insert
    fun insertProducts(productList: List<StoreDetailsModel.Product>)

    @Query("SELECT * FROM " + RestaurantProductDB.TABLE_NAME)
    fun getAllProducts(): LiveData<List<Product>>


    @Query("UPDATE "+ RestaurantProductDB.TABLE_NAME +" SET cart_quantity=:quantity WHERE product_id = :product_id")
    fun updateProducts(quantity: Int?, product_id: String)

//    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME + " WHERE product_category_id = :product_category_id")
//    fun getProductsListByCategory(category: String?): ArrayList<Product?>?

//    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME + " WHERE product_sub_category_id = :product_sub_category_id")
//    fun getProductsListBySubCat(subcategoryId: String): Product?

    @Update
    fun updateProduct(products: StoreDetailsModel.Product): Int

    @Delete
    fun deleteProduct(products: StoreDetailsModel.Product): Int

    @Query("DELETE FROM Products_table")
    fun deleteAll()
}