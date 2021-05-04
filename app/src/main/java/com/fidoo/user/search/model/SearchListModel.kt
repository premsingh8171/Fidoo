package com.fidoo.user.search.model

class SearchListModel(
    var accessToken: String,
    var accountId: String,
    var error: Boolean,
    var error_code: Int,
    var product_list: List<Any>,
    var store: List<Store>
)

 class Store(
    var delivery_time: Any,
    var key: Int,
    var productsList: List<ProductsList>,
    var store_id: String,
    var store_image: Any,
    var store_name: String
)

 class ProductsList (
    var cart_quantity: Int,
    var category_name: String,
    var company_name: String,
    var customize_item: List<Any>,
    var delivery_time: Any,
    var is_customize: String,
    var is_customize_quantity: Int,
    var is_nonveg: String,
    var is_prescription: String,
    var offer_price: String,
    var price: String,
    var product_desc: String,
    var product_id: String,
    var product_image: String,
    var product_name: String,
    var rating: String,
    var service_name: String,
    var store_address: String,
    var store_id: String,
    var store_image: String,
    var store_name: String,
    var unit: String,
    var weight: String
)