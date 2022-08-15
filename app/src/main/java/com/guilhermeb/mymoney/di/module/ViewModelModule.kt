package com.guilhermeb.mymoney.di.module

import com.guilhermeb.mymoney.model.repository.authentication.AuthenticationRepository
import com.guilhermeb.mymoney.model.repository.money.MoneyRepository
import com.guilhermeb.mymoney.viewmodel.account.AccountViewModel
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import com.guilhermeb.mymoney.viewmodel.login.CreateAccountViewModel
import com.guilhermeb.mymoney.viewmodel.login.ForgotPasswordViewModel
import com.guilhermeb.mymoney.viewmodel.login.LoginViewModel
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel
import com.guilhermeb.mymoney.viewmodel.money.chart.ChartViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ViewModelModule {

    @Singleton
    @Provides
    fun provideAuthenticationViewModel(authenticationRepository: AuthenticationRepository): AuthenticationViewModel {
        return AuthenticationViewModel(authenticationRepository)
    }

    @Singleton
    @Provides
    fun provideLoginViewModel(authenticationViewModel: AuthenticationViewModel): LoginViewModel {
        return LoginViewModel(authenticationViewModel)
    }

    @Singleton
    @Provides
    fun provideCreateAccountViewModel(authenticationViewModel: AuthenticationViewModel): CreateAccountViewModel {
        return CreateAccountViewModel(authenticationViewModel)
    }

    @Singleton
    @Provides
    fun provideForgotPasswordViewModel(authenticationViewModel: AuthenticationViewModel): ForgotPasswordViewModel {
        return ForgotPasswordViewModel(authenticationViewModel)
    }

    @Singleton
    @Provides
    fun provideAccountViewModel(
        moneyViewModel: MoneyViewModel,
        authenticationViewModel: AuthenticationViewModel
    ): AccountViewModel {
        return AccountViewModel(moneyViewModel, authenticationViewModel)
    }

    @Singleton
    @Provides
    fun provideMoneyViewModel(
        moneyRepository: MoneyRepository,
        authenticationViewModel: AuthenticationViewModel
    ): MoneyViewModel {
        return MoneyViewModel(moneyRepository, authenticationViewModel)
    }

    @Singleton
    @Provides
    fun provideChartViewModel(moneyViewModel: MoneyViewModel): ChartViewModel {
        return ChartViewModel(moneyViewModel)
    }
}