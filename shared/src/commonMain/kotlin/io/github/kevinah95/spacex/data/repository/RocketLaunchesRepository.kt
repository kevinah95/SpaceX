/*
 * Copyright 2025 kevinah95 (Kevin A. Hernández Rostrán)
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

class RocketLaunchesRepository(
    private val localRocketLaunchesDataSource: ILocalRocketLaunchesDataSource,
    private val remoteRocketLaunchesDataSource: IRemoteRocketLaunchesDataSource,
    private val defaultDispatcher: CoroutineDispatcher,
) : IRocketLaunchesRepository {

  override val latestLaunches: Flow<List<RocketLaunch>> =
      remoteRocketLaunchesDataSource
          .latestLaunches()
          .onEach { launches -> // Executes on the default dispatcher
            localRocketLaunchesDataSource.clearAndCreateLaunches(launches)
          }
          // flowOn affects the upstream flow ↑
          .flowOn(defaultDispatcher)
          // the downstream flow ↓ is not affected
          // If an error happens, emit the last cached values
          .catch { exception -> // Executes in the consumer's context
            val cachedLaunches = localRocketLaunchesDataSource.getAllLaunches()
            if (cachedLaunches.isNotEmpty()) {
              emit(cachedLaunches)
            }
          }
}
