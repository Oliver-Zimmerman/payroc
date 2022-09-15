package com.payroc.transactionprocessor.hilt

import com.payroc.transactionprocessor.ui.receipts.ReceiptRepository
import com.payroc.transactionprocessor.ui.receipts.ReceiptRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
//Repositories will live same as the activity that requires them
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providesReceiptRepository(impl: ReceiptRepositoryImpl): ReceiptRepository
}