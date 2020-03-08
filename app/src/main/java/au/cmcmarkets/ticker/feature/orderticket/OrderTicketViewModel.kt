package au.cmcmarkets.ticker.feature.orderticket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.cmcmarkets.ticker.data.api.BitcoinApi
import au.cmcmarkets.ticker.data.model.PriceUpdate
import au.cmcmarkets.ticker.data.model.Ticker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class OrderTicketViewModel @Inject constructor(private val bitcoinApi: BitcoinApi) : ViewModel() {

    val sellingPrice: MutableLiveData<PriceUpdate> by lazy {
        MutableLiveData<PriceUpdate>()
    }

    fun fetchPrices() {
        bitcoinApi.fetchPrices()?.enqueue(object : Callback<Ticker> {

            override fun onFailure(call: Call<Ticker>?, t: Throwable?) {
                print(t?.localizedMessage)
            }

            override fun onResponse(call: Call<Ticker>?, response: Response<Ticker>?) {
                if (response?.isSuccessful!!) {
                    val ticker = response.body()
                    sellingPrice.value = ticker?.priceUpdate ?: return
                } else {
                    print(response?.errorBody()?.string())
                }
            }

        })
    }
}