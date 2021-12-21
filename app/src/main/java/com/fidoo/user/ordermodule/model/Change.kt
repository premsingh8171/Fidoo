package com.fidoo.user.ordermodule.model

data class Change(
    val change_id: String,
    val is_out_of_stock: String,
    val out_of_stock_reason: String,
    val price: String,
    val product_name: String,
    val quantity_avail: Int,
    val quantity_ordered: String,
    val quantity_unavailable: String,
    val refund_amount: Int,
    val unit: String,
    val updated_price: String,
    val weight: String
)