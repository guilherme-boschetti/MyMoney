package com.guilhermeb.mymoney

import android.app.Application
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyMoneyApplication : Application() {

    @Inject
    lateinit var authenticationViewModel: AuthenticationViewModel

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
}