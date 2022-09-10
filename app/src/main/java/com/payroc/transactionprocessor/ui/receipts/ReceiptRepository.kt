package com.payroc.transactionprocessor.ui.receipts

import com.payroc.transactionprocessor.database.entities.Receipt

interface ReceiptRepository {
    fun insertReceipt(receipt: Receipt)
    fun getReceipts(): List<Receipt>
}