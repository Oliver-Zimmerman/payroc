package com.payroc.transactionprocessor.ui.receipts

import com.payroc.transactionprocessor.database.entities.Receipt

interface ReceiptRepository {
    suspend fun insertReceipt(receipt: Receipt)
    suspend fun getReceipts(): List<Receipt>
}