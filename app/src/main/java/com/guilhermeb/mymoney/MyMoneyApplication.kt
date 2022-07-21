package com.guilhermeb.mymoney

import android.app.Application
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import java.util.*

class MyMoneyApplication : Application() {

    private val authenticationViewModel by lazy {
        AuthenticationViewModel()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        @Volatile
        private var INSTANCE: MyMoneyApplication? = null

        @Synchronized
        fun getInstance(): MyMoneyApplication {
            if (INSTANCE == null)
                throw IllegalStateException("Configure the application on AndroidManifest.xml")
            return INSTANCE!!
        }
    }

    fun getCurrentUserEmail(): String? = authenticationViewModel.getCurrentUserEmail()

    @Suppress("DEPRECATION")
    fun setLocale() {
        val localeString =
            SharedPreferencesHelper.getInstance()
                .getValue(getSharedPreferencesKey(Constants.LOCALE), null)

        if (localeString != null) {

            val split = localeString.split("-")
            val language = split[0]
            val country = split[1]

            val locale = Locale(language, country)
            Locale.setDefault(locale)

            val configuration = applicationContext.resources.configuration

            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            //val context = createConfigurationContext(configuration)
            applicationContext.resources.updateConfiguration(
                configuration,
                applicationContext.resources.displayMetrics
            )
        }
    }
}