package com.guilhermeb.mymoney.model.data.local.datastore.preferences.dataaccess

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.guilhermeb.mymoney.model.data.local.datastore.preferences.DataStorePrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataStorePrefsDataAccess(private val dataSotorePrefs: DataStorePrefs) {

    fun saveStringIntoDataStorePreferences(preferencesKey: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSotorePrefs.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(
                    preferencesKey
                )] = value
            }
        }
    }

    @Suppress("unused")
    fun getStringFromDataStorePreferencesAsync(preferencesKey: String): Flow<String?> {
        val stringFlow: Flow<String?> = dataSotorePrefs.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(
                    preferencesKey
                )]
            }
        return stringFlow
    }

    fun getStringFromDataStorePreferences(preferencesKey: String): String? {
        var stringValue: String?
        // synchronous
        runBlocking {
            stringValue =
                dataSotorePrefs.dataStore.data.first()[stringPreferencesKey(
                    preferencesKey
                )]
        }
        return stringValue
    }

    fun removeStringFromDataStorePreferences(preferencesKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val stringPrefKey = stringPreferencesKey(preferencesKey)
            dataSotorePrefs.dataStore.edit {
                if (it.contains(stringPrefKey)) {
                    it.remove(stringPrefKey)
                }
            }
        }
    }
}