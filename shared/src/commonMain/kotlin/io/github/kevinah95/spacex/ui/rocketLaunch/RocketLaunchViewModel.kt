package io.github.kevinah95.spacex.ui.rocketLaunch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kevinah95.spacex.data.sdk.SpaceXSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RocketLaunchViewModel(private val sdk: SpaceXSDK) : ViewModel() {
    private val _uiState = MutableStateFlow(RocketLaunchUiState())
    val uiState: StateFlow<RocketLaunchUiState> = _uiState.asStateFlow()

    init {
        loadLaunches()
    }

    fun loadLaunches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, launches = emptyList())
            try {
                sdk.latestLaunches.collect { launches ->
                    _uiState.value = _uiState.value.copy(isLoading = false, launches = launches)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, launches = emptyList())
            }
        }
    }
}