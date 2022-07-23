package com.guilhermeb.mymoney.view.login.activity

import android.content.Intent
import android.os.Bundle
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.extension.afterTextChanged
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.common.util.isNetworkAvailable
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.ActivityForgotPasswordBinding
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.app.offline.activity.OfflineActivity
import com.guilhermeb.mymoney.viewmodel.login.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity : AbstractActivity() {

    private lateinit var forgotPasswordViewBinding: ActivityForgotPasswordBinding

    @Inject
    lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordViewBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(forgotPasswordViewBinding.root)
        setTitle(R.string.recover_password)

        handleInputEvents()

        handleClicks()

        val intentActivity = intent
        if (intentActivity.hasExtra(Constants.INTENT_EXTRA_KEY_EMAIL)) {
            forgotPasswordViewBinding.edtEmail.setText(intentActivity.getStringExtra(Constants.INTENT_EXTRA_KEY_EMAIL))
        }
        if (forgotPasswordViewBinding.edtEmail.text.toString().isEmpty()) {
            forgotPasswordViewBinding.btnRecoverPassword.isEnabled = false
        }
    }

    private fun handleInputEvents() {
        forgotPasswordViewBinding.edtEmail.apply {

            changeHintOnFocusChange(
                this@ForgotPasswordActivity,
                getString(R.string.hint_email),
                ""
            )

            afterTextChanged {
                forgotPasswordViewBinding.inEmail.error = null
                forgotPasswordViewBinding.btnRecoverPassword.isEnabled =
                    forgotPasswordViewBinding.edtEmail.text.toString().isNotEmpty()
            }
        }
    }

    private fun handleClicks() {
        forgotPasswordViewBinding.btnRecoverPassword.setOnClickListener {
            if (isNetworkAvailable()) {

                val email = forgotPasswordViewBinding.edtEmail.text.toString()
                if (forgotPasswordViewModel.isFormDataValid(email)) {
                    progressDialog.show()
                    forgotPasswordViewModel.sendPasswordResetEmail(email, object : AsyncProcess {
                        override fun onComplete(isSuccessful: Boolean, errorMessage: String?) {
                            progressDialog.dismiss()
                            if (isSuccessful) {
                                goToRecoverPasswordActivityWithEmailExtra(email)
                            } else {
                                val message = errorMessage
                                    ?: getString(R.string.failed_to_send_password_recovery_email)
                                showToast(this@ForgotPasswordActivity, message)
                            }
                        }
                    })
                } else {
                    forgotPasswordViewBinding.inEmail.error = getString(R.string.invalid_email)
                }

            } else {
                val intent = Intent(this@ForgotPasswordActivity, OfflineActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun goToRecoverPasswordActivityWithEmailExtra(email: String) {
        val intent = Intent(this@ForgotPasswordActivity, RecoverPasswordActivity::class.java)
        if (email.isNotEmpty()) {
            intent.putExtra(Constants.INTENT_EXTRA_KEY_EMAIL, email)
        }
        startActivity(intent)
    }
}