package com.payroc.transaction.data

import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.AuthenticateResponse
import com.payroc.transaction.data.model.response.TransactionResponse

// The class that gets the data from the server
class PayrocRepository(private val webService: PayrocWebService = PayrocWebService()) {

    suspend fun authenticate(apiKey: String): AuthenticateResponse {
        return webService.authenticate(apiKey)
    }

    suspend fun createTransaction(token: String, transactionRequest: TransactionRequest): TransactionResponse {
        return webService.createTransaction(token, transactionRequest)
    }

    // Easy singleton implementation
    companion object {
        @Volatile
        private var instance: PayrocRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: PayrocRepository().also { instance = it }
        }
    }
}