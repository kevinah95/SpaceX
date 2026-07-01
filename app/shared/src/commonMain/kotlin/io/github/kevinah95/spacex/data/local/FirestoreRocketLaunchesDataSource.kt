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
package io.github.kevinah95.spacex.data.local

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.github.kevinah95.spacex.domain.entity.RocketLaunch

class FirestoreRocketLaunchesDataSource : ILocalRocketLaunchesDataSource {
  private val firestore = Firebase.firestore
  private val launchesCollection = firestore.collection("launches")
  private val metadataCollection = firestore.collection("metadata")

  override suspend fun getAllLaunches(): List<RocketLaunch> {
    return try {
      val snapshot = launchesCollection.get()
      snapshot.documents.map { it.data() }
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun clearAndCreateLaunches(launches: List<RocketLaunch>) {
    try {
      val snapshot = launchesCollection.get()
      snapshot.documents.forEach { doc ->
        doc.reference.delete()
      }
      launches.forEach { launch ->
        launchesCollection.document(launch.id).set(launch)
      }
    } catch (e: Exception) {
      // Ignored
    }
  }

  override suspend fun getLastFetchedAt(): Long? {
    return try {
      val doc = metadataCollection.document("last_fetched").get()
      if (doc.exists) {
        doc.get<Long>("timestamp")
      } else {
        null
      }
    } catch (e: Exception) {
      null
    }
  }

  override suspend fun saveLastFetchedAt(epochMillis: Long) {
    try {
      metadataCollection.document("last_fetched").set(mapOf("timestamp" to epochMillis))
    } catch (e: Exception) {
      // Ignored
    }
  }
}
