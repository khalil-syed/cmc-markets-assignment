package au.cmcmarkets.ticker.feature.orderticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import au.cmcmarkets.ticker.R
import au.cmcmarkets.ticker.core.di.viewmodel.ViewModelFactory
import au.cmcmarkets.ticker.data.model.PriceUpdate
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class OrderTicketFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    var txtSellValue: TextView? = null
    var txtBuyValue: TextView? = null

    private val viewModel: OrderTicketViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_ticket, container, false)
        txtSellValue = view.findViewById(R.id.txtSellValue)
        txtBuyValue = view.findViewById(R.id.txtBuyValue)
        return view
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val priceObserver = Observer<PriceUpdate> { price ->
            price?.let {
                txtSellValue?.text = price.sell.toString()
                txtBuyValue?.text = price.buy.toString()
            }

        }
        viewModel.sellingPrice.observe(this, priceObserver)
        viewModel.fetchPrices()
    }
}
