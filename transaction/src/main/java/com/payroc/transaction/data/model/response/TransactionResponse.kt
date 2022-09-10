package com.payroc.transaction.data.model.response

import com.google.gson.annotations.SerializedName
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.request.CustomerAccount

data class TransactionResponse(
    @SerializedName("uniqueReference") var uniqueReference: String,
    @SerializedName("terminal") var terminal: String,
    @SerializedName("order") var order: Order,
    @SerializedName("customerAccount") var customerAccount: CustomerAccount,
    @SerializedName("securityCheck") var securityCheck: SecurityCheck,
    @SerializedName("transactionResult") var transactionResult: TransactionResult,
    @SerializedName("additionalDataFields") var additionalDataFields: ArrayList<AdditionalDataFields> = arrayListOf(),
    @SerializedName("emvTags") var emvTags: ArrayList<EmvTags> = arrayListOf(),
)

data class SecurityCheck(
    @SerializedName("cvvResult") var cvvResult: String,
    @SerializedName("avsResult") var avsResult: String,
)

data class StoredPaymentCredentials(
    @SerializedName("terminal") var terminal: String,
    @SerializedName("merchantReference") var merchantReference: String,
    @SerializedName("cardholderName") var cardholderName: String,
    @SerializedName("credentialsNumber") var credentialsNumber: String,
    @SerializedName("maskedPan") var maskedPan: String,
    @SerializedName("securityCheck") var securityCheck: String,
)

data class TransactionResult(
    @SerializedName("type") var type: String,
    @SerializedName("status") var status: String,
    @SerializedName("approvalCode") var approvalCode: String,
    @SerializedName("dateTime") var dateTime: String,
    @SerializedName("currency") var currency: String,
    @SerializedName("authorizedAmount") var authorizedAmount: Int,
    @SerializedName("resultCode") var resultCode: String,
    @SerializedName("resultMessage") var resultMessage: String,
    @SerializedName("storedPaymentCredentials") var storedPaymentCredentials: StoredPaymentCredentials,
)

data class AdditionalDataFields(
    @SerializedName("name") var name: String,
    @SerializedName("value") var value: String,
)

data class EmvTags(
    @SerializedName("hex") var hex: String,
    @SerializedName("value") var value: String,
)