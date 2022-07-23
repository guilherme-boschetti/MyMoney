package com.guilhermeb.mymoney.di.module

import com.guilhermeb.mymoney.model.data.local.datastore.preferences.dataaccess.DataStorePrefsDataAccess
import com.guilhermeb.mymoney.model.data.local.room.dao.money.MoneyDao
import com.guilhermeb.mymoney.model.data.local.sharedpreferences.dataaccess.SharedPrefsDataAccess
import com.guilhermeb.mymoney.model.repository.authentication.AuthenticationRepository
import com.guilhermeb.mymoney.model.repository.contract.Authenticable
import com.guilhermeb.mymoney.model.repository.datastore.preferences.DataStorePreferencesRepository
import com.guilhermeb.mymoney.model.repository.money.MoneyRepository
import com.guilhermeb.mymoney.model.repository.sharedpreferences.SharedPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthenticationRepository(auth: Authenticable): AuthenticationRepository {
        return AuthenticationRepository(auth)
    }

    @Singleton
    @Provides
    fun provideMoneyRepository(dataSource: MoneyDao): MoneyRepository {
        return MoneyRepository(dataSource)
    }

    @Singleton
    @Provides
    fun provideSharedPreferencesRepository(dataSource: SharedPrefsDataAccess): SharedPreferencesRepository {
        return SharedPreferencesRepository(dataSource)
    }

    @Singleton
    @Provides
    fun provideDataStorePreferencesRepository(dataSource: DataStorePrefsDataAccess): DataStorePreferencesRepository {
        return DataStorePreferencesRepository(dataSource)
    }
}