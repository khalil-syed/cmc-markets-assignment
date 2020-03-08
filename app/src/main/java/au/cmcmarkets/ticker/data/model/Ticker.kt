package au.cmcmarkets.ticker.data.model

import com.google.gson.annotations.SerializedName

data class Ticker(@SerializedName("GBP") val priceUpdate: PriceUpdate)