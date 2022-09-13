package com.payroc.transaction.data.model.request

import com.payroc.transaction.data.model.CustomerAccount
import com.payroc.transaction.data.model.Order

data class TransactionRequest(
    var channel: String = "POS",
    var terminal: String,
    var order: Order,
    var customerAccount: CustomerAccount,
)

data class Device(
    var type: String = "PAX_A920_PRO",
    var dataKsn: String,
    var serialNumber: String? = null,
)

data class CardDetails (
    var device: Device,
    var encryptedData: String
)

data class EMVTags(
    var hex: String,
    var value: String,
)
