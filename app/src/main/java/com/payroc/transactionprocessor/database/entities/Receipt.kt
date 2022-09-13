package com.payroc.transactionprocessor.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.response.AdditionalDataFields
import com.payroc.transaction.data.model.response.EmvTags
import com.payroc.transaction.data.model.response.SecurityCheck
import com.payroc.transaction.data.model.response.TransactionResult

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
    @PrimaryKey var uniqueReference: String,
    var terminal: String,
    //ToDo can't use data object as types in Room - Retrieve the whole thing as JSON String. or more likely use a converter to convert params into JSONString
    /*var order: Order,
    var customerAccount: CustomerAccount,
    var securityCheck: SecurityCheck,
    var transactionResult: TransactionResult,
    var additionalDataFields: ArrayList<AdditionalDataFields> = arrayListOf(),
    var emvTags: ArrayList<EmvTags> = arrayListOf(),*/
)

