package com.kifiya.banking.di

import eu.anifantakis.lib.ksafe.KSafe
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { KSafe(androidApplication().applicationContext) }
}

