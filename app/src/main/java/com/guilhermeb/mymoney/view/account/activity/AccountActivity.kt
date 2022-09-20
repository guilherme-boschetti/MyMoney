package com.guilhermeb.mymoney.view.account.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.extension.showConfirmationDialog
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.ActivityAccountBinding
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.login.activity.LoginActivity
import com.guilhermeb.mymoney.viewmodel.account.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountActivity : AbstractActivity() {

    private lateinit var accountViewBinding: ActivityAccountBinding

    @Inject
    lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewBinding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(accountViewBinding.root)
        setTitle(R.string.account)

        buildOptionsMenu()
        initScreen()
    }

    private fun buildOptionsMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.add(1, 1, 1, R.string.terms_and_conditions_title)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == 1) {
                    val intent = Intent(this@AccountActivity, TermsAndConditionsActivity::class.java)
                    startActivity(intent)
                    return true
                }
                return false
            }
        })
    }

    private fun initScreen() {
        accountViewBinding.edtEmail.setText(accountViewModel.getCurrentUserEmail())

        handleClicks()
    }

    private fun handleClicks() {
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
                accountViewModel.deleteUser(object : AsyncProcess<String?> {
                    override fun onComplete(isSuccessful: Boolean, result: String?) {
                        if (isSuccessful) {
                            showToast(this@AccountActivity, R.string.account_deleted_successfully)
                            goToLoginActivity()
                        } else {
                            val message =
                                result ?: getString(R.string.failed_to_delete_account)
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