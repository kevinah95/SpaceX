package io.github.kevinah95.spacex.data.repository

import io.github.kevinah95.spacex.data.local.LocalRocketLaunchesDataSource
import io.github.kevinah95.spacex.data.local.DriverFactory
import io.github.kevinah95.spacex.data.remote.RemoteRocketLaunchesDataSource
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class RocketLaunchesRepository(
    private val databaseDriverFactory: DriverFactory,
    private val remoteRocketLaunchesDataSource: RemoteRocketLaunchesDataSource,
    private val defaultDispatcher: CoroutineDispatcher
) {
    private val localRocketLaunchesDataSource = LocalRocketLaunchesDataSource(databaseDriverFactory)

    val latestLaunches: Flow<List<RocketLaunch>> =
        remoteRocketLaunchesDataSource.latestLaunches
            .onEach { launches -> // Executes on the default dispatcher
                localRocketLaunchesDataSource.clearAndCreateLaunches(launches)
            }
            // flowOn affects the upstream flow ↑
            .flowOn(defaultDispatcher)
            // the downstream flow ↓ is not affected
            // If an error happens, emit the last cached values
            .catch { exception -> // Executes in the consumer's context
                val cachedLaunches = localRocketLaunchesDataSource.getAllLaunches()
                if (cachedLaunches.isNotEmpty() ) {
                    emit(cachedLaunches)
                }
            }
}