package com.fidoo.user.grocery.roomdatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fidoo.user.grocery.model.getGroceryProducts.Product
import com.fidoo.user.grocery.roomdatabase.database.ProductsDatabase


interface ProductsDao {
    @Insert
    fun insertProducts( vararg product: Product)

    @Insert
    fun insertProducts(productList: ArrayList<Product>)

    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME_TODO)
    fun getAllProducts(): LiveData<ArrayList<Product>>

    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME_TODO.toString() + " WHERE product_category_id = :product_category_id")
    fun getProductsListByCategory(category: String?): ArrayList<Product?>?

    @Query("SELECT * FROM " + ProductsDatabase.TABLE_NAME_TODO.toString() + " WHERE product_sub_category_id = :product_sub_category_id")
    fun getProductsListBySubCat(subcategoryId: String): Product?

    @Update
    fun updateProduct(products: Product): Int

    @Delete
    fun deleteProduct(products: Product): Int
}