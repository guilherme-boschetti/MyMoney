package com.guilhermeb.mymoney.common.helper

import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.model.data.local.datastore.preferences.DataStorePrefs
import com.guilhermeb.mymoney.model.data.local.datastore.preferences.dataaccess.DataStorePrefsDataAccess
import com.guilhermeb.mymoney.model.repository.datastore.preferences.DataStorePreferencesRepository

class DataStorePreferencesHelper {

    private val dataStorePrefs =
        DataStorePrefs.getInstance(MyMoneyApplication.getInstance().applicationContext)
    private val dataStorePrefsDataAccess =
        DataStorePrefsDataAccess(dataStorePrefs)
    private val dataStorePreferencesRepository =
        DataStorePreferencesRepository(dataStorePrefsDataAccess)

    /**
     * Singleton instance
     */
    companion object {
        @Volatile
        private var instance: DataStorePreferencesHelper? = null

        @Synchronized
        fun getInstance(): DataStorePreferencesHelper {
            if (instance == null) {
                instance =
                    DataStorePreferencesHelper()
            }
            return instance!!
        }
    }

    fun saveStringIntoDataStorePreferences(preferencesKey: String, value: String) {
        dataStorePreferencesRepository.saveStringIntoDataStorePreferences(preferencesKey, value)
    }

    fun getStringFromDataStorePreferences(preferencesKey: String): String? {
        return dataStorePreferencesRepository.getStringFromDataStorePreferences(preferencesKey)
    }

    fun removeStringFromDataStorePreferences(preferencesKey: String) {
        dataStorePreferencesRepository.removeStringFromDataStorePreferences(preferencesKey)
    }
}

fun getPreferencesDataStoreKey(key: String): String {
    return MyMoneyApplication.getInstance().getCurrentUserEmail() + "/" + key
}

fun getPreferencesDataStoreKey(userEmail: String?, key: String): String {
    return "$userEmail/$key"
}