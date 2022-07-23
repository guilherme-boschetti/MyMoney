package com.guilhermeb.mymoney.common.helper

import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.model.repository.sharedpreferences.SharedPreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class SharedPreferencesHelper {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SharedPreferencesRepositoryInterface {
        fun getSharedPreferencesRepository(): SharedPreferencesRepository
    }

    private val sharedPreferencesRepository: SharedPreferencesRepository

    init {
        val sharedPreferencesRepositoryInterface = EntryPoints.get(
            MyMoneyApplication.getInstance().applicationContext,
            SharedPreferencesRepositoryInterface::class.java
        )
        sharedPreferencesRepository =
            sharedPreferencesRepositoryInterface.getSharedPreferencesRepository()
    }

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