package com.guilhermeb.mymoney.common.helper

import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.model.data.local.sharedpreferences.SharedPrefs
import com.guilhermeb.mymoney.model.data.local.sharedpreferences.dataaccess.SharedPrefsDataAccess
import com.guilhermeb.mymoney.model.repository.sharedpreferences.SharedPreferencesRepository

class SharedPreferencesHelper {

    private val sharedPrefs =
        SharedPrefs.getInstance(MyMoneyApplication.getInstance().applicationContext)
    private val sharedPrefsDataAccess =
        SharedPrefsDataAccess(sharedPrefs)
    private val sharedPreferencesRepository =
        SharedPreferencesRepository(sharedPrefsDataAccess)

    /**
     * Singleton instance
     */
    companion object {
        @Volatile
        private var instance: SharedPreferencesHelper? = null

        @Synchronized
        fun getInstance(): SharedPreferencesHelper {
            if (instance == null) {
                instance =
                    SharedPreferencesHelper()
            }
            return instance!!
        }
    }

    fun getValue(key: String?, returnOnNull: String?): String? {
        return sharedPreferencesRepository.getValue(key, returnOnNull)
    }

    fun getValue(key: String?, returnOnNull: Boolean): Boolean {
        return sharedPreferencesRepository.getValue(key, returnOnNull)
    }

    fun getValue(key: String?, returnOnNull: Int): Int {
        return sharedPreferencesRepository.getValue(key, returnOnNull)
    }

    fun setValue(key: String?, value: String?) {
        sharedPreferencesRepository.setValue(key, value)
    }

    fun setValue(key: String?, value: Boolean) {
        sharedPreferencesRepository.setValue(key, value)
    }

    fun setValue(key: String?, value: Int) {
        sharedPreferencesRepository.setValue(key, value)
    }

    fun remove(key: String?) {
        sharedPreferencesRepository.remove(key)
    }
}

fun getSharedPreferencesKey(key: String): String {
    return MyMoneyApplication.getInstance().getCurrentUserEmail() + "/" + key
}

fun getSharedPreferencesKey(userEmail: String?, key: String): String {
    return "$userEmail/$key"
}