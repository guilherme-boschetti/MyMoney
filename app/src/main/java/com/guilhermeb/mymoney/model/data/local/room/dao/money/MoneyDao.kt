package com.guilhermeb.mymoney.model.data.local.room.dao.money

import androidx.room.*
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(money: Money): Long

    @Update
    suspend fun update(money: Money)

    @Delete
    suspend fun delete(money: Money)

    @Query("DELETE FROM MONEY WHERE USER_EMAIL = :userEmail")
    suspend fun removeAllMoneyItemsByUser(userEmail: String)

    @Query("SELECT * FROM MONEY WHERE ID = :id")
    suspend fun getMoneyItemById(id: Long): Money?

    @Query(
        "SELECT * FROM MONEY " +
                "WHERE USER_EMAIL = :userEmail " +
                "AND CASE WHEN PAY_DATE IS NOT NULL THEN PAY_DATE BETWEEN :startDate AND :endDate " +
                "ELSE CREATION_DATE BETWEEN :startDate AND :endDate END " +
                "ORDER BY CREATION_DATE DESC"
    )
    fun getAllMoneyItemsByUserBetweenDates(
        userEmail: String,
        startDate: String,
        endDate: String
    ): Flow<List<Money>>

    @Query("SELECT DISTINCT SUBTYPE FROM MONEY WHERE USER_EMAIL = :userEmail AND SUBTYPE LIKE '%' || :filter || '%'")
    fun getSubtypesByUserAndFilter(userEmail: String, filter: String): Flow<List<String>>

    @Query(
        "SELECT DISTINCT SUBSTR(CASE WHEN PAY_DATE IS NOT NULL THEN PAY_DATE ELSE CREATION_DATE END, 1, 7) AS YEAR_AND_MONTH " +
                "FROM MONEY " +
                "WHERE USER_EMAIL = :userEmail " +
                "ORDER BY YEAR_AND_MONTH DESC"
    )
    fun getAllMonthsByUser(userEmail: String): Flow<List<String>>
}