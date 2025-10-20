package io.github.kevinah95.spacex.data.repository

import io.github.kevinah95.spacex.data.local.LocalRocketLaunchDataSource
import io.github.kevinah95.spacex.data.local.DriverFactory
import io.github.kevinah95.spacex.data.remote.RemoteRocketLaunchDataSource
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class RocketLaunchRepository(
    private val databaseDriverFactory: DriverFactory,
    private val api: RemoteRocketLaunchDataSource,
    private val defaultDispatcher: CoroutineDispatcher
) {
    private val localRocketLaunchDataSource = LocalRocketLaunchDataSource(databaseDriverFactory)

    val latestLaunches: Flow<List<RocketLaunch>> =
        api.latestLaunches
            .onEach { launches -> // Executes on the default dispatcher
                localRocketLaunchDataSource.clearAndCreateLaunches(launches)
            }
            // flowOn affects the upstream flow ↑
            .flowOn(defaultDispatcher)
            // the downstream flow ↓ is not affected
            // If an error happens, emit the last cached values
            .catch { exception -> // Executes in the consumer's context
                val cachedLaunches = localRocketLaunchDataSource.getAllLaunches()
                if (cachedLaunches.isNotEmpty() ) {
                    emit(cachedLaunches)
                }
            }
}