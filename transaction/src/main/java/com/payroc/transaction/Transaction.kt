package com.payroc.transaction

import android.util.Log
import com.google.gson.Gson
import com.payroc.transaction.data.PayrocRepository
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.CustomerAccount
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.OrderBreakdown
import com.payroc.transaction.data.model.Error
import com.payroc.transaction.data.model.request.CardDetails
import com.payroc.transaction.data.model.request.Device
import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.utility.createOrderID
import com.payroc.transaction.utility.generateTlv
import retrofit2.HttpException

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

    private val repository: PayrocRepository = PayrocRepository.getInstance()

    /**
     * Authenticate with the provided API Key in order to generate a Bearer Token for subsequent calls.
     * This ensures that a token is never expired for calls used at a later stage.
     *
     * If the API Key is valid, and authentication is successful, the token will be set and returned
     * to the transaction related API call that requires it. (Per call Authentication)
     *
     * If authentication fails, [TransactionState] will update to [TransactionState.ERROR] and the
     * [Error] returned with the response will be provided back to the client via the [TransactionListener]
     * which will update [PayrocClient.getClientErrorResponse] and provide the error to be used by the
     * developer.
     *
     * @param apiKey the API key to be used for authentication.
     * @see [TransactionListener.clientErrorReceived]
     */
    private suspend fun authenticate(apiKey: String): String? {
        var token: String? = null
        val response = repository.authenticate(apiKey)
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

    /**
     * Receives a [Card] that was provided to [PayrocClient] in order to process a transaction. An
     * Authentication call will be done first in order to generate an up to date Bearer Token via the
     * [authenticate] method.
     *
     * If the [authenticate] method is successful, and returns a non-null token String,
     * a [buildTransactionRequest] API call will begin.
     *
     * Otherwise, if [authenticate] fails, and a null token string is returned, [TransactionState]
     * will update to [TransactionState.ERROR] and an appropriate error message will be provided.
     *
     * @param card the card that will be used to debit the amount specified when creating a [Transaction]
     */
    suspend fun provideCard(card: Card) {
        val token = authenticate(apiKey)
        token?.let {
            transactionListener.updateState(TransactionState.READING)
            buildTransactionRequest(token, card)
        } ?: run {
            transactionListener.updateState(TransactionState.ERROR)
            transactionListener.clientMessageReceived("There was an issue reading the provided card")
            Log.e(TAG, "There was an issue reading the provided card")
        }
    }

    /**
     * Builds an appropriate [TransactionRequest] based on the type of [Card] provided. This SDK
     * currently only supports EMV and MAG_STRIPE payments. Any other provided card will result in
     * a [TransactionState.ERROR].
     *
     * If a supported [Card] is provided [transactionAPIRequest] will be called.
     *
     *@param token the valid Bearer Token created by the [authenticate] method
     *@param card the card that will be used to debit the amount specified when creating a [Transaction]
     */
    private suspend fun buildTransactionRequest(token: String, card: Card) {
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
        transactionRequest?.let { request ->
            transactionAPIRequest(token, request)
        } ?: run {
            transactionListener.updateState(TransactionState.ERROR)
            transactionListener.clientMessageReceived("Unknown card type used")
            Log.e(TAG, "Unknown card type used")
        }
    }

    /**
     * Uses the valid Bearer Token created by the [authenticate] method and the [TransactionRequest]
     * created by the [buildTransactionRequest] method to send a request to create and process this
     * [Transaction]
     *
     * If a transaction is successfully completed, [TransactionState] will update to
     * [TransactionState.COMPLETE] and the customer and merchant receipts will be provided to the
     * [PayrocClient] via the [TransactionListener.receiptReceived] listener method where the
     * developer can retrieve it via [PayrocClient.getClientReceiptResponse]
     *
     * In the event of an unsuccessful API request [TransactionState] will update to [TransactionState.ERROR] and the
     * [Error] returned with the response will be provided back to the client via the [TransactionListener]
     * which will update [PayrocClient.getClientErrorResponse] and provide the error to be used by the
     * developer.
     *
     * @param token the valid Bearer Token created by the [authenticate] method
     * @param transactionRequest the transactionRequest built from [buildTransactionRequest]
     * @see [TransactionListener]
     */
    private suspend fun transactionAPIRequest(
        token: String,
        transactionRequest: TransactionRequest,
    ) {
        transactionListener.clientMessageReceived("Going online")
        transactionRequest.let {
            val response = repository.createTransaction(token, transactionRequest)
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
        }
    }


    /**
     * A generic [Throwable] error handler.
     *
     * Will update [TransactionState] to [TransactionState.ERROR] and provide a generic client error
     * message via [TransactionListener.clientMessageReceived] and, if the throwable contains an
     * error message, will log said message (otherwise a generic exception log will be provided)
     *
     * @param t the [Throwable] to be handled.
     */
    private fun notifyThrowableError(t: Throwable) {
        transactionListener.updateState(TransactionState.ERROR)
        transactionListener.clientMessageReceived("There was an error processing the payment")
        val message = t.message ?: "Unknown exception thrown, please check logs"
        Log.e(TAG, message)
    }
}

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