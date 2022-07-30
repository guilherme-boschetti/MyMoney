package com.guilhermeb.mymoney.model.repository.authentication

import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.model.repository.contract.Authenticable

class AuthenticationRepository(private val auth: Authenticable) {

    fun getCurrentUserEmail(): String? {
        return auth.getCurrentUserEmail()
    }

    fun createUser(email: String, password: String, asyncProcess: AsyncProcess<String?>) {
        auth.createUser(email, password, asyncProcess)
    }

    fun signIn(email: String, password: String, asyncProcess: AsyncProcess<String?>) {
        auth.signIn(email, password, asyncProcess)
    }

    fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess<String?>) {
        auth.sendPasswordResetEmail(email, asyncProcess)
    }

    fun updatePassword(newPassword: String, asyncProcess: AsyncProcess<String?>) {
        auth.updatePassword(newPassword, asyncProcess)
    }

    fun deleteUser(asyncProcess: AsyncProcess<String?>) {
        auth.deleteUser(asyncProcess)
    }

    fun signOut() {
        auth.signOut()
    }
}