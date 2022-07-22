package com.guilhermeb.mymoney.model.repository.sharedpreferences

import com.guilhermeb.mymoney.model.data.local.sharedpreferences.dataaccess.SharedPrefsDataAccess

class SharedPreferencesRepository(private val sharedPrefsDataAccess: SharedPrefsDataAccess) {

    fun getValue(key: String?, returnOnNull: String?): String? {
        return sharedPrefsDataAccess.getValue(key, returnOnNull)
    }

    fun getValue(key: String?, returnOnNull: Boolean): Boolean {
        return sharedPrefsDataAccess.getValue(key, returnOnNull)
    }

    fun getValue(key: String?, returnOnNull: Int): Int {
        return sharedPrefsDataAccess.getValue(key, returnOnNull)
    }

    fun setValue(key: String?, value: String?) {
        sharedPrefsDataAccess.setValue(key, value)
    }

    fun setValue(key: String?, value: Boolean) {
        sharedPrefsDataAccess.setValue(key, value)
    }

    fun setValue(key: String?, value: Int) {
        sharedPrefsDataAccess.setValue(key, value)
    }

    fun remove(key: String?) {
        sharedPrefsDataAccess.remove(key)
    }
}