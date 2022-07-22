package com.guilhermeb.mymoney.model.data.local.sharedpreferences.dataaccess

import com.guilhermeb.mymoney.model.data.local.sharedpreferences.SharedPrefs

class SharedPrefsDataAccess(private val sharedPrefs: SharedPrefs) {

    /**
     * Get a String value from Shared Preferences
     *
     * @param key
     * @param returnOnNull
     * @return
     */
    fun getValue(key: String?, returnOnNull: String?): String? {
        return sharedPrefs.sharedPreferences.getString(key, returnOnNull)
    }

    /**
     * Get a Boolean value from Shared Preferences
     *
     * @param key
     * @param returnOnNull
     * @return
     */
    fun getValue(key: String?, returnOnNull: Boolean): Boolean {
        return sharedPrefs.sharedPreferences.getBoolean(key, returnOnNull)
    }

    /**
     * Get a Int value from Shared Preferences
     *
     * @param key
     * @param returnOnNull
     * @return
     */
    fun getValue(key: String?, returnOnNull: Int): Int {
        return sharedPrefs.sharedPreferences.getInt(key, returnOnNull)
    }

    /**
     * Put a String value in Shared Preferences
     *
     * @param key
     * @param value
     */
    fun setValue(key: String?, value: String?) {
        val editor = sharedPrefs.sharedPreferences.edit()
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
        val editor = sharedPrefs.sharedPreferences.edit()
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
        val editor = sharedPrefs.sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    /**
     * Remove a key from Shared Preferences
     *
     * @param key
     */
    fun remove(key: String?) {
        val editor = sharedPrefs.sharedPreferences.edit()
        editor.remove(key)
        editor.commit()
    }
}