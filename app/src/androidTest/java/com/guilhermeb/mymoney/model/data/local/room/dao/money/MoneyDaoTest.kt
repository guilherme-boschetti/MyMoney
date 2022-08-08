package com.guilhermeb.mymoney.model.data.local.room.dao.money

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.model.data.local.room.database.MyMoneyDB
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
@HiltAndroidTest
class MoneyDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_money_db")
    lateinit var dataBase: MyMoneyDB

    @Inject
    @Named("test_money_dao")
    lateinit var moneyDao: MoneyDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun insertMoney() {
        runTest {
            val moneyInsert = Money(
                userEmail = "test@test.com",
                title = "Test",
                description = "Description Test",
                value = BigDecimal("9.87"),
                type = MoneyType.EXPENSE.name,
                subtype = "Test",
                payDate = Date(),
                paid = true,
                fixed = true,
                dueDay = 10
            )
            val id = moneyDao.insert(moneyInsert)

            moneyInsert.id = id

            val moneyRetrieve = moneyDao.getMoneyItemById(id)

            assertThat(moneyInsert.toString()).isEqualTo(moneyRetrieve.toString())
        }
    }

    @After
    fun tearDown() {
        dataBase.close()
    }
}