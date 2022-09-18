package com.payroc.transaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.Error
import com.payroc.transaction.data.model.response.Receipts


/**
 * The PayrocClient class that can be used to create [Transaction] instances as well as emit Client
 * events such as [Error], [TransactionState] and [Receipts] changes.
 *
 * @property terminal the terminal assigned to this application
 * @property apiKey the API key to be used for authenticating calls
 */
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

    /**
     * Creates a transaction with the specified amount - checking if the amount is valid first
     * A valid amount is a double value greater than 0.00.
     *
     * In the case of a valid amount, an instance of [Transaction] is created and [TransactionState]
     * will update to [TransactionState.CARD_REQUEST] - implying a card should now be provided to
     * continue.
     *
     * If the amount is invalid, [TransactionState] will update to [TransactionState.ERROR]
     *
     * @param amount the transaction amount to be processed.
     * @see [Transaction]
     */
    fun startTransaction(amount: Double) {
        if (amount <= 0.0) {
            stateLiveData.postValue(TransactionState.ERROR)
            clientMessageLiveData.postValue("Invalid transaction amount")
        } else {
            transaction = Transaction(amount, terminal, apiKey, this)
            stateLiveData.postValue(TransactionState.CARD_REQUEST)
            clientMessageLiveData.postValue("Please provide card")
        }
    }

    /**
     * Cancels an ongoing transaction.
     *
     * A Transaction can only be cancelled when there is an ongoing [Transaction] and
     * [TransactionState] is [TransactionState.CARD_REQUEST] otherwise an error message will be
     * provided via the [getClientMessageResponse] LiveData
     *
     * @see [TransactionState]
     */
    fun cancelTransaction() {
        if (stateLiveData.value == TransactionState.CARD_REQUEST) {
            stateLiveData.postValue(TransactionState.IDLE)
            clientMessageLiveData.postValue("Transaction cancelled")
        } else {
            transaction?.let {
                clientMessageLiveData.postValue("Transaction in invalid state. Unable to cancel")
            } ?: run {
                clientMessageLiveData.postValue("No transaction to cancel")
            }
        }

    }

    /**
     * Reads the data off of a provided [Card] in order to process an ongoing [Transaction].
     * A card will only be processed if there is an ongoing [Transaction] and [TransactionState] is
     * [TransactionState.CARD_REQUEST], otherwise a error will be provided.
     *
     * If there is an ongoing [Transaction] and [TransactionState] is correct, the [Transaction.provideCard]
     * method will be called, which begins the authentication and transaction handling process.
     *
     * @param card the card that will be used to debit the amount specified when creating a [Transaction]
     * @see [Transaction.provideCard]
     */
    suspend fun readCardData(card: Card) {
        if (stateLiveData.value == TransactionState.CARD_REQUEST) {
            stateLiveData.postValue(TransactionState.STARTED)
            transaction?.let {
                transaction?.provideCard(card)
            } ?: run {
                stateLiveData.postValue(TransactionState.ERROR)
                Log.e(TAG, "No ongoing transaction")
                clientMessageLiveData.postValue("No ongoing transaction")
            }
        } else {
            Log.e(TAG, "No card required")
            clientMessageLiveData.postValue("No card required")
        }
    }

    override fun updateState(state: TransactionState) {
        Log.i(TAG, "State Received :: $state")
        stateLiveData.postValue(state)
        // clear current transaction
        if (state == TransactionState.IDLE || state == TransactionState.COMPLETE || state == TransactionState.ERROR) {
            transaction = null
        }
    }

    override fun clientMessageReceived(message: String) {
        Log.i(TAG, "Message Received :: $message")
        clientMessageLiveData.postValue(message)

    }

    override fun clientErrorReceived(error: Error) {
        Log.i(TAG, "Error Received :: $error")
        clientErrorLiveData.postValue(error)
    }

    override fun receiptReceived(receipts: ArrayList<Receipts>) {
        Log.i(TAG, "Receipts Received :: $receipts")
        clientReceiptLiveData.postValue(receipts)
    }
}

