package io.github.kevinah95.spacex.di


import io.github.kevinah95.spacex.RocketLaunchViewModel
import io.github.kevinah95.spacex.SpaceXSDK
import io.github.kevinah95.spacex.cache.DriverFactory
import io.github.kevinah95.spacex.network.SpaceXApi
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SpaceXApi> { SpaceXApi() }
    single<SpaceXSDK> { SpaceXSDK(get(), get()) }
    factory { DriverFactory(this) }
    viewModel { RocketLaunchViewModel(sdk = get()) }
}