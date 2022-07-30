package com.guilhermeb.mymoney.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.cryptography.md5
import com.guilhermeb.mymoney.common.helper.DataStorePreferencesHelper
import com.guilhermeb.mymoney.common.helper.getPreferencesDataStoreKey
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import com.guilhermeb.mymoney.viewmodel.login.state.CreateAccountFormState
import com.guilhermeb.mymoney.common.validation.isEmailValid
import com.guilhermeb.mymoney.common.validation.isPasswordValid
import javax.inject.Inject

class CreateAccountViewModel @Inject constructor(private val authenticationViewModel: AuthenticationViewModel) :
    ViewModel() {

    private val _createAccountFormState = MutableLiveData<CreateAccountFormState>()
    val createAccountFormState: LiveData<CreateAccountFormState> = _createAccountFormState

    fun createAccountFormDataChanged(
        email: String,
        emailRepeated: String,
        password: String,
        passwordRepeated: String,
        termsAndConditionsChecked: Boolean
    ) {
        val isFormFilled =
            email.isNotEmpty() && emailRepeated.isNotEmpty() && password.isNotEmpty() && passwordRepeated.isNotEmpty() && termsAndConditionsChecked
        _createAccountFormState.value = CreateAccountFormState(isFormCompleted = isFormFilled)
    }

    fun isCreateAccountFormDataValid(
        email: String,
        emailRepeated: String,
        password: String,
        passwordRepeated: String,
        termsAndConditionsChecked: Boolean
    ): Boolean {
        if (!isEmailValid(email)) {
            _createAccountFormState.value =
                CreateAccountFormState(isFormCompleted = true, emailError = R.string.invalid_email)
        } else if (!isEmailValid(emailRepeated)) {
            _createAccountFormState.value =
                CreateAccountFormState(
                    isFormCompleted = true,
                    emailRepeatedError = R.string.invalid_email
                )
        } else if (!isPasswordValid(password)) {
            _createAccountFormState.value =
                CreateAccountFormState(
                    isFormCompleted = true,
                    passwordError = R.string.invalid_password
                )
        } else if (!isPasswordValid(passwordRepeated)) {
            _createAccountFormState.value =
                CreateAccountFormState(
                    isFormCompleted = true,
                    passwordRepeatedError = R.string.invalid_password
                )
        } else if (!termsAndConditionsChecked) {
            _createAccountFormState.value =
                CreateAccountFormState(
                    isFormCompleted = true,
                    termsAndConditionsError = R.string.terms_and_conditions_acceptance_required
                )
        } else if (email != emailRepeated) {
            _createAccountFormState.value =
                CreateAccountFormState(
                    isFormCompleted = true,
                    differentEmailError = R.string.the_emails_are_different
                )
        } else if (password != passwordRepeated) {
            _createAccountFormState.value =
                CreateAccountFormState(
                    isFormCompleted = true,
                    differentPasswordError = R.string.the_passwords_are_different
                )
        } else {
            _createAccountFormState.value =
                CreateAccountFormState(isFormCompleted = true, isDataValid = true)
            return true
        }
        return false
    }

    fun createUser(email: String, password: String, asyncProcess: AsyncProcess<String?>) {
        authenticationViewModel.createUser(email, password, object : AsyncProcess<String?> {
            override fun onComplete(isSuccessful: Boolean, result: String?) {
                DataStorePreferencesHelper.getInstance().saveStringIntoDataStorePreferences(
                    getPreferencesDataStoreKey(Constants.PASSWORD),
                    md5(password)
                )
                asyncProcess.onComplete(isSuccessful, result)
            }
        })
    }
}