package com.payroc.transaction.data.model

data class Order(
    var orderId: String,
    var currency: String = "USD",
    var totalAmount: Double,
    var orderBreakdown: OrderBreakdown,
)

data class OrderBreakdown(
    var subtotalAmount: Double,
)