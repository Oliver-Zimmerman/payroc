package com.payroc.transactionprocessor.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject
import com.payroc.transaction.data.model.response.*

/**
 * A basic class representing an entity that is a row in a one-column database table.
 *
 * @ Entity - You must annotate the class as an entity and supply a table name if not class name.
 * @ PrimaryKey - You must identify the primary key.
 * @ ColumnInfo - You must supply the column name if it is different from the variable name.
 *
 * See the documentation for the full rich set of annotations.
 * https://developer.android.com/topic/libraries/architecture/room.html
 */
@Entity(tableName = "receipt_table")
data class Receipt(
    @PrimaryKey(autoGenerate = true) var uniqueReference: Int? = null,
    var receipts: String,
)

