# Payroc Transaction Processor SDK
[![Unit Tests](https://github.com/Oliver-Zimmerman/payroc/actions/workflows/dispatch_unit_tests.yml/badge.svg)](https://github.com/Oliver-Zimmerman/payroc/actions/workflows/dispatch_unit_tests.yml)

Enable Mag Stripe and EMV Card payments on Android :credit_card:

<p align="center">
   <img align="center" width="20%" height="20%" src="https://user-images.githubusercontent.com/9112652/190905411-5bf6f52f-2ab5-42d0-a853-17d802b2a24b.gif">
</p>


## Project structure: 

- **Transaction Module:** the actual transaction handler module, containing all SDK logic and unit tests
- **App Module:** the sample demo application utilizing the SDK Transaction Module to demonstrate its functionality

<p align="center">
   <img align="center" src="https://user-images.githubusercontent.com/9112652/190907673-d3b9ce49-a5f4-4f3a-95eb-b9c0afa0a4c8.png">
</p>

 ## Usage
 
 ### Payroc Client
 
To initialize the PayRoc client tou will need to provide a terminal String value, as well as your apiKey String. Once an instance is created, you will be able to create and cancel transactions as well as receive event updates. 

#### Starting a transaction

To start a transaction, you simply need to provide an amount in the form of a Double

```kotlin
  payrocClient = PayrocClient("5140001", "<Your API Key>")
  payrocClient.startTransaction(50.80)
```

Starting a transaction will create an instance of the Transaction class which will handle all transaction related processes. Only one transaction can be handled at a time. 


#### Cancelling a transaction

A transaction can be cancelled by calling 

```kotlin
  payrocClient.cancelTransaction()
```

It is important to note that a transaction can only be cancelled when it is in the CARD_REQUEST state. An error will be emitted otherwise. 

#### Provide Card Data

Once a Transaction is created with a specified amount, the client state will go into CARD_REQUEST state. When the client is in this state, a payment card can be provided by calling the readCardData method. 

```kotlin
  payrocClient.readCardData(card)
```

A Card is a data class provided by the SDK that specifies the required fields for handling a transaction

```kotlin
data class Card(
    var payloadType: String,
    var dataKsn: String,
    var tags: ArrayList<Tags>? = null,
    var cardholdername: String? = null,
    var serialNumber: String? = null,
    var encryptedData: String? = null,
)
```
**Note:** For the purpose of this sample app, we are parsing an XML file with stored card data. This is not a likely real life use case...

### Observing client events

In order to handle various SDK events, you will need to listen for the various UI events. All SDK events are provided via LiveData which can be observed:

```kotlin
    fun getState() = payrocClient.getStateResponse()

    fun getClientMessage() = payrocClient.getClientMessageResponse()

    fun getClientError() = payrocClient.getClientErrorResponse()

    fun getReceipt() = payrocClient.getClientReceiptResponse()
``` 

#### getState()
Returns the current SDK State response in the form of LiveData

State is represented by an enum provided by the SDK:

```kotlin
/**
 *
 * Enum class to represent the different Transaction States that a transaction can be in.
 *
 * @property IDLE idle state, no transaction is being handled
 * @property STARTED a transaction has been started, the amount has been provided
 * @property CARD_REQUEST the client is waiting for a card to process the transaction
 * @property READING a card has been provided and the information is being read
 * @property PROCESSING the client is processing the transaction with the provided amount and card details
 * @property COMPLETE a transaction has successfully completed
 * @property ERROR an error has occurred
 */
enum class TransactionState {
    IDLE,
    STARTED,
    CARD_REQUEST,
    READING,
    PROCESSING,
    COMPLETE,
    ERROR
}
```

#### getClientMessage()
Returns the most recent client message response in the form of LiveData

#### getClientErrorResponse()
Returns the most recent client error response in the form of LiveData.

Error is a data class provided by the SDK:

```kotlin
data class Error(
    val debugIdentifier: String,
    val details: ArrayList<ErrorDetail>,
)

data class ErrorDetail(
    val errorCode: String,
    val errorMessage: String,
    val about: String?,
    val source: ErrorSource?,
)

data class ErrorSource(
    val location: String,
    val resource: String?,
    val property: String?,
    val value: String?,
    val expected: String?,
)
```

These values are directly mapped to the properties defined at the error response section here:
https://developers.worldnetpayments.com/apis/merchant/#operation/payment

#### getClientReceiptResponse()
Returns the receipt as a result of a transaction in the form of LiveData

Receipts is a data class provided by the SDK:

```kotlin
data class Receipts(
    var copy: String,
    var header: String,
    var merchantDetails: ArrayList<MerchantDetails> = arrayListOf(),
    var transactionData: ArrayList<TransactionData> = arrayListOf(),
    var customFields: ArrayList<String> = arrayListOf(),
    var iccData: ArrayList<IccData> = arrayListOf(),
    var footer: String,
)
```

We can then use these methods to create an observer that listens for an events - in this example we assume the LiveData methods are within a ViewModel.

```kotlin
 private fun subscribeToObservables() {
        // State Observable
        payViewModel.getState().observe(viewLifecycleOwner) { state ->
            Timber.i("State :: $state")
            // Handle updated state, update UI, show toast message etc. 
        }

        // Client Message Observable
        payViewModel.getClientMessage().observe(viewLifecycleOwner) { message ->
            Timber.i("Message :: $message")
            // Handle received client message, update UI, show toast message etc.  
            // These are generic messages provided by the SDK. 
            // They can be ignored and custom messages can be used as a result of getState() and getClientError() if preferred
        }
        
        // Client Error Observable
        payViewModel.getClientError().observe(viewLifecycleOwner) { error ->
            Timber.i("Error :: $error")
            
            // Error data class received (see above). Use data class and handle error. eg.
            
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
            
            // Receipts received. Receipts will come in an ArrayList containing both the Merchant Copy as well as the Customer Copy
            // You can handle these however you like. eg. 
            
            receiptsArray?.let { receipts ->
                receipts.forEach { receipt ->
                    when (receipt.header) {
                        "MERCHANT COPY" -> {
                           //ToDo store merchant copy in local DB
                        }
                        "CARDHOLDER COPY" -> {
                            //ToDo print card holder copy for customer
                        }
                    }
                }
            }
        }
    }
```

 ## Testing
 
 Unit tests have been created for the SDK module for both the [PayrocClient](https://github.com/Oliver-Zimmerman/payroc/blob/main/transaction/src/test/java/com/payroc/transaction/PayrocClientUnitTest.kt) and [Transaction](https://github.com/Oliver-Zimmerman/payroc/blob/main/transaction/src/test/java/com/payroc/transaction/TransactionUnitTest.kt) classes.
 
 These Unit tests have been integrated with Github actions and can be run as a workflow [here](https://github.com/Oliver-Zimmerman/payroc/actions/workflows/dispatch_unit_tests.yml)




