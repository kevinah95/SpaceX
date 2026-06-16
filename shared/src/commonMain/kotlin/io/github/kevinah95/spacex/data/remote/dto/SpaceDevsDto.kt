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
package io.github.kevinah95.spacex.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpaceDevsLaunchResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<SpaceDevsLaunchDto>,
)

@Serializable
data class SpaceDevsLaunchDto(
    val id: String,
    val name: String,
    val net: String,
    val status: SpaceDevsStatusDto,
    val image: SpaceDevsImageDto?,
    val mission: SpaceDevsMissionDto?,
)

@Serializable
data class SpaceDevsStatusDto(
    val id: Int,
    val name: String,
    val abbrev: String,
    val description: String,
)

@Serializable
data class SpaceDevsImageDto(
    val id: Int,
    val name: String,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("thumbnail_url") val thumbnailUrl: String?,
)

@Serializable
data class SpaceDevsMissionDto(
    val id: Int,
    val name: String,
    val description: String?,
    val type: String,
)
