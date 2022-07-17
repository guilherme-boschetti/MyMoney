package com.guilhermeb.mymoney.model.repository.contract

interface Authenticable {
    fun getCurrentUserEmail(): String?
    fun createUser(email: String, password: String, asyncProcess: AsyncProcess)
    fun signIn(email: String, password: String, asyncProcess: AsyncProcess)
    fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess)
    fun updatePassword(newPassword: String, asyncProcess: AsyncProcess)
    fun deleteUser(asyncProcess: AsyncProcess)
    fun signOut()
}