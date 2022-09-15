package com.payroc.transactionprocessor.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.payroc.transactionprocessor.database.entities.Receipt

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipt_table")
    suspend fun getAllReceipts(): List<Receipt>

    @Insert
    suspend fun insertReceipt(receipt: Receipt)
}