package com.guilhermeb.mymoney.viewmodel.authentication

import androidx.lifecycle.ViewModel
import com.guilhermeb.mymoney.model.repository.authentication.AuthenticationRepository
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) :
    ViewModel() {

    fun getCurrentUserEmail(): String? {
        return authenticationRepository.getCurrentUserEmail()
    }

    fun createUser(email: String, password: String, asyncProcess: AsyncProcess) {
        authenticationRepository.createUser(email, password, asyncProcess)
    }

    fun signIn(email: String, password: String, asyncProcess: AsyncProcess) {
        authenticationRepository.signIn(email, password, asyncProcess)
    }

    fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess) {
        authenticationRepository.sendPasswordResetEmail(email, asyncProcess)
    }

    fun updatePassword(newPassword: String, asyncProcess: AsyncProcess) {
        authenticationRepository.updatePassword(newPassword, asyncProcess)
    }

    fun signOut() {
        authenticationRepository.signOut()
    }

    fun deleteUser(asyncProcess: AsyncProcess) {
        authenticationRepository.deleteUser(asyncProcess)
    }
}