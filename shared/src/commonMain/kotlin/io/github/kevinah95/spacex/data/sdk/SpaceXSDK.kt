package io.github.kevinah95.spacex.data.sdk

import io.github.kevinah95.spacex.data.cache.Database
import io.github.kevinah95.spacex.data.cache.DriverFactory
import io.github.kevinah95.spacex.data.network.SpaceXApi
import io.github.kevinah95.spacex.domain.entity.RocketLaunch

class SpaceXSDK(databaseDriverFactory: DriverFactory, val api: SpaceXApi) {
    private val database = Database(databaseDriverFactory)

    @Throws(Exception::class)
    suspend fun getLaunches(forceReload: Boolean): List<RocketLaunch> {
        val cachedLaunches = database.getAllLaunches()
        return if (cachedLaunches.isNotEmpty() && !forceReload) {
            cachedLaunches
        } else {
            api.getAllLaunches().also {
                database.clearAndCreateLaunches(it)
            }
        }
    }
}