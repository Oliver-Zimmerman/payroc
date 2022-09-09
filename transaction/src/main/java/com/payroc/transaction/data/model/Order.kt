package com.payroc.transaction.data.model

import com.google.gson.annotations.SerializedName

data class Order (
    @SerializedName("orderId"        ) var orderId        : String?         = null,
    @SerializedName("currency"       ) var currency       : String?         = null,
    @SerializedName("totalAmount"    ) var totalAmount    : Int?            = null,
    @SerializedName("orderBreakdown" ) var orderBreakdown : OrderBreakdown? = OrderBreakdown()
)

data class OrderBreakdown (
    @SerializedName("subtotalAmount" ) var subtotalAmount : Int? = null
)