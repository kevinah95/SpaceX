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

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import io.github.kevinah95.spacex.data.local.ILocalRocketLaunchesDataSource
import io.github.kevinah95.spacex.data.remote.IRemoteRocketLaunchesDataSource
import io.github.kevinah95.spacex.domain.entity.Links
import io.github.kevinah95.spacex.domain.entity.Patch
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class RocketLaunchesRepositoryTest {

  private val localDataSource = mock<ILocalRocketLaunchesDataSource>()
  private val remoteDataSource = mock<IRemoteRocketLaunchesDataSource>()

  @Test
  fun `latestLaunches should fetch from remote and save to local`() = runTest {
    // Arrange
    val remoteLaunches =
        listOf(
            RocketLaunch(
                flightNumber = 1,
                missionName = "Falcon 1",
                launchDateUTC = "2006-03-24T22:30:00.000Z",
                details = null,
                launchSuccess = false,
                links = Links(Patch(null, null), null),
            )
        )

    every { remoteDataSource.latestLaunches() } returns flowOf(remoteLaunches)
    every { localDataSource.clearAndCreateLaunches(remoteLaunches) } returns Unit

    val repository =
        RocketLaunchesRepository(
            localRocketLaunchesDataSource = localDataSource,
            remoteRocketLaunchesDataSource = remoteDataSource,
            defaultDispatcher = Dispatchers.Unconfined,
        )

    // Act
    val result = repository.latestLaunches.first()

    // Assert
    assertEquals(remoteLaunches, result)
    verify { localDataSource.clearAndCreateLaunches(remoteLaunches) }
  }

  @Test
  fun `latestLaunches should return cached data when remote fails`() = runTest {
    // Arrange
    val cachedLaunches =
        listOf(
            RocketLaunch(
                flightNumber = 2,
                missionName = "Cached Mission",
                launchDateUTC = "2024-01-01T00:00:00Z",
                details = null,
                launchSuccess = true,
                links = Links(Patch(null, null), null),
            )
        )
    every { remoteDataSource.latestLaunches() } returns flow { throw Exception("Remote error") }
    every { localDataSource.getAllLaunches() } returns cachedLaunches

    val repository =
        RocketLaunchesRepository(
            localRocketLaunchesDataSource = localDataSource,
            remoteRocketLaunchesDataSource = remoteDataSource,
            defaultDispatcher = Dispatchers.Unconfined,
        )

    // Act
    val result = repository.latestLaunches.first()

    // Assert
    assertEquals(cachedLaunches, result)
    verify { localDataSource.getAllLaunches() }
  }
}
