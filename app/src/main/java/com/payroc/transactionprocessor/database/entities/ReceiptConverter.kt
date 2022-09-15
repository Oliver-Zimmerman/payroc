package com.payroc.transactionprocessor.database.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class ReceiptConverter {
    @TypeConverter
    fun toReceipt(receipt: String): JsonObject {
        val type = object : TypeToken<JsonObject>() {}.type
        return Gson().fromJson(receipt, type)
    }

    @TypeConverter
    fun toReceiptJson(receipt: JsonObject): String {
        val type = object : TypeToken<JsonObject>() {}.type
        return Gson().toJson(receipt, type)
    }
}