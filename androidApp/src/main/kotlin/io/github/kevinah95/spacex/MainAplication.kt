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

import android.app.Application
import android.util.Log
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import io.github.kevinah95.spacex.di.initKoin
import io.github.kevinah95.spacex.notifications.PushMessagingReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {

  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  override fun onCreate() {
    super.onCreate()

    Firebase.initialize(this)
    syncCurrentFcmToken()

    initKoin {
      androidContext(this@MainApplication)
      androidLogger()
    }
  }

  private fun syncCurrentFcmToken() {
    applicationScope.launch {
      runCatching { PushMessagingReporter.getToken() }
          .onSuccess { token ->
            Log.d(TAG, "FCM token generado: $token")
            // TODO: Enviar este token a tu backend para segmentar envios por usuario/dispositivo.
          }
          .onFailure { error -> Log.e(TAG, "No se pudo obtener el token FCM", error) }
    }
  }

  companion object {
    private const val TAG = "MainApplication"
  }
}
