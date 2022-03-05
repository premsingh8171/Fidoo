package com.fidoo.user.ordermodule.model

data class TrackPackageOrderModel(
    var accessToken: String,
    var accountId: String,
    var delivery_boy_id: String,
    var delivery_boy_name: String,
    var delivery_boy_phone: String,
    var customer_phone: String,
    var error: Boolean,
    var error_code: String,
    var message: String,
    var order_id: String,
    var order_message: String,
    var order_status: String,
    var rider_status: String,
    var total_delivery_charge: String,
    var updated_at: String
)