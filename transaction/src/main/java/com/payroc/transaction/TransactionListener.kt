package com.payroc.transaction

import com.payroc.transaction.data.model.response.Receipts
import com.payroc.transaction.data.model.Error

interface TransactionListener {

    /** Fires once a transaction state has changed
     * @param state the most recent state to emit
     * @see [TransactionState]
     */
    fun updateState(state: TransactionState)

    /** Fires once a message is being emitted from the client
     * @param message the message being emitted from the client
     */
    fun clientMessageReceived(message: String)

    /** Fires when an error is being emitted from the client
     * @param error the message being emitted from the client
     * @see [Error]
     */
    fun clientErrorReceived(error: Error)

    /** Fires once a receipt has been received as a result of a successful transaction
     * @param receipts the receipts section of the response provided by the transaction
     * @see [Receipts]
     */
    fun receiptReceived(receipts: ArrayList<Receipts>)
}