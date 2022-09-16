package com.payroc.transaction.data.model.response

import com.google.gson.annotations.SerializedName
import com.payroc.transaction.data.model.Order

data class TransactionResponse(
    @SerializedName("uniqueReference") var uniqueReference: String,
    @SerializedName("terminal") var terminal: String,
    @SerializedName("order") var order: Order,
    @SerializedName("customerAccount") var customerAccount: CustomerAccount,
    @SerializedName("securityCheck") var securityCheck: SecurityCheck,
    @SerializedName("transactionResult") var transactionResult: TransactionResult,
    @SerializedName("additionalDataFields") var additionalDataFields: ArrayList<AdditionalDataFields> = arrayListOf(),
    @SerializedName("emvTags") var emvTags: ArrayList<EmvTags> = arrayListOf(),
    @SerializedName("receipts") var receipts: ArrayList<Receipts> = arrayListOf(),
)

data class SecurityCheck(
    @SerializedName("cvvResult") var cvvResult: String,
    @SerializedName("avsResult") var avsResult: String,
)

data class CustomerAccount(
    @SerializedName("cardType") var cardType: String? = null,
    @SerializedName("cardholderName") var cardholderName: String? = null,
    @SerializedName("maskedPan") var maskedPan: String? = null,
    @SerializedName("expiryDate") var expiryDate: String? = null,
    @SerializedName("entryMethod") var entryMethod: String? = null,
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
    @SerializedName("authorizedAmount") var authorizedAmount: Double,
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

data class Receipts(
    @SerializedName("copy") var copy: String? = null,
    @SerializedName("header") var header: String? = null,
    @SerializedName("merchantDetails") var merchantDetails: ArrayList<MerchantDetails> = arrayListOf(),
    @SerializedName("transactionData") var transactionData: ArrayList<TransactionData> = arrayListOf(),
    @SerializedName("customFields") var customFields: ArrayList<String> = arrayListOf(),
    @SerializedName("iccData") var iccData: ArrayList<IccData> = arrayListOf(),
    @SerializedName("footer") var footer: String? = null,
)

data class IccData(
    @SerializedName("order") var order: Int? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("value") var value: String? = null,
)

data class TransactionData(
    @SerializedName("order") var order: Int? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("value") var value: String? = null,
)

data class MerchantDetails(
    @SerializedName("order") var order: Int? = null,
    @SerializedName("label") var label: String? = null,
    @SerializedName("value") var value: String? = null,
)