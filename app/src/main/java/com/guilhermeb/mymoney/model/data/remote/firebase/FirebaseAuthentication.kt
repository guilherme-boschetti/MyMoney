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

    override fun getCurrentUserEmail(): String? {
        return Firebase.auth.currentUser?.email
    }

    override fun createUser(email: String, password: String, asyncProcess: AsyncProcess) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
            }
    }

    override fun signIn(email: String, password: String, asyncProcess: AsyncProcess) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
            }
    }

    override fun sendPasswordResetEmail(email: String, asyncProcess: AsyncProcess) {
        val auth: FirebaseAuth = Firebase.auth
        auth.useAppLanguage()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
            }
    }

    override fun updatePassword(newPassword: String, asyncProcess: AsyncProcess) {
        val user = Firebase.auth.currentUser
        user?.let {
            it.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    asyncProcess.onComplete(task.isSuccessful, errorVerification(task.exception))
                }
        }
    }

    override fun deleteUser(asyncProcess: AsyncProcess) {
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
        // http://www.techotopia.com/index.php/Handling_Firebase_Authentication_Errors_and_Failures
        when (e) {
            is FirebaseAuthWeakPasswordException -> {
                return MyMoneyApplication.getInstance()?.getString(R.string.weak_password)
            }
            is FirebaseAuthUserCollisionException -> {
                when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> {
                        return MyMoneyApplication.getInstance()
                            ?.getString(R.string.account_already_exists)
                    }
                    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                        return MyMoneyApplication.getInstance()
                            ?.getString(R.string.email_already_in_use)
                    }
                    "ERROR_CREDENTIAL_ALREADY_IN_USE" -> {
                        return MyMoneyApplication.getInstance()
                            ?.getString(R.string.credentials_already_in_use)
                    }
                    else -> {
                        return e.localizedMessage
                    }
                }
            }
            is FirebaseAuthInvalidUserException -> {
                return when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> {
                        MyMoneyApplication.getInstance()?.getString(R.string.invalid_account)
                    }
                    "ERROR_USER_DISABLED" -> {
                        MyMoneyApplication.getInstance()
                            ?.getString(R.string.account_disabled)
                    }
                    else -> {
                        e.getLocalizedMessage()
                    }
                }
            }
            is FirebaseAuthInvalidCredentialsException -> {
                return when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> {
                        MyMoneyApplication.getInstance()?.getString(R.string.invalid_email)
                    }
                    "ERROR_WRONG_PASSWORD" -> {
                        MyMoneyApplication.getInstance()
                            ?.getString(R.string.invalid_password)
                    }
                    else -> {
                        e.localizedMessage
                    }
                }
            }
            else -> {
                return e?.localizedMessage
            }
        }
    }
}