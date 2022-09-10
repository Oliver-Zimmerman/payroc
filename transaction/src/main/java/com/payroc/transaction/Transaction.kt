package com.payroc.transaction

import android.util.Log
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.payroc.transaction.data.Card
import com.payroc.transaction.data.Cards
import com.payroc.transaction.data.PayrocRepository
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.OrderBreakdown
import com.payroc.transaction.data.model.request.CustomerAccount
import com.payroc.transaction.data.model.request.Device
import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.TransactionResponse
import com.payroc.transaction.utility.createOrderID
import com.taptrack.kotlin_tlv.TLV
import java.io.File
import kotlin.random.Random

class Transaction(
    private val amount: Double,
    private val terminal: String,
    private val apiKey: String,
    private val transactionListener: TransactionListener,
) {

    private val _repository: PayrocRepository = PayrocRepository.getInstance()

    private suspend fun authenticate(apiKey: String): String {
        val authResponse = _repository.authenticate(apiKey)
        return authResponse.token
    }

    suspend fun provideCard(): TransactionResponse {
        transactionListener.updateState(TransactionState.READING)
        return transactionRequest(getCard())
    }

    private suspend fun transactionRequest(card: Card): TransactionResponse {
        if (card.payloadType == "EMV") {
            val token = authenticate(apiKey)
            val transactionRequest = TransactionRequest(
                terminal = terminal,
                order = Order(orderId = createOrderID(),
                    totalAmount = amount,
                    orderBreakdown = OrderBreakdown(subtotalAmount = amount)),
                customerAccount = CustomerAccount(device = Device(dataKsn = card.dataKsn),
                    generateTlv(),
                    card.payloadType)
            )
            transactionListener.updateState(TransactionState.PROCESSING)
            return _repository.createTransaction(token, transactionRequest)
        } else {
            Log.e("Error", "MAG_STRIPE not yet implemented")
            //ToDo change this to be MagSTRIPE implementation
            val token = authenticate(apiKey)
            val transactionRequest = TransactionRequest(
                terminal = terminal,
                order = Order(orderId = createOrderID(),
                    totalAmount = amount,
                    orderBreakdown = OrderBreakdown(subtotalAmount = amount)),
                customerAccount = CustomerAccount(device = Device(dataKsn = card.dataKsn),
                    generateTlv(),
                    card.payloadType)
            )
            transactionListener.updateState(TransactionState.PROCESSING)
            return _repository.createTransaction(token, transactionRequest)
        }
    }

    private fun getCard(): Card {
        val cards = convertXMLToDataClass()
        return cards.cards[Random.nextInt(cards.cards.size)]
    }

    // Move to utility?
    private fun convertXMLToDataClass(): Cards {
        val file = File("card_data.xml")
        val xmlMapper = XmlMapper()
        return xmlMapper.readValue(file, Cards::class.java)
    }

    // Move to utility?
    private fun generateTlv(): String {
        return TLV(1, byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00)).toString()
    }

}