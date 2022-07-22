package com.guilhermeb.mymoney.common.util

import androidx.appcompat.app.AppCompatDelegate
import com.guilhermeb.mymoney.BuildConfig
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey

fun getAppVersion(): String {
    return MyMoneyApplication.getInstance().applicationContext.getString(
        R.string.version,
        BuildConfig.VERSION_NAME
    )
}

fun configNightMode() {
    val nightMode: String? =
        SharedPreferencesHelper.getInstance()
            .getValue(getSharedPreferencesKey(Constants.NIGHT_MODE), Constants.FOLLOW_SYSTEM)
    if (nightMode != null) {
        when (nightMode) {
            Constants.NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Constants.YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}