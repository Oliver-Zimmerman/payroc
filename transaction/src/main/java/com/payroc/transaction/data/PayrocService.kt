package com.payroc.transaction.data

import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.AuthenticateResponse
import com.payroc.transaction.data.model.response.TransactionResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class PayrocWebService {
    private var api: PayrocService

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://testpayments.worldnettps.com/merchant/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(PayrocService::class.java)
    }

    suspend fun authenticate(apiKey: String): Response<AuthenticateResponse> {
        return api.authenticate("Basic $apiKey")
    }

    suspend fun createTransaction(
        token: String,
        transactionRequest: TransactionRequest,
    ): Response<TransactionResponse> {
        return api.createTransaction("Bearer $token", transactionRequest)
    }

    interface PayrocService {
        @Headers(
            "Content-Type: application/json",
        )
        @GET("account/authenticate")
        suspend fun authenticate(@Header("Authorization") apiKey: String): Response<AuthenticateResponse>

        @Headers(
            "Content-Type: application/json",
        )
        @POST("transaction/payments")
        suspend fun createTransaction(
            @Header("Authorization") token: String,
            @Body transactionRequest: TransactionRequest,
        ): Response<TransactionResponse>
    }
}