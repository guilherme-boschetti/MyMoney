package com.guilhermeb.mymoney.view.account.activity

import android.content.Intent
import android.os.Bundle
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.extension.afterTextChanged
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.common.util.isNetworkAvailable
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.ActivityChangePasswordBinding
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.app.offline.activity.OfflineActivity
import com.guilhermeb.mymoney.viewmodel.account.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordActivity : AbstractActivity() {

    private lateinit var changePasswordViewBinding: ActivityChangePasswordBinding

    @Inject
    lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordViewBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(changePasswordViewBinding.root)
        setTitle(R.string.change_password)

        handleInputEvents()

        handleClicks()

        observeProperties()
    }

    private fun handleInputEvents() {
        changePasswordViewBinding.edtCurrentPassword.apply {

            changeHintOnFocusChange(
                this@ChangePasswordActivity,
                getString(R.string.hint_password),
                ""
            )

            afterTextChanged {
                changePasswordViewBinding.inCurrentPassword.error = null
                accountViewModel.changePasswordFormDataChanged(
                    changePasswordViewBinding.edtCurrentPassword.text.toString(),
                    changePasswordViewBinding.edtNewPassword.text.toString(),
                    changePasswordViewBinding.edtRepeatNewPassword.text.toString()
                )
            }
        }

        changePasswordViewBinding.edtNewPassword.apply {

            changeHintOnFocusChange(
                this@ChangePasswordActivity,
                getString(R.string.hint_password),
                ""
            )

            afterTextChanged {
                changePasswordViewBinding.inNewPassword.error = null
                accountViewModel.changePasswordFormDataChanged(
                    changePasswordViewBinding.edtCurrentPassword.text.toString(),
                    changePasswordViewBinding.edtNewPassword.text.toString(),
                    changePasswordViewBinding.edtRepeatNewPassword.text.toString()
                )
            }
        }

        changePasswordViewBinding.edtRepeatNewPassword.apply {

            changeHintOnFocusChange(
                this@ChangePasswordActivity,
                getString(R.string.hint_password),
                ""
            )

            afterTextChanged {
                changePasswordViewBinding.inRepeatNewPassword.error = null
                accountViewModel.changePasswordFormDataChanged(
                    changePasswordViewBinding.edtCurrentPassword.text.toString(),
                    changePasswordViewBinding.edtNewPassword.text.toString(),
                    changePasswordViewBinding.edtRepeatNewPassword.text.toString()
                )
            }
        }
    }

    private fun handleClicks() {
        changePasswordViewBinding.btnChangePassword.setOnClickListener {
            if (isNetworkAvailable()) {

                val currentPassword = changePasswordViewBinding.edtCurrentPassword.text.toString()
                val newPassword = changePasswordViewBinding.edtNewPassword.text.toString()
                val newPasswordRepeated =
                    changePasswordViewBinding.edtRepeatNewPassword.text.toString()
                progressDialog.show()
                if (accountViewModel.isChangePasswordFormDataValid(
                        currentPassword,
                        newPassword,
                        newPasswordRepeated
                    )
                ) {
                    accountViewModel.updatePassword(newPassword, object : AsyncProcess<String?> {
                        override fun onComplete(isSuccessful: Boolean, result: String?) {
                            progressDialog.dismiss()
                            if (isSuccessful) {
                                showToast(
                                    this@ChangePasswordActivity,
                                    R.string.password_updated_successfully
                                )
                                finish()
                            } else {
                                val message =
                                    result ?: getString(R.string.failed_to_update_password)
                                showToast(this@ChangePasswordActivity, message)
                            }
                        }
                    })
                } else {
                    progressDialog.dismiss()
                }

            } else {
                val intent = Intent(this@ChangePasswordActivity, OfflineActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun observeProperties() {
        accountViewModel.changePasswordFormState.observe(this) { formState ->
            // disable change password button unless form is completed
            changePasswordViewBinding.btnChangePassword.isEnabled = formState.isFormCompleted

            if (formState.currentPasswordError != null) {
                changePasswordViewBinding.inCurrentPassword.error =
                    getString(formState.currentPasswordError)
            } else {
                changePasswordViewBinding.inCurrentPassword.error = null
            }
            if (formState.newPasswordError != null) {
                changePasswordViewBinding.inNewPassword.error =
                    getString(formState.newPasswordError)
            } else {
                changePasswordViewBinding.inNewPassword.error = null
            }
            if (formState.newPasswordRepeatedError != null) {
                changePasswordViewBinding.inRepeatNewPassword.error =
                    getString(formState.newPasswordRepeatedError)
            } else {
                changePasswordViewBinding.inRepeatNewPassword.error = null
            }
            if (formState.differentPasswordError != null) {
                changePasswordViewBinding.inNewPassword.error =
                    getString(formState.differentPasswordError)
                changePasswordViewBinding.inRepeatNewPassword.error =
                    getString(formState.differentPasswordError)
            } else {
                if (formState.newPasswordError == null) {
                    changePasswordViewBinding.inNewPassword.error = null
                }
                if (formState.newPasswordRepeatedError == null) {
                    changePasswordViewBinding.inRepeatNewPassword.error = null
                }
            }
        }
    }
}