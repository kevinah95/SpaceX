package io.github.kevinah95.spacex.di


import io.github.kevinah95.spacex.ui.rocketLaunch.RocketLaunchViewModel
import io.github.kevinah95.spacex.data.sdk.SpaceXSDK
import io.github.kevinah95.spacex.data.cache.DriverFactory
import io.github.kevinah95.spacex.data.network.SpaceXApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SpaceXApi> { SpaceXApi(Dispatchers.IO) }
    single<SpaceXSDK> { SpaceXSDK(get(), get(), Dispatchers.Default) }
    factory { DriverFactory(this) }
    viewModel { RocketLaunchViewModel(sdk = get()) }
}