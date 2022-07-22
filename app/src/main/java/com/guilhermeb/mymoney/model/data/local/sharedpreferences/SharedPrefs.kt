package com.guilhermeb.mymoney.model.data.local.sharedpreferences

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs private constructor(context: Context) {

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MY_MONEY_SHARED_PREFERENCES", Context.MODE_PRIVATE)

    /**
     * Singleton instance
     */
    companion object {
        @Volatile
        private var instance: SharedPrefs? = null

        @Synchronized
        fun getInstance(context: Context): SharedPrefs {
            if (instance == null) {
                instance =
                    SharedPrefs(context)
            }
            return instance!!
        }
    }
}