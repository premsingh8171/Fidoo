package com.fidoo.user.newRestaurants.listener

interface NewCustomCartPlusMinusClick {
	fun onIdSelected(
		productId: String?,
		type: String?,
		price: String?,
		sub_cat_name: String?,
		tempSelectionCount: Int
	)

}