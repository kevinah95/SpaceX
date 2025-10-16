package io.github.kevinah95.spacex

import androidx.compose.ui.window.ComposeUIViewController
import io.github.kevinah95.spacex.di.initKoin

fun MainViewController() = ComposeUIViewController(configure = { initKoin() }) { App() }