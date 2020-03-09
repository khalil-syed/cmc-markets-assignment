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


class OrderTicketViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val POLLING_INTERVAL_MILLIS: Long = 15000
    }

    @Inject
    lateinit var bitcoinApi: BitcoinApi

    val sellingPrice: MutableLiveData<PriceUpdate> by lazy {
        MutableLiveData<PriceUpdate>()
    }

    val highlightValues: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val amount: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val unit: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val enableConfirmButton: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val spread: LiveData<Double> = Transformations.map(sellingPrice){ it.buy - it.sell }

    fun startPolling() {
        pollingTimer.schedule(timerTask { fetchPrices() }, 0, POLLING_INTERVAL_MILLIS)
    }

    fun stopPolling() {
        pollingTimer.cancel()
    }

    fun onAmountEnteredByUser() {
        amount.value?.let {
            val buy = sellingPrice.value?.let { it.buy } ?: return
            val units = it/buy
            unit.value = units
            enableConfirmButton.value = it > 0 && units > 0
        }
    }

    fun onUnitEnteredByUser() {
        unit.value?.let {
            val buy = sellingPrice.value?.let { it.buy } ?: return
            val amountValue = it*buy
            amount.value = amountValue
            enableConfirmButton.value = it > 0 && amountValue > 0
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