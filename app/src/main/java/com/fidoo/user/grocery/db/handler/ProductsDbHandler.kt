package com.fidoo.products.grocery.db.handler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.fidoo.user.grocery.model.getGroceryProducts.Product

class ProductsDbHandler(context:Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "ProductsList"
        private val TABLE_PRODUCTS = "ProductTable"
        private val KEY_ID = "id"
        private val CATEGORY_ID = "category_id"
        private val SUBCATEGORY_ID = "subcategory_id"
        private val PRODUCT_ID = "product_id"
        private val PRODUCT_NAME = "product_name"
        private val COMPANY_NAME = "company_name"
        private val IMAGE = "image"
        private val PRICE = "price"
        private val OFFER_PRICE = "offer_price"
        private val WEIGHT = "weight"
        private val UNIT = "unit"
        private val IS_NONVEG = "is_nonveg"
        private val IS_CUSTOMIZE = "is_customize"
        private val IS_PRESCRIPTION = "is_prescription"
        private val CART_ID = "cart_id"
        private val CART_QUANTITY = "cart_quantity"
        private val IS_CUSTOMIZE_QUANTITY = "is_customize_quantity"
        private val IN_OUT_OF_STOCK_STATUS = "in_out_of_stock_status"
        private val CUSTOMIZE_ITEM = "customize_item"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + CATEGORY_ID + " TEXT,"
                + SUBCATEGORY_ID + " TEXT," + CATEGORY_ID + " TEXT,"+ SUBCATEGORY_ID + " TEXT,"
                + PRODUCT_ID + " TEXT,"+ PRODUCT_ID + " TEXT,"+ PRODUCT_NAME + " TEXT,"
                + COMPANY_NAME + " TEXT,"+ IMAGE + " TEXT,"+ PRICE + " TEXT,"
                + OFFER_PRICE + " TEXT,"+ WEIGHT + " TEXT,"+ UNIT + " TEXT,"
                + IS_NONVEG + " TEXT,"+ IS_CUSTOMIZE + " TEXT,"+ IS_PRESCRIPTION + " TEXT,"
                + CART_ID + " TEXT,"+ CART_QUANTITY + " TEXT,"+ IS_CUSTOMIZE_QUANTITY + " TEXT,"
                + IN_OUT_OF_STOCK_STATUS + " TEXT,"+ CUSTOMIZE_ITEM + " TEXT,"
                + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
       //do work
    }


    //method to insert data
    fun insertData(products: Product) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CATEGORY_ID, products.product_category_id)
        contentValues.put(SUBCATEGORY_ID, products.product_sub_category_id)
        contentValues.put(PRODUCT_ID, products.product_id)
        contentValues.put(PRODUCT_NAME, products.product_name)
        contentValues.put(COMPANY_NAME, products.company_name)
        contentValues.put(IMAGE, products.image)
        contentValues.put(PRICE, products.price)
        contentValues.put(OFFER_PRICE, products.offer_price)
        contentValues.put(WEIGHT, products.weight)
        contentValues.put(UNIT, products.unit)
        contentValues.put(IS_NONVEG, products.is_nonveg)
        contentValues.put(IS_CUSTOMIZE, products.is_customize)
        contentValues.put(IS_PRESCRIPTION, products.is_prescription)
        contentValues.put(CART_ID, products.cart_id)
        contentValues.put(CART_QUANTITY, products.cart_quantity)
        contentValues.put(IS_CUSTOMIZE_QUANTITY, products.is_customize_quantity)
        contentValues.put(IN_OUT_OF_STOCK_STATUS, products.in_out_of_stock_status)
        contentValues.put(CUSTOMIZE_ITEM, "")

        val result = database.insert(TABLE_PRODUCTS, null, contentValues)
        if (result == (0).toLong()) {
            Log.d("result_if__",result.toString())
        }
        else {
            Log.d("result_else__",result.toString())
        }
    }

    //method to read data(fetch data)
    fun getProductsData(): MutableList<Product> {
        val list: MutableList<Product> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLE_PRODUCTS"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val model = Product()
                model.product_category_id = result.getString(result.getColumnIndex(CATEGORY_ID))
                model.product_sub_category_id = result.getString(result.getColumnIndex(SUBCATEGORY_ID))
                model.product_id = result.getString(result.getColumnIndex(PRODUCT_ID))
                model.product_name = result.getString(result.getColumnIndex(PRODUCT_NAME))
                model.company_name = result.getString(result.getColumnIndex(COMPANY_NAME))
                model.image = result.getString(result.getColumnIndex(IMAGE))
                model.price = result.getString(result.getColumnIndex(PRICE))
                model.offer_price = result.getString(result.getColumnIndex(OFFER_PRICE))
                model.weight = result.getString(result.getColumnIndex(WEIGHT))
                model.unit = result.getString(result.getColumnIndex(UNIT))
                model.is_nonveg = result.getString(result.getColumnIndex(IS_NONVEG))
                model.is_customize = result.getString(result.getColumnIndex(IS_CUSTOMIZE))
                model.is_prescription = result.getString(result.getColumnIndex(IS_PRESCRIPTION))
                model.cart_id = result.getString(result.getColumnIndex(CART_ID))
                result.getString(result.getColumnIndex(CART_QUANTITY)).also { model.cart_quantity = it.toInt() }
                result.getString(result.getColumnIndex(IS_CUSTOMIZE_QUANTITY)).also { model.is_customize_quantity = it.toInt() }

                model.in_out_of_stock_status = result.getString(result.getColumnIndex(
                    IN_OUT_OF_STOCK_STATUS))

                list.add(model)
            }
            while (result.moveToNext())
        }
        return list
    }

    //method to update data
    fun updateProduct(pModel: Product):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CATEGORY_ID, pModel.product_category_id)
        contentValues.put(SUBCATEGORY_ID, pModel.product_sub_category_id)
        contentValues.put(PRODUCT_ID, pModel.product_id)
        contentValues.put(PRODUCT_NAME, pModel.product_name)
        contentValues.put(COMPANY_NAME, pModel.company_name)
        contentValues.put(IMAGE, pModel.image)
        contentValues.put(PRICE, pModel.price)
        contentValues.put(OFFER_PRICE, pModel.offer_price)
        contentValues.put(WEIGHT, pModel.weight)
        contentValues.put(UNIT, pModel.unit)
        contentValues.put(IS_NONVEG, pModel.is_nonveg)
        contentValues.put(IS_CUSTOMIZE, pModel.is_customize)
        contentValues.put(IS_PRESCRIPTION, pModel.is_prescription)
        contentValues.put(CART_ID, pModel.cart_id)
        contentValues.put(CART_QUANTITY, pModel.cart_quantity)
        contentValues.put(IS_CUSTOMIZE_QUANTITY, pModel.is_customize_quantity)

        // Updating Row
        val success = db.update(TABLE_PRODUCTS, contentValues,"product_id="+pModel.product_id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to delete data
    fun deleteProduct(pModel: Product):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
       // contentValues.put(KEY_ID, pModel.product_id)
        // Deleting Row
        val success = db.delete(TABLE_PRODUCTS,"product_id="+pModel.product_id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }


    //method to delete data
    fun deleteAllProducts(pModel: Product){
        val db = this.writableDatabase
        db.execSQL("delete from "+ TABLE_PRODUCTS);
        db.close() // Closing database connection
    }



}