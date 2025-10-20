package io.github.kevinah95.spacex.di


import io.github.kevinah95.spacex.ui.rocketLaunch.RocketLaunchViewModel
import io.github.kevinah95.spacex.data.repository.RocketLaunchesRepository
import io.github.kevinah95.spacex.data.local.DriverFactory
import io.github.kevinah95.spacex.data.remote.RemoteRocketLaunchesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<RemoteRocketLaunchesDataSource> { RemoteRocketLaunchesDataSource(Dispatchers.IO) }
    single<RocketLaunchesRepository> { RocketLaunchesRepository(get(), get(), Dispatchers.Default) }
    factory { DriverFactory(this) }
    viewModel { RocketLaunchViewModel(rocketLaunchesRepository = get()) }
}