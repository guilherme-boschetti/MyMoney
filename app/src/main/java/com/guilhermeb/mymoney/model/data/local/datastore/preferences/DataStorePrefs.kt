package com.guilhermeb.mymoney.model.data.local.datastore.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class DataStorePrefs private constructor(context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "MY_MONEY_DATA_STORE_PREFERENCES")
    val dataStore: DataStore<Preferences> = context.dataStore

    /**
     * Singleton instance
     */
    companion object {
        @Volatile
        private var instance: DataStorePrefs? = null

        @Synchronized
        fun getInstance(context: Context): DataStorePrefs {
            if (instance == null) {
                instance =
                    DataStorePrefs(context)
            }
            return instance!!
        }
    }
}