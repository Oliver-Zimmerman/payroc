package com.payroc.transactionprocessor.ui.home

import androidx.lifecycle.ViewModel
import com.payroc.transaction.PayrocClient
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class HomeViewModel: ViewModel() {

    //ToDo API key should be in a properties file that needs to be added each clone
    private val payrocClient: PayrocClient = PayrocClient("5140001", "14c7974ba5b38d0dbcb7a0d8bdf3959e3b316a812ab3815db17878719f50c0ce8da30b7c1690ba5698ce2ae3a714047052b389b3d7401dbb457fcc7abdac3ffd")

    fun payClicked(amount: Double) {
        payrocClient.startTransaction(amount)
    }
}