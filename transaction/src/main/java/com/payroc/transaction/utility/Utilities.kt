package com.payroc.transaction.utility

import com.payroc.transaction.data.model.Card
import java.lang.StringBuilder
import java.math.BigInteger

internal fun createOrderID(): String {
    val allowedChars = ('A'..'Z') + ('0'..'9')
    return (1..6)
        .map { allowedChars.random() }
        .joinToString("")
}

internal fun hexToBinary(hex: String): String {
    val len = hex.length * 4
    var bin: String = BigInteger(hex, 16).toString(2)

    //left pad the string result with 0s if converting to BigInteger removes them.
    if (bin.length < len) {
        val diff = len - bin.length
        var pad = ""
        for (i in 0 until diff) {
            pad += "0"
        }
        bin = pad + bin
    }
    return bin
}

// Move to utility?
internal fun generateTlv(card: Card): String {

    val stringBuilder = StringBuilder()
    // Step 1, convert Hex value to binary string
    // Step 2, get the length of the binary string
    // Step 3, divide binary string by 8 which is the bytes
    card.tags?.forEach { tag ->
        val bin = hexToBinary(tag.value)
        val hex = Integer.toHexString(bin.length / 8)
        stringBuilder.append(tag.key.uppercase())
        if (hex.length < 2) {
            stringBuilder.append("0" + hex.uppercase())
        } else {
            stringBuilder.append(hex.uppercase())
        }
        stringBuilder.append(tag.value.uppercase())
    }

    return stringBuilder.toString()
}