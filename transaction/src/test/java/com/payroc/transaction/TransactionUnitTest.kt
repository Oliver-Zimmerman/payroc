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
class TransactionUnitTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Spy
    private lateinit var transaction: Transaction

    @Spy
    private lateinit var payrocClient: PayrocClient

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, true, true, true)
        Dispatchers.setMain(testDispatcher)

        payrocClient = spy(PayrocClient("5140001", ""))
        transaction = spy(Transaction(1.00, "5140001", "", payrocClient))
    }

    @Test
    fun `provideCard with invalid API key adjusts transaction state to Error`() =
        runTest(testDispatcher) {
            val mockCard = Mockito.mock(Card::class.java)
            transaction.provideCard(mockCard)
            assertEquals(
                payrocClient.getStateResponse().getOrAwaitValue(),
                TransactionState.ERROR
            )
        }
}