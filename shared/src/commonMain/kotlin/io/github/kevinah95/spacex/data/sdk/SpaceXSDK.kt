package io.github.kevinah95.spacex.data.sdk

import io.github.kevinah95.spacex.data.cache.Database
import io.github.kevinah95.spacex.data.cache.DriverFactory
import io.github.kevinah95.spacex.data.network.SpaceXApi
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class SpaceXSDK(
    private val databaseDriverFactory: DriverFactory,
    private val api: SpaceXApi,
    private val defaultDispatcher: CoroutineDispatcher
) {
    private val database = Database(databaseDriverFactory)

    val latestLaunches: Flow<List<RocketLaunch>> =
        api.latestLaunches
            .onEach { launches -> // Executes on the default dispatcher
                database.clearAndCreateLaunches(launches)
            }
            // flowOn affects the upstream flow ↑
            .flowOn(defaultDispatcher)
            // the downstream flow ↓ is not affected
            // If an error happens, emit the last cached values
            .catch { exception -> // Executes in the consumer's context
                val cachedLaunches = database.getAllLaunches()
                if (cachedLaunches.isNotEmpty() ) {
                    emit(cachedLaunches)
                }
            }
}