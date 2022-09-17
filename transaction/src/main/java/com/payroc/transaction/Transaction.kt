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
import com.payroc.transaction.data.model.response.AuthenticateResponse
import com.payroc.transaction.data.model.response.TransactionResponse
import com.payroc.transaction.utility.createOrderID
import com.payroc.transaction.utility.generateTlv
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class Transaction(
    private val amount: Double,
    private val terminal: String,
    private val apiKey: String,
    private val transactionListener: TransactionListener,
) {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val gson = Gson()

    private val _repository: PayrocRepository = PayrocRepository.getInstance()

    private suspend fun authenticate(apiKey: String): String? {
        var token: String? = null
        val response = _repository.authenticate(apiKey)
        try {
            if (response.isSuccessful) {
                token = response.body()?.token
            } else {
                kotlin.runCatching {
                    transactionListener.updateState(TransactionState.ERROR)
                    val error =
                        gson.fromJson(response.errorBody()?.string(), Error::class.java)
                    transactionListener.clientErrorReceived(error)
                    transactionListener.clientMessageReceived("There was an error processing the payment")
                    error.details.forEach {
                        Log.e(TAG, it.errorMessage)
                    }
                }
            }
        } catch (e: HttpException) {
            Log.e(TAG, e.message())
        } catch (t: Throwable) {
            notifyThrowableError(t)
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
            val response = _repository.createTransaction(token, transactionRequest)
            try {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    transactionListener.updateState(TransactionState.COMPLETE)
                    // A successful response will always include receipts (!!)
                    transactionListener.receiptReceived(apiResponse!!.receipts)
                    transactionListener.clientMessageReceived("Transaction complete")
                } else {
                    kotlin.runCatching {
                        transactionListener.updateState(TransactionState.ERROR)
                        val error =
                            gson.fromJson(response.errorBody()?.string(), Error::class.java)
                        transactionListener.clientErrorReceived(error)
                        transactionListener.clientMessageReceived("There was an error processing the payment")
                        error.details.forEach {
                            Log.e(TAG, it.errorMessage)
                        }
                    }
                }
            } catch (e: HttpException) {
                Log.e(TAG, e.message())
            } catch (t: Throwable) {
                notifyThrowableError(t)
            }
        } ?: run {
            transactionListener.updateState(TransactionState.ERROR)
            transactionListener.clientMessageReceived("Unknown card type used")
            Log.e(TAG, "Unknown card type used")
        }
    }

    private fun notifyThrowableError(t: Throwable) {
        transactionListener.updateState(TransactionState.ERROR)
        transactionListener.clientMessageReceived("There was an error processing the payment")
        val message = t.message ?: "Unknown exception thrown, please check logs"
        Log.e(TAG, message)
    }
}

data class Error(
    val debugIdentifier: String,
    val details: ArrayList<ErrorDetail>,
)

data class ErrorDetail(
    val errorCode: String,
    val errorMessage: String,
    val about: String?,
    val source: ErrorSource?,
)

data class ErrorSource(
    val location: String,
    val resource: String?,
    val property: String?,
    val value: String?,
    val expected: String?,
)

/**
 *
 * Enum class to represent the different Transaction States that a transaction can be in.
 *
 * @property IDLE idle state, no transaction is being handled
 * @property STARTED a transaction has been started, the amount has been provided
 * @property CARD_REQUEST the client is waiting for a card to process the transaction
 * @property READING a card has been provided and the information is being read
 * @property PROCESSING the client is processing the transaction with the provided amount and card details
 * @property COMPLETE a transaction has successfully completed
 * @property ERROR an error has occurred
 */
enum class TransactionState {
    IDLE,
    STARTED,
    CARD_REQUEST,
    READING,
    PROCESSING,
    COMPLETE,
    ERROR
}