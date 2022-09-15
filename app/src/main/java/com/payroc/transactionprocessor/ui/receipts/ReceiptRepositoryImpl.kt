package com.payroc.transactionprocessor.ui.receipts

import com.payroc.transactionprocessor.database.dao.ReceiptDao
import com.payroc.transactionprocessor.database.entities.Receipt
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(private val receiptDao: ReceiptDao) :
    ReceiptRepository {
    override fun insertReceipt(receipt: Receipt) {
        receiptDao.insertReceipt(receipt)
    }

    override fun getReceipts(): List<Receipt> {
        return receiptDao.getAllReceipts()
    }
}