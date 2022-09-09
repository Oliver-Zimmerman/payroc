package com.payroc.transaction.data.model.response

import com.google.gson.annotations.SerializedName
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.request.CustomerAccount

data class TransactionResponse(
    @SerializedName("uniqueReference") var uniqueReference: String? = null,
    @SerializedName("terminal") var terminal: String? = null,
    @SerializedName("order") var order: Order? = Order(),
    @SerializedName("customerAccount") var customerAccount: CustomerAccount? = CustomerAccount(),
    @SerializedName("securityCheck") var securityCheck: SecurityCheck? = SecurityCheck(),
    @SerializedName("transactionResult") var transactionResult: TransactionResult? = TransactionResult(),
    @SerializedName("additionalDataFields") var additionalDataFields: ArrayList<AdditionalDataFields> = arrayListOf(),
    @SerializedName("emvTags") var emvTags: ArrayList<EmvTags> = arrayListOf(),
)

data class SecurityCheck(
    @SerializedName("cvvResult") var cvvResult: String? = null,
    @SerializedName("avsResult") var avsResult: String? = null,
)

data class StoredPaymentCredentials(
    @SerializedName("terminal") var terminal: String? = null,
    @SerializedName("merchantReference") var merchantReference: String? = null,
    @SerializedName("cardholderName") var cardholderName: String? = null,
    @SerializedName("credentialsNumber") var credentialsNumber: String? = null,
    @SerializedName("maskedPan") var maskedPan: String? = null,
    @SerializedName("securityCheck") var securityCheck: String? = null,
)

data class TransactionResult(
    @SerializedName("type") var type: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("approvalCode") var approvalCode: String? = null,
    @SerializedName("dateTime") var dateTime: String? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("authorizedAmount") var authorizedAmount: Int? = null,
    @SerializedName("resultCode") var resultCode: String? = null,
    @SerializedName("resultMessage") var resultMessage: String? = null,
    @SerializedName("storedPaymentCredentials") var storedPaymentCredentials: StoredPaymentCredentials? = StoredPaymentCredentials(),
)

data class AdditionalDataFields(
    @SerializedName("name") var name: String? = null,
    @SerializedName("value") var value: String? = null,
)

data class EmvTags(
    @SerializedName("hex") var hex: String? = null,
    @SerializedName("value") var value: String? = null,
)