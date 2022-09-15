package com.payroc.transactionprocessor.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.payroc.transactionprocessor.database.dao.ReceiptDao
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.database.entities.ReceiptConverter

@Database(entities = [Receipt::class], version = 1)
@TypeConverters(ReceiptConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}