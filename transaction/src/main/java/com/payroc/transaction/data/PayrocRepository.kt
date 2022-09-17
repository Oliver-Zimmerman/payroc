package com.payroc.transaction.data

import com.google.gson.JsonObject
import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.AuthenticateResponse
import com.payroc.transaction.data.model.response.TransactionResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call

// The class that gets the data from the server
class PayrocRepository(private val webService: PayrocWebService = PayrocWebService()) {

    suspend fun authenticate(apiKey: String): ApiResponse<AuthenticateResponse> {
         return webService.authenticate(apiKey)
    }

     fun createTransaction(token: String, transactionRequest: TransactionRequest): Call<TransactionResponse> {
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