package io.github.kevinah95.spacex

import io.github.kevinah95.spacex.cache.Database
import io.github.kevinah95.spacex.cache.DriverFactory
import io.github.kevinah95.spacex.entity.RocketLaunch
import io.github.kevinah95.spacex.network.SpaceXApi


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