package com.payroc.transaction.data.model

import com.google.gson.annotations.SerializedName

data class CardList(
    @SerializedName("cards") var cards: Cards = Cards(),
)

data class Cards(
    @SerializedName("card") var card: ArrayList<Card> = arrayListOf(),
)

data class Card(
    @SerializedName("payloadType") var payloadType: String,
    @SerializedName("dataKsn") var dataKsn: String,
    @SerializedName("tags") var tags: ArrayList<Tags>? = null,
    @SerializedName("cardholdername") var cardholdername: String? = null,
    @SerializedName("serialNumber") var serialNumber: String? = null,
    @SerializedName("encryptedData") var encryptedData: String? = null,
)

data class Tags(
    @SerializedName("value") var value: String,
    @SerializedName("key") var key: String,
)