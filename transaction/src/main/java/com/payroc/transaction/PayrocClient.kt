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
    private val clientReceiptLiveData = MutableLiveData<ArrayList<Receipts>>()

    /* private val _stateFlow = MutableStateFlow(TransactionState.IDLE)
     val stateFlow = _stateFlow.asStateFlow()

     private val _clientMessageFlow = MutableStateFlow("")
     val clientMessageFlow = _clientMessageFlow.asStateFlow()

     private val _clientReceiptFlow = MutableStateFlow<ArrayList<Receipts>?>(null)
     val clientReceiptFlow = _clientReceiptFlow.asStateFlow()*/

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
     * Returns the receipt as a result of a transaction in the form of LiveData
     * @see [Receipts]
     */
    fun getClientReceiptResponse(): LiveData<ArrayList<Receipts>> = clientReceiptLiveData

    fun startTransaction(amount: Double) {
        transaction = Transaction(amount, terminal, apiKey, this)
        // _stateFlow.value = TransactionState.CARD_REQUEST
        // _clientMessageFlow.value = "Please provide card"
        stateLiveData.value = TransactionState.CARD_REQUEST
        clientMessageLiveData.value = "Please provide card"
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

    override fun receiptReceived(receipts: ArrayList<Receipts>) {
        Log.i(TAG, "Receipts Received :: $receipts")
        clientReceiptLiveData.value = receipts
        stateLiveData.value = TransactionState.COMPLETE
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