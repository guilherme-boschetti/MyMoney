package com.guilhermeb.mymoney.di.module

import com.guilhermeb.mymoney.model.data.remote.firebase.FirebaseAuthentication
import com.guilhermeb.mymoney.model.repository.contract.Authenticable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticableModule {

    @Singleton
    @Binds
    abstract fun bindAuthenticable(authenticableImpl: FirebaseAuthentication): Authenticable
}