package com.guilhermeb.mymoney.viewmodel.money.state

/**
 * Data validation state of the money form.
 */
data class MoneyFormState(
    val titleError: Int? = null,
    val valueError: Int? = null,
    val typeError: Int? = null,
    val payDateError: Int? = null,
    val dueDayError: Int? = null,
    val isDataValid: Boolean = false
)
