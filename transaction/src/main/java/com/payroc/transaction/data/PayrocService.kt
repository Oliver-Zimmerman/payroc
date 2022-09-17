package com.payroc.transaction.data

import com.google.gson.JsonObject
import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.AuthenticateResponse
import com.payroc.transaction.data.model.response.TransactionResponse
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
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
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(PayrocService::class.java)
    }

    suspend fun authenticate(apiKey: String): ApiResponse<AuthenticateResponse> {
        return api.authenticate("Basic $apiKey")
    }

     fun createTransaction(
        token: String,
        transactionRequest: TransactionRequest,
    ): Call<TransactionResponse> {
        return api.createTransaction("Bearer $token", transactionRequest)
    }

    interface PayrocService {
        @Headers(
            "Content-Type: application/json",
        )
        @GET("account/authenticate")
        suspend fun authenticate(@Header("Authorization") apiKey: String): ApiResponse<AuthenticateResponse>

        @Headers(
            "Content-Type: application/json",
        )
        @POST("transaction/payments")
         fun createTransaction(
            @Header("Authorization") token: String,
            @Body transactionRequest: TransactionRequest,
        ): Call<TransactionResponse>
    }
}