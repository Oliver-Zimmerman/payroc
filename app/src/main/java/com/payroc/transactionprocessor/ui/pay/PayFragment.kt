package com.payroc.transactionprocessor.ui.pay

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.payroc.transaction.TransactionState
import com.payroc.transaction.data.model.Card
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.databinding.FragmentPayBinding
import com.payroc.transactionprocessor.utility.convertXMLToDataClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class PayFragment : Fragment(R.layout.fragment_pay),
    NumberKeyboardListener {

    private lateinit var navBar: BottomNavigationView

    private lateinit var binding: FragmentPayBinding

    private lateinit var payViewModel: PayViewModel

    @Inject
    lateinit var gson: Gson

    private var amountText: String = ""
    private var amount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        payViewModel = ViewModelProvider(this)[PayViewModel::class.java]
        binding.paymentEntryDialpad.setListener(this)

        navBar = requireActivity().findViewById(R.id.bottomNavigationView)

        subscribeToObservables()

        binding.payButtonId.setOnClickListener {
            payViewModel.payClicked(amount)
        }
    }

    override fun onDestroyView() {
        // Reset amount values
        resetAmountValues()
        super.onDestroyView()
    }

    private fun subscribeToObservables() {
        // State Observable
        payViewModel.getState().observe(viewLifecycleOwner) { state ->
            Timber.i("State :: $state")
            binding.stateTextView.text = state.toString()

            when (state) {
                TransactionState.STARTED -> {
                    binding.linearProgressIndicator.visibility = View.VISIBLE
                }
                TransactionState.COMPLETE -> {
                    binding.linearProgressIndicator.visibility = View.INVISIBLE
                    // Reset amount values
                    resetAmountValues()
                    binding.amountTextView.text = "$0"
                }
                TransactionState.ERROR -> {
                    binding.linearProgressIndicator.visibility = View.INVISIBLE
                }
                else -> {
                    if (state == TransactionState.CARD_REQUEST) {
                        cardConfirmationAlert()
                    }
                }
            }
        }

        // Client Message Observable
        payViewModel.getClientMessage().observe(viewLifecycleOwner) { message ->
            Timber.i("Message :: $message")
            binding.clientMessageTextView.text = message
        }

        // Client Error Observable
        payViewModel.getClientError().observe(viewLifecycleOwner) { error ->
            Timber.i("Error :: $error")
            Toast.makeText(
                context,
                error.details[0].errorMessage,
                Toast.LENGTH_LONG
            ).show()
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

        payViewModel.allReceipts.observe(viewLifecycleOwner) { receiptList ->
            navBar.getOrCreateBadge(R.id.menu_item_receipts).apply {
                number = receiptList.size
                isVisible = true
            }
        }
    }

    private fun cardConfirmationAlert() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder)
        {
            setTitle("Provide Card")
            setMessage("Tap to proceed")
            setIcon(R.drawable.ic_home)
            setPositiveButton("Tap") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    payViewModel.provideCard(getCard())

                }
            }
            setNegativeButton("Cancel") { _, _ ->
                payViewModel.cancelTransaction()
            }
            show()
        }
    }

    private fun resetAmountValues() {
        amountText = ""
        amount = 0.0
    }

    private fun getCard(): Card {
        val cards = convertXMLToDataClass(requireContext())
        // Pick a random card from the list of available cards.
        return cards.card[Random.nextInt(cards.card.size)]
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
        val newAmount =
            if (newAmountText.isEmpty()) 0.0 else java.lang.Double.parseDouble(newAmountText.replace(
                ",".toRegex(),
                "."))
        if (newAmount in 0.0..MAX_ALLOWED_AMOUNT
            && getNumDecimals(newAmountText) <= MAX_ALLOWED_DECIMALS
        ) {
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