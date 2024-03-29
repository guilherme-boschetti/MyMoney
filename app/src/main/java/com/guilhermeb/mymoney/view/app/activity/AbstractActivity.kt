package com.guilhermeb.mymoney.view.app.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.component.CustomProgressDialog
import com.guilhermeb.mymoney.common.util.getSelectedTheme
import com.guilhermeb.mymoney.common.util.setLocale
import com.guilhermeb.mymoney.view.money.activity.MoneyHostActivity

abstract class AbstractActivity : AppCompatActivity() {

    protected val progressDialog by lazy {
        CustomProgressDialog(this, R.string.loading, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getSelectedTheme(this is MoneyHostActivity))
        setLocale(baseContext)
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun recreate() {
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Treated UpButton here, because the approach in the link below is not working properly
            // https://developer.android.com/training/appbar/up-action
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}