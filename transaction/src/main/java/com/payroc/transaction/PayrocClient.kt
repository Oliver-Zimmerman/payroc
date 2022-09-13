package com.payroc.transaction

import android.util.Log
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.response.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PayrocClient(private val terminal: String, private val apiKey: String) : TransactionListener {

    private val _stateFlow = MutableStateFlow(TransactionState.IDLE)
    val stateFlow = _stateFlow.asStateFlow()

    private val _cardReadRequestStatusFlow = MutableStateFlow(false)
    val cardReadRequestStatusFlow = _cardReadRequestStatusFlow.asStateFlow()

    private val _clientMessageFlow = MutableStateFlow("")
    val clientMessageFlow = _clientMessageFlow.asStateFlow()

    private val _clientReceiptFlow = MutableStateFlow<TransactionResponse?>(null)
    val clientReceiptFlow = _clientReceiptFlow.asStateFlow()

    private var transaction: Transaction? = null

    fun startTransaction(amount: Double) {
        transaction = Transaction(amount, terminal, apiKey, this)
        _stateFlow.value = TransactionState.CARD_REQUEST
        _clientMessageFlow.value = "Please provide card"
    }

    suspend fun readCardData(card: Card) {
        if (_stateFlow.value == TransactionState.CARD_REQUEST) {
           // _stateFlow.value = TransactionState.STARTED
            transaction?.let {
               transaction?.provideCard(card)
               // _clientReceiptFlow.value = receipt
            } ?: run {
                _stateFlow.value = TransactionState.ERROR
                Log.e("Payroc", "No ongoing transaction")
            }
        } else {
            Log.e("Payroc", "No card required")
        }
    }

    override fun updateState(state: TransactionState) {
       // _stateFlow.value = state
    }

    override fun receiptReceived(transactionResponse: TransactionResponse) {
        _clientReceiptFlow.value = transactionResponse
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