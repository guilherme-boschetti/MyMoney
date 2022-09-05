package com.guilhermeb.mymoney.viewmodel.currency.state

/**
 * Data validation state of the currency converter form.
 */
data class CurrencyConverterFormState(
    val amountError: Int? = null,
    val fromCurrencyError: Int? = null,
    val toCurrencyError: Int? = null,
    val isFormCompleted: Boolean = false,
    val isDataValid: Boolean = false
)