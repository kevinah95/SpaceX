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
package io.github.kevinah95.spacex.monitoring

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.analytics.logEvent

actual object AnalyticsReporter {
  actual fun logEvent(name: String, params: Map<String, Any?>) {
    Firebase.analytics.logEvent(name) {
      params.forEach { (key, value) ->
        when (value) {
          null -> Unit
          is String -> param(key, value)
          is Boolean -> param(key, if (value) 1L else 0L)
          is Int -> param(key, value.toLong())
          is Long -> param(key, value)
          is Float -> param(key, value.toDouble())
          is Double -> param(key, value)
          else -> param(key, value.toString())
        }
      }
    }
  }

  actual fun setUserId(userId: String?) {
    Firebase.analytics.setUserId(userId.orEmpty())
  }

  actual fun setUserProperty(name: String, value: String?) {
    Firebase.analytics.setUserProperty(name, value.orEmpty())
  }
}
