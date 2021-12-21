package com.fidoo.user.grocerynewui.onclicklistener

import com.fidoo.user.grocerynewui.model.getGroceryProducts.Product
import com.fidoo.user.grocerynewui.model.getGroceryProducts.Subcategory

interface ItemViewOnClickListener {
    fun addtoCart(main_position:Int,productModel: Product, pos: Int, store_id: String, item_count: String,type:String)
    fun plusItemCart(main_position:Int,productModel: Product, pos: Int, store_id: String, item_count: String,type:String)
    fun minusItemCart(main_position:Int,productModel: Product, pos: Int, store_id: String, item_count: String,type:String)
    fun onClickViewAll(main_position:Int, productModel: Subcategory, pos: Int, store_id: String, item_count: String, type:String)
}