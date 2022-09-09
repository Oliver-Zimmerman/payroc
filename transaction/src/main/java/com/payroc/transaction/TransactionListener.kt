package com.payroc.transaction

import com.payroc.transaction.data.model.response.TransactionResponse

interface TransactionListener {

    /** Fires once a transaction state has changed
     * @param state the most recent state to emit
     * @see [TransactionState]
     */
    fun updateState(state: TransactionState)

    /** Fires once a receipt has been received as a result of a successful transaction
     * @param transactionResponse the response provided by the transaction
     * @see [TransactionResponse]
     */
    fun receiptReceived(transactionResponse: TransactionResponse)
}