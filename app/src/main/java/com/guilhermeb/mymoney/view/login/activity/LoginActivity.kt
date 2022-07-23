package com.guilhermeb.mymoney.view.login.activity

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.extension.afterTextChanged
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.common.extension.hideSoftKeyboard
import com.guilhermeb.mymoney.common.util.configNightMode
import com.guilhermeb.mymoney.common.util.isNetworkAvailable
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.ActivityLoginBinding
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.app.offline.activity.OfflineActivity
import com.guilhermeb.mymoney.view.money.activity.MoneyHostActivity
import com.guilhermeb.mymoney.viewmodel.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AbstractActivity() {

    private lateinit var loginViewBinding: ActivityLoginBinding

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Handle the splash screen transition.
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        loginViewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginViewBinding.root)

        handleInputEvents()

        handleClicks()

        observeProperties()

        val intentActivity = intent
        if (intentActivity.hasExtra(Constants.INTENT_EXTRA_KEY_EMAIL)) {
            loginViewBinding.edtEmail.setText(intentActivity.getStringExtra(Constants.INTENT_EXTRA_KEY_EMAIL))
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in
        val currentUserEmail = loginViewModel.getCurrentUserEmail()
        if (currentUserEmail != null) {
            configNightModeAndGoToMoneyHostActivity()
        }
    }

    private fun handleInputEvents() {
        loginViewBinding.edtEmail.apply {

            changeHintOnFocusChange(
                this@LoginActivity,
                getString(R.string.hint_email),
                ""
            )

            afterTextChanged {
                loginViewBinding.inEmail.error = null
            }
        }

        loginViewBinding.edtPassword.apply {

            changeHintOnFocusChange(
                this@LoginActivity,
                getString(R.string.hint_password),
                ""
            )

            afterTextChanged {
                loginViewModel.loginFormDataChanged(
                    loginViewBinding.edtEmail.text.toString(),
                    this@apply.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        hideSoftKeyboard()
                        if (loginViewModel.isLoginFormDataValid(
                                loginViewBinding.edtEmail.text.toString(),
                                this@apply.text.toString()
                            )
                        ) {
                            signIn()
                        }
                    }
                }
                false
            }
        }
    }

    private fun handleClicks() {
        loginViewBinding.btnLogin.setOnClickListener {
            if (isNetworkAvailable()) {
                signIn()
            } else {
                val intent = Intent(this@LoginActivity, OfflineActivity::class.java)
                startActivity(intent)
            }
        }

        loginViewBinding.txtForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            val email = loginViewBinding.edtEmail.text.toString()
            if (email.isNotEmpty()) {
                intent.putExtra(Constants.INTENT_EXTRA_KEY_EMAIL, email)
            }
            startActivity(intent)
        }

        loginViewBinding.btnCreateAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            val email = loginViewBinding.edtEmail.text.toString()
            if (email.isNotEmpty()) {
                intent.putExtra(Constants.INTENT_EXTRA_KEY_EMAIL, email)
            }
            val password = loginViewBinding.edtPassword.text.toString()
            if (password.isNotEmpty()) {
                intent.putExtra(Constants.INTENT_EXTRA_KEY_PASSWORD, password)
            }
            startActivity(intent)
        }
    }

    private fun observeProperties() {
        loginViewModel.loginFormState.observe(this) { loginState ->
            // disable login button unless both email / password is valid
            loginViewBinding.btnLogin.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) {
                loginViewBinding.inEmail.error = getString(loginState.emailError)
            } else {
                loginViewBinding.inEmail.error = null
            }
            if (loginState.passwordError != null) {
                loginViewBinding.inPassword.error = getString(loginState.passwordError)
            } else {
                loginViewBinding.inPassword.error = null
            }
        }
    }

    private fun configNightModeAndGoToMoneyHostActivity() {
        configNightMode()
        goToMoneyHostActivity()
    }

    private fun goToMoneyHostActivity() {
        val intent = Intent(this, MoneyHostActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signIn() {
        val email = loginViewBinding.edtEmail.text.toString()
        val password = loginViewBinding.edtPassword.text.toString()
        progressDialog.show()
        loginViewModel.signIn(email, password, object : AsyncProcess {
            override fun onComplete(isSuccessful: Boolean, errorMessage: String?) {
                progressDialog.dismiss()
                if (isSuccessful) {
                    configNightModeAndGoToMoneyHostActivity()
                } else {
                    val message = errorMessage ?: getString(R.string.failed_to_login)
                    showToast(this@LoginActivity, message)
                    if (errorMessage.equals(getString(R.string.invalid_account)) ||
                        errorMessage.equals(getString(R.string.invalid_email))
                    ) {
                        loginViewBinding.inEmail.error = errorMessage
                    } else if (errorMessage.equals(getString(R.string.invalid_password))) {
                        loginViewBinding.inPassword.error = errorMessage
                    }
                }
            }
        })
    }
}