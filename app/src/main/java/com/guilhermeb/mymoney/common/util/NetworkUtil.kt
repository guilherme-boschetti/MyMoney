package com.guilhermeb.mymoney.common.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.guilhermeb.mymoney.MyMoneyApplication

fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        MyMoneyApplication.getInstance().applicationContext.getSystemService(ConnectivityManager::class.java)
    val currentNetwork = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}