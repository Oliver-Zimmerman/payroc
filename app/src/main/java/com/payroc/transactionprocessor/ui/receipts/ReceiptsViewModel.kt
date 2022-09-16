package com.payroc.transactionprocessor.ui.receipts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payroc.transaction.data.model.response.Receipts
import com.payroc.transactionprocessor.database.entities.Receipt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptsViewModel @Inject constructor(private val receiptRepository: ReceiptRepository) : ViewModel() {


   // internal val allReceipts : LiveData<List<Receipt>> = receiptRepository.getReceipts()

    /**
     * Launching a new coroutine to retrieve the data in a non-blocking way
     */
    suspend fun getReceipts(): List<Receipt>{
        var receipts: List<Receipt> = listOf()
        viewModelScope.launch {
            receipts = receiptRepository.getReceipts()
        }
        return receipts
    }

}