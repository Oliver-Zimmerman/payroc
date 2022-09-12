package com.payroc.transaction

import android.util.Log
import android.util.Log.i
import com.payneteasy.tlv.BerTag
import com.payneteasy.tlv.BerTlvBuilder
import com.payneteasy.tlv.HexUtil
import com.payroc.transaction.data.Card
import com.payroc.transaction.data.PayrocRepository
import com.payroc.transaction.data.model.Order
import com.payroc.transaction.data.model.OrderBreakdown
import com.payroc.transaction.data.model.request.CustomerAccount
import com.payroc.transaction.data.model.request.Device
import com.payroc.transaction.data.model.request.TransactionRequest
import com.payroc.transaction.data.model.response.TransactionResponse
import com.payroc.transaction.utility.createOrderID
import com.taptrack.kotlin_tlv.TLV
import com.taptrack.kotlin_tlv.writeOutTLVBinary


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

    suspend fun provideCard(card: Card): TransactionResponse {
        val token = authenticate(apiKey)
        transactionListener.updateState(TransactionState.READING)
        return transactionRequest(token, card)
    }

    private suspend fun transactionRequest(token: String, card: Card): TransactionResponse {
        if (card.payloadType == "EMV") {
            val transactionRequest = TransactionRequest(
                terminal = terminal,
                order = Order(orderId = createOrderID(),
                    totalAmount = amount,
                    orderBreakdown = OrderBreakdown(subtotalAmount = amount)),
                customerAccount = CustomerAccount(device = Device(dataKsn = card.dataKsn),
                    generateTlv(card),
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
                    generateTlv(card),
                    card.payloadType)
            )
            transactionListener.updateState(TransactionState.PROCESSING)
            return _repository.createTransaction(token, transactionRequest)
        }
    }

    // Move to utility?
    private fun generateTlv(card: Card): String {

        val tlvs : MutableList<TLV> = mutableListOf()
        card.tags.forEach {
            i("Adding", "${Integer.parseInt(it.key)} and ${it.value.toByteArray()}")
            tlvs.add(TLV(), it.value.toByteArray()))
        }

        val byteArray: ByteArray = tlvs.toList().writeOutTLVBinary()

        val bytes = BerTlvBuilder()
        card.tags.forEach {
            i("Adding", "${Integer.decode(it.key)} and ${it.value}")
            bytes.addHex(BerTag(), it.value)
           // bytes.addText(BerTag(it.key!!.toByteArray()), it.value)
           // bytes.addHex(BerTag(it.key!!.encodeToByteArray()), it.value)
          //  bytes.add
        }
        val tlv = bytes.buildTlvs()
        //return tlv
        return "DF79033535335F280208409F6E2008400000303000000000000000000000000000000000000000000000000000009F120A4D6173746572636172649F110101500A4D4153544552434152445F24032512315F25032103018C279F02069F03069F1A0295055F2A029A039C019F37049F35019F45029F4C089F34039F21039F7C148D0C910A8A0295059F37049F4C088E0C00000000000000005E031F039F0702FF009F0D05B4508080009F0E0500000000009F0F05B4708080009F160A393432353832313733339F1C0831323334353637389F1E0831383530303130389F420208409F4E0E57616C6D6172744E6577596F726BDF780518500108689F4104000005019F10120111A04009220400000000000000000000FF9F090200028407A00000000410105F3401015F2D02656E9F370410A4E2809C01009F21031529379B0200009A032209055F2A020840950500000080019F3501229F1A0208409F3303E008089F3901079F2701809F34031F03029F3602013F820219809F0607A00000000410104F07A00000000410109F260881BF9F3FEF7DD0B79F03060000000000009F02060000000005009F40056000F0B0019F530146C22043D764D2C035C0CEF759BAB058E868BCAC1A11F71278E611DFDF2D99F87538B5C00AF87654321100000000795F200D4E6F7420417661696C61626C65DF78051850010868"
    }
}