package io.github.kevinah95.spacex.data.sdk

import io.github.kevinah95.spacex.data.cache.Database
import io.github.kevinah95.spacex.data.cache.DriverFactory
import io.github.kevinah95.spacex.data.network.SpaceXApi
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpaceXSDK(databaseDriverFactory: DriverFactory, val api: SpaceXApi) {
    private val database = Database(databaseDriverFactory)

    @Throws(Exception::class)
    suspend fun getLaunches(forceReload: Boolean): Flow<List<RocketLaunch>> = flow {
        val cachedLaunches = database.getAllLaunches()
        if (cachedLaunches.isNotEmpty() && !forceReload) {
            emit(cachedLaunches)
        } else {
            emit(api.getAllLaunches().also {
                database.clearAndCreateLaunches(it)
            })
        }
    }
}