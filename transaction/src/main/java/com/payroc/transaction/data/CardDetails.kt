package com.payroc.transaction.data

sealed class CardDetails

data class EMVCard(val payloadType: String = "EMV", val dataKsn: String, val tags: List<Tags>) :
    CardDetails()

data class Tags(val value: String, val key: String)

data class MAGStripeCard(
    val serialNumber: String,
    val payloadType: String = "MAG_STRIPE",
    val cardholdername: String,
    val encryptedData: String,
    val dataKsn: String,
) : CardDetails()
