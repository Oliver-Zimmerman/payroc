package com.payroc.transaction.data

import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.AuthenticateResponse
import com.payroc.transaction.data.model.response.TransactionResponse
import retrofit2.Response

class PayrocRepository(private val webService: PayrocWebService = PayrocWebService()) {

    suspend fun authenticate(apiKey: String): Response<AuthenticateResponse> {
        return webService.authenticate(apiKey)
    }

    suspend fun createTransaction(
        token: String,
        transactionRequest: TransactionRequest,
    ): Response<TransactionResponse> {
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