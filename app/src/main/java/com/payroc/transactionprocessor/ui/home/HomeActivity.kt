package com.payroc.transactionprocessor.ui.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.payroc.transaction.TransactionState
import com.payroc.transaction.data.model.Card
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.databinding.ActivityMainBinding
import com.payroc.transactionprocessor.ui.pay.PayFragment
import com.payroc.transactionprocessor.ui.receipts.ReceiptsFragment
import com.payroc.transactionprocessor.utility.convertXMLToDataClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val payFragment = PayFragment()
        val receiptsFragment = ReceiptsFragment()

        setCurrentFragment(payFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_home -> setCurrentFragment(payFragment)
                R.id.menu_item_receipts -> setCurrentFragment(receiptsFragment)
            }
            true
        }

        binding.bottomNavigationView.getOrCreateBadge(R.id.menu_item_receipts).apply {
            number = 10
            isVisible = true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_fragment, fragment).commit()
        }

    /* private lateinit var binding: ActivityMainBinding

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
             if (state == TransactionState.CARD_REQUEST) {
                 //ToDo pop up dialog to provide / tap card
                 lifecycleScope.launch(Dispatchers.Main) {
                     delay(2000)
                     homeViewModel.provideCard(getCard())
                 }
             }
         }

         // Client Message Observable
         homeViewModel.getClientMessage().observe(this) { message ->
             Timber.i("Message :: $message")
             binding.clientMessageTextView.text = message
         }

         // Client Receipt Observable
         homeViewModel.getReceipt().observe(this)
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
         val cards = convertXMLToDataClass(applicationContext)
         // Pick a random card from the list of available cards.
         return cards.card[Random.nextInt(cards.card.size)]
     } */
}