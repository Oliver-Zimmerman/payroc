package com.payroc.transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.response.Receipts
import com.payroc.transaction.data.model.response.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PayrocClient(private val terminal: String, private val apiKey: String) : TransactionListener {
    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val stateLiveData = MutableLiveData<TransactionState>()
    private val clientMessageLiveData = MutableLiveData<String>()
    private val clientErrorLiveData = MutableLiveData<Error>()
    private val clientReceiptLiveData = MutableLiveData<ArrayList<Receipts>>()

    private var transaction: Transaction? = null

    /**
     * Returns the current SDK State response in the form of LiveData
     * @see [TransactionState]
     */
    fun getStateResponse(): LiveData<TransactionState> = stateLiveData

    /**
     * Returns the most recent client message response in the form of LiveData
     */
    fun getClientMessageResponse(): LiveData<String> = clientMessageLiveData

    /**
     * Returns the most recent client error response in the form of LiveData
     */
    fun getClientErrorResponse(): LiveData<Error> = clientErrorLiveData

    /**
     * Returns the receipt as a result of a transaction in the form of LiveData
     * @see [Receipts]
     */
    fun getClientReceiptResponse(): LiveData<ArrayList<Receipts>> = clientReceiptLiveData

    fun startTransaction(amount: Double) {
        if (amount <= 0.0) {
            stateLiveData.value = TransactionState.ERROR
            clientMessageLiveData.value = "Invalid transaction amount"
        } else {
            transaction = Transaction(amount, terminal, apiKey, this)
            stateLiveData.value = TransactionState.CARD_REQUEST
            clientMessageLiveData.value = "Please provide card"
        }
    }

    fun cancelTransaction() {
        if (stateLiveData.value == TransactionState.CARD_REQUEST) {
            transaction = null
            stateLiveData.value = TransactionState.IDLE
            clientMessageLiveData.value = "Transaction cancelled"
        } else {
            transaction?.let {
                clientMessageLiveData.value = "Transaction in invalid state. Unable to cancel"
            } ?: run {
                clientMessageLiveData.value = "No transaction to cancel"
            }
        }

    }

    suspend fun readCardData(card: Card) {
        if (stateLiveData.value == TransactionState.CARD_REQUEST) {
            stateLiveData.value = TransactionState.STARTED
            transaction?.let {
                transaction?.provideCard(card)
            } ?: run {
                stateLiveData.value = TransactionState.ERROR
                Log.e(TAG, "No ongoing transaction")
                clientMessageLiveData.value = "No ongoing transaction"
            }
        } else {
            Log.e(TAG, "No card required")
            clientMessageLiveData.value = "No card required"
        }
    }

    override fun updateState(state: TransactionState) {
        Log.i(TAG, "State Received :: $state")
        stateLiveData.value = state
    }

    override fun clientMessageReceived(message: String) {
        Log.i(TAG, "Message Received :: $message")
        clientMessageLiveData.value = message

    }

    override fun clientErrorReceived(error: Error) {
        Log.i(TAG, "Error Received :: $error")
        clientErrorLiveData.value = error
    }

    override fun receiptReceived(receipts: ArrayList<Receipts>) {
        Log.i(TAG, "Receipts Received :: $receipts")
        clientReceiptLiveData.value = receipts
    }
}

