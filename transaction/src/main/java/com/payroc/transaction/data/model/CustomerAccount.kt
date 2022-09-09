package com.payroc.transaction.data.model

import com.google.gson.annotations.SerializedName

data class CustomerAccount(
    @SerializedName("cardType") var cardType: String? = null,
    @SerializedName("cardholderName") var cardholderName: String? = null,
    @SerializedName("maskedPan") var maskedPan: String? = null,
    @SerializedName("expiryDate") var expiryDate: String? = null,
    @SerializedName("entryMethod") var entryMethod: String? = null,
)
