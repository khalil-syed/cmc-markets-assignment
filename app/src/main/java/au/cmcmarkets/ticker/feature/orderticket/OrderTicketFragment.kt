package au.cmcmarkets.ticker.feature.orderticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import au.cmcmarkets.ticker.R
import au.cmcmarkets.ticker.core.di.viewmodel.ViewModelFactory
import au.cmcmarkets.ticker.data.model.PriceUpdate
import au.cmcmarkets.ticker.databinding.FragmentOrderTicketBinding
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_order_ticket.*
import javax.inject.Inject

class OrderTicketFragment : DaggerFragment() {

    companion object {
        @BindingAdapter("android:text")
        @JvmStatic
        fun setDouble(view: TextView, oldValue: Double, value: Double) {
            if (oldValue == value) {
                return
            }
            if (value.isNaN()) {
                view.text = ""
                return
            }

            view.text = value.toString()
        }

        @InverseBindingAdapter(attribute = "android:text")
        @JvmStatic
        fun getDouble(view: TextView): Double {
            val num = view.text
            if(num.isEmpty()) return 0.0;
            return num.toString().toDouble()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: OrderTicketViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentOrderTicketBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_ticket, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val priceObserver = Observer<PriceUpdate> { price ->
            price?.let {
                setSellPrice(price.sell)
                setBuyingPrice(price.buy)
            }
        }
        viewModel.sellingPrice.observe(viewLifecycleOwner, priceObserver)

        val amountObserver = Observer<Double> {
            if (editTxtAmount.hasFocus()) {
                viewModel.onAmountEnteredByUser()
            }
        }
        viewModel.amount.observe(viewLifecycleOwner, amountObserver)

        val unitObserver = Observer<Double> {
            if (editTxtUnits.hasFocus()) {
                viewModel.onUnitEnteredByUser()
            }
        }
        viewModel.unit.observe(viewLifecycleOwner, unitObserver)
    }

    private fun setSellPrice(sellingPrice: Double) {
        val components = sellingPrice.toString().split(".")
        if (components.size > 1) {
            txtSellValueWhole.text = components[0]
            txtSellValueFraction.text = String.format(".%s", components[1])
        }
    }

    private fun setBuyingPrice(buyingPrice: Double) {
        val components = buyingPrice.toString().split(".")
        if (components.size > 1) {
            txtBuyValueWhole.text = components[0]
            txtBuyValueFraction.text = String.format(".%s", components[1])
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPolling()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startPolling()
    }
}
