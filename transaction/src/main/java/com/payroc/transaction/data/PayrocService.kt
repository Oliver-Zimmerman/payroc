package com.payroc.transaction.data

import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.AuthenticateResponse
import com.payroc.transaction.data.model.response.TransactionResponse
import retrofit2.Retrofit
import retrofit2.http.*
import retrofit2.converter.gson.GsonConverterFactory

class PayrocWebService {
    private var api: PayrocService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://testpayments.worldnettps.com/merchant/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(PayrocService::class.java)
    }

    suspend fun authenticate(apiKey: String): AuthenticateResponse {
        return api.authenticate("Basic $apiKey")
    }

    suspend fun createTransaction(token: String, transactionRequest: TransactionRequest): TransactionResponse {
        return api.createTransaction("Bearer $token", transactionRequest)
    }

    interface PayrocService {
        @Headers(
            "Content-Type: application/json",
        )
        @GET("account/authenticate")
       suspend fun authenticate(@Header("Authorization") apiKey: String): AuthenticateResponse

        @Headers(
            "Content-Type: application/json",
        )
        @POST("transaction/payments")
       suspend fun createTransaction(@Header("Authorization") token: String, @Body transactionRequest: TransactionRequest): TransactionResponse
    }
}