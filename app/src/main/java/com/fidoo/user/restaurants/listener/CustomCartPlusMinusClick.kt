package com.fidoo.user.restaurants.listener

interface CustomCartPlusMinusClick {
    fun onIdSelected(productId: String?,
                     type: String?,
                     price: String?,
                     sub_cat_name: String?,
                     tempSelectionCount: Int)

}