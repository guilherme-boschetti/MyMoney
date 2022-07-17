package com.guilhermeb.mymoney.viewmodel.login.state

/**
 * Data validation state of the create account form.
 */
data class CreateAccountFormState(
    val emailError: Int? = null,
    val emailRepeatedError: Int? = null,
    val passwordError: Int? = null,
    val passwordRepeatedError: Int? = null,
    val termsAndConditionsError: Int? = null,
    val differentEmailError: Int? = null,
    val differentPasswordError: Int? = null,
    val isFormCompleted: Boolean = false,
    val isDataValid: Boolean = false
)
