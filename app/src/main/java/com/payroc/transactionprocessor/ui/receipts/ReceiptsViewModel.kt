package com.payroc.transactionprocessor.ui.receipts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.payroc.transactionprocessor.database.entities.Receipt
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReceiptsViewModel @Inject constructor(receiptRepository: ReceiptRepository) :
    ViewModel() {

    internal val allReceipts: LiveData<List<Receipt>> = receiptRepository.getReceipts()

}