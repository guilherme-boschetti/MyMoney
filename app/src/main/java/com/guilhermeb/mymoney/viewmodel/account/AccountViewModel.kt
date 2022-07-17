package com.guilhermeb.mymoney.viewmodel.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.cryptography.md5
import com.guilhermeb.mymoney.common.datastore.getPasswordFromDataStorePreferences
import com.guilhermeb.mymoney.common.datastore.getPreferencesDataStoreKey
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import com.guilhermeb.mymoney.common.validation.isPasswordValid
import com.guilhermeb.mymoney.viewmodel.account.state.ChangePasswordFormState
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel

class AccountViewModel : ViewModel() {

    private val authenticationViewModel by lazy {
        AuthenticationViewModel()
    }
    private val moneyViewModel by lazy {
        MoneyViewModel()
    }

    // == -- Account == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    fun signOut() {
        authenticationViewModel.signOut()
    }

    fun deleteUser(asyncProcess: AsyncProcess) {
        authenticationViewModel.deleteUser(object : AsyncProcess {
            override fun onComplete(isSuccessful: Boolean, errorMessage: String?) {
                if (isSuccessful) {
                    moneyViewModel.removeAllMoneyItemsByUser(authenticationViewModel.getCurrentUserEmail()!!)
                }
                asyncProcess.onComplete(isSuccessful, errorMessage)
            }
        })
    }

    // == -- Change Password == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    private val _changePasswordFormState = MutableLiveData<ChangePasswordFormState>()
    val changePasswordFormState: LiveData<ChangePasswordFormState> = _changePasswordFormState

    fun changePasswordFormDataChanged(
        currentPassword: String,
        newPassword: String,
        newPasswordRepeated: String
    ) {
        val isFormFilled =
            currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPasswordRepeated.isNotEmpty()
        _changePasswordFormState.value = ChangePasswordFormState(isFormCompleted = isFormFilled)
    }

    fun isChangePasswordFormDataValid(
        currentPassword: String,
        newPassword: String,
        newPasswordRepeated: String
    ): Boolean {
        val passwordFromDataStore =
            getPasswordFromDataStorePreferences(getPreferencesDataStoreKey(Constants.PASSWORD))
        if (!isPasswordValid(currentPassword) || md5(currentPassword) != passwordFromDataStore) {
            _changePasswordFormState.value =
                ChangePasswordFormState(
                    isFormCompleted = true,
                    currentPasswordError = R.string.invalid_password
                )
        } else if (!isPasswordValid(newPassword)) {
            _changePasswordFormState.value =
                ChangePasswordFormState(
                    isFormCompleted = true,
                    newPasswordError = R.string.invalid_password
                )
        } else if (!isPasswordValid(newPasswordRepeated)) {
            _changePasswordFormState.value =
                ChangePasswordFormState(
                    isFormCompleted = true,
                    newPasswordRepeatedError = R.string.invalid_password
                )
        } else if (newPassword != newPasswordRepeated) {
            _changePasswordFormState.value =
                ChangePasswordFormState(
                    isFormCompleted = true,
                    differentPasswordError = R.string.the_passwords_are_different
                )
        } else {
            _changePasswordFormState.value =
                ChangePasswordFormState(isFormCompleted = true, isDataValid = true)
            return true
        }
        return false
    }

    fun updatePassword(newPassword: String, asyncProcess: AsyncProcess) {
        authenticationViewModel.updatePassword(newPassword, asyncProcess)
    }

    // == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    fun getCurrentUserEmail(): String? {
        return authenticationViewModel.getCurrentUserEmail()
    }
}