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
package io.github.kevinah95.spacex

import io.github.kevinah95.spacex.data.local.ILocalRocketLaunchesDataSource
import io.github.kevinah95.spacex.domain.entity.Links
import io.github.kevinah95.spacex.domain.entity.Patch
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class FakeLocal() : ILocalRocketLaunchesDataSource {
  // In-memory storage for our fake
  private val launches =
      mutableListOf<RocketLaunch>(
          RocketLaunch(
              flightNumber = 1,
              missionName = "Test Mission",
              launchDateUTC = "2025-01-01T00:00:00Z",
              details = "This is a test launch.",
              launchSuccess = true,
              links = Links(Patch("", ""), ""),
          )
      )

  override fun getAllLaunches(): List<RocketLaunch> {
    return launches.toList()
  }

  override fun clearAndCreateLaunches(launches: List<RocketLaunch>) {
    this.launches.clear()
    this.launches.addAll(launches)
  }

  // Helper method for testing setup
  fun simulateEmptyDatabase() {
    launches.clear()
  }
}

class MyTest : KoinTest {

  @BeforeTest
  fun setup() {
    startKoin { modules(module { single<ILocalRocketLaunchesDataSource> { FakeLocal() } }) }
  }

  @AfterTest
  fun tearDown() {
    stopKoin()
  }

  @OptIn(ExperimentalNativeApi::class)
  @Test
  fun `test LocalRocketLaunchesDataSource is correctly injected`() {
    val dataSource = get<ILocalRocketLaunchesDataSource>()
    assertNotNull(dataSource)
    assert(dataSource is FakeLocal)
    assert(dataSource.getAllLaunches().isNotEmpty()) // Should contain the initial test launch
    assert(
        dataSource.getAllLaunches()[0].missionName == "Test Mission"
    ) // Verify the test launch data
  }
}
