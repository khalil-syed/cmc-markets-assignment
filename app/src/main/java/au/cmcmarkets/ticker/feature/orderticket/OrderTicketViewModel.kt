package au.cmcmarkets.ticker.feature.orderticket

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import au.cmcmarkets.ticker.data.api.BitcoinApi
import au.cmcmarkets.ticker.data.model.PriceUpdate
import au.cmcmarkets.ticker.data.model.Ticker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask


class OrderTicketViewModel @Inject constructor(private val bitcoinApi: BitcoinApi) : ViewModel() {

    companion object {
        const val POLLING_INTERVAL_MILLIS: Long = 15000
    }

    val sellingPrice: MutableLiveData<PriceUpdate> by lazy {
        MutableLiveData<PriceUpdate>()
    }

    val highlightValues: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val amountValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val unitValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val enableConfirmButton: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val spreadValue: LiveData<String> = Transformations.map(sellingPrice){ (it.buy - it.sell).toString() }

    fun startPolling() {
        pollingTimer.schedule(timerTask { fetchPrices() }, 0, POLLING_INTERVAL_MILLIS)
    }

    fun stopPolling() {
        pollingTimer.cancel()
    }

    fun onAmountEnteredByUser() {
        amountValue.value?.let {
            val buy = sellingPrice.value?.let { it.buy } ?: return
            val amount = it.toIntOrNull()

            val units = amount?.let { (amount/buy).toString() } ?: ""
            unitValue.value = units
            enableConfirmButton.value = it.isValid() && units.isValid()
        }
    }

    fun onUnitEnteredByUser() {
        unitValue.value?.let {
            val buy = sellingPrice.value?.let { it.buy } ?: return
            val units = it.toIntOrNull()
            val amount = units?.let { (units*buy).toString() } ?: ""
            amountValue.value = amount
            enableConfirmButton.value = it.isValid() && amount.isValid()
        }
    }

    private fun String?.isValid(): Boolean {
        if (this != null) {
            return this != "0" && this.isNotEmpty()
        }
        return false
    }

    private val pollingTimer by lazy {
        Timer("PricePollTimer", false)
    }

    private fun fetchPrices() {
        bitcoinApi.fetchPrices()?.enqueue(object : Callback<Ticker> {

            override fun onFailure(call: Call<Ticker>?, t: Throwable?) {
                print(t?.localizedMessage)
            }

            override fun onResponse(call: Call<Ticker>?, response: Response<Ticker>?) {
                response?.let {
                    if (response.isSuccessful) {
                        val ticker = response.body()
                        sellingPrice.value = ticker?.priceUpdate ?: return
                        highlightValues.value = true
                        Handler().postDelayed({ highlightValues.value = false }, 2000)
                    } else {
                        print(response.errorBody()?.string())
                    }
                }
            }

        })
    }
}