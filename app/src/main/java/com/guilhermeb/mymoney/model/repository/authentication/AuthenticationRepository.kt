package com.guilhermeb.mymoney.model.repository.authentication

import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.model.repository.contract.Authenticable

class AuthenticationRepository(private val auth: Authenticable) {

    fun getCurrentUserEmail(): String? {
        return auth.getCurrentUserEmail()
    }

    fun createUser(email: String, password: String, asyncProcess: AsyncProcess) {
        auth.createUser(email, password, asyncProcess)
    }

    fun signIn(email: String, password: String, asyncProcess: AsyncProcess) {
        auth.signIn(email, password, asyncProcess)
    }

    fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess) {
        auth.sendPasswordResetEmail(email, asyncProcess)
    }

    fun updatePassword(newPassword: String, asyncProcess: AsyncProcess) {
        auth.updatePassword(newPassword, asyncProcess)
    }

    fun signOut() {
        auth.signOut()
    }

    fun deleteUser(asyncProcess: AsyncProcess) {
        auth.deleteUser(asyncProcess)
    }
}