package com.guilhermeb.mymoney.common.util

import android.os.Build
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey
import java.util.*

fun getLocale(): Locale? {
    val locale: Locale?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val locales =
            MyMoneyApplication.getInstance().applicationContext.resources.configuration.locales
        locale = locales[0]
    } else {
        @Suppress("DEPRECATION")
        locale = MyMoneyApplication.getInstance().applicationContext.resources.configuration.locale
    }
    return locale
}

fun getLocaleOrDefault(): Locale {
    val locale: Locale? = getLocale()
    return locale ?: Locale.getDefault()
}

private fun getCurrentAppLanguageLocale(): String? {
    val locale: Locale? = getLocale()
    if (locale != null) {
        return "${locale.language}-${locale.country}"
    }
    return null
}

fun getCurrentLanguageLocale(): String? {
    return SharedPreferencesHelper.getInstance()
        .getValue(getSharedPreferencesKey(Constants.LOCALE), null) ?: getCurrentAppLanguageLocale()
}