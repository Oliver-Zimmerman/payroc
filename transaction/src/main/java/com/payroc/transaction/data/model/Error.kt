package com.payroc.transaction.data.model

data class Error(
    val debugIdentifier: String,
    val details: ArrayList<ErrorDetail>,
)

data class ErrorDetail(
    val errorCode: String,
    val errorMessage: String,
    val about: String?,
    val source: ErrorSource?,
)

data class ErrorSource(
    val location: String,
    val resource: String?,
    val property: String?,
    val value: String?,
    val expected: String?,
)