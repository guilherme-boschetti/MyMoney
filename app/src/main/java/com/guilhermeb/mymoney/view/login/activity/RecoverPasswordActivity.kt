package com.guilhermeb.mymoney.view.login.activity

import android.content.Intent
import android.os.Bundle
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.databinding.ActivityRecoverPasswordBinding
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity

class RecoverPasswordActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recoverPasswordViewBinding: ActivityRecoverPasswordBinding =
            ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(recoverPasswordViewBinding.root)
        setTitle(R.string.recover_password)

        var email: String? = null
        val intentActivity = intent
        if (intentActivity.hasExtra(Constants.INTENT_EXTRA_KEY_EMAIL)) {
            email = intentActivity.getStringExtra(Constants.INTENT_EXTRA_KEY_EMAIL)
        }

        recoverPasswordViewBinding.btnBackToLogin.setOnClickListener {
            goToLoginActivityWithEmailExtra(email)
        }
    }

    private fun goToLoginActivityWithEmailExtra(email: String?) {
        val intent = Intent(this, LoginActivity::class.java)
        if (email != null && email.isNotEmpty()) {
            intent.putExtra(Constants.INTENT_EXTRA_KEY_EMAIL, email)
        }
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // close the activities on stack
        startActivity(intent)
    }
}