package com.payroc.transactionprocessor.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.payroc.transaction.TransactionState
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.CardList
import com.payroc.transaction.data.model.Cards
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //private lateinit var homeViewModel: HomeViewModel
    private val homeViewModel by viewModels<HomeViewModel>()

    /* private val homeViewModel: HomeViewModel by viewModels {
         HomeViewModelFactory((application as PayrocApplication).repository)
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //  homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]

        subscribeToObservables()

        binding.payButtonId.setOnClickListener {
            homeViewModel.payClicked(binding.amountEditText.text.toString().toDouble())
        }
    }

    private fun subscribeToObservables() {
        // State Observable
        homeViewModel.getState().observe(this) { state ->
            Timber.i("State :: $state")
            binding.stateTextView.text = state.toString()
            when (state) {
                TransactionState.CARD_REQUEST -> {
                    //ToDo pop up dialog to provide / tap card
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(2000)
                        homeViewModel.provideCard(getCard())
                    }
                }
                else -> {
                    Timber.e("Error :: Unverified state")
                }
            }
        }

        // Client Message Observable
        homeViewModel.getClientMessage().observe(this) { message ->
            Timber.i("Message :: $message")
        }

        // Client Receipt Observable
        homeViewModel.getReceipt().observe(this) { receiptsArray ->
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
                                homeViewModel.insertReceipt(receiptEntity)
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

    /*private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getState().collect { state ->
                Timber.i("State :: $state")
                binding.stateTextView.text = state.toString()
                when (state) {
                    TransactionState.CARD_REQUEST -> {
                        //ToDo pop up dialog to provide / tap card
                        delay(2000)
                        homeViewModel.provideCard(getCard())
                    }
                    else -> {
                        Timber.e("Error :: Unverified state")
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.getClientMessage().collect { message ->
                Timber.i("Message :: $message")
            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.getReceipt().collect { receiptsArray ->
                Timber.i("Receipt Received :: $receiptsArray")
                receiptsArray?.let { receipts ->
                    receipts.forEach { receipt ->
                        when (receipt.header) {
                            "MERCHANT COPY" -> {
                                // Should be stored internally for POS reference
                                val gson = Gson()
                                val receiptJsonString = gson.toJson(receipt)
                                val receiptEntity = Receipt(receipts = receiptJsonString)
                                homeViewModel.insertReceipt(receiptEntity)
                            }
                            "CARDHOLDER COPY" -> {
                                //ToDo print card holder copy for customer
                            }
                        }
                    }
                }
            }
        }*/

    private fun getCard(): Card {
        val cards = convertXMLToDataClass()
        // Pick a random card from the list of available cards.
        return cards.card[Random.nextInt(cards.card.size)]
    }

    // Move to utility?
    private fun convertXMLToDataClass(): Cards {
        Timber.i("filepath :: ${filesDir.path}")
        val file = resources.openRawResource(R.raw.card_data)
        val xmlToJson = XmlToJson.Builder(file, null).build()
        file.close()

        val cardsJsonObject = xmlToJson.toString()

        val gson = Gson()
        return gson.fromJson(cardsJsonObject, CardList::class.java).cards
    }
}