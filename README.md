# Payroc Transaction Processor SDK
[![Unit Tests](https://github.com/Oliver-Zimmerman/payroc/actions/workflows/dispatch_unit_tests.yml/badge.svg)](https://github.com/Oliver-Zimmerman/payroc/actions/workflows/dispatch_unit_tests.yml)

Enable Mag Stripe and EMV Card payments on Android :credit_card:

## Project structure: 

- **Transaction Module:** the actual transaction handler module, containing all SDK logic and unit tests
- **App Module:** the sample demo application utilizing the SDK Transaction Module to demonstrate its functionality

<p align="center">
   <img align="center" src="https://user-images.githubusercontent.com/9112652/190907673-d3b9ce49-a5f4-4f3a-95eb-b9c0afa0a4c8.png">
</p>

 ## Usage
 
 ### Payroc Client
 
To initialize the PayRoc client tou will need to provide a terminal String value, as well as your apiKey String. Once an instance is created, you will be able to create and cancel transactions as well as receive event updates. 

```kotlin
  payrocClient = PayrocClient("5140001", "<Your API Key>")
  payrocClient.startTransaction(50.80)
```

Starting a transaction will create an instance of the Transaction class which will handle all transaction related processes. Only one transaction can be handled at a time. 

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


<p align="center">
   <img align="center" width="25%" height="25%" src="https://user-images.githubusercontent.com/9112652/190905411-5bf6f52f-2ab5-42d0-a853-17d802b2a24b.gif">
</p>

