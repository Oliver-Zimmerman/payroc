package com.payroc.transaction.utility

import com.payroc.transaction.data.model.Card
import java.lang.StringBuilder
import java.math.BigInteger

/**
 * Utility helper method to generate a 6 character Order ID containing A-Z (uppercase) and 0-9
 */
internal fun createOrderID(): String {
    val allowedChars = ('A'..'Z') + ('0'..'9')
    return (1..6)
        .map { allowedChars.random() }
        .joinToString("")
}

/**
 * Utility helper method to convert a hex String to binary String
 */
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

/**
 * Utility helper method to create a TLV String with a provided [Card]
 *
 * A TLV String is generate with the following steps:
 * 1. Convert Hex String ([Card.tags] value field) to Binary String
 * 2. Get the length of the converted Binary String
 * 3. Divide the calculated length by 8, which is the number of bytes
 * 4. Use the number of bytes and create an [Integer.toHexString]
 * 5. Append the tag, the new hex string, and value together. Adding a 0 to the start of the hex
 *    value if its length is smaller than 2
 * 6. Loop through all tags and append them all together with this process.
 *
 */
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