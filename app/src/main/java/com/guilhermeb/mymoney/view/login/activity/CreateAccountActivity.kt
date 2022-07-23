package com.guilhermeb.mymoney.view.login.activity

import android.content.Intent
import android.os.Bundle
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.extension.afterTextChanged
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.common.util.isNetworkAvailable
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.ActivityCreateAccountBinding
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.app.offline.activity.OfflineActivity
import com.guilhermeb.mymoney.view.login.dialog.TermsAndConditionsDialog
import com.guilhermeb.mymoney.view.money.activity.MoneyHostActivity
import com.guilhermeb.mymoney.viewmodel.login.CreateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountActivity : AbstractActivity(),
    TermsAndConditionsDialog.TermsAndConditionsCallback {

    private lateinit var createAccountViewBinding: ActivityCreateAccountBinding

    @Inject
    lateinit var createAccountViewModel: CreateAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createAccountViewBinding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(createAccountViewBinding.root)
        setTitle(R.string.create_account)

        handleInputEvents()

        handleClicks()

        observeProperties()

        val intent = intent
        if (intent.hasExtra(Constants.INTENT_EXTRA_KEY_EMAIL)) {
            createAccountViewBinding.edtEmail.setText(intent.getStringExtra(Constants.INTENT_EXTRA_KEY_EMAIL))
        }
        if (intent.hasExtra(Constants.INTENT_EXTRA_KEY_PASSWORD)) {
            createAccountViewBinding.edtPassword.setText(intent.getStringExtra(Constants.INTENT_EXTRA_KEY_PASSWORD))
        }
    }

    private fun handleInputEvents() {
        createAccountViewBinding.edtEmail.apply {

            changeHintOnFocusChange(
                this@CreateAccountActivity,
                getString(R.string.hint_email),
                ""
            )

            afterTextChanged {
                createAccountViewBinding.inEmail.error = null
                createAccountViewModel.createAccountFormDataChanged(
                    createAccountViewBinding.edtEmail.text.toString(),
                    createAccountViewBinding.edtRepeatEmail.text.toString(),
                    createAccountViewBinding.edtPassword.text.toString(),
                    createAccountViewBinding.edtRepeatPassword.text.toString(),
                    createAccountViewBinding.chkTermsAndConditions.isChecked
                )
            }
        }

        createAccountViewBinding.edtRepeatEmail.apply {

            changeHintOnFocusChange(
                this@CreateAccountActivity,
                getString(R.string.hint_email),
                ""
            )

            afterTextChanged {
                createAccountViewBinding.inRepeatEmail.error = null
                createAccountViewModel.createAccountFormDataChanged(
                    createAccountViewBinding.edtEmail.text.toString(),
                    createAccountViewBinding.edtRepeatEmail.text.toString(),
                    createAccountViewBinding.edtPassword.text.toString(),
                    createAccountViewBinding.edtRepeatPassword.text.toString(),
                    createAccountViewBinding.chkTermsAndConditions.isChecked
                )
            }
        }

        createAccountViewBinding.edtPassword.apply {

            changeHintOnFocusChange(
                this@CreateAccountActivity,
                getString(R.string.hint_password),
                ""
            )

            afterTextChanged {
                createAccountViewBinding.inPassword.error = null
                createAccountViewModel.createAccountFormDataChanged(
                    createAccountViewBinding.edtEmail.text.toString(),
                    createAccountViewBinding.edtRepeatEmail.text.toString(),
                    createAccountViewBinding.edtPassword.text.toString(),
                    createAccountViewBinding.edtRepeatPassword.text.toString(),
                    createAccountViewBinding.chkTermsAndConditions.isChecked
                )
            }
        }

        createAccountViewBinding.edtRepeatPassword.apply {

            changeHintOnFocusChange(
                this@CreateAccountActivity,
                getString(R.string.hint_password),
                ""
            )

            afterTextChanged {
                createAccountViewBinding.inRepeatPassword.error = null
                createAccountViewModel.createAccountFormDataChanged(
                    createAccountViewBinding.edtEmail.text.toString(),
                    createAccountViewBinding.edtRepeatEmail.text.toString(),
                    createAccountViewBinding.edtPassword.text.toString(),
                    createAccountViewBinding.edtRepeatPassword.text.toString(),
                    createAccountViewBinding.chkTermsAndConditions.isChecked
                )
            }
        }

        createAccountViewBinding.chkTermsAndConditions.setOnCheckedChangeListener { _, _ ->
            createAccountViewModel.createAccountFormDataChanged(
                createAccountViewBinding.edtEmail.text.toString(),
                createAccountViewBinding.edtRepeatEmail.text.toString(),
                createAccountViewBinding.edtPassword.text.toString(),
                createAccountViewBinding.edtRepeatPassword.text.toString(),
                createAccountViewBinding.chkTermsAndConditions.isChecked
            )
        }
    }

    private fun handleClicks() {
        createAccountViewBinding.apply {
            txtTermsAndConditions.setOnClickListener {
                TermsAndConditionsDialog(
                    this@CreateAccountActivity,
                    this@CreateAccountActivity
                ).openDialog()
            }

            btnCreateAccount.setOnClickListener {
                if (isNetworkAvailable()) {

                    val email = edtEmail.text.toString()
                    val emailRepeated = edtRepeatEmail.text.toString()
                    val password = edtPassword.text.toString()
                    val passwordRepeated = edtRepeatPassword.text.toString()
                    val termsAndConditionsChecked = chkTermsAndConditions.isChecked

                    if (createAccountViewModel.isCreateAccountFormDataValid(
                            email,
                            emailRepeated,
                            password,
                            passwordRepeated,
                            termsAndConditionsChecked
                        )
                    ) {
                        progressDialog.show()
                        createAccountViewModel.createUser(email, password, object : AsyncProcess {
                            override fun onComplete(isSuccessful: Boolean, errorMessage: String?) {
                                progressDialog.dismiss()
                                if (isSuccessful) {
                                    goToMoneyHostActivity()
                                } else {
                                    val message =
                                        errorMessage ?: getString(R.string.failed_to_create_account)
                                    showToast(this@CreateAccountActivity, message)
                                }
                            }
                        })
                    }

                } else {
                    val intent = Intent(this@CreateAccountActivity, OfflineActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun observeProperties() {
        createAccountViewModel.createAccountFormState.observe(this) { formState ->
            // disable create account button unless form is completed
            createAccountViewBinding.btnCreateAccount.isEnabled = formState.isFormCompleted

            if (formState.emailError != null) {
                createAccountViewBinding.inEmail.error = getString(formState.emailError)
            } else {
                createAccountViewBinding.inEmail.error = null
            }
            if (formState.emailRepeatedError != null) {
                createAccountViewBinding.inRepeatEmail.error =
                    getString(formState.emailRepeatedError)
            } else {
                createAccountViewBinding.inRepeatEmail.error = null
            }
            if (formState.passwordError != null) {
                createAccountViewBinding.inPassword.error = getString(formState.passwordError)
            } else {
                createAccountViewBinding.inPassword.error = null
            }
            if (formState.passwordRepeatedError != null) {
                createAccountViewBinding.inRepeatPassword.error =
                    getString(formState.passwordRepeatedError)
            } else {
                createAccountViewBinding.inRepeatPassword.error = null
            }
            if (formState.differentEmailError != null) {
                createAccountViewBinding.inEmail.error = getString(formState.differentEmailError)
                createAccountViewBinding.inRepeatEmail.error =
                    getString(formState.differentEmailError)
            } else {
                if (formState.emailError == null) {
                    createAccountViewBinding.inEmail.error = null
                }
                if (formState.emailRepeatedError == null) {
                    createAccountViewBinding.inRepeatEmail.error = null
                }
            }
            if (formState.differentPasswordError != null) {
                createAccountViewBinding.inPassword.error =
                    getString(formState.differentPasswordError)
                createAccountViewBinding.inRepeatPassword.error =
                    getString(formState.differentPasswordError)
            } else {
                if (formState.passwordError == null) {
                    createAccountViewBinding.inPassword.error = null
                }
                if (formState.passwordRepeatedError == null) {
                    createAccountViewBinding.inRepeatPassword.error = null
                }
            }
            if (formState.termsAndConditionsError != null) {
                showToast(this, formState.termsAndConditionsError)
            }
        }
    }

    override fun termsAndConditionsAccepted() {
        createAccountViewBinding.apply {
            chkTermsAndConditions.isEnabled = true
            chkTermsAndConditions.isChecked = true
        }
    }

    private fun goToMoneyHostActivity() {
        val intent = Intent(this@CreateAccountActivity, MoneyHostActivity::class.java)
        startActivity(intent)
        finish()
    }
}