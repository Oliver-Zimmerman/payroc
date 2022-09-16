package com.payroc.transactionprocessor.ui.pay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.google.gson.Gson
import com.payroc.transaction.TransactionState
import com.payroc.transaction.data.model.Card
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.databinding.FragmentPayBinding
import com.payroc.transactionprocessor.utility.convertXMLToDataClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class PayFragment : Fragment(R.layout.fragment_pay), NumberKeyboardListener {

    private lateinit var binding: FragmentPayBinding

    private lateinit var payViewModel: PayViewModel

    private var amountText: String = ""
    private var amount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        payViewModel = ViewModelProvider(this)[PayViewModel::class.java]
        binding.paymentEntryDialpad.setListener(this)

        subscribeToObservables()

        binding.payButtonId.setOnClickListener {
            payViewModel.payClicked(amount)
        }
    }

    private fun subscribeToObservables() {
        // State Observable
        payViewModel.getState().observe(viewLifecycleOwner) { state ->
            Timber.i("State :: $state")
            binding.stateTextView.text = state.toString()
            if (state == TransactionState.CARD_REQUEST) {
                //ToDo pop up dialog to provide / tap card
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(2000)
                    payViewModel.provideCard(getCard())
                }
            }
        }

        // Client Message Observable
        payViewModel.getClientMessage().observe(viewLifecycleOwner) { message ->
            Timber.i("Message :: $message")
            binding.clientMessageTextView.text = message
        }

        // Client Receipt Observable
        payViewModel.getReceipt().observe(viewLifecycleOwner)
        { receiptsArray ->
            Timber.i("Receipt Received :: $receiptsArray")
            receiptsArray?.let { receipts ->
                receipts.forEach { receipt ->
                    when (receipt.header) {
                        "MERCHANT COPY" -> {
                            // Should be stored internally for POS reference
                            //ToDo inject this
                            val gson = Gson()
                            val receiptJsonString = gson.toJson(receipt)
                            val receiptEntity = Receipt(receipts = receiptJsonString)
                            lifecycleScope.launch(Dispatchers.IO) {
                                payViewModel.insertReceipt(receiptEntity)
                            }
                        }
                        "CARDHOLDER COPY" -> {
                            //ToDo print card holder copy for customer
                        }
                    }
                }
            }
        }
    }

    private fun getCard(): Card {
        val cards = convertXMLToDataClass(requireContext())
        // Pick a random card from the list of available cards.
      //  return cards.card[Random.nextInt(cards.card.size)]
        return cards.card[cards.card.size-1]
    }

    // Comma implementation
    override fun onLeftAuxButtonClicked() {
        if (!hasComma(amountText)) {
            amountText = if (amountText.isEmpty()) "0," else "$amountText,"
            showAmount(amountText)
        }
    }

    // Number entry implementation
    override fun onNumberClicked(number: Int) {
        if (amountText.isEmpty() && number == 0) {
            return
        }
        updateAmount(amountText + number)
    }

    // Delete implementation
    override fun onRightAuxButtonClicked() {
        if (amountText.isEmpty()) {
            return
        }
        var newAmountText: String
        if (amountText.length <= 1) {
            newAmountText = ""
        } else {
            newAmountText = amountText.substring(0, amountText.length - 1)
            if (newAmountText[newAmountText.length - 1] == ',') {
                newAmountText = newAmountText.substring(0, newAmountText.length - 1)
            }
            if ("0" == newAmountText) {
                newAmountText = ""
            }
        }
        updateAmount(newAmountText)
    }

    /**
     * Update new entered amount if it is valid.
     */
    private fun updateAmount(newAmountText: String) {
        val newAmount = if (newAmountText.isEmpty()) 0.0 else java.lang.Double.parseDouble(newAmountText.replace(",".toRegex(), "."))
        if (newAmount in 0.0..MAX_ALLOWED_AMOUNT
            && getNumDecimals(newAmountText) <= MAX_ALLOWED_DECIMALS) {
            amountText = newAmountText
            amount = newAmount
            showAmount(amountText)
        }
    }


    /**
     * Shows amount in UI.
     */
    private fun showAmount(amount: String) {
        binding.amountTextView.text = "$" + amount.ifEmpty { "0" }
    }

    /**
     * Checks whether the string has a comma.
     */
    private fun hasComma(text: String): Boolean {
        for (element in text) {
            if (element == ',') {
                return true
            }
        }
        return false
    }

    /**
     * Calculate the number of decimals of the string.
     */
    private fun getNumDecimals(num: String): Int {
        return if (!hasComma(num)) {
            0
        } else num.substring(num.indexOf(',') + 1, num.length).length
    }

    companion object {
        private const val MAX_ALLOWED_AMOUNT = 9999.99
        private const val MAX_ALLOWED_DECIMALS = 2
    }
}