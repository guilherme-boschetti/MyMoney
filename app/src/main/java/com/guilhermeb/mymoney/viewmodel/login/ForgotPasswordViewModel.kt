package com.guilhermeb.mymoney.viewmodel.login

import androidx.lifecycle.ViewModel
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import com.guilhermeb.mymoney.common.validation.isEmailValid
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor(private val authenticationViewModel: AuthenticationViewModel) :
    ViewModel() {

    fun isFormDataValid(email: String): Boolean {
        return isEmailValid(email)
    }

    fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess<String?>) {
        authenticationViewModel.sendPasswordResetEmail(email, asyncProcess)
    }
}