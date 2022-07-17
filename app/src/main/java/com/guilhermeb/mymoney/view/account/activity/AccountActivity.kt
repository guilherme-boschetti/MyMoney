package com.guilhermeb.mymoney.view.account.activity

import android.content.Intent
import android.os.Bundle
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.extension.showConfirmationDialog
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.ActivityAccountBinding
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.login.activity.LoginActivity
import com.guilhermeb.mymoney.viewmodel.account.AccountViewModel

class AccountActivity : AbstractActivity() {

    private val accountViewModel by lazy {
        AccountViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accountViewBinding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(accountViewBinding.root)
        setTitle(R.string.account)

        accountViewBinding.edtEmail.setText(accountViewModel.getCurrentUserEmail())

        accountViewBinding.btnLogout.setOnClickListener {
            showConfirmationDialog(
                R.string.are_you_sure_you_want_to_logout
            ) {
                accountViewModel.signOut()
                goToLoginActivity()
            }
        }

        accountViewBinding.txtChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        accountViewBinding.btnDeleteAccount.setOnClickListener {
            showConfirmationDialog(
                R.string.are_you_sure_you_want_to_delete_account
            ) {
                accountViewModel.deleteUser(object : AsyncProcess {
                    override fun onComplete(isSuccessful: Boolean, errorMessage: String?) {
                        if (isSuccessful) {
                            showToast(this@AccountActivity, R.string.account_deleteded_successfully)
                            goToLoginActivity()
                        } else {
                            val message =
                                errorMessage ?: getString(R.string.failed_to_delete_account)
                            showToast(this@AccountActivity, message)
                        }
                    }
                })
            }
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // close the activities on stack
        startActivity(intent)
    }
}