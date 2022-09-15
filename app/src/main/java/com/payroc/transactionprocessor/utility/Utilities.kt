package com.payroc.transactionprocessor.utility

import android.content.Context
import com.google.gson.Gson
import com.payroc.transaction.data.model.CardList
import com.payroc.transaction.data.model.Cards
import com.payroc.transactionprocessor.R
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import timber.log.Timber

internal fun convertXMLToDataClass(context: Context): Cards {
    Timber.i("filepath :: ${context.filesDir.path}")
    val file = context.resources.openRawResource(R.raw.card_data)
    val xmlToJson = XmlToJson.Builder(file, null).build()
    file.close()

    val cardsJsonObject = xmlToJson.toString()

    val gson = Gson()
    return gson.fromJson(cardsJsonObject, CardList::class.java).cards
}