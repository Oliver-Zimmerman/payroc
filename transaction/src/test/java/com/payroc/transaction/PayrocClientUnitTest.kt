@file:OptIn(ExperimentalCoroutinesApi::class)

package com.payroc.transaction

import com.payroc.transaction.data.model.Card
import com.payroc.transaction.testhelpers.extensions.CoroutinesTestExtension
import com.payroc.transaction.testhelpers.extensions.InstantExecutorExtension
import com.payroc.transaction.testhelpers.extensions.getOrAwaitValue
import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.mockito.Spy

@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
class PayrocClientUnitTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Spy
    private lateinit var payrocClient: PayrocClient

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, true, true, true)
        Dispatchers.setMain(testDispatcher)

        payrocClient = spy(PayrocClient("5140001", ""))
    }

    @Test
    fun `start transaction with invalid amount creates error state`() {
        payrocClient.startTransaction(0.00)
        assertEquals(
            payrocClient.getStateResponse().getOrAwaitValue(),
            TransactionState.ERROR
        )
    }

    @Test
    fun `start transaction with valid amount creates card request state`() {
        payrocClient.startTransaction(1.00)
        assertEquals(
            payrocClient.getStateResponse().getOrAwaitValue(),
            TransactionState.CARD_REQUEST
        )
    }

    @Test
    fun `cancel transaction when there is no transaction provides client message`() {
        payrocClient.cancelTransaction()
        assertEquals(
            payrocClient.getClientMessageResponse().getOrAwaitValue(),
            "No transaction to cancel"
        )
    }

    @Test
    fun `cancel transaction resets state to IDLE when used in correct CARD_REQUEST state`() {
        payrocClient.startTransaction(1.00)
        payrocClient.cancelTransaction()
        assertEquals(
            payrocClient.getStateResponse().getOrAwaitValue(),
            TransactionState.IDLE
        )
    }

    @Test
    fun `read card data when not in CARD_REQUEST state provides error message`() =
        runTest(testDispatcher) {
            val mockCard = Mockito.mock(Card::class.java)
            payrocClient.readCardData(mockCard)
            assertEquals(
                payrocClient.getClientMessageResponse().getOrAwaitValue(),
                "No card required"
            )
        }
}