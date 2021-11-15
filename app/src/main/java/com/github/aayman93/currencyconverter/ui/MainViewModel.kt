package com.github.aayman93.currencyconverter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aayman93.currencyconverter.repository.MainRepository
import com.github.aayman93.currencyconverter.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    sealed class ConversionEvent {
        class Success(val result: Double, val rate: Double) : ConversionEvent()
        class Failure(val errorMessage: String) : ConversionEvent()
        object Loading : ConversionEvent()
        object Empty : ConversionEvent()
    }

    private val _conversionState = MutableStateFlow<ConversionEvent>(ConversionEvent.Empty)
    val conversionState: StateFlow<ConversionEvent> = _conversionState

    fun convert(
        fromCurrency: String,
        toCurrency: String,
        amount: String
    ) {
        val validationError = when {
            amount.isBlank() -> {
                "Please enter an amount to convert"
            }
            amount.toDoubleOrNull() == null -> {
                "Please enter a valid amount"
            }
            else -> {
                null
            }
        }

        validationError?.let {
            _conversionState.value = ConversionEvent.Failure(it)
            return
        }

        _conversionState.value = ConversionEvent.Loading
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = repository.convert(fromCurrency, toCurrency, amount)) {
                is Resource.Error -> {
                    _conversionState.value = ConversionEvent.Failure(response.message!!)
                }
                is Resource.Success -> {
                    val result = round(response.data!!.conversionResult * 100) / 100
                    val rate = response.data.conversionRate
                    _conversionState.value = ConversionEvent.Success(result, rate)
                }
            }
        }
    }
}