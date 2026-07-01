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
package io.github.kevinah95.spacex.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kevinah95.spacex.auth.AuthClient
import io.github.kevinah95.spacex.monitoring.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(AuthUiState())
  val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

  init {
    refreshSession()
  }

  fun refreshSession() {
    val currentUserId = AuthClient.currentUserId()
    _uiState.value =
        _uiState.value.copy(
            isAuthenticated = currentUserId != null,
            isAnonymousUser = AuthClient.isAnonymousUser(),
            userId = currentUserId,
            errorMessage = null,
        )
  }

  fun signInAnonymously() {
    if (_uiState.value.isLoading) return

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

      runCatching { AuthClient.signInAnonymously() }
          .onSuccess { userId ->
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    isAnonymousUser = true,
                    userId = userId,
                )
          }
          .onFailure { error ->
            CrashReporter.recordException(error, "Anonymous sign-in failed")
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    errorMessage = error.message ?: "Could not authenticate",
                )
          }
    }
  }

  fun signOut() {
    if (_uiState.value.isLoading) return

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

      runCatching { AuthClient.signOut() }
          .onSuccess {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    isAnonymousUser = false,
                    userId = null,
                )
          }
          .onFailure { error ->
            CrashReporter.recordException(error, "Anonymous sign-out failed")
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Could not sign out",
                )
          }
    }
  }
}
