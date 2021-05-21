package com.fidoo.user.grocery.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase

@Dao
interface ProductsDao {
    @Insert
    fun insertProducts( vararg product: Product)

    @Insert
    fun insertProducts(productList: ArrayList<Product>)

    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME)
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME +" LIMIT 20")
    fun getAllProducts2(): LiveData<List<Product>>



    @Query("UPDATE "+ ProductsDatabase.TABLE_NAME +" SET cart_quantity=:quantity WHERE product_id = :product_id")
    fun updateProducts(quantity: Int?, product_id: String)

//    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME + " WHERE product_category_id = :product_category_id")
//    fun getProductsListByCategory(category: String?): ArrayList<Product?>?

//    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME + " WHERE product_sub_category_id = :product_sub_category_id")
//    fun getProductsListBySubCat(subcategoryId: String): Product?

    @Update
    fun updateProduct(products: Product): Int

    @Delete
    fun deleteProduct(products: Product): Int

    @Query("DELETE FROM Products_table")
    fun deleteAll()
}