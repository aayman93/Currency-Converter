package com.github.aayman93.currencyconverter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.aayman93.currencyconverter.R
import com.github.aayman93.currencyconverter.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivConvert.setOnClickListener {
            viewModel.convert(
                binding.spinnerFromCurrency.selectedItem.toString(),
                binding.spinnerToCurrency.selectedItem.toString(),
                binding.inputAmount.text.toString()
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.conversionState.collect { event ->
                when (event) {
                    is MainViewModel.ConversionEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.ivConvert.isVisible = true

                        binding.tvResult.text = event.result.toString()
                        binding.tvConversionRate.text = getString(
                            R.string.conversion_rate,
                            event.rate
                        )
                    }
                    is MainViewModel.ConversionEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.ivConvert.isVisible = true
                        Snackbar.make(binding.root, event.errorMessage, Snackbar.LENGTH_LONG).show()
                    }
                    is MainViewModel.ConversionEvent.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.ivConvert.isVisible = false
                    }
                    is MainViewModel.ConversionEvent.Empty -> Unit
                }
            }
        }
    }
}