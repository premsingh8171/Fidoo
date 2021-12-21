package com.fidoo.user.dailyneed.onclicklistener

import com.fidoo.user.dailyneed.model.prdmodel.Data
import com.fidoo.user.dailyneed.model.prdmodel.Product

interface ItemOnCatClickListener {
    fun addtoCart(main_position:Int, productModel: Product, pos: Int, store_id: String, item_count: String, type:String)
    fun plusItemCart(main_position:Int,productModel: Product, pos: Int, store_id: String, item_count: String,type:String)
    fun minusItemCart(main_position:Int,productModel: Product, pos: Int, store_id: String, item_count: String,type:String)
    fun onClickViewAll(main_position: Int, model: Data)
}

