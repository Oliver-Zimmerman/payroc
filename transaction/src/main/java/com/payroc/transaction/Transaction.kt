package com.payroc.transaction

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.payroc.transaction.data.PayrocRepository
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.CustomerAccount
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.OrderBreakdown
import com.payroc.transaction.data.model.request.CardDetails
import com.payroc.transaction.data.model.request.Device
import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.utility.createOrderID
import com.payroc.transaction.utility.generateTlv
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess

class Transaction(
    private val amount: Double,
    private val terminal: String,
    private val apiKey: String,
    private val transactionListener: TransactionListener,
) {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val _repository: PayrocRepository = PayrocRepository.getInstance()

    private suspend fun authenticate(apiKey: String): String? {
        var token: String? = null
        val authResponse = _repository.authenticate(apiKey)
        authResponse.onSuccess {
            token = this.data.token
        }.onError {
            Log.e(TAG, message())
            transactionListener.clientMessageReceived("There was an issue authenticating your client")
        }.onException {
            Log.e(TAG, message())
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
            transactionListener.clientMessageReceived("There was an issue reading the provided card")
            Log.e(TAG, "There was an issue reading the provided card")
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
        transactionListener.clientMessageReceived("Going online")
        transactionRequest?.let {
            transactionListener.updateState(TransactionState.PROCESSING)
            _repository.createTransaction(token, transactionRequest).onSuccess {
                val response = this.data
                transactionListener.updateState(TransactionState.COMPLETE)
                transactionListener.receiptReceived(response.receipts)
                transactionListener.clientMessageReceived("Transaction complete")
            }.onError {
                transactionListener.updateState(TransactionState.ERROR)
                // Delete this and implement enum
                transactionListener.clientMessageReceived("${this.statusCode.code} : There was an error processing the payment")
                Log.e(TAG, message())
            }.onException {
                transactionListener.updateState(TransactionState.ERROR)
                transactionListener.clientMessageReceived("There was an error processing the payment")
                Log.e(TAG, message())
            }
        } ?: run {
            transactionListener.updateState(TransactionState.ERROR)
            transactionListener.clientMessageReceived("Unknown card type used")
            Log.e(TAG, "Unknown card type used")
        }
    }
}