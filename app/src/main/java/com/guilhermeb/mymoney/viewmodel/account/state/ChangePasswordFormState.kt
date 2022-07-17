package com.guilhermeb.mymoney.viewmodel.account.state

/**
 * Data validation state of the change password form.
 */
data class ChangePasswordFormState(
    val currentPasswordError: Int? = null,
    val newPasswordError: Int? = null,
    val newPasswordRepeatedError: Int? = null,
    val differentPasswordError: Int? = null,
    val isFormCompleted: Boolean = false,
    val isDataValid: Boolean = false
)