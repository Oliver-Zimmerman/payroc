package com.payroc.transaction.data.model

import com.payroc.transaction.data.model.request.CardDetails
import com.payroc.transaction.data.model.request.Device

sealed class CustomerAccount {
    data class EMVCustomerAccount(
        var device: Device,
        var tlv: String,
        var payloadType: String
    ) : CustomerAccount()

    data class MAGCustomerAccount(
        var payloadType: String,
        var cardholderName: String,
        var cardDetails: CardDetails
    ) : CustomerAccount()
}