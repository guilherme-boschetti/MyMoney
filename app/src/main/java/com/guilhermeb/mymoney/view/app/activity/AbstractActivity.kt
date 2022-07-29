package com.guilhermeb.mymoney.view.app.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.component.CustomProgressDialog
import com.guilhermeb.mymoney.common.util.setLocale

abstract class AbstractActivity : AppCompatActivity() {

    protected val progressDialog by lazy {
        CustomProgressDialog(this, R.string.loading, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(baseContext)
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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