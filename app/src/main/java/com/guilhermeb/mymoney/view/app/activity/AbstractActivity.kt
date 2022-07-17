package com.guilhermeb.mymoney.view.app.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.component.CustomProgressDialog
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey
import java.util.*

abstract class AbstractActivity : AppCompatActivity() {

    protected val progressDialog by lazy {
        CustomProgressDialog(this, R.string.loading, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale()
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

    @Suppress("DEPRECATION")
    private fun setLocale() {
        val localeString =
            SharedPreferencesHelper.getInstance()
                ?.getValue(getSharedPreferencesKey(Constants.LOCALE), null)

        if (localeString != null) {

            val split = localeString.split("-")
            val language = split[0]
            val country = split[1]

            val locale = Locale(language, country)
            Locale.setDefault(locale)

            val configuration = resources.configuration

            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            //val context = createConfigurationContext(configuration)
            baseContext.resources.updateConfiguration(
                configuration,
                baseContext.resources.displayMetrics
            )
        }
    }
}