package com.guilhermeb.mymoney.di.module

import android.content.Context
import com.guilhermeb.mymoney.model.data.local.datastore.preferences.DataStorePrefs
import com.guilhermeb.mymoney.model.data.local.datastore.preferences.dataaccess.DataStorePrefsDataAccess
import com.guilhermeb.mymoney.model.data.local.room.dao.money.MoneyDao
import com.guilhermeb.mymoney.model.data.local.room.database.MyMoneyDB
import com.guilhermeb.mymoney.model.data.local.sharedpreferences.SharedPrefs
import com.guilhermeb.mymoney.model.data.local.sharedpreferences.dataaccess.SharedPrefsDataAccess
import com.guilhermeb.mymoney.model.data.remote.firebase.FirebaseAuthentication
import com.guilhermeb.mymoney.model.data.remote.firebase.FirebaseRealTimeDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuthentication(): FirebaseAuthentication {
        return FirebaseAuthentication()
    }

    @Singleton
    @Provides
    fun provideFirebaseRealTimeDataBase(): FirebaseRealTimeDataBase {
        return FirebaseRealTimeDataBase()
    }

    @Singleton
    @Provides
    fun provideMoneyDao(@ApplicationContext context: Context): MoneyDao {
        return MyMoneyDB.getInstance(context).moneyDao
    }

    @Singleton
    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPrefs {
        return SharedPrefs.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideSharedPrefsDataAccess(sharedPrefs: SharedPrefs): SharedPrefsDataAccess {
        return SharedPrefsDataAccess(sharedPrefs)
    }

    @Singleton
    @Provides
    fun provideDataStorePrefs(@ApplicationContext context: Context): DataStorePrefs {
        return DataStorePrefs.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideDataStorePrefsDataAccess(dataStorePrefs: DataStorePrefs): DataStorePrefsDataAccess {
        return DataStorePrefsDataAccess(dataStorePrefs)
    }
}