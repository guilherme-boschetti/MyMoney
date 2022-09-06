package com.guilhermeb.mymoney.model.repository.currency

import com.guilhermeb.mymoney.model.data.remote.retrofit.currency.api.CurrencyApi
import com.guilhermeb.mymoney.model.data.remote.retrofit.currency.api.response.ApiResponse
import com.guilhermeb.mymoney.model.data.remote.retrofit.currency.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CurrencyRepository(private val dataSourceApi: CurrencyApi) {

    fun getCurrency(currency: String): Flow<ApiResponse<List<Currency?>?>> {
        return flow {
            var errorMessage: String? = null
            var resultObject: List<Currency>? = null
            try {
                val response = dataSourceApi.getCurrency(currency)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    resultObject = body
                } else {
                    errorMessage = response.message()
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
            emit(ApiResponse(resultObject, errorMessage))
        }
    }
}