package com.fidoo.user.restaurants.listener

 interface AdapterCartAddRemoveClick {
    fun onAddItemClick(
        quantity:String?,
        productId: String?,
        items: String?,
        offerPrice: String?,
        customizeid: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    )

    fun onRemoveItemClick(
        productId: String?,
        quantity: String?,
        customizeid: String?,
        prodcustCustomizeId: String?,
        cart_id: String?
    )
}