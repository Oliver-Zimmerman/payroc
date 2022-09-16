package com.payroc.transactionprocessor.ui.receipts

import androidx.lifecycle.LiveData
import com.payroc.transactionprocessor.database.dao.ReceiptDao
import com.payroc.transactionprocessor.database.entities.Receipt
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(private val receiptDao: ReceiptDao) :
    ReceiptRepository {
    override suspend fun insertReceipt(receipt: Receipt) {
        receiptDao.insertReceipt(receipt)
    }

    override fun getReceipts(): LiveData<List<Receipt>> {
        return receiptDao.getAllReceipts()
    }
}