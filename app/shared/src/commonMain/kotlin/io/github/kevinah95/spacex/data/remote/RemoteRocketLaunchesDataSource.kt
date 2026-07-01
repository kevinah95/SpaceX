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
package io.github.kevinah95.spacex.data.remote

import io.github.kevinah95.spacex.data.remote.dto.SpaceDevsLaunchResponse
import io.github.kevinah95.spacex.domain.entity.Links
import io.github.kevinah95.spacex.domain.entity.Patch
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private const val LAUNCHES_URL =
    "https://ll.thespacedevs.com/2.3.0/launches/?format=json&mode=normal"

class RemoteRocketLaunchesDataSource(
    private val httpClient: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : IRemoteRocketLaunchesDataSource {
  override fun latestLaunches(): Flow<List<RocketLaunch>> =
      flow {
            val response = httpClient.get(LAUNCHES_URL).body<SpaceDevsLaunchResponse>()
            val launches =
                response.results.map { dto ->
                  RocketLaunch(
                      id = dto.id,
                      missionName = dto.name,
                      launchDateUTC = dto.net,
                      details = dto.mission?.description,
                      launchSuccess =
                          when (dto.status.abbrev) {
                            "Success" -> true
                            "Failure" -> false
                            else -> null
                          },
                      links =
                          Links(
                              patch =
                                  dto.image?.let {
                                    Patch(small = it.thumbnailUrl, large = it.imageUrl)
                                  },
                              article = null,
                          ),
                  )
                }
            emit(launches)
          }
          .flowOn(ioDispatcher)
}
