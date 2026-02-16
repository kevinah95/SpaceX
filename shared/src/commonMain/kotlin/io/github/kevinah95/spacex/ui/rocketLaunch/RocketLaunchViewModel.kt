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
package io.github.kevinah95.spacex.ui.rocketLaunch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kevinah95.spacex.data.repository.IRocketLaunchesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RocketLaunchViewModel(private val rocketLaunchesRepository: IRocketLaunchesRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(RocketLaunchUiState())
  val uiState: StateFlow<RocketLaunchUiState> = _uiState.asStateFlow()

  init {
    loadLaunches()
  }

  fun loadLaunches() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, launches = emptyList())
      try {
        rocketLaunchesRepository.latestLaunches.collect { launches ->
          _uiState.value = _uiState.value.copy(isLoading = false, launches = launches)
        }
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(isLoading = false, launches = emptyList())
      }
    }
  }
}
