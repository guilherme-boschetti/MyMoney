package com.guilhermeb.mymoney.di.module

import android.content.Context
import androidx.room.Room
import com.guilhermeb.mymoney.model.data.local.room.dao.money.MoneyDao
import com.guilhermeb.mymoney.model.data.local.room.database.MyMoneyDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    @Singleton
    @Provides
    @Named("test_money_db")
    fun provideMyMoneyDB(@ApplicationContext context: Context): MyMoneyDB {
        return Room.inMemoryDatabaseBuilder(
            context,
            MyMoneyDB::class.java
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    @Named("test_money_dao")
    fun provideMoneyDao(@Named("test_money_db") myMoneyDB: MyMoneyDB): MoneyDao {
        return myMoneyDB.moneyDao
    }
}