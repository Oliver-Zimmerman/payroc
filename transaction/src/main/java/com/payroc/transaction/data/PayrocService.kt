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

    fun authenticate(): AuthenticateResponse {
        return api.authenticate()
    }

    fun createTransaction(transactionRequest: TransactionRequest): TransactionResponse {
        return api.createTransaction(transactionRequest)
    }

    interface PayrocService {
        @Headers(
            "Authorization: <param>"
        )
        @GET("account/authenticate")
        fun authenticate(): AuthenticateResponse

        @Headers(
            "Content-Type: application/json",
            "Authorization: <param>"
        )
        @POST("transaction/payments")
        fun createTransaction(@Body transactionRequest: TransactionRequest): TransactionResponse
    }
}