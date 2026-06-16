/*
 * Copyright 2025-2026 kevinah95 (Kevin A. Hernández Rostrán)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.kevinah95.spacex.data.repository

import io.github.kevinah95.spacex.data.local.ILocalRocketLaunchesDataSource
import io.github.kevinah95.spacex.data.remote.IRemoteRocketLaunchesDataSource
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import io.github.kevinah95.spacex.monitoring.CrashReporter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// ll.thespacedevs.com throttles at 15 requests per hour for anonymous users (~1 per 4 minutes)
private const val MIN_FETCH_INTERVAL_MS = 4 * 60 * 1000L

class RocketLaunchesRepository(
    private val localRocketLaunchesDataSource: ILocalRocketLaunchesDataSource,
    private val remoteRocketLaunchesDataSource: IRemoteRocketLaunchesDataSource,
    private val defaultDispatcher: CoroutineDispatcher,
) : IRocketLaunchesRepository {

  override val latestLaunches: Flow<List<RocketLaunch>> =
      flow {
            // 1. Always emit the local cache first (local-first strategy)
            val cached = localRocketLaunchesDataSource.getAllLaunches()
            if (cached.isNotEmpty()) {
              emit(cached)
            }

            // 2. Respect rate limit: only fetch from network if enough time has passed
            val lastFetchedAt = localRocketLaunchesDataSource.getLastFetchedAt()
            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val isCacheStale =
                lastFetchedAt == null || (now - lastFetchedAt) >= MIN_FETCH_INTERVAL_MS

            if (isCacheStale) {
              try {
                remoteRocketLaunchesDataSource.latestLaunches().collect { launches ->
                  localRocketLaunchesDataSource.clearAndCreateLaunches(launches)
                  localRocketLaunchesDataSource.saveLastFetchedAt(
                      kotlin.time.Clock.System.now().toEpochMilliseconds()
                  )
                  emit(launches)
                }
              } catch (exception: Exception) {
                CrashReporter.recordException(
                    exception,
                    "Failed fetching launches from network. Falling back to local cache.",
                )
                if (cached.isEmpty()) {
                  val freshCached = localRocketLaunchesDataSource.getAllLaunches()
                  if (freshCached.isNotEmpty()) {
                    emit(freshCached)
                  }
                }
              }
            }
          }
          .flowOn(defaultDispatcher)
}
