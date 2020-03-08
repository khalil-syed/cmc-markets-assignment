package au.cmcmarkets.ticker.data.api

import au.cmcmarkets.ticker.data.model.Ticker
import retrofit2.Call
import retrofit2.http.GET

interface BitcoinApi {

    //TODO. Bitcoin api here
    @GET("ticker")
    fun fetchPrices(): Call<Ticker>?
}