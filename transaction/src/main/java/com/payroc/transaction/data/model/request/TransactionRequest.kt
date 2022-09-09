package com.payroc.transaction.data.model.request

import com.payroc.transaction.data.model.Order

data class TransactionRequest(
    var channel: String = "POS",
    var terminal: String,
    var order: Order,
    var customerAccount: CustomerAccount,
)

data class Device(
    var type: String = "PAX_A920_PRO",
    var dataKsn: String
)

data class CustomerAccount(
    var device: Device,
    var tlv: String,
    var payloadType: String
)