package com.payroc.transaction.utility

internal fun createOrderID(): String {
    val allowedChars = ('A'..'Z') + ('0'..'9')
    return (1..6)
        .map { allowedChars.random() }
        .joinToString("")
}