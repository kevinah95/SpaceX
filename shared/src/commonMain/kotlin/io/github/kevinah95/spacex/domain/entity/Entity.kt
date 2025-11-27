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
package io.github.kevinah95.spacex.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RocketLaunch(
    @SerialName("flight_number") val flightNumber: Int,
    @SerialName("name") val missionName: String,
    @SerialName("date_utc") val launchDateUTC: String,
    @SerialName("details") val details: String?,
    @SerialName("success") val launchSuccess: Boolean?,
    @SerialName("links") val links: Links,
) {
  var launchYear = 2025 // TODO: Default value, should be parsed from launchDateUTC
}

@Serializable
data class Links(
    @SerialName("patch") val patch: Patch?,
    @SerialName("article") val article: String?,
)

@Serializable
data class Patch(@SerialName("small") val small: String?, @SerialName("large") val large: String?)
