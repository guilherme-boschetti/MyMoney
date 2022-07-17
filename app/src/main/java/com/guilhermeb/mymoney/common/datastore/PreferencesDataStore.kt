package com.guilhermeb.mymoney.common.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.guilhermeb.mymoney.MyMoneyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "MY_MONEY_DATASTORE")

fun savePasswordIntoDataStorePreferences(preferencesKey: String, password: String) {
    CoroutineScope(Dispatchers.IO).launch {
        MyMoneyApplication.getInstance()?.applicationContext?.let { context ->
            context.applicationContext!!.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(preferencesKey)] = password
            }
        }
    }
}

fun getPasswordFromDataStorePreferences(preferencesKey: String): String? {
    var password: String? = null
    /*CoroutineScope(Dispatchers.IO).launch {
        MyMoneyApplication.getInstance()?.applicationContext?.let { context ->
            context.dataStore.data.collect { preferences ->
                preferences[stringPreferencesKey(preferencesKey)]?.let {
                    password = it
                }
            }
        }
    }*/
    // synchronous
    runBlocking {
        MyMoneyApplication.getInstance()?.applicationContext?.let { context ->
            password = context.dataStore.data.first()[stringPreferencesKey(preferencesKey)]
        }
    }
    return password
}

fun getPreferencesDataStoreKey(key: String): String {
    return MyMoneyApplication.getInstance()?.getCurrentUserEmail() + "/" + key
}