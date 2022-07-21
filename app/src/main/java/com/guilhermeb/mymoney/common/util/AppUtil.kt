package com.guilhermeb.mymoney.common.util

import com.guilhermeb.mymoney.BuildConfig
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R

fun getAppVersion(): String {
    return MyMoneyApplication.getInstance().applicationContext.getString(
        R.string.version,
        BuildConfig.VERSION_NAME
    )
}