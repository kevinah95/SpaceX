package io.github.kevinah95.spacex

import androidx.compose.runtime.Composable

@Composable expect fun rememberAppVersionName(): String

internal fun formatAppVersion(versionName: String?): String =
    versionName?.takeIf { it.isNotBlank() }?.let { "v$it" } ?: "v0.0.0-dev"
