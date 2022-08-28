package com.guilhermeb.mymoney.model.data.remote.firebase

import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.model.repository.contract.Authenticable
import java.lang.Exception

class FirebaseAuthentication : Authenticable {

    override fun getCurrentUserUid(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override fun getCurrentUserEmail(): String? {
        return Firebase.auth.currentUser?.email
    }

    override fun createUser(email: String, password: String, asyncProcess: AsyncProcess<String?>) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
            }
    }

    override fun signIn(email: String, password: String, asyncProcess: AsyncProcess<String?>) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
            }
    }

    override fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess<String?>) {
        val auth: FirebaseAuth = Firebase.auth
        auth.useAppLanguage()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
            }
    }

    override fun updatePassword(newPassword: String, asyncProcess: AsyncProcess<String?>) {
        val user = Firebase.auth.currentUser
        user?.let {
            it.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
                }
        }
    }

    override fun deleteUser(asyncProcess: AsyncProcess<String?>) {
        val user = Firebase.auth.currentUser
        user?.let {
            it.delete()
                .addOnCompleteListener { task ->
                    asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
                }
        }
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

    private fun errorVerification(e: Exception?): String? {
        val context = MyMoneyApplication.getInstance().applicationContext
        // http://www.techotopia.com/index.php/Handling_Firebase_Authentication_Errors_and_Failures
        when (e) {
            is FirebaseAuthWeakPasswordException -> {
                return context.getString(R.string.weak_password)
            }
            is FirebaseAuthInvalidCredentialsException -> {
                return when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> {
                        context.getString(R.string.invalid_email)
                    }
                    "ERROR_WRONG_PASSWORD" -> {
                        context.getString(R.string.invalid_password)
                    }
                    else -> {
                        e.localizedMessage
                    }
                }
            }
            is FirebaseAuthUserCollisionException -> {
                return when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> {
                        context.getString(R.string.account_already_exists)
                    }
                    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                        context.getString(R.string.email_already_in_use)
                    }
                    "ERROR_CREDENTIAL_ALREADY_IN_USE" -> {
                        context.getString(R.string.credentials_already_in_use)
                    }
                    else -> {
                        e.localizedMessage
                    }
                }
            }
            is FirebaseAuthInvalidUserException -> {
                return when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> {
                        context.getString(R.string.invalid_account)
                    }
                    "ERROR_USER_DISABLED" -> {
                        context.getString(R.string.account_disabled)
                    }
                    else -> {
                        e.getLocalizedMessage()
                    }
                }
            }
            else -> {
                return e?.localizedMessage
            }
        }
    }
}