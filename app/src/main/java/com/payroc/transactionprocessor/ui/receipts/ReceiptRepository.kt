package com.payroc.transactionprocessor.ui.receipts

import androidx.lifecycle.LiveData
import com.payroc.transactionprocessor.database.entities.Receipt

interface ReceiptRepository {
    suspend fun insertReceipt(receipt: Receipt)
    fun getReceipts(): LiveData<List<Receipt>>
}