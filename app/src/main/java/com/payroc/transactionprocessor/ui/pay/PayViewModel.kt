package com.payroc.transactionprocessor.ui.pay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroc.transaction.PayrocClient
import com.payroc.transaction.data.model.Card
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.ui.receipts.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayViewModel  @Inject constructor(private val receiptRepository: ReceiptRepository) : ViewModel() {

    //ToDo API key should be in a properties file that needs to be added each clone
    private val payrocClient: PayrocClient = PayrocClient("5140001",
        "14c7974ba5b38d0dbcb7a0d8bdf3959e3b316a812ab3815db17878719f50c0ce8da30b7c1690ba5698ce2ae3a714047052b389b3d7401dbb457fcc7abdac3ffd")

    fun payClicked(amount: Double) {
        payrocClient.startTransaction(amount)
    }

    suspend fun provideCard(card: Card) {
        payrocClient.readCardData(card)
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    suspend fun insertReceipt(receipt: Receipt) = viewModelScope.launch {
        receiptRepository.insertReceipt(receipt)
    }

    fun getState() = payrocClient.getStateResponse()

    fun getClientMessage() = payrocClient.getClientMessageResponse()

    fun getReceipt() = payrocClient.getClientReceiptResponse()
}