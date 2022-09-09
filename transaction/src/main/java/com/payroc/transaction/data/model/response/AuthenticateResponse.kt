package com.payroc.transaction.data.model.response

import com.google.gson.annotations.SerializedName

data class AuthenticateResponse(
    @SerializedName("audience") var audience: String,
    @SerializedName("boundTo") var boundTo: String,
    @SerializedName("tokenType") var tokenType: String,
    @SerializedName("token") var token: String,
    @SerializedName("expiresIn") var expiresIn: Int,
    @SerializedName("enableReceipts") var enableReceipts: Boolean,
    @SerializedName("enableHypermedia") var enableHypermedia: Boolean,
    @SerializedName("roles") var roles: ArrayList<String> = arrayListOf(),
    @SerializedName("allowedTerminals") var allowedTerminals: ArrayList<String> = arrayListOf(),
)