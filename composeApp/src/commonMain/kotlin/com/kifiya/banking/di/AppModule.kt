package com.kifiya.banking.di

import com.kifiya.banking.data.local.TokenStorage
import com.kifiya.banking.data.remote.api.BankingApiService
import com.kifiya.banking.data.remote.api.MockBankingApiService
import com.kifiya.banking.data.repository.AccountRepositoryImpl
import com.kifiya.banking.data.repository.AuthRepositoryImpl
import com.kifiya.banking.data.repository.TransactionRepositoryImpl
import com.kifiya.banking.domain.repository.AccountRepository
import com.kifiya.banking.domain.repository.AuthRepository
import com.kifiya.banking.domain.repository.TransactionRepository
import com.kifiya.banking.domain.usecase.GetAccountsUseCase
import com.kifiya.banking.domain.usecase.GetTransactionsUseCase
import com.kifiya.banking.domain.usecase.LoginUseCase
import com.kifiya.banking.domain.usecase.RefreshTokenUseCase
import com.kifiya.banking.domain.usecase.RegisterUseCase
import com.kifiya.banking.domain.usecase.TransferFundsUseCase
import com.kifiya.banking.presentation.viewmodel.AccountViewModel
import com.kifiya.banking.presentation.viewmodel.AuthViewModel
import com.kifiya.banking.presentation.viewmodel.TransactionViewModel
import eu.anifantakis.lib.ksafe.KSafe
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single<BankingApiService> { MockBankingApiService() }

    single { TokenStorage(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<AccountRepository> { AccountRepositoryImpl(get(), get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }
}

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { RefreshTokenUseCase(get()) }
    factory { GetAccountsUseCase(get()) }
    factory { TransferFundsUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }
}

val presentationModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::TransactionViewModel)
}

val appModules = listOf(platformModule(), dataModule, domainModule, presentationModule)


