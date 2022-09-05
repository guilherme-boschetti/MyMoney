package com.guilhermeb.mymoney.viewmodel.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.model.data.remote.retrofit.currency.api.response.ApiResponse
import com.guilhermeb.mymoney.model.data.remote.retrofit.currency.model.Currency
import com.guilhermeb.mymoney.model.repository.currency.CurrencyRepository
import com.guilhermeb.mymoney.viewmodel.currency.state.CurrencyConverterFormState
import kotlinx.coroutines.launch
import javax.inject.Inject

class CurrencyConverterViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) :
    ViewModel() {

    private val _currencyApiResponse = MutableLiveData<ApiResponse<List<Currency>>>()
    val currencyApiResponse: LiveData<ApiResponse<List<Currency>>> get() = _currencyApiResponse

    fun getCurrency(currency: String) {
        viewModelScope.launch {
            currencyRepository.getCurrency(currency).collect {
                _currencyApiResponse.value = it
            }
        }
    }

    // == -- Form validation == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    private val _currencyConverterFormState = MutableLiveData<CurrencyConverterFormState>()
    val currencyConverterFormState: LiveData<CurrencyConverterFormState> =
        _currencyConverterFormState

    fun currencyConverterFormDataChanged(amount: String, fromCurrency: String, toCurrency: String) {
        val isFormFilled =
            amount.isNotEmpty() && fromCurrency.isNotEmpty() && toCurrency.isNotEmpty()
        _currencyConverterFormState.value =
            CurrencyConverterFormState(isFormCompleted = isFormFilled)
    }

    fun isCurrencyConverterFormDataValid(
        amount: String,
        fromCurrency: String,
        toCurrency: String
    ): Boolean {
        if (amount.isEmpty()) {
            _currencyConverterFormState.value = CurrencyConverterFormState(
                isFormCompleted = true,
                amountError = R.string.amount_should_not_be_empty
            )
        } else if (fromCurrency.isEmpty()) {
            _currencyConverterFormState.value = CurrencyConverterFormState(
                isFormCompleted = true,
                fromCurrencyError = R.string.from_currency_required
            )
        } else if (toCurrency.isEmpty()) {
            _currencyConverterFormState.value = CurrencyConverterFormState(
                isFormCompleted = true,
                toCurrencyError = R.string.to_currency_required
            )
        } else if (fromCurrency.equals(toCurrency)) {
            _currencyConverterFormState.value = CurrencyConverterFormState(
                isFormCompleted = true,
                fromCurrencyError = R.string.from_to_currency_should_not_be_equal,
                toCurrencyError = R.string.from_to_currency_should_not_be_equal
            )
        } else {
            _currencyConverterFormState.value =
                CurrencyConverterFormState(isFormCompleted = true, isDataValid = true)
            return true
        }
        return false
    }

    // == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==
}