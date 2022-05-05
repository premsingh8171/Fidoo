package com.fidoo.user.ordermodule.NewOrderTrackModule.NewTrackModel


import com.google.gson.annotations.SerializedName

data class OrderDetail(
    @SerializedName("customize_qty")
    val customizeQty: String,
    @SerializedName("customize_sub_cat_id")
    val customizeSubCatId: String,
    @SerializedName("offer_price")
    val offerPrice: String,
    @SerializedName("product_customize_id")
    val productCustomizeId: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("qty")
    val qty: String,
    @SerializedName("sub_cat_name")
    val subCatName: String
)