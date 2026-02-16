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
package io.github.kevinah95.spacex.data.local

import io.github.kevinah95.spacex.domain.entity.Links
import io.github.kevinah95.spacex.domain.entity.Patch
import io.github.kevinah95.spacex.domain.entity.RocketLaunch

class LocalRocketLaunchesDataSource(database: AppDatabase) : ILocalRocketLaunchesDataSource {
  private val dbQuery = database.appDatabaseQueries

  override fun getAllLaunches(): List<RocketLaunch> {
    return dbQuery.selectAllLaunchesInfo(::mapLaunchSelecting).executeAsList()
  }

  private fun mapLaunchSelecting(
      flightNumber: Long,
      missionName: String,
      details: String?,
      launchSuccess: Boolean?,
      launchDateUTC: String,
      patchUrlSmall: String?,
      patchUrlLarge: String?,
      articleUrl: String?,
  ): RocketLaunch {
    return RocketLaunch(
        flightNumber = flightNumber.toInt(),
        missionName = missionName,
        details = details,
        launchDateUTC = launchDateUTC,
        launchSuccess = launchSuccess,
        links =
            Links(
                patch = Patch(small = patchUrlSmall, large = patchUrlLarge),
                article = articleUrl,
            ),
    )
  }

  override fun clearAndCreateLaunches(launches: List<RocketLaunch>) {
    dbQuery.transaction {
      dbQuery.removeAllLaunches()
      launches.forEach { launch ->
        dbQuery.insertLaunch(
            flightNumber = launch.flightNumber.toLong(),
            missionName = launch.missionName,
            details = launch.details,
            launchSuccess = launch.launchSuccess ?: false,
            launchDateUTC = launch.launchDateUTC,
            patchUrlSmall = launch.links.patch?.small,
            patchUrlLarge = launch.links.patch?.large,
            articleUrl = launch.links.article,
        )
      }
    }
  }
}
