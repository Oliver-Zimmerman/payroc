package com.payroc.transaction.data.model.response

import com.google.gson.annotations.SerializedName

data class AuthenticateResponse(
    @SerializedName("audience") var audience: String? = null,
    @SerializedName("boundTo") var boundTo: String? = null,
    @SerializedName("tokenType") var tokenType: String? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("expiresIn") var expiresIn: Int? = null,
    @SerializedName("enableReceipts") var enableReceipts: Boolean? = null,
    @SerializedName("enableHypermedia") var enableHypermedia: Boolean? = null,
    @SerializedName("roles") var roles: ArrayList<String> = arrayListOf(),
    @SerializedName("allowedTerminals") var allowedTerminals: ArrayList<String> = arrayListOf(),
)