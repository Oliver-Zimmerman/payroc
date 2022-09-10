package com.payroc.transactionprocessor.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.payroc.transactionprocessor.database.dao.ReceiptDao
import com.payroc.transactionprocessor.database.entities.Receipt

@Database(entities = [Receipt::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}