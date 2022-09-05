package com.guilhermeb.mymoney.model.data.remote.retrofit.currency

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyRetrofitClient {

    companion object {
        private const val BASE_URL = "https://economia.awesomeapi.com.br"

        @Volatile
        private var INSTANCE: Retrofit? = null

        fun getInstance(): Retrofit {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return INSTANCE!!
            }
        }
    }
}