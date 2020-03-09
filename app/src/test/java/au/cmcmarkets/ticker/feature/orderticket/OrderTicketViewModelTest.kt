package au.cmcmarkets.ticker.feature.orderticket

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import au.cmcmarkets.ticker.data.model.PriceUpdate
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OrderTicketViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun testUnitEntryImpactOnAmount() {

        val viewModel = OrderTicketViewModel()
        viewModel.sellingPrice.value = PriceUpdate(50.0,50.0)
        viewModel.unit.value = 4.0
        viewModel.onUnitEnteredByUser()
        assertEquals(viewModel.amount.value,200.0)
    }

    @Test
    fun testSpreadValue() {
        val viewModel = OrderTicketViewModel()
        viewModel.sellingPrice.postValue(PriceUpdate(750.0,50.0))
        viewModel.spread.observeForever { }
        assertEquals(viewModel.spread.value,700.0)
    }

}