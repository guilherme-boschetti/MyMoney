package com.guilhermeb.mymoney.model.repository.money

import com.guilhermeb.mymoney.model.data.local.room.dao.money.MoneyDao
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import kotlinx.coroutines.flow.Flow

class MoneyRepository(private val dataSource: MoneyDao) {

    suspend fun insert(moneyItem: Money) {
        dataSource.insert(moneyItem)
    }

    suspend fun update(moneyItem: Money) {
        dataSource.update(moneyItem)
    }

    suspend fun delete(moneyItem: Money) {
        dataSource.delete(moneyItem)
    }

    suspend fun removeAllMoneyItemsByUser(userEmail: String) {
        dataSource.removeAllMoneyItemsByUser(userEmail)
    }

    suspend fun getMoneyItemById(id: Long): Money? {
        return dataSource.getMoneyItemById(id)
    }

    fun getAllMoneyItemsByUserBetweenDates(
        userEmail: String,
        startDate: String,
        endDate: String
    ): Flow<List<Money>> {
        return dataSource.getAllMoneyItemsByUserBetweenDates(userEmail, startDate, endDate)
    }

    fun getSubtypesByUserAndFilter(userEmail: String, filter: String): Flow<List<String>> {
        return dataSource.getSubtypesByUserAndFilter(userEmail, filter)
    }

    fun getAllMonthsByUser(userEmail: String): Flow<List<String>> {
        return dataSource.getAllMonthsByUser(userEmail)
    }
}