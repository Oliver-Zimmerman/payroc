package com.payroc.transaction

import android.util.Log
import com.payroc.transaction.data.PayrocRepository
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.CustomerAccount
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.OrderBreakdown
import com.payroc.transaction.data.model.request.CardDetails
import com.payroc.transaction.data.model.request.Device
import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.utility.createOrderID
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import java.lang.StringBuilder
import java.math.BigInteger


class Transaction(
    private val amount: Double,
    private val terminal: String,
    private val apiKey: String,
    private val transactionListener: TransactionListener,
) {

    private val _repository: PayrocRepository = PayrocRepository.getInstance()


    private suspend fun authenticate(apiKey: String): String? {
        var token: String? = null
        val authResponse = _repository.authenticate(apiKey)
        authResponse.onSuccess {
            token = this.data.token
        }.onError {
            Log.e("Error", "Error....")
        }.onException {
            Log.e("Error", "Error....")
        }
        return token
    }

    suspend fun provideCard(card: Card) {
        val token = authenticate(apiKey)
        token?.let {
            transactionListener.updateState(TransactionState.READING)
            transactionRequest(token, card)
        } ?: run {
            transactionListener.updateState(TransactionState.ERROR)
            //ToDo log state properly
            Log.e("Error", "Error....")
        }
    }

    private suspend fun transactionRequest(token: String, card: Card) {
        var transactionRequest: TransactionRequest? = null
        when (card.payloadType) {
            "EMV" -> {
                transactionRequest = TransactionRequest(
                    terminal = terminal,
                    order = Order(orderId = createOrderID(),
                        totalAmount = amount,
                        orderBreakdown = OrderBreakdown(subtotalAmount = amount)),
                    customerAccount = CustomerAccount.EMVCustomerAccount(device = Device(dataKsn = card.dataKsn),
                        generateTlv(card),
                        card.payloadType)
                )
            }
            "MAG_STRIPE" -> {
                transactionRequest = TransactionRequest(
                    terminal = terminal,
                    order = Order(orderId = createOrderID(),
                        totalAmount = amount,
                        orderBreakdown = OrderBreakdown(subtotalAmount = amount)),
                    customerAccount = CustomerAccount.MAGCustomerAccount(payloadType = card.payloadType,
                        cardholderName = card.cardholdername!!,
                        cardDetails = CardDetails(
                            device = Device(dataKsn = card.dataKsn,
                                serialNumber = card.serialNumber),
                            encryptedData = card.encryptedData!!
                        ))
                )

            }
        }
        transactionRequest?.let {
            transactionListener.updateState(TransactionState.PROCESSING)
            val transactionResponse =
                _repository.createTransaction(token, transactionRequest).onSuccess {
                    //ToDo handle success filter object and obtain receipts only
                    // transactionListener.receiptReceived(receipt)
                }.onError {
                    //ToDo log state properly
                    Log.e("Error", "Error....")

                }.onException {
                    //ToDo log state properly
                    Log.e("Error", "Error....")
                }
        } ?: run {
            //ToDO update error state with message to client
            Log.e("Error", "Unknown card type used")
        }
    }
}

private fun hexToBinary(hex: String): String {
    val len = hex.length * 4
    var bin: String = BigInteger(hex, 16).toString(2)

    //left pad the string result with 0s if converting to BigInteger removes them.
    if (bin.length < len) {
        val diff = len - bin.length
        var pad = ""
        for (i in 0 until diff) {
            pad += "0"
        }
        bin = pad + bin
    }
    return bin
}

// Move to utility?
private fun generateTlv(card: Card): String {

    val stringBuilder = StringBuilder()
    // Step 1, convert Hex value to binary string
    // Step 2, get the length of the binary string
    // Step 3, divide binary string by 8 which is the bytes
    card.tags?.forEach { tag ->
        val bin = hexToBinary(tag.value)
        val hex = Integer.toHexString(bin.length / 8)
        stringBuilder.append(tag.key.uppercase())
        if (hex.length < 2) {
            stringBuilder.append("0" + hex.uppercase())
        } else {
            stringBuilder.append(hex.uppercase())
        }
        stringBuilder.append(tag.value.uppercase())
    }

    return stringBuilder.toString()
}