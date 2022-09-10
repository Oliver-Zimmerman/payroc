package com.payroc.transactionprocessor.hilt

import android.content.Context
import androidx.room.Room
import com.payroc.transactionprocessor.database.AppDatabase
import com.payroc.transactionprocessor.database.dao.ReceiptDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Dependencies provided via this module will stay alive as long as application is running.
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideReceiptDao(appDatabase: AppDatabase): ReceiptDao {
        return appDatabase.receiptDao()
    }

    @Provides
    @Singleton // Initialize only once
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Receipts"
        ).build()
    }
}