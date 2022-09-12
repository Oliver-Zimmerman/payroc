package com.payroc.transaction.data

import com.google.gson.annotations.SerializedName

/*sealed class CardDetails

data class EMVCard(val payloadType: String = "EMV", val dataKsn: String, val tags: List<Tags>) :
    CardDetails()

data class Tags(val value: String, val key: String)

data class MAGStripeCard(
    val serialNumber: String,
    val payloadType: String = "MAG_STRIPE",
    val cardholdername: String,
    val encryptedData: String,
    val dataKsn: String,
) : CardDetails()*/

/*data class Cards(
    val cards: ArrayList<Card>,
)

data class Card(
    val dataKsn: String,
    val payloadType: String,
    val tags: List<Tag>? = null,
    val cardholdername: String? = null,
    val serialNumber: String? = null,
    val encryptedData: String? = null,
)

data class Tag(
    val key: String,
    val value: String,
)*/
data class CardDetails(
    @SerializedName("cards") var cards: Cards = Cards(),
)

data class Cards(
    @SerializedName("card") var card: ArrayList<Card> = arrayListOf(),
)

data class Card(
    @SerializedName("payloadType") var payloadType: String,
    @SerializedName("dataKsn") var dataKsn: String,
    @SerializedName("tags") var tags: ArrayList<Tags> = arrayListOf(),
)

data class Tags(
    @SerializedName("value") var value: String,
    @SerializedName("key") var key: String,
)