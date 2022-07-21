package com.guilhermeb.mymoney.common.helper

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences
import com.guilhermeb.mymoney.MyMoneyApplication

class SharedPreferencesHelper private constructor(context: Context) {

    private var preferences: SharedPreferences? = null

    init {
        preferences =
            context.getSharedPreferences(MyMoneyApplication::class.java.toString(), MODE_PRIVATE)
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
                    SharedPreferencesHelper(MyMoneyApplication.getInstance().applicationContext)
            }
            return instance!!
        }
    }

    /**
     * Get a String value from Shared Preferences
     *
     * @param key
     * @param returnOnNull
     * @return
     */
    fun getValue(key: String?, returnOnNull: String?): String? {
        return preferences!!.getString(key, returnOnNull)
    }

    /**
     * Get a Boolean value from Shared Preferences
     *
     * @param key
     * @param returnOnNull
     * @return
     */
    fun getValue(key: String?, returnOnNull: Boolean): Boolean {
        return preferences!!.getBoolean(key, returnOnNull)
    }

    /**
     * Get a Int value from Shared Preferences
     *
     * @param key
     * @param returnOnNull
     * @return
     */
    fun getValue(key: String?, returnOnNull: Int): Int {
        return preferences!!.getInt(key, returnOnNull)
    }

    /**
     * Put a String value in Shared Preferences
     *
     * @param key
     * @param value
     */
    fun setValue(key: String?, value: String?) {
        val editor = preferences!!.edit()
        editor.putString(key, value)
        editor.commit()
    }


    /**
     * Put a Boolean value in Shared Preferences
     *
     * @param key
     * @param value
     */
    fun setValue(key: String?, value: Boolean) {
        val editor = preferences!!.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    /**
     * Put a Int value in Shared Preferences
     *
     * @param key
     * @param value
     */
    fun setValue(key: String?, value: Int) {
        val editor = preferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    /**
     * Remove a key from Shared Preferences
     *
     * @param key
     */
    @Suppress("unused")
    fun remove(key: String?) {
        val editor = preferences!!.edit()
        editor.remove(key)
        editor.commit()
    }
}

fun getSharedPreferencesKey(key: String): String {
    return MyMoneyApplication.getInstance().getCurrentUserEmail() + "/" + key
}

fun getSharedPreferencesKey(userEmail: String?, key: String): String {
    return "$userEmail/$key"
}