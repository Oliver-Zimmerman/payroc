package com.payroc.transactionprocessor.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.payroc.transaction.TransactionState
import com.payroc.transaction.data.model.Card
import com.payroc.transaction.data.model.CardList
import com.payroc.transaction.data.model.Cards
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]

        subscribeToObservables()

        binding.payButtonId.setOnClickListener {
            homeViewModel.payClicked(binding.amountEditText.text.toString().toDouble())
        }
    }

    private fun subscribeCardRead() {

    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getState().collectLatest { state ->
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

            homeViewModel.getClientMessage().collectLatest { message ->
                Timber.i("Message :: $message")
            }

            homeViewModel.getReceipt().collectLatest { receipt ->
                Timber.i("Receipt :: $receipt")
            }
        }
    }

    private fun getCard(): Card {
        val cards = convertXMLToDataClass()
        // Ensure we only send the MAG_STRIPE card.
        return cards.card[cards.card.size - 1]
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