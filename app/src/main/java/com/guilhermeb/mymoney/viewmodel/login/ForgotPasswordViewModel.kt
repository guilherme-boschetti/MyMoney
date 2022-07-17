package com.guilhermeb.mymoney.viewmodel.login

import androidx.lifecycle.ViewModel
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import com.guilhermeb.mymoney.common.validation.isEmailValid

class ForgotPasswordViewModel : ViewModel() {

    private val authenticationViewModel by lazy {
        AuthenticationViewModel()
    }

    fun isFormDataValid(email: String): Boolean {
        return isEmailValid(email)
    }

    fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess) {
        authenticationViewModel.sendPasswordResetEmail(email, asyncProcess)
    }
}