package com.payroc.transaction

import android.util.Log
import com.payroc.transaction.data.PayrocRepository
import com.payroc.transaction.utility.createOrderID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PayrocClient(private val terminal: String, private val apiKey: String) {

    private val _repository: PayrocRepository = PayrocRepository.getInstance()

    private val _stateFlow = MutableStateFlow(TransactionState.IDLE)
    val stateFlow = _stateFlow.asStateFlow()

    private val _cardReadRequestStatusFlow = MutableStateFlow(false)
    val cardReadRequestStatusFlow = _cardReadRequestStatusFlow.asStateFlow()

    private val _clientMessageFlow = MutableStateFlow("")
    val clientMessageFlow = _clientMessageFlow.asStateFlow()

    private var transaction: Transaction? = null

    fun startTransaction(amount: Double) {
        // Create a transaction instance
        transaction = Transaction(amount, createOrderID())
        _stateFlow.value = TransactionState.STARTED
        _clientMessageFlow.value = "Please provide card"
    }

    fun readCardData() {
        if (_stateFlow.value == TransactionState.CARD_REQUEST) {
            transaction?.provideCard() ?: Log.e("Payroc", "No ongoing transaction")
        } else {
            Log.e("Payroc", "No card required")
        }
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