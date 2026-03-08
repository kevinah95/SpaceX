package io.github.kevinah95.spacex.di.modules

import io.github.kevinah95.spacex.data.local.DriverFactory
import org.koin.dsl.module

actual fun platformModule() = module { single { DriverFactory() } }
