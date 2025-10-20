package io.github.kevinah95.spacex.ui.rocketLaunch

import io.github.kevinah95.spacex.domain.entity.RocketLaunch

data class RocketLaunchUiState(
    val isLoading: Boolean = false,
    val launches: List<RocketLaunch> = emptyList()
)